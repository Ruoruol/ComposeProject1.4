package com.example.composeproject1.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeproject1.AlarmTimer
import com.example.composeproject1.AlarmTimer.TIMER_ACTION
import com.example.composeproject1.database.MedicationData
import com.example.composeproject1.model.DatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MedicationListVm(private val application: Application) : AndroidViewModel(application) {
    val list: List<MedicationData> = mutableStateListOf()
    val invalidList: List<MedicationData> = mutableStateListOf()
    private fun initEvent() {
        viewModelScope.launch {
            val resultList = DatabaseRepository.getMedicationList()
            resultList.forEach {
                if (it.isValidOrInTime()) {
                    list.add(it)
                } else {
                    invalidList.add(it)
                }
            }
        }
    }

    private fun deleteEvent(id: Int) {
        viewModelScope.launch {
            DatabaseRepository.deleteMedicationById(id)
            withContext(Dispatchers.Main) {
                list.removeId(id)
                invalidList.removeId(id)
            }
        }
    }

    private fun List<MedicationData>.removeId(id: Int) {
        var targetIndex = -1
        forEachIndexed { index, medicationData ->
            if (medicationData.id == id) {
                targetIndex = index
                AlarmTimer.cancelAlarmTimer(application, medicationData.id, TIMER_ACTION)
                return@forEachIndexed
            }
        }
        if (targetIndex >= 0) {
            (this as MutableList<MedicationData>).removeAt(targetIndex)
        }
    }


    fun dispatch(event: MedicationListEvent) {
        when (event) {
            is MedicationListEvent.Delete -> {
                deleteEvent(event.id)
            }

            is MedicationListEvent.Init -> {
                initEvent()
            }
        }
    }

    private fun <T> List<T>.add(t: T) {
        (this as MutableList<T>).add(t)
    }

    private fun <T> List<T>.addAll(list: List<T>) {
        (this as MutableList<T>).addAll(list)
    }

    private fun <T> List<T>.clear() {
        (this as MutableList<T>).clear()
    }
}

sealed class MedicationListEvent {
    class Delete(val id: Int) : MedicationListEvent()
    object Init : MedicationListEvent()
}