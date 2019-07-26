package com.orange.note.h5cache.util

import com.google.gson.Gson

import java.lang.reflect.Type

/**
 * @author maomao
 * @date 2018/11/14
 */
internal object GsonUtil {

    private val mGson = Gson()

    fun toJson(o: Any): String {
        return mGson.toJson(o)
    }

    fun <T> fromJson(json: String, tClass: Class<T>): T {
        return mGson.fromJson(json, tClass)
    }

}
