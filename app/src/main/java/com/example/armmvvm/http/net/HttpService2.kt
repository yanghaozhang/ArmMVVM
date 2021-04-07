package com.example.armmvvm.http.net

import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 17:51
 *  description :
 */
interface HttpService2 {

    @FormUrlEncoded
    @POST("/historyWeather/weather")
    suspend fun getCityWeather2(@FieldMap bean: Map<String, String>): ResponseBean<WeatherBean>
}