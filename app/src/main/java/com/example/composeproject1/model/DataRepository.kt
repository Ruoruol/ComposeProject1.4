package com.example.composeproject1.model

object DataRepository {
    private val medicationList = listOf(
        "血压药",
        "血糖药",
        "xxx"
    )
    fun getMedicationList():List<String> = medicationList
}