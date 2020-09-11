package com.example.arm.base

import android.app.Application
import android.content.Context
import com.example.arm.di.APP_MODEL
import com.example.arm.di.GlobalConfigModule
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bind
import org.kodein.di.singleton

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class BaseApplication : Application(), DIAware {

    private lateinit var applicationDelegate: ApplicationDelegate

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        applicationDelegate = ApplicationDelegate(base)
        applicationDelegate.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        applicationDelegate.onCreate(this)
    }

    override val di: DI = DI.lazy {
        import(androidXModule(this@BaseApplication))
        import(applicationDelegate.globalConfigModule.globalConfigModule)
        import(APP_MODEL)
        bind<Context>() with singleton { this@BaseApplication }
        bind<ApplicationDelegate>() with singleton { applicationDelegate }
        bind<GlobalConfigModule>() with singleton { applicationDelegate.globalConfigModule }
    }
}