package com.example.armmvvm.http.net

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 17:51
 *  description :
 */
interface HttpService {
    @GET("/historyWeather/province")
    fun getSupportProvince(): Observable<ResponseListBean<ProvinceBean>>

    @GET("/historyWeather/province")
    fun getSupportProvince2(): Call<ResponseListBean<ProvinceBean>>

    @FormUrlEncoded
    @POST("/historyWeather/citys")
    fun getCityByProvince(@FieldMap bean: Map<String, String>): Call<ResponseListBean<CityBean>>

    @FormUrlEncoded
    @POST("/historyWeather/weather")
    fun getCityWeather(@FieldMap bean: Map<String, String>): Call<ResponseBean<WeatherBean>>

    @FormUrlEncoded
    @POST("/historyWeather/weather")
    suspend fun getCityWeather2(@FieldMap bean: Map<String, String>): ResponseBean<WeatherBean>
}