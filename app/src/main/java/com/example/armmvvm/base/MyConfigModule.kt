package com.example.armmvvm.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.http.imageloader.ImageLoader
import com.example.arm.http.log.RequestInterceptor
import com.example.arm.integration.ConfigModule
import com.example.arm.integration.lifecycle.AppLifecycle
import com.example.armmvvm.BuildConfig
import com.example.armmvvm.base.constant.WEATHER_BASE_URL
import com.example.armmvvm.base.constant.WEATHER_BASE_URL_TAG
import com.example.armmvvm.base.constant.WEATHER_BASE_URL_TAG_TEST
import com.example.armmvvm.http.imageloader.GlideImageLoaderStrategy
import com.example.armmvvm.http.imageloader.ImageConfigImpl
import okhttp3.HttpUrl
import org.kodein.di.bind
import org.kodein.di.singleton
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:28
 *  description : 设置相关配置,包括App的生命周期,Activity的生命周期监听等
 */
class MyConfigModule : ConfigModule {
    override fun applyOption(context: Context, configModule: GlobalConfigModule) {
        configModule.run {
            configActivityDelegate {
                if (it is AppCompatActivity) {
                    MyActivityDelegateImp(it)
                }
            }
            configAppDI {
                bind<ImageLoader<ImageConfigImpl>>() with singleton {
                    ImageLoader(GlideImageLoaderStrategy())
                }
            }
            mDefaultHttpUrlTag = WEATHER_BASE_URL_TAG
            addHttpUrlWithTag(HttpUrl.parse(WEATHER_BASE_URL)!!, WEATHER_BASE_URL_TAG)
            addHttpUrlWithTag(HttpUrl.parse(WEATHER_BASE_URL)!!, WEATHER_BASE_URL_TAG_TEST)
            mErrorListener = MyErrorListener()
            mLevel = if (BuildConfig.DEBUG) RequestInterceptor.Level.ALL else RequestInterceptor.Level.NONE
            mImageLoaderStrategy = GlideImageLoaderStrategy()
            mGlobalHttpHandler = MyGlobalHttpHandler()
        }
    }

    override fun injectAppLifecycle(context: Context, appLifecycleList: MutableList<AppLifecycle>) {
        appLifecycleList.add(MyAppLifecycle())
        Timber.tag("MyConfigModule").d(
            "injectAppLifecycle() called with: context = $context, appLifecycleList = $appLifecycleList%s ",
            ""
        )
    }
}