package com.example.arm.di

import android.app.Application
import com.example.arm.integration.IRepositoryManager
import com.example.arm.integration.RepositoryManager
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import com.example.arm.util.AppManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 14:20
 *  description :
 */
val APP_MODEL = DI.Module("APP_MODULE") {
    bind<Gson>() with singleton {
        instance<GsonBuilder>().create()
    }

    bind<GsonBuilder>() with singleton {
        val gsonBuilder = GsonBuilder()
        val gsonConfig = instance<((Application, GsonBuilder?) -> Unit)>()
        gsonConfig(instance(), gsonBuilder)
        gsonBuilder
    }

    bind<AppManager>() with singleton {
        AppManager.instance.init(instance())
    }

    bind<Cache<String, Any>>() with singleton {
        instance<Cache.Factory>().build(CacheType.EXTRAS)
    }

    bind<IRepositoryManager>() with singleton {
        RepositoryManager(instance())
    }


}
