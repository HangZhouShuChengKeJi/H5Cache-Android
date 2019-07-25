package com.orange.note.cachewebview.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.orange.note.h5cache.H5CacheManager
import com.orange.note.net.NetServiceFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val API_SERVICE = "com.orange.note.h5cache"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetServiceFactory.getInstance().setGlobalBaseUrl("http://www.91chengguo.com")
        button.setOnClickListener {
            H5CacheManager.checkUpdate(API_SERVICE)
        }
    }
}
