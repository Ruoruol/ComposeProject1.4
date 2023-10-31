package com.example.composeproject1.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM bloodPressure WHERE bloodPressureTime>=:startTime AND bloodPressureTime<=:endTime")
    fun fetchBloodPressureListBetween(startTime: Long, endTime: Long): List<BloodPressureData>

    @Query("SELECT * FROM bloodPressure WHERE bloodPressureUserId = :userId")
    fun fetchBloodPressureByUserId(userId: Int): List<BloodPressureData>

    @Query("SELECT * FROM bloodPressure WHERE bloodPressureId = :id limit 1")
    fun fetchBloodPressureById(id: Long): List<BloodPressureData>

    @Update
    fun updateBloodPressure(bloodPressureData: BloodPressureData)
}