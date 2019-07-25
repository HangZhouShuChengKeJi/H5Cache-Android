package com.orange.note.h5cache.http

import com.orange.note.h5cache.entity.H5CacheResponse
import com.orange.note.net.NetService
import com.orange.note.net.response.NetResponse
import com.orange.note.net.rx.NetResponseTransformer
import okhttp3.ResponseBody
import rx.Observable

/**
 * H5静态资源文件缓存请求任务
 * @author maomao
 * @date 2019-07-25
 */
object H5CacheTask {

    /**
     * 请求静态资源文件任务
     */
    fun checkUpdate(version: String?, service: String?): Observable<H5CacheResponse> {
        return NetService.create(
            H5CacheService::class.java
        ).checkUpdate(version, service)
            .compose(NetResponseTransformer<NetResponse<H5CacheResponse>, H5CacheResponse>())
    }

    fun download(url: String): Observable<ResponseBody> {
        return NetService.create(
            H5CacheService::class.java
        ).download(url)
    }

}