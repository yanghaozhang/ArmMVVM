package com.example.arm.integration

import android.app.Application
import android.content.Context
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.di
import org.kodein.di.instance
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.Proxy

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 15:09
 *  description :
 */
class RepositoryManager(
    val application: Application,
    var mObtainServiceDelegate: IRepositoryManager.ObtainServiceDelegate? = null
) : IRepositoryManager, DIAware {

    // 必须写在顶部先赋值,不然如果属性用到kodein依赖,会报错
    override val di: DI by di(application)
    override val context: Context by instance()
    val mRetrofit: Retrofit by instance()

    val mRetrofitServiceCache: Cache<String, Any> by instance(arg = CacheType.RETROFIT_SERVICE_CACHE)

    init {
        Timber.tag("RepositoryManager").d("null() called   %s ", "$context + $mRetrofit + $mRetrofitServiceCache")
    }

    override fun <T> obtainRetrofitService(serviceClass: Class<T>): T {
        requireNotNull(mRetrofitServiceCache) { "Cannot return null from a Cache.Factory#build(int) method" }
        val retrofitService = mRetrofitServiceCache[serviceClass.canonicalName!!]
            ?: mObtainServiceDelegate?.createRetrofitService(mRetrofit, serviceClass)
            ?: Proxy.newProxyInstance(
                serviceClass.classLoader, arrayOf(serviceClass),
                RetrofitServiceProxyHandler(mRetrofit, serviceClass)
            )
        mRetrofitServiceCache.put(serviceClass.canonicalName!!, retrofitService)
        return retrofitService as T
    }

    override fun clearAllCache() {
        mRetrofitServiceCache.clear()
    }
}