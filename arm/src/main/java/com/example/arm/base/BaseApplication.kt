package com.example.arm.base

import android.app.Application
import android.content.Context

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class BaseApplication : Application() {

    private lateinit var applicationModel: ApplicationDelegate

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        applicationModel = ApplicationDelegate(base)
        applicationModel.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        applicationModel.onCreate(this)
    }
}