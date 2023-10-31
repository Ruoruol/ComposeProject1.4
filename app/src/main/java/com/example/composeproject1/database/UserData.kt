package com.example.composeproject1.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")
    val userId: Long = 0,
    @ColumnInfo(name = "userName")
    val userName: String,
    @ColumnInfo(name = "userAccount")
    val userAccount: String,
    @ColumnInfo(name = "userPassword")
    val userPassword: String
)