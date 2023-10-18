package com.example.composeproject1.bean

data class CityAreaEntity(
    val cityName: String,
    val areaList: List<AreaEntity>
)

data class AreaEntity(
    val areaId: String,
    val city: String,
    val areaName: String
)