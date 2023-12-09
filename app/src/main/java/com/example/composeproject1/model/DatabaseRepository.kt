package com.example.composeproject1.model

import com.example.composeproject1.App
import com.example.composeproject1.database.AppDatabase
import com.example.composeproject1.database.BloodPressureData
import com.example.composeproject1.database.MedicationData
import com.example.composeproject1.database.MyHistoryData
import com.example.composeproject1.database.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.Cleaner
import java.util.Calendar
import java.util.UUID

object DatabaseRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val userDao = AppDatabase.getDatabase(App.appContext).userDao()
    private val medicationDao = AppDatabase.getDatabase(App.appContext).medicationDao()
    private val bloodPressureDao = AppDatabase.getDatabase(App.appContext).bloodPressureDao()
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

    fun setMedicationSetNextTime(medicationData: MedicationData) {
        scope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = medicationData.time
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            AppDatabase.getDatabase(App.appContext).medicationDao().updateMedication(
                if (medicationData.count != Int.MAX_VALUE) {
                    medicationData.copy(
                        time = calendar.timeInMillis,
                        count = medicationData.count - 1
                    )
                } else {
                    medicationData.copy(
                        time = calendar.timeInMillis,
                    )
                }
            )
        }
    }

    suspend fun createMedicationData(
        title: String,
        description: String,
        count: Int,
        time: Long
    ): MedicationData? {
        return withContext(Dispatchers.IO) {
            val medicationData = MedicationData(
                keyId = UUID.randomUUID().toString(),
                userId = AppGlobalRepository.userName,
                title = title,
                description = description,
                time = time,
                isValid = 1,
                count = count
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
                item = time,
                userId = AppGlobalRepository.userId
            )
            AppDatabase.getDatabase(App.appContext).myHistoryDao().insertData(historyData)
            return@withContext AppDatabase.getDatabase(App.appContext).myHistoryDao()
                .fetchDataByKey(key).getOrNull(0)
        }
    }

    suspend fun fetchHistoryDataList(start: Long, end: Long): List<MyHistoryData> {
        return withContext(Dispatchers.IO) {
            return@withContext AppDatabase.getDatabase(App.appContext).myHistoryDao()
                .fetchDataListBetweenDate(start, end,AppGlobalRepository.userId)
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

    suspend fun getBloodPressureListBetween(
        userId: Long,
        startInMilli: Long,
        endInMilli: Long
    ): Flow<List<BloodPressureData>> {
        return withContext(Dispatchers.IO) {
            return@withContext bloodPressureDao.fetchBloodPressureListBetween(
                userId,
                startInMilli,
                endInMilli
            )
        }

    }

    fun saveBloodPressureData(
        bloodHeight: String,
        bloodLow: String,
        heartBeat: String,
        bloodPressureDayDesc: Int,
        bloodPressureTime: Long,
        bloodPressureUserId: Long
    ) {
        val bloodHeightInt = bloodHeight.toIntOrNull() ?: -1
        val bloodLowInt = bloodLow.toIntOrNull() ?: -1
        val heartBeatInt = heartBeat.toIntOrNull() ?: -1
        if (bloodHeightInt == -1 || bloodLowInt == -1 || heartBeatInt == -1) {
            return
        }
        saveBloodPressureData(
            bloodHeightInt,
            bloodLowInt,
            heartBeatInt,
            bloodPressureDayDesc,
            bloodPressureTime,
            bloodPressureUserId
        )

    }

    private fun saveBloodPressureData(
        bloodHeight: Int,
        bloodLow: Int,
        heartBeat: Int,
        bloodPressureDayDesc: Int,
        bloodPressureTime: Long,
        bloodPressureUserId: Long
    ) {
        scope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = bloodPressureTime
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val curData = BloodPressureData(
                bloodPressureHigh = bloodHeight,
                bloodPressureLow = bloodLow,
                heartBeat = heartBeat,
                year = year,
                month = month,
                day = day,
                bloodPressureDayDesc = bloodPressureDayDesc,
                bloodPressureTime = bloodPressureTime,
                bloodPressureUserId = bloodPressureUserId
            )
            val dbData = bloodPressureDao.selectBloodPressureByTime(
                curData.bloodPressureUserId,
                curData.year,
                curData.month,
                curData.day,
                curData.bloodPressureDayDesc
            ).getOrNull(0)
            if (dbData == null) {
                bloodPressureDao.insertBloodPressure(curData)
            } else {
                bloodPressureDao.updateBloodPressure(
                    curData.copy(
                        bloodPressureId = dbData.bloodPressureId
                    )
                )
            }
        }
    }

    fun deleteBloodPressureData(id: Long) {
        scope.launch {
            bloodPressureDao.deleteBloodPressureById(id)
        }
    }

    suspend fun getBloodPressureById(id: Long): BloodPressureData? {
        return withContext(Dispatchers.IO) {
            return@withContext bloodPressureDao.fetchBloodPressureById(id = id).getOrNull(0)
        }
    }
}