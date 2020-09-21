package com.example.armmvvm.http.net

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 17:51
 *  description :
 */
interface HttpService {
    @GET("/historyWeather/province")
    fun getSupportProvince(): Observable<ProvinceBean>

    @GET("/historyWeather/province")
    fun getSupportProvince2(): Call<ProvinceBean>

    @POST("/historyWeather/citys")
    fun getWeatherByProvince(bean: WeatherRequestBean): Observable<WeatherBean>

}