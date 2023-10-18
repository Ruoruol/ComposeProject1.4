package com.example.composeproject1.bean

data class CityAreaBean(
    val sitename: String? = null,
    val county: String? = null,
    val aqi: String? = null,
    val pollutant: String? = null,
    val status: String? = null,
    val so2: String? = null,
    val co: String? = null,
    val o3: String? = null,
    val o3_8hr: String? = null,
    val pm10: String? = null,
    val pm2_5: String? = null,
    val no2: String? = null,
    val nox: String? = null,
    val no: String? = null,
    val wind_speed: String? = null,
    val wind_direc: String? = null,
    val publishtime: String? = null,
    val co_8hr: String? = null,
    val pm2_5_avg: String? = null,
    val pm10_avg: String? = null,
    val so2_avg: String? = null,
    val longitude: String? = null,
    val latitude: String? = null,
    val siteid: String? = null,
)

data class CityNetBean(
    val records: List<CityAreaBean>
)
