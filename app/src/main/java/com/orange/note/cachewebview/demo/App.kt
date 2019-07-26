package com.orange.note.cachewebview.demo

import android.app.Application
import com.orange.note.h5cache.H5CacheManager
import com.orange.note.net.NetServiceFactory

/**
 * @author maomao
 * @date 2019-07-25
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        H5CacheManager.init(this)
        NetServiceFactory.getInstance().setGlobalBaseUrl("http://www.91chengguo.com")
    }

}