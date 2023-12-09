package com.example.composeproject1.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("history_data")
class MyHistoryData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("userId")
    val userId:Long,
    @ColumnInfo("key")
    val key: String,
    @ColumnInfo("date")
    val date: Long,
    @ColumnInfo("item")
    val item: String
)