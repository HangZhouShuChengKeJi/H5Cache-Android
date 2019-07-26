H5Cache
===
介绍
---
对 WebView 的 H5 静态资源文件进行提前缓存，加快加载 H5 页面的速度，提升用户体验。

提前缓存主要体现在两方面：

1. 在 apk 中预置一份 H5 zip 包；
2. 后期缓存文件需要更新时，会提前下载；

须知
---
在 assets 中需要提前预置 H5 资源文件的压缩包，以 zip 格式为准，命名为 h5Cache.zip，详见demo

在 assets 中需要提前预置 H5 mapping 文件，以 json 格式为准，命名为 h5Cache.json，详见demo

h5Cache.zip 、h5Cache.json

使用本项目需要额外添加以下依赖：

* [RxJava](https://github.com/ReactiveX/RxJava)
* [Retrofit](https://github.com/square/retrofit)
* [gson](https://github.com/google/gson)
* [Net](https://gitlab.91chengguo.com/Orange-Android/Net)
   
   
Usage
===
1. 在 repositories 中添加橙果的仓库


        buildscript {
            repositories {
                google()
                jcenter()
                maven {
                    url "http://maven.91chengguo.com/repository/maven-releases/"
                    credentials {
                        username mvnUsername
                        password mvnPassword
                    }
                }
                maven {
                    url "http://maven.91chengguo.com/repository/maven-snapshots/"
                    credentials {
                        username mvnUsername
                        password mvnPassword
                    }
                }
            }
        }
        
2. 在 dependencies 添加 H5Cache 依赖


        implementation 'com.orange.note:h5cache:1.0.0'
        
3. 在 Application 的 onCreate 方法中初始化


        class MyApplication : Application() {
        
            override fun onCreate() {
                super.onCreate()
                H5CacheManager.init(this)
            }
        
        }

4. 在 WebView 中对请求做拦截


        // webView init code here
        ...
        
        H5CacheInterceptor.addHostList("www.91chengguo.com")
        webView.webViewClient = object : WebViewClient() {
        
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return H5CacheInterceptor.shouldInterceptRequest(request?.url.toString())
            }
        
            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                return H5CacheInterceptor.shouldInterceptRequest(url)
            }
        }
      
      
Advanced Usage    
===     
1. 在后期 H5 资源文件需要更新时，调用


        H5CacheManager.checkUpdate(serviceName)    
        
2. 获取 H5 资源缓存整体的版本号


        H5CacheManager.getVersion()     
        
        
Changelog
===
* v1.0.0 init commit

License
===
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) [杭州数橙科技有限公司](https://github.com/HangZhouShuChengKeJi)

