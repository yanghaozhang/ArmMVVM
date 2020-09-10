package com.example.arm.integration

import android.content.Context
import com.example.arm.ext.di
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import org.kodein.di.*
import retrofit2.Retrofit
import java.lang.reflect.Proxy

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 15:09
 *  description :
 */
class RepositoryManager(
    var mObtainServiceDelegate: IRepositoryManager.ObtainServiceDelegate? = null
) : IRepositoryManager, DIAware {

    override val context: Context by instance()
    val mRetrofit: Retrofit by instance()
    val mCacheFactory: Cache.Factory by instance()


    private var mRetrofitServiceCache: Cache<String, Any>? = mCacheFactory?.build(CacheType.RETROFIT_SERVICE_CACHE)

    override fun <T> obtainRetrofitService(serviceClass: Class<T>): T {
        requireNotNull(mRetrofitServiceCache) { "Cannot return null from a Cache.Factory#build(int) method" }
        val retrofitService =
            mRetrofitServiceCache?.get(serviceClass.canonicalName)
                ?: mObtainServiceDelegate?.createRetrofitService(mRetrofit, serviceClass)
                ?: Proxy.newProxyInstance(
                    serviceClass.classLoader, arrayOf(serviceClass),
                    RetrofitServiceProxyHandler(mRetrofit, serviceClass)
                )
        mRetrofitServiceCache?.put(serviceClass.canonicalName, retrofitService!!)
        return retrofitService as T
    }

    override fun clearAllCache() {
        mRetrofitServiceCache?.clear()
    }

    override val di: DI = context.di
}