package com.orange.note.cachewebview.demo

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.orange.note.h5cache.H5CacheInterceptor
import com.orange.note.h5cache.H5CacheManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val API_SERVICE = "com.orange.note.h5cache"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            H5CacheManager.checkUpdate(API_SERVICE)
        }
        H5CacheInterceptor.addHostList("www.91chengguo.com")
        val settings = webView.settings
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
        }
        webView.webViewClient = object : WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return H5CacheInterceptor.shouldInterceptRequest(request?.url.toString())
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                return H5CacheInterceptor.shouldInterceptRequest(url)
            }
        }
        webView.loadUrl("http://www.91chengguo.com/appPackage/lxlib.js")
    }

}
