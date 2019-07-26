package com.orange.note.h5cache.util


import android.content.Context
import android.content.SharedPreferences

/**
 * @author maomao
 * @date 2019/4/18
 */
object SharedPreferenceUtil {
    /**
     * 保存在手机里面的文件名
     */
    private const val FILE_NAME = "sp_h5_cache"

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param `object`
     */
    fun put(context: Context, key: String, any: Any) {

        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sp.edit()

        when (any) {
            is String -> editor.putString(key, any)
            is Int -> editor.putInt(key, any)
            is Boolean -> editor.putBoolean(key, any)
            is Float -> editor.putFloat(key, any)
            is Long -> editor.putLong(key, any)
            else -> editor.putString(key, any.toString())
        }

        try {
            editor.apply()
        } catch (e: Exception) {
            editor.commit()
        }

    }

    fun getInt(context: Context, key: String, defaultValue: Int): Int {
        return get(context, key, defaultValue) as Int
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    private operator fun get(context: Context, key: String, defaultObject: Any): Any? {
        val sp = context.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )

        return when (defaultObject) {
            is String -> sp.getString(key, defaultObject)
            is Int -> sp.getInt(key, defaultObject)
            is Boolean -> sp.getBoolean(key, defaultObject)
            is Float -> sp.getFloat(key, defaultObject)
            is Long -> sp.getLong(key, defaultObject)
            else -> null
        }
    }

}
