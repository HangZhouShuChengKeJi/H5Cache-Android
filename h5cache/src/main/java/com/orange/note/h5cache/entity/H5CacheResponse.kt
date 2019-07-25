package com.orange.note.h5cache.entity

import android.support.annotation.Keep

/**
 * h5 cache 接口的网络响应
 * @author maomao
 * @date 2019-07-25
 */
@Keep
class H5CacheResponse {
    /**
     * 线上最新的版本号
     */
    var latestVersion: String? = null
    /**
     * 是否需要更新
     */
    var needUpdate: Boolean? = null
    /**
     * 线上所有的静态资源文件 H5 cache 集合
     */
    var itemList: List<H5CacheItem>? = null

}
