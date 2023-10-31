package com.example.composeproject1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userAccount = :account limit 1")
    fun getUserByAccount(account: String): List<UserData>

    @Query("SELECT * FROM users WHERE userAccount = :account and userPassword=:password limit 1")
    fun getUserByAccountPassword(account: String, password: String): List<UserData>

    @Query("SELECT * FROM users WHERE userId = :userId limit 1")
    fun getUserById(userId: Long): List<UserData>

    @Insert
    fun insertUser(user: UserData)
}