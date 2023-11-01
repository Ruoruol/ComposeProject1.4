package com.example.composeproject1.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bloodPressure")
data class BloodPressureData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bloodPressureId")
    val bloodPressureId: Long = 0L,
    @ColumnInfo(name = "bloodPressureHigh")
    val bloodPressureHigh: Int,
    @ColumnInfo(name = "bloodPressureLow")
    val bloodPressureLow: Int,
    @ColumnInfo(name = "heartBeat")
    val heartBeat: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "day")
    val day: Int,
    //早上 中午 晚上
    @ColumnInfo(name = "bloodPressureDayDesc")
    val bloodPressureDayDesc: Int,
    //日期
    @ColumnInfo(name = "bloodPressureTime")
    val bloodPressureTime: Long,
    //用户id
    @ColumnInfo(name = "bloodPressureUserId")
    val bloodPressureUserId: Long
)