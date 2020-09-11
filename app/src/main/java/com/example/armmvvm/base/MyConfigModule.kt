package com.example.armmvvm.base

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.arm.base.AppLifecycle
import com.example.arm.di.GlobalConfigModule
import com.example.arm.integration.ConfigModule
import okhttp3.HttpUrl
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:28
 *  description :
 */
class MyConfigModule : ConfigModule {
    override fun applyOption(context: Context, configModule: GlobalConfigModule) {
        configModule.run {
            configActivityDelegate {
                if (it is AppCompatActivity) {
                    MyActivityDelegateImp(it)
                }
            }
            mHttpUrl1 = HttpUrl.parse("http://www.baidu.com")
//            mErrorListener = MyErrorListener()
        }
    }

    override fun injectAppLifecycle(context: Context, appLifecycleList: MutableList<AppLifecycle>) {
        appLifecycleList.add(MyAppLifecycle())
        Timber.tag("MyConfigModule").d(
            "injectAppLifecycle() called with: context = $context, appLifecycleList = $appLifecycleList%s ",
            ""
        )
    }

    override fun injectActivityLifecycle(
        context: Context,
        activityLifecycleList: MutableList<Application.ActivityLifecycleCallbacks>
    ) {
        Timber.tag("MyConfigModule").d(
            "injectActivityLifecycle() called with: context = $context, activityLifecycleList = $activityLifecycleList%s ",
            ""
        )
    }

    override fun injectFragmentLifecycle(
        context: Context,
        fragmentLifecycleList: MutableList<FragmentManager.FragmentLifecycleCallbacks>
    ) {
        Timber.tag("MyConfigModule").d(
            "injectFragmentLifecycle() called with: context = $context, fragmentLifecycleList = $fragmentLifecycleList%s ",
            ""
        )
    }
}