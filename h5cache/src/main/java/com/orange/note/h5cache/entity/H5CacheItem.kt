package com.orange.note.h5cache.entity

import android.support.annotation.Keep

/**
 * 静态资源文件
 * @author maomao
 * @date 2019-07-25
 */
@Keep
data class H5CacheItem(
    /**
     * 静态资源文件对应 url 的 path，客户端以此来判断是否符合拦截条件
     * 比如 http://www.91chengguo.com/app/test/bridge.js
     * 那么对应的 path 就是 /app/test/bridge.js
     */
    val path: String?,
    /**
     * path 对应文件的 md5 值
     */
    val md5: String?,
    /**
     * path 对应文件的 url
     * 比如 http://www.91chengguo.com/app/test/bridge.js
     */
    val url: String? = null
)