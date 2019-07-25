package com.orange.note.h5cache.entity

import android.support.annotation.Keep

/**
 * 静态资源文件
 * @author maomao
 * @date 2019-07-25
 */
@Keep
class H5CacheItem {
    /**
     * 静态资源文件对应 url 的 path，客户端以此来判断是否符合拦截条件
     * 比如 http://www.91chengguo.com/app/test/bridge.js
     * 那么对应的 path 就是 /app/test/bridge.js
     */
    var path: String? = null
    /**
     * path 对应文件的 md5 值
     */
    var md5: String? = null
    /**
     * path 对应文件的 url
     * 比如 http://www.91chengguo.com/app/test/bridge.js
     */
    var url: String? = null

}