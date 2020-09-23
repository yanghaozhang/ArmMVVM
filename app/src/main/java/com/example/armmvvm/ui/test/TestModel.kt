package com.example.armmvvm.ui.test

import com.example.arm.mvvm.BaseModel
import com.example.armmvvm.base.constant.WEATHER_BASE_URL_TAG_TEST
import com.example.armmvvm.http.net.*
import io.reactivex.Observable
import retrofit2.Call

/**
 *  author : yanghaozhang
 *  date : 2020/9/18 11:04
 *  description :
 */
class TestModel : BaseModel() {

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
        return mRepositoryManager.obtainRetrofitService(HttpService::class.java, WEATHER_BASE_URL_TAG_TEST)
            .getCityWeather(bean)
    }
}