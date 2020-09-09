package com.example.arm.base

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.arm.integration.ConfigModule
import com.example.arm.integration.ManifestParser
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class ApplicationDelegate(context: Context) :AppLifecycle{

    private lateinit var application: Application
    private val configModuleList: MutableList<ConfigModule>

    private val appLifecycleList = mutableListOf<AppLifecycle>()
    private val activityLifecycleList = mutableListOf<Application.ActivityLifecycleCallbacks>()
    private val fragmentLifecycleList = mutableListOf<FragmentManager.FragmentLifecycleCallbacks>()

    init {
        val manifestParser = ManifestParser(context)
        configModuleList = manifestParser.parse()
        configModuleList.forEach {
            it.injectAppLifecycle(context, appLifecycleList)
        }
    }


    override fun attachBaseContext(context: Context) {
        for (appLifecycle in appLifecycleList) {
            appLifecycle.attachBaseContext(context)
        }
    }

    override fun onCreate(application: Application) {
        this.application =  application

        for (appLifecycle in appLifecycleList) {
            appLifecycle.onCreate(application)
        }

        for (configModule in configModuleList) {
            configModule.injectActivityLifecycle(application, activityLifecycleList)
        }

        for (lifecycleCallbacks in activityLifecycleList) {
            application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
        }
    }
}