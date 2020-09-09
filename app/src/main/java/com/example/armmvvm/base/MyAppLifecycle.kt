package com.example.armmvvm.base

import android.app.Application
import android.content.Context
import com.example.arm.base.AppLifecycle
import timber.log.Timber

/**
 * author : yanghaozhang
 * date : 2020/9/9 11:44
 * description :
 */
class MyAppLifecycle : AppLifecycle {
    override fun attachBaseContext(base: Context) {
        Timber.plant(MyTimberTree())
        Timber.tag("MyAppLifecycle").d("attachBaseContext() called with: base = $base%s ", "")
    }

    override fun onCreate(application: Application) {
        Timber.tag("MyAppLifecycle").d("onCreate() called%s ", "")
    }

}