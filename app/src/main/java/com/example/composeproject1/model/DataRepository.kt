package com.example.composeproject1.model

object DataRepository {
    private val medicationList = listOf(
        "降血壓藥",
        "降血糖藥",
        "消炎藥",
        "感冒藥"
    )
    fun getMedicationList():List<String> = medicationList
}