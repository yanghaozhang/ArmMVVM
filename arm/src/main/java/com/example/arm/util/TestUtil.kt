package com.example.arm.util

import android.app.Application
import android.util.Log
import com.example.arm.ext.AppDIAware
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import org.kodein.di.description
import org.kodein.di.instance

/**
 *  author : yanghaozhang
 *  date : 2020/9/15 18:00
 *  description :
 */
class TestUtil :AppDIAware{

    val application: Application by instance()

    val mCacheFactory: Cache.Factory by instance()

    private var mRetrofitServiceCache: Cache<String, Any>? = mCacheFactory.build(CacheType.RETROFIT_SERVICE_CACHE)

    init {
        Log.i("Kodein111111111111111", di.container.tree.bindings.description())
        println(application)
        println(mCacheFactory)
        println(mRetrofitServiceCache)
    }
}