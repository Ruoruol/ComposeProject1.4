package com.example.composeproject1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM bloodPressure WHERE bloodPressureUserId = :userId AND bloodPressureTime>=:startTime AND bloodPressureTime<=:endTime")
    fun fetchBloodPressureListBetween(
        userId: Long,
        startTime: Long,
        endTime: Long
    ): Flow<List<BloodPressureData>>

    @Query("SELECT * FROM bloodPressure WHERE bloodPressureUserId = :userId")
    fun fetchBloodPressureByUserId(userId: Long): List<BloodPressureData>

    @Query("SELECT * FROM bloodPressure WHERE bloodPressureId = :id limit 1")
    fun fetchBloodPressureById(id: Long): List<BloodPressureData>

    @Query("SELECT * FROM bloodPressure WHERE bloodPressureUserId = :userId AND year=:year AND month=:month AND day=:day AND bloodPressureDayDesc=:bloodPressureDayDesc")
    fun selectBloodPressureByTime(
        userId: Long,
        year: Int,
        month: Int,
        day: Int,
        bloodPressureDayDesc: Int
    ): List<BloodPressureData>

    @Update
    fun updateBloodPressure(bloodPressureData: BloodPressureData)
    @Insert
    fun insertBloodPressure(bloodPressureData: BloodPressureData)
}