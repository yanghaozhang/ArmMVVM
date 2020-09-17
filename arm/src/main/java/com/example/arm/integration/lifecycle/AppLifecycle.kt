package com.example.arm.integration.lifecycle

import android.app.Application
import android.content.Context

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:12
 *  description :
 */
interface AppLifecycle {
    fun attachBaseContext(base: Context)

    fun onCreate(application: Application)
}