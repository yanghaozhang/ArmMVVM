package com.example.arm.base

import android.app.Application
import android.content.Context
import com.example.arm.di.APP_MODEL
import com.example.arm.di.GlobalConfigModule
import com.example.arm.util.TestUtil
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class BaseApplication : Application(), DIAware {

    override val di: DI = DI.lazy {
        import(androidXModule(this@BaseApplication))
        import(applicationDelegate.globalConfigModule.globalConfigModule)
        import(APP_MODEL)
        bind<Context>() with singleton { this@BaseApplication }
        bind<TestUtil>() with singleton { TestUtil() }
        bind<ApplicationDelegate>() with singleton { applicationDelegate }
        bind<GlobalConfigModule>() with singleton { applicationDelegate.globalConfigModule }

        for (diConfig in applicationDelegate.globalConfigModule.mAppDIConfig) {
            diConfig()
        }
    }

    override val diContext: DIContext<*>
        get() = diContext(this)

    private lateinit var applicationDelegate: ApplicationDelegate

    companion object {
        lateinit var INSTANCE: BaseApplication
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // kodein提示的错误信息带有完整的class name
        DI.defaultFullDescriptionOnError = true
        applicationDelegate = ApplicationDelegate(base)
        applicationDelegate.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        applicationDelegate.onCreate(this)

        Timber.tag("Kodein").d("=====================-BINDINGS-=====================")
        Timber.tag("Kodein").d("/n${di.container.tree.bindings.description()}")
        Timber.tag("Kodein").d("=======================-END-=======================")
    }

}