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
    fun applyOption(context: Context, globalConfigModule: GlobalConfigModule)

    fun injectAppLifecycle(context: Context, appLifecycleList: MutableList<AppLifecycle>)

//    如果需要监听所有Activity(包括第三方),可以通过下面函数让Module配置监听
/*    fun injectActivityLifecycle(
        context: Context,
        activityLifecycleList: MutableList<Application.ActivityLifecycleCallbacks>
    )

    fun injectFragmentLifecycle(
        context: Context,
        fragmentLifecycleList: MutableList<FragmentManager.FragmentLifecycleCallbacks>
    )*/
}