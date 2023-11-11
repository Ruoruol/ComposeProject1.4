package com.example.composeproject1.database

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "medication_data")
data class MedicationData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "keyId")
    val keyId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "isValid")
    val isValid: Int,
    @ColumnInfo(name = "count")
    val count: Int
) {
    fun isValidOrInTime() = isValid == 1 && (time > System.currentTimeMillis()||count>0)
}