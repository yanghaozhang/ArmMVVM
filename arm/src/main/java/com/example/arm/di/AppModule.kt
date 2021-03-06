package com.example.arm.di

import android.app.Application
import com.example.arm.http.log.RequestInterceptor
import com.example.arm.integration.IRepositoryManager
import com.example.arm.integration.RepositoryManager
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import com.example.arm.util.AppManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.kodein.di.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

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
        GsonBuilder().also {
            // 添加其他Module配置
            instance<((Application, GsonBuilder?) -> Unit)>().invoke(instance(), it)
        }
    }

    bind<AppManager>() with singleton {
        AppManager.instance.init(instance())
    }

    bind<Cache<String, Any>>() with factory { cacheType: CacheType ->
        instance<Cache.Factory>().build(cacheType)
    }

    bind<IRepositoryManager>() with singleton {
        RepositoryManager(instance(), instance<GlobalConfigModule>().mObtainServiceDelegate)
    }

    bind<OkHttpClient>() with singleton {
        val builder = OkHttpClient.Builder().apply {
            //使用项目统一的线程池
            // 注::如果要Retrofit使用suspend协程获取数据,那么不能指定线程池
//            dispatcher(Dispatcher(instance()))
            // 添加监听,实现GlobalHttpHandler对请求进行预处理
            addInterceptor(instance(tag = "HttpHandlerInterceptor"))
            for (interceptor in instance<MutableList<Interceptor>>(tag = "Interceptors")) {
                addInterceptor(interceptor)
            }

            for (interceptor in instance<MutableList<Interceptor>>(tag = "NetWorkInterceptor")) {
                addNetworkInterceptor(interceptor)
            }
            // 添加默认打印配置
            addNetworkInterceptor(RequestInterceptor())
            // 添加其他Module配置
            instance<(Application, OkHttpClient.Builder) -> Unit>().invoke(instance(), this)
        }
        builder.build()
    }

    bind<Retrofit>() with multiton { httpUrlTag: String ->
        val builder = Retrofit.Builder().apply {
            baseUrl(instance<String, HttpUrl>(arg = httpUrlTag))
            client(instance())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用 RxJava
            addConverterFactory(GsonConverterFactory.create(instance()))//使用 Gson;
            //addConverterFactory(ScalarsConverterFactory.create(gson));// 该库增加返回值为String的支持;

            // 添加其他Module配置
            instance<(Application, Retrofit.Builder) -> Unit>().invoke(instance(), this)
        }
        builder.build()
    }
}
