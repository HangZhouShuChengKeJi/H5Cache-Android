package com.orange.note.h5cache.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * @author maomao
 * @date 2019-07-12
 */
internal object AppUtil {

    fun getVersionCode(context: Context): Int {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

}


