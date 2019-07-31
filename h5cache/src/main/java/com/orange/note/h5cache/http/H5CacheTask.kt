package com.orange.note.h5cache.http

import com.orange.note.h5cache.entity.H5CacheResponse
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

/**
 * H5静态资源文件缓存请求任务
 * @author maomao
 * @date 2019-07-25
 */
object H5CacheTask {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://www.91chengguo.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build()

    /**
     * 请求静态资源文件任务
     */
    fun checkUpdate(version: String?): Observable<H5CacheResponse> {
        return retrofit.create(
            H5CacheService::class.java
        ).checkUpdate(version)
    }

    fun download(url: String): Observable<ResponseBody> {
        return retrofit.create(
            H5CacheService::class.java
        ).download(url)
    }

}