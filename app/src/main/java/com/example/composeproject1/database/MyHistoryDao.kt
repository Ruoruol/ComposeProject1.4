package com.example.composeproject1.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyHistoryDao {
    @Query("SELECT * FROM history_data ORDER BY date DESC")
    fun fetchDataList(): List<MyHistoryData>

    @Query("SELECT * FROM history_data WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun fetchDataListBetweenDate(start: Long, end: Long): List<MyHistoryData>

    @Query("SELECT * FROM history_data WHERE `key`=:key")
    fun fetchDataByKey(key: String): List<MyHistoryData>

    @Delete
    fun deleteData(myHistoryData: MyHistoryData)

    @Insert
    fun insertData(myHistoryData: MyHistoryData)

    @Query("SELECT * FROM history_data WHERE id=:id")
    fun fetchDataById(id: Int): List<MyHistoryData>
}