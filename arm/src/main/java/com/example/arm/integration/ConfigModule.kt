package com.example.arm.integration

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.arm.base.AppLifecycle

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:12
 *  description :
 */
interface ConfigModule {
//    fun applyOption(Context context, GlobalConfigModule.Builder configModule)

    fun injectAppLifecycle(context: Context, appLifecycleList: MutableList<AppLifecycle>)

    fun injectActivityLifecycle(
        context: Context,
        activityLifecycleList: MutableList<Application.ActivityLifecycleCallbacks>
    )

    fun injectFragmentLifecycle(
        context: Context,
        fragmentLifecycleList: MutableList<FragmentManager.FragmentLifecycleCallbacks>
    )

}