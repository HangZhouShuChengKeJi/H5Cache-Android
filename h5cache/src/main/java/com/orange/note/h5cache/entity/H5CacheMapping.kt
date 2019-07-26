package com.orange.note.h5cache.entity

import android.support.annotation.Keep

/**
 * h5 cache 本地数据
 * @author maomao
 * @date 2019-07-12
 */
@Keep
data class H5CacheMapping(
    /**
     * 本地版本号
     */
    val version: String?,
    /**
     * 本地的所有的静态资源文件 H5 cache 集合
     */
    var resourceList: List<H5CacheItem>?
)