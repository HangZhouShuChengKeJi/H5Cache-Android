package com.orange.note.h5cache.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author maomao
 * @date 2018/11/14
 */
public class GsonUtil {

    private static final Gson mGson = new Gson();

    private GsonUtil() {
        //no instance
    }

    public static String toJson(Object o) {
        return mGson.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        return mGson.fromJson(json, tClass);
    }

    public static <T> T fromJson(String json, Type type) {
        return mGson.fromJson(json,  type);
    }
}
