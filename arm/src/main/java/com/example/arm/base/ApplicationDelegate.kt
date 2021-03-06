package com.example.arm.base

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.integration.ConfigModule
import com.example.arm.integration.ManifestParser
import com.example.arm.integration.lifecycle.ActivityLifecycleForRxJava
import com.example.arm.integration.lifecycle.AppLifecycle
import com.example.arm.integration.lifecycle.FragmentDelegateForRxJava

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class ApplicationDelegate(context: Context) : AppLifecycle {

    lateinit var globalConfigModule: GlobalConfigModule
    private lateinit var application: Application
    private val configModuleList: MutableList<ConfigModule>

    private val appLifecycleList = mutableListOf<AppLifecycle>()

    init {
        val manifestParser = ManifestParser(context)
        configModuleList = manifestParser.parse()
        configModuleList.forEach {
            it.injectAppLifecycle(context, appLifecycleList)
        }
    }


    override fun attachBaseContext(base: Context) {
        for (appLifecycle in appLifecycleList) {
            appLifecycle.attachBaseContext(base)
        }
    }

    override fun onCreate(application: Application) {
        this.application = application

        for (appLifecycle in appLifecycleList) {
            appLifecycle.onCreate(application)
        }

        globalConfigModule = GlobalConfigModule().apply {
            // 每个模块都可以对GlobalConfigModule进行配置,比如添加AppCompatActivity监听,OkHttpClient监听
            for (configModule in configModuleList) {
                configModule.applyOption(application, this)
            }
            // 默认订阅监听Activity/Fragment的生命周期管理RxJava,范围:手动订阅的Activity/Fragment
            configActivityDelegate {
                if (it is AppCompatActivity) {
                    // 同步RxJava的生命周期
                    ActivityLifecycleForRxJava(it)
                }
            }
            configFragmentDelegate {
                FragmentDelegateForRxJava(it)
            }
            // 默认监听所有的Activity/Fragment,打印log,添加AppManager管理,范围:所有,包括所有Module和第三方库
            configFragmentLifecycleCallbacks(FragmentLifecycleCallback())
            configActivityLifecycleCallbacks(ActivityLifecycleCallback(mFragmentLifecycleCallbacks))
        }

        for (activityLifecycleCallback in globalConfigModule.mActivityLifecycleCallbacks) {
            application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
        }
    }
}