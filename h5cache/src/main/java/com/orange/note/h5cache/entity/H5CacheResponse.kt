package com.orange.note.h5cache.entity

import android.support.annotation.Keep

/**
 * h5 cache 接口的网络响应
 * @author maomao
 * @date 2019-07-25
 */
@Keep
data class H5CacheResponse(
    /**
     * 线上最新的版本号
     */
    val latestVersion: String?,
    /**
     * 是否需要更新
     */
    val needUpdate: Boolean?,
    /**
     * 线上所有的静态资源文件 H5 cache 集合
     */
    val resourceList: List<H5CacheItem>?
)