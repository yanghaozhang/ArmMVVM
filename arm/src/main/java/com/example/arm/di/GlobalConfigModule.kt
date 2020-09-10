package com.example.arm.di

import android.app.Activity
import android.app.Application
import com.example.arm.http.BaseImageLoaderStrategy
import com.example.arm.http.ErrorListener
import com.example.arm.http.GlobalHttpHandler
import com.example.arm.http.log.DefaultFormatPrinter
import com.example.arm.http.log.FormatPrinter
import com.example.arm.http.log.RequestInterceptor
import com.example.arm.integration.IRepositoryManager
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import com.example.arm.integration.cache.IntelligentCache
import com.example.arm.integration.cache.LruCache
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import retrofit2.Retrofit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 15:56
 *  description :
 */
class GlobalConfigModule {

    // 可为空
    var mHttpUrl1: HttpUrl? = null
    var mHttpUrl2: HttpUrl? = null
    var mFactory: Cache.Factory? = null
    var mRequestPrinter: FormatPrinter? = null
    var mLevel: RequestInterceptor.Level? = null
    var mExecutorService: ExecutorService? = null
    var mErrorListener: ErrorListener? = null
    var mObtainServiceDelegate: IRepositoryManager.ObtainServiceDelegate? = null

    // 不暴露
    private val mInterceptors: List<Interceptor> = mutableListOf()
    private val mNetWorkInterceptor: List<Interceptor> = mutableListOf()
    private var mOkHttpConfiguration: ((Application, OkHttpClient.Builder?) -> Unit)? = null
    private var mRetrofitConfiguration: ((Application, Retrofit.Builder?) -> Unit)? = null
    private var mGsonConfiguration: ((Application, GsonBuilder?) -> Unit)? = null
    private var mActivityDelegate: ((Activity) -> Unit)? = null

    // 不能为空
    lateinit var mImageLoaderStrategy: BaseImageLoaderStrategy<*>
    lateinit var mGlobalHttpHandler: GlobalHttpHandler


    fun configOkHttp(okHttpConfig: (Application, OkHttpClient.Builder?) -> Unit) {
        if (mOkHttpConfiguration == null) {
            mOkHttpConfiguration = okHttpConfig
        } else {
            val temp = mOkHttpConfiguration
            mOkHttpConfiguration = { application: Application, builder: OkHttpClient.Builder? ->
                temp?.invoke(application, builder)
                okHttpConfig(application, builder)
            }
        }
    }

    fun configRetrofit(okHttpConfig: (Application, Retrofit.Builder?) -> Unit) {
        if (mRetrofitConfiguration == null) {
            mRetrofitConfiguration = okHttpConfig
        } else {
            val temp = mRetrofitConfiguration
            mRetrofitConfiguration = { application: Application, builder: Retrofit.Builder? ->
                temp?.invoke(application, builder)
                okHttpConfig(application, builder)
            }
        }
    }

    fun configGsonBuilder(okHttpConfig: (Application, GsonBuilder?) -> Unit) {
        if (mGsonConfiguration == null) {
            mGsonConfiguration = okHttpConfig
        } else {
            val temp = mGsonConfiguration
            mGsonConfiguration = { application: Application, builder: GsonBuilder? ->
                temp?.invoke(application, builder)
                okHttpConfig(application, builder)
            }
        }
    }

    fun configActivityDelegate(okHttpConfig: (Activity) -> Unit) {
        if (mActivityDelegate == null) {
            mActivityDelegate = okHttpConfig
        } else {
            val temp = mActivityDelegate
            mActivityDelegate = { activity: Activity ->
                temp?.invoke(activity)
                okHttpConfig(activity)
            }
        }
    }

    val globalConfigModule = DI.Module(this.javaClass.simpleName) {
        bind<HttpUrl>() with singleton {
            (mHttpUrl1 ?: mHttpUrl2)!!
        }

        bind<Cache.Factory>() with singleton {
            mFactory ?: object : Cache.Factory {
                override fun build(type: CacheType?): Cache<String, Any> {
                    return when (type!!.cacheTypeId) {
                        CacheType.EXTRAS_TYPE_ID, CacheType.ACTIVITY_CACHE_TYPE_ID, CacheType.FRAGMENT_CACHE_TYPE_ID ->
                            IntelligentCache(type!!.calculateCacheSize(instance()))
                        CacheType.RETROFIT_SERVICE_CACHE_TYPE_ID, CacheType.CACHE_SERVICE_CACHE_TYPE_ID ->
                            LruCache(type!!.calculateCacheSize(instance()))
                        else -> LruCache(type!!.calculateCacheSize(instance()))
                    }
                }
            }
        }

        bind<FormatPrinter>() with singleton {
            mRequestPrinter ?: DefaultFormatPrinter()
        }

        bind<RequestInterceptor.Level>() with singleton {
            mLevel ?: RequestInterceptor.Level.ALL
        }

        bind<ExecutorService>() with singleton {
            mExecutorService ?: Executors.newCachedThreadPool(object : ThreadFactory {
                private var num = 0
                override fun newThread(runnable: Runnable): Thread {
                    return Thread("arms-thread" + num++)
                }
            })
        }

        bind<BaseImageLoaderStrategy<*>>() with singleton {
            mImageLoaderStrategy
        }

        bind<GlobalHttpHandler>() with singleton {
            mGlobalHttpHandler
        }

        bind<List<Interceptor>>(tag = "Interceptors") with singleton {
            mInterceptors
        }

        bind<List<Interceptor>>(tag = "NetWorkInterceptor") with singleton {
            mNetWorkInterceptor
        }

        bind<(Application, OkHttpClient.Builder?) -> Unit>() with singleton {
            mOkHttpConfiguration ?: { application: Application, builder: OkHttpClient.Builder? -> }
        }

        bind<(Application, Retrofit.Builder?) -> Unit>() with singleton {
            mRetrofitConfiguration ?: { application: Application, builder: Retrofit.Builder? -> }
        }

        bind<(Application, GsonBuilder?) -> Unit>() with singleton {
            mGsonConfiguration ?: { application: Application, builder: GsonBuilder? -> }
        }

        bind<(Activity) -> Unit>() with singleton {
            mActivityDelegate ?: { activity: Activity -> }
        }
    }
}