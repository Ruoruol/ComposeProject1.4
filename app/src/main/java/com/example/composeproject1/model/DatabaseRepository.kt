package com.example.composeproject1.model

import com.example.composeproject1.App
import com.example.composeproject1.database.AppDatabase
import com.example.composeproject1.database.MedicationData
import com.example.composeproject1.database.MyHistoryData
import com.example.composeproject1.database.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

object DatabaseRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val userDao = AppDatabase.getDatabase(App.appContext).userDao()
    suspend fun getMedicationList(): List<MedicationData> {
        return withContext(Dispatchers.IO) {
            return@withContext AppDatabase.getDatabase(App.appContext).medicationDao()
                .queryMedicationWithUserId(AppGlobalRepository.userName)
        }
    }

    fun getMedicationById(id: Int): MedicationData? {
        return AppDatabase.getDatabase(App.appContext).medicationDao().queryMedicationWithId(id)
            .getOrNull(0)
    }

    fun getDataHistoryById(id: Int): MyHistoryData? {
        return AppDatabase.getDatabase(App.appContext).myHistoryDao().fetchDataById(id)
            .getOrNull(0)
    }

    fun deleteMedicationById(id: Int) {
        scope.launch {
            AppDatabase.getDatabase(App.appContext).medicationDao().deleteById(id)
        }
    }

    fun setMedicationInvalid(id: Int) {
        scope.launch {
            AppDatabase.getDatabase(App.appContext).medicationDao().setMedicationInvalid(id)
        }
    }

    suspend fun createMedicationData(
        title: String,
        description: String,
        time: Long
    ): MedicationData? {
        return withContext(Dispatchers.IO) {
            val medicationData = MedicationData(
                keyId = UUID.randomUUID().toString(),
                userId = AppGlobalRepository.userName,
                title = title,
                description = description,
                time = time,
                isValid = 1
            )
            AppDatabase.getDatabase(App.appContext).medicationDao().insertMedication(medicationData)
            return@withContext AppDatabase.getDatabase(App.appContext).medicationDao()
                .queryMedicationWithKey(medicationData.keyId).getOrNull(0)
        }
    }

    suspend fun createHistoryData(date: Long, time: String): MyHistoryData? {
        return withContext(Dispatchers.IO) {
            val key = UUID.randomUUID().toString()
            val historyData = MyHistoryData(
                key = key,
                date = date,
                item = time
            )
            AppDatabase.getDatabase(App.appContext).myHistoryDao().insertData(historyData)
            return@withContext AppDatabase.getDatabase(App.appContext).myHistoryDao()
                .fetchDataByKey(key).getOrNull(0)
        }
    }

    suspend fun fetchHistoryDataList(start: Long, end: Long): List<MyHistoryData> {
        return withContext(Dispatchers.IO) {
            return@withContext AppDatabase.getDatabase(App.appContext).myHistoryDao()
                .fetchDataListBetweenDate(start, end)
        }
    }

    fun deleteHistoryData(data: MyHistoryData) {
        scope.launch {
            AppDatabase.getDatabase(App.appContext).myHistoryDao().deleteData(data)
        }
    }

    suspend fun hasUserAccount(userAccount: String): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext userDao.getUserByAccount(userAccount).isNotEmpty()
        }
    }

    fun insertUser(userAccount: String, password: String) {
        scope.launch {
            userDao.insertUser(
                UserData(
                    userAccount = userAccount,
                    userPassword = password,
                    userName = UUID.randomUUID().toString()
                )
            )
        }
    }

    fun getUserInfoByAccountAndPassword(account: String, password: String): UserData? {
        return userDao.getUserByAccountPassword(account, password).getOrNull(0)
    }
}