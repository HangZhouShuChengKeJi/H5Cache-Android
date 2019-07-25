package com.orange.note.h5cache

import android.content.Context
import android.text.TextUtils
import com.orange.note.h5cache.entity.H5CacheMapping
import com.orange.note.h5cache.entity.H5CacheResponse
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

    /**
     * init, please called in application.onCreate()
     */
    fun init(ctx: Context) {
        val context = ctx.applicationContext
        // 路径 /data/data/com.orange.note/files/h5cache
        cachePathDir = "${context.filesDir}/h5cache"

        val versionCode = SharedPreferenceUtil.getInt(context, SP_H5_CACHE_VERSION_CODE, -1)
        val currentVersionCode = AppUtil.getVersionCode(context)
        // 第一次打开或者版本升级/降级
        if (versionCode == -1 || currentVersionCode != versionCode) {
            unZipFromAssets(context)
        }
        readMappingFile(context, currentVersionCode)
    }

    /**
     * get global version
     */
    fun getVersion() = version


    /**
     * check need or not update for h5 resource cache
     */
    fun checkUpdate(service: String?) {
        if (isRunning) {
            return
        }
        H5CacheTask.checkUpdate(version, service)
            .doOnSubscribe {
                isRunning = true
            }
            .observeOn(Schedulers.io())
            .filter {
                return@filter it.needUpdate
            }
            .flatMap {
                return@flatMap Observable.from(it.itemList)
                    .flatMap inner@{ item ->
                        val file = File("$cachePathDir${item.path}?v=${item.md5}")
                        // 如果文件存在并且md5一致，就不用重新下载了
                        if (file.exists() && file.isFile && MD5Util.md5(file).toLowerCase() == item.md5?.toLowerCase()) {
                            return@inner Observable.just(true)
                        }
                        return@inner H5CacheTask.download(item.url!!)
                            .map { body ->
                                val uri = URI(item.url!!)
                                val path = item.url!!.replace("${uri.scheme}://${uri.host}", "")
                                return@map FileUtil.saveFile(body.byteStream(), cachePathDir + path)
                            }
                            .map { newFile ->
                                if (item.md5?.toLowerCase() == MD5Util.md5(newFile).toLowerCase()) {
                                    // 更新内存 cacheMapping
                                    cacheMapping[item.path] = item.md5
                                    return@map true
                                } else {
                                    newFile.delete()
                                    return@map false
                                }
                            }
                            .onErrorReturn {
                                return@onErrorReturn false
                            }
                    }
                    .toList()
                    .flatMap inner@{ list ->
                        list.forEach { success ->
                            // if someone download failed, return error to subscriber
                            if (!success) {
                                return@inner Observable.error<H5CacheResponse>(IllegalStateException("something wrong"))
                            }
                        }
                        return@inner Observable.just(it)
                    }
            }
            .subscribe(object : Subscriber<H5CacheResponse>() {

                override fun onNext(t: H5CacheResponse?) {
                    // update h5Cache.json
                    if (t?.needUpdate == true) {
                        version = t.latestVersion
                        val h5CacheMapping = H5CacheMapping()
                        h5CacheMapping.version = t.latestVersion
                        h5CacheMapping.resourceList = t.itemList
                        val json = GsonUtil.toJson(h5CacheMapping)
                        val file = File(cachePathDir + File.separator + H5_CACHE_JSON)
                        file.writeText(URLDecoder.decode(json, "UTF-8"))
                    }
                }

                override fun onCompleted() {
                    isRunning = false
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    isRunning = false
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
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<File>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    // clear the flag because of failed
                    SharedPreferenceUtil.put(context, SP_H5_CACHE_VERSION_CODE, -1)
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
            }.subscribe(object : Subscriber<Unit>() {
                override fun onNext(t: Unit?) {
                    SharedPreferenceUtil.put(context, SP_H5_CACHE_VERSION_CODE, versionCode)
                }

                override fun onCompleted() {
                }

                override fun onError(e: Throwable?) {
                    SharedPreferenceUtil.put(context, SP_H5_CACHE_VERSION_CODE, -1)
                }
            })
    }


}
