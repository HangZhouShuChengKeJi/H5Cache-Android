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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            H5CacheManager.checkUpdate()
        }
        H5CacheInterceptor.addHostSet("www.91chengguo.com")
        webView.webViewClient = object : WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return H5CacheInterceptor.shouldInterceptRequest(request?.url.toString()) ?: super.shouldInterceptRequest(view, request)
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                return H5CacheInterceptor.shouldInterceptRequest(url) ?: super.shouldInterceptRequest(view, url)
            }
        }
        webView.loadUrl("http://www.91chengguo.com/app-package/index.js")
    }

}
