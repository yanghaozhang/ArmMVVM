package com.example.arm.di

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.example.arm.http.ErrorListener
import com.example.arm.http.GlobalHttpHandler
import com.example.arm.http.HttpHandlerInterceptor
import com.example.arm.http.imageloader.BaseImageLoaderStrategy
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
import org.kodein.di.*
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
    var mFactory: Cache.Factory? = null
    var mRequestPrinter: FormatPrinter? = null
    var mLevel: RequestInterceptor.Level? = null
    var mExecutorService: ExecutorService? = null
    var mErrorListener: ErrorListener? = null
    var mObtainServiceDelegate: IRepositoryManager.ObtainServiceDelegate? = null
    var mGlobalHttpHandler: GlobalHttpHandler? = null
    var mAppDIConfig: MutableList<DI.MainBuilder.() -> Unit> = mutableListOf()

    // 不暴露
    private val mHttpUrlMap: MutableMap<String, HttpUrl> = mutableMapOf()
    private val mInterceptors: MutableList<Interceptor> = mutableListOf()
    private val mNetWorkInterceptor: MutableList<Interceptor> = mutableListOf()
    private var mOkHttpConfiguration: MutableList<(Application, OkHttpClient.Builder) -> Unit> = mutableListOf()
    private var mRetrofitConfiguration: MutableList<(Application, Retrofit.Builder) -> Unit> = mutableListOf()
    private var mGsonConfiguration: MutableList<(Application, GsonBuilder) -> Unit> = mutableListOf()
    private var mActivityDelegate: MutableList<(Activity) -> Unit> = mutableListOf()
    private var mFragmentDelegate: MutableList<(Fragment) -> Unit> = mutableListOf()

    // 不能为空
    lateinit var mImageLoaderStrategy: BaseImageLoaderStrategy<*>
    lateinit var mDefaultHttpUrlTag: String

    fun addInterceptors(interceptor:Interceptor) {
        mInterceptors.add(interceptor)
    }

    fun addNetWorkInterceptor(interceptor: Interceptor) {
        mNetWorkInterceptor.add(interceptor)
    }

    fun configOkHttp(okHttpConfig: (Application, OkHttpClient.Builder?) -> Unit) {
        mOkHttpConfiguration.add(okHttpConfig)
    }

    fun configRetrofit(retrofitConfig: (Application, Retrofit.Builder?) -> Unit) {
        mRetrofitConfiguration.add(retrofitConfig)
    }

    fun configGsonBuilder(gsonConfig: (Application, GsonBuilder?) -> Unit) {
        mGsonConfiguration.add(gsonConfig)
    }

    fun configActivityDelegate(activityDelegateConfig: (Activity) -> Unit) {
        mActivityDelegate.add(activityDelegateConfig)
    }

    fun configFragmentDelegate(fragmentDelegateConfig: (Fragment) -> Unit) {
        mFragmentDelegate.add(fragmentDelegateConfig)
    }

    fun configAppDI(appDIConfig: DI.MainBuilder.() -> Unit) {
        mAppDIConfig.add(appDIConfig)
    }

    fun addHttpUrlWithTag(httpUrl: HttpUrl, tag: String) {
        mHttpUrlMap[tag] = httpUrl
    }

    val globalConfigModule = DI.Module(this.javaClass.simpleName) {

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

        bind<HttpUrl>() with factory { tag: String ->
            mHttpUrlMap[tag] ?: HttpUrl.parse("http://www.google.com")!!
        }

        bind<String>(tag = "DefaultHttpUrlTag") with singleton {
            mDefaultHttpUrlTag
        }

        bind() from singleton { mGlobalHttpHandler ?: GlobalHttpHandler.EMPTY }

        bind<Interceptor>(tag = "HttpHandlerInterceptor") with singleton { HttpHandlerInterceptor(instance()) }

        bind(tag = "Interceptors") from singleton { mInterceptors }

        bind(tag = "NetWorkInterceptor") from singleton { mNetWorkInterceptor }

        bind<(Application, OkHttpClient.Builder) -> Unit>() with singleton {
            { application: Application, builder: OkHttpClient.Builder ->
                mOkHttpConfiguration.forEach {
                    it(application, builder)
                }
            }
        }

        bind<(Application, Retrofit.Builder) -> Unit>() with singleton {
            { application: Application, builder: Retrofit.Builder ->
                mRetrofitConfiguration.forEach {
                    it(application, builder)
                }
            }
        }

        bind<(Application, GsonBuilder) -> Unit>() with singleton {
            { application: Application, builder: GsonBuilder ->
                mGsonConfiguration.forEach {
                    it(application, builder)
                }
            }
        }

        bind<(Activity) -> Unit>() with singleton {
            { activity: Activity ->
                mActivityDelegate.forEach {
                    it(activity)
                }
            }
        }

        bind<(Fragment) -> Unit>() with singleton {
            { fragment: Fragment ->
                mFragmentDelegate.forEach {
                    it(fragment)
                }
            }
        }
    }
}