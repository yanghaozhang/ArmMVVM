package com.example.arm.integration

import android.content.Context
import com.example.arm.integration.lifecycle.AppLifecycle
import com.example.arm.di.GlobalConfigModule

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:12
 *  description :
 */
interface ConfigModule {
    fun applyOption(context: Context, configModule: GlobalConfigModule)

    fun injectAppLifecycle(context: Context, appLifecycleList: MutableList<AppLifecycle>)
}