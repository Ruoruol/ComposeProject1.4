package com.example.composeproject1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MedicationData::class, MyHistoryData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun myHistoryDao(): MyHistoryDao

    companion object {
        private const val DB_NAME = "health_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}