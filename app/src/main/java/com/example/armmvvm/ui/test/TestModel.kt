package com.example.armmvvm.ui.test

import com.example.arm.mvvm.BaseModel
import com.example.armmvvm.http.net.*
import io.reactivex.Observable
import okhttp3.HttpUrl
import org.kodein.di.instance
import retrofit2.Call
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/18 11:04
 *  description :
 */
class TestModel : BaseModel() {

    val mHttpUrl: HttpUrl by instance()

    fun logInstance() {
        Timber.tag("TestModel").d("register() called   %s ", "$mHttpUrl")
    }

    fun request(): Observable<ResponseListBean<ProvinceBean>> {
        return mRepositoryManager.obtainRetrofitService(HttpService::class.java)
            .getSupportProvince()
    }

    fun request2(): Call<ResponseListBean<ProvinceBean>> {
        return mRepositoryManager.obtainRetrofitService(HttpService::class.java)
            .getSupportProvince2()
    }

    fun requestCity(bean: Map<String,String>): Call<ResponseListBean<CityBean>> {
        return mRepositoryManager.obtainRetrofitService(HttpService::class.java)
            .getCityByProvince(bean)
    }

    fun requestWeather(bean: Map<String,String>): Call<ResponseBean<WeatherBean>> {
        return mRepositoryManager.obtainRetrofitService(HttpService::class.java)
            .getCityWeather(bean)
    }
}