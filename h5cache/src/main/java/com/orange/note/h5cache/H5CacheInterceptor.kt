package com.orange.note.h5cache

import android.os.Build
import android.text.TextUtils
import android.webkit.WebResourceResponse
import com.orange.note.h5cache.util.MD5Util
import com.orange.note.h5cache.util.MimeTypeMapUtil
import java.io.File
import java.net.URI

/**
 * H5静态资源缓存拦截器
 * @author maomao
 * @date 2019-07-11
 */
object H5CacheInterceptor {

    private const val CODE_200 = 200
    private const val MESSAGE_OK = "OK"
    private val hostSet = mutableSetOf<String>()

    fun clearHostSet() = hostSet.clear()

    fun addHostSet(host: String) = hostSet.add(host)

    fun removeHostSet(host: String) = hostSet.remove(host)

    /**
     * please called in WebViewClient shouldInterceptRequest
     */
    fun shouldInterceptRequest(url: String?): WebResourceResponse? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        val uri = URI(url!!)
        if (!hostSet.contains(uri.host)) {
            return null
        }
        var path = uri.path
        if (!H5CacheManager.cacheMapping.containsKey(path)) {
            return null
        }
        val md5 = H5CacheManager.cacheMapping[path] ?: ""
        if (TextUtils.isEmpty(md5)) {
            return null
        }
        path = "$path?v=$md5"
        val file = File(H5CacheManager.cachePathDir + path)
        if (!file.exists() || !file.isFile) {
            return null
        }
        val fileMd5 = MD5Util.md5(file) ?: ""
        if (fileMd5.toLowerCase() != md5.toLowerCase()) {
            return null
        }

        val mimeType = MimeTypeMapUtil.getMimeTypeFromUrl(url) ?: ""
        val webResourceResponse = WebResourceResponse(mimeType, "", file.inputStream())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webResourceResponse.setStatusCodeAndReasonPhrase(CODE_200, MESSAGE_OK)
        }
        return webResourceResponse
    }


}