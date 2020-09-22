package com.example.armmvvm.http.net

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 18:11
 *  description :
 */
data class ResponseListBean<T>(
    val error_code: Int,
    val reason: String,
    val result: List<T>,
)
data class ResponseBean<T>(
    val error_code: Int,
    val reason: String,
    val result: T,
)

data class ProvinceBean(
    val id: String,
    val province: String,
)

data class CityBean(
    val city_name: String,
    val id: String,
    val province_id: String,
)

data class WeatherBean(
    val city_id: String,
    val city_name: String,
    val day_temp: String,
    val day_weather: String,
    val day_weather_id: String,
    val day_wind: String,
    val day_wind_comp: String,
    val night_temp: String,
    val night_weather: String,
    val night_weather_id: String,
    val night_wind: String,
    val night_wind_comp: String,
    val weather_date: String
)