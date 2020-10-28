package com.example.armmvvm.base

import android.R
import android.app.Application
import android.content.Context
import com.example.arm.integration.lifecycle.AppLifecycle
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import timber.log.Timber

/**
 * author : yanghaozhang
 * date : 2020/9/9 11:44
 * description :
 */
class MyAppLifecycle : AppLifecycle {

    init {
        Timber.plant(MyTimberTree())
    }

    override fun attachBaseContext(base: Context) {
        Timber.tag("MyAppLifecycle").d("attachBaseContext() called with: base = $base%s ", "")
    }

    override fun onCreate(application: Application) {
        Timber.tag("MyAppLifecycle").d("onCreate() called%s ", "")
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.darker_gray, R.color.white) //全局设置主题颜色
            ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20f)
        }

    }

}