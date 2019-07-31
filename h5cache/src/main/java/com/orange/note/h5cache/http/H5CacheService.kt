package com.orange.note.h5cache.http

import com.orange.note.h5cache.entity.H5CacheResponse
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

/**
 * H5静态资源文件缓存接口
 * @author maomao
 * @date 2019-07-25
 */
interface H5CacheService {

    /**
     * 请求静态资源文件接口
     */
    @FormUrlEncoded
    @POST("/api/getJsonResult.do")
    fun checkUpdate(
        @Field("version") code: String?
    ): Observable<H5CacheResponse>

    /**
     * 下载文件
     */
    @GET
    fun download(@Url url: String): Observable<ResponseBody>

}