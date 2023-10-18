package com.example.composeproject1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication_data WHERE user_id = :userId  ORDER BY time DESC")
    fun queryMedicationWithUserId(userId: String): List<MedicationData>

    @Insert
    fun insertMedication(medicationData: MedicationData)

    @Query("SELECT * FROM medication_data WHERE keyId = :key")
    fun queryMedicationWithKey(key: String): List<MedicationData>

    @Query("SELECT * FROM medication_data WHERE id = :id")
    fun queryMedicationWithId(id: Int): List<MedicationData>

    @Query("DELETE FROM medication_data WHERE id = :id")
    fun deleteById(id: Int)

    @Query("UPDATE medication_data SET isValid=0 WHERE id = :id")
    fun setMedicationInvalid(id: Int)
}