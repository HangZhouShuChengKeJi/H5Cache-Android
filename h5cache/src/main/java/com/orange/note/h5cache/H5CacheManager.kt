package com.orange.note.h5cache

import android.content.Context
import android.text.TextUtils
import com.orange.note.h5cache.entity.H5CacheItem
import com.orange.note.h5cache.entity.H5CacheMapping
import com.orange.note.h5cache.entity.H5CacheResponse
import com.orange.note.h5cache.exception.DownloadErrorException
import com.orange.note.h5cache.http.H5CacheTask
import com.orange.note.h5cache.util.*
import rx.Observable
import rx.Subscriber
import rx.schedulers.Schedulers
import java.io.File
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * H5静态资源文件缓存管理器
 * @author maomao
 * @date 2019-07-11
 */
object H5CacheManager {

    /**
     * app 的版本号 key
     */
    private const val SP_H5_CACHE_VERSION_CODE = "sp_h5_cache_version_code"
    /**
     * assets 中的静态资源文件 zip 包名
     */
    private const val H5_CACHE_ZIP_FILE = "h5Cache.zip"
    /**
     * assets 中静态资源文件 mapping 文件名
     */
    private const val H5_CACHE_JSON = "h5Cache.json"
    /**
     * app 的版本号默认 value
     */
    private const val DEFAULT_VERSION_CODE = -1
    /**
     * 是否在执行更新
     */
    @Volatile
    private var isRunning: Boolean = false
    /**
     * 静态资源文件总体版本号
     */
    private var version: String? = null
    /**
     * h5 静态资源文件保存的根目录
     */
    internal var cachePathDir: String? = null
    /**
     * 所有 h5 静态资源文件 map
     * e.g.
     * <key, value> = </appPackage/test/bridge.js, 7a2b958ae9978b1abd6209e0ffce049c>
     */
    internal var cacheMapping: ConcurrentMap<String, String> = ConcurrentHashMap()

    private val countDownLatch: CountDownLatch = CountDownLatch(2)

    /**
     * init, please called in application.onCreate()
     */
    fun init(ctx: Context) {
        val context = ctx.applicationContext
        // 路径 /data/data/com.orange.note/files/h5cache
        cachePathDir = "${context.filesDir}/h5cache"

        val versionCode = SharedPreferenceUtil.getInt(context, SP_H5_CACHE_VERSION_CODE, DEFAULT_VERSION_CODE)
        val currentVersionCode = AppUtil.getVersionCode(context)
        // 第一次打开或者版本升级/降级
        if (versionCode == DEFAULT_VERSION_CODE || currentVersionCode != versionCode) {
            unZipFromAssets(context)
        } else {
            countDownLatch.countDown()
        }
        readMappingFile(context, currentVersionCode)
        countDownLatch.await()
    }

    /**
     * get global version
     */
    fun getVersion() = version


    /**
     * check need or not update for h5 resource cache
     */
    fun checkUpdate() {
        if (isRunning) {
            return
        }
        Observable
            .interval(0, 5 * 60 , TimeUnit.SECONDS)
            .observeOn(Schedulers.io())
            .doOnSubscribe {
                isRunning = true
            }
            .flatMap {
                return@flatMap H5CacheTask.checkUpdate(version)
            }
            .filter {
                return@filter it.needUpdate
            }
            .flatMap {
                return@flatMap Observable.from(it.resourceList)
                    .flatMap inner@{ item ->
                        val file = File("$cachePathDir${item.path}?v=${item.md5}")
                        // 如果文件存在并且md5一致，就不用重新下载了
                        if (file.exists() && file.isFile && MD5Util.md5(file).toLowerCase() == item.md5?.toLowerCase()) {
                            return@inner Observable.just(true)
                        }
                        return@inner H5CacheTask.download(item.url!!)
                            .map { body ->
                                val uri = URI(item.url)
                                val path = item.url.replace("${uri.scheme}://${uri.host}", "")
                                return@map FileUtil.saveFile(body.byteStream(), cachePathDir + path)
                            }
                            .map { newFile ->
                                // 如果文件下载成功后，检查 md5 值
                                if (item.md5?.toLowerCase() == MD5Util.md5(newFile).toLowerCase()) {
                                    return@map true
                                } else {
                                    newFile.delete()
                                    return@map false
                                }
                            }
                            .onErrorReturn {
                                return@onErrorReturn false
                            }
                            .doOnNext {
                                // 更新内存 cacheMapping
                                cacheMapping[item.path] = item.md5
                            }
                    }
                    .toList()
                    .flatMap inner@{ list ->
                        return@inner if (list.contains(false))
                            Observable.error<H5CacheResponse>(DownloadErrorException("something download failed"))
                        else
                            Observable.just(it)
                    }
            }
            .subscribe(object : Subscriber<H5CacheResponse>() {

                override fun onNext(t: H5CacheResponse?) {
                    // update h5Cache.json
                    if (t?.needUpdate == true) {
                        version = t.latestVersion
                        val h5CacheMapping = H5CacheMapping(t.latestVersion, t.resourceList)
                        val json = GsonUtil.toJson(h5CacheMapping)
                        val file = File(cachePathDir + File.separator + H5_CACHE_JSON)
                        file.writeText(URLDecoder.decode(json, "UTF-8"))
                    }
                }

                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    // update h5Cache.json if something downloaded failed
                    if (e is DownloadErrorException) {
                        val h5List = mutableListOf<H5CacheItem>()
                        cacheMapping.forEach {
                            val item = H5CacheItem(it.key, it.value)
                            h5List.add(item)
                        }
                        // global version will not change
                        val h5CacheMapping = H5CacheMapping(version, h5List)
                        val json = GsonUtil.toJson(h5CacheMapping)
                        val file = File(cachePathDir + File.separator + H5_CACHE_JSON)
                        file.writeText(URLDecoder.decode(json, "UTF-8"))
                    }
                }

            })
    }

    /**
     * unzip the zip package from assets
     */
    private fun unZipFromAssets(context: Context) {
        if (TextUtils.isEmpty(cachePathDir)) {
            return
        }
        val file = File(cachePathDir!!)
        if (!file.exists()) {
            file.mkdirs()
        }

        Observable.just(H5_CACHE_ZIP_FILE)
            .observeOn(Schedulers.io())
            .map {
                context.resources.assets.open(it)
            }
            .map {
                val filePath = "$cachePathDir/$H5_CACHE_ZIP_FILE"
                return@map FileUtil.saveFile(it, filePath)
            }
            .map {
                ZipUtils.upZipFile(it, cachePathDir)
                return@map it
            }
            .subscribe(object : Subscriber<File>() {
                override fun onCompleted() {
                    unsubscribe()
                    countDownLatch.countDown()
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    // clear the flag because of failed
                    SharedPreferenceUtil.put(context, SP_H5_CACHE_VERSION_CODE, DEFAULT_VERSION_CODE)
                    unsubscribe()
                    countDownLatch.countDown()
                }

                override fun onNext(t: File?) {
                    // if succeed, delete it
                    t?.delete()
                }
            })
    }

    /**
     * read H5CacheMapping from cacheDir/h5Cache.json
     */
    private fun readMappingFile(context: Context, versionCode: Int) {
        val file = File(cachePathDir + File.separator + H5_CACHE_JSON)
        Observable.just(file)
            .observeOn(Schedulers.io())
            .map {
                if (file.isFile && file.exists()) {
                    return@map file.readText()
                } else {
                    val input = context.resources.assets.open(H5_CACHE_JSON)
                    val filePath = "$cachePathDir/$H5_CACHE_JSON"
                    val saveFile = FileUtil.saveFile(input, filePath)
                    return@map saveFile.readText()
                }
            }
            .map {
                val h5Cache = GsonUtil.fromJson(it, H5CacheMapping::class.java)
                version = h5Cache.version
                if (h5Cache.resourceList?.isNotEmpty() == true) {
                    h5Cache.resourceList!!.forEach { item ->
                        cacheMapping[item.path] = item.md5
                    }
                }
            }
            .subscribe(object : Subscriber<Unit>() {
                override fun onNext(t: Unit?) {
                    SharedPreferenceUtil.put(context, SP_H5_CACHE_VERSION_CODE, versionCode)
                }

                override fun onCompleted() {
                    unsubscribe()
                    countDownLatch.countDown()
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    SharedPreferenceUtil.put(context, SP_H5_CACHE_VERSION_CODE, DEFAULT_VERSION_CODE)
                    unsubscribe()
                    countDownLatch.countDown()
                }
            })
    }


}
