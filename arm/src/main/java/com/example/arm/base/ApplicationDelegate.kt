package com.example.arm.base

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.integration.ConfigModule
import com.example.arm.integration.ManifestParser

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class ApplicationDelegate(context: Context) :AppLifecycle{

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


    override fun attachBaseContext(context: Context) {
        for (appLifecycle in appLifecycleList) {
            appLifecycle.attachBaseContext(context)
        }
    }

    override fun onCreate(application: Application) {
        this.application = application

        for (appLifecycle in appLifecycleList) {
            appLifecycle.onCreate(application)
        }

        globalConfigModule = GlobalConfigModule().apply {
            // 添加默认的ActivityDelegate监听AppCompatActivity的生命周期
            configActivityDelegate {
                if (it is AppCompatActivity) {
                    ActivityDelegateImp(it)
                }
            }
        }
        // 每个模块都可以对GlobalConfigModule进行配置,比如添加AppCompatActivity监听,OkHttpClient监听
        for (configModule in configModuleList) {
            configModule.applyOption(application, globalConfigModule)
        }

    }
}