package com.example.armmvvm.http.net

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 18:11
 *  description :
 */
data class ProvinceBean(
    val error_code: Int,
    val reason: String,
    val result: List<Result>,
)

data class Result(
    val id: String,
    val province: String,
)

data class WeatherBean(
    val error_code: Int,
    val reason: String,
    val result: List<WeatherDetailBean>,
)

data class WeatherDetailBean(
    val city_name: String,
    val id: String,
    val province_id: String,
)