package com.example.composeproject1.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.composeproject1.database.BloodPressureData
import com.example.composeproject1.model.AppGlobalRepository
import com.example.composeproject1.model.DatabaseRepository
import com.example.composeproject1.mvi.CommonEvent
import com.example.composeproject1.mvi.IEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class BloodPressureVm(application: Application) : BaseVm<BloodPressureEvent>(application) {
    var currentSelectTime by mutableLongStateOf(System.currentTimeMillis())
    var systolicPointList by mutableStateOf(listOf<Pair<Float, Float>>())
    var diastolicPointList by mutableStateOf(listOf<Pair<Float, Float>>())
    var dataList by mutableStateOf(listOf<BloodPressureData>())
    private var beforeJob: Job? = null

    val userId by lazy {
        AppGlobalRepository.userId
    }

    private fun initTime() {
        val cleaner = Calendar.getInstance()
        cleaner.timeInMillis = currentSelectTime
        val currentYear = cleaner.get(Calendar.YEAR)
        val currentMonth = cleaner.get(Calendar.MONTH)
        val maxDay = cleaner.getActualMaximum(Calendar.DAY_OF_MONTH)
        val maxDayTimeInMillis = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, maxDay)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis
        val minDayTimeInMillis = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        beforeJob?.cancel()
        beforeJob = viewModelScope.launch {
            DatabaseRepository.getBloodPressureListBetween(
                userId,
                minDayTimeInMillis,
                maxDayTimeInMillis
            ).collectLatest { pressureData ->
                pressureData.let { bloodPressureList ->
                    withContext(Dispatchers.IO) {
                        val list: List<BloodPressureData> = bloodPressureList.toList().sortedBy {
                            it.year * 100000 + it.month * 1000 + it.day * 10 + it.bloodPressureDayDesc
                        }
                        val sysList = list.map { bloodPressureData ->
                            cleaner.timeInMillis = bloodPressureData.bloodPressureTime
                            val x = cleaner.get(Calendar.DAY_OF_MONTH).toFloat() - 1
                            val y = bloodPressureData.bloodPressureHigh.toFloat()
                            (x * 3 + bloodPressureData.bloodPressureDayDesc) to y
                        }
                        val diaList = list.map { bloodPressureData ->
                            cleaner.timeInMillis = bloodPressureData.bloodPressureTime
                            val x = cleaner.get(Calendar.DAY_OF_MONTH).toFloat() - 1
                            val y = bloodPressureData.bloodPressureLow.toFloat()
                            (x * 3 + bloodPressureData.bloodPressureDayDesc) to y
                        }
                        withContext(Dispatchers.Main) {
                            dataList = list
                            systolicPointList = sysList
                            diastolicPointList = diaList
                        }
                    }

                }
            }
        }
    }

    private fun timeBackOrForwardMonth(isBack: Boolean) {
        val cleaner = Calendar.getInstance()
        cleaner.timeInMillis = currentSelectTime
        if (isBack) {
            cleaner.add(Calendar.MONTH, -1)
        } else {
            cleaner.add(Calendar.MONTH, 1)
        }
        currentSelectTime = cleaner.timeInMillis
    }

    override fun dispatchEvent(event: BloodPressureEvent) {
        when (event) {
            is BloodPressureEvent.GetBloodPressureInit -> {
                initTime()
            }

            is BloodPressureEvent.NextMonthPressure -> {
                timeBackOrForwardMonth(false)
                initTime()
            }

            is BloodPressureEvent.BeforeMonthPressure -> {
                timeBackOrForwardMonth(true)
                initTime()
            }

            is BloodPressureEvent.EditPressureEvent -> {
                emitEffect(BloodPressureEffect.GoEditEffect(event.id))
            }

            is BloodPressureEvent.DeletePressureEvent -> {
                DatabaseRepository.deleteBloodPressureData(event.id)
            }

            is BloodPressureEvent.OtherDatePressure -> {
                emitEffect(BloodPressureEffect.ShowDateEffect())
            }

            is BloodPressureEvent.UpdateTimeEvent -> {
                currentSelectTime = event.time
                initTime()
            }

            else -> {

            }
        }
    }

}

sealed class BloodPressureEvent : CommonEvent() {
    object GetBloodPressureInit : BloodPressureEvent()
    object NextMonthPressure : BloodPressureEvent()
    object OtherDatePressure : BloodPressureEvent()
    object BeforeMonthPressure : BloodPressureEvent()
    class EditPressureEvent(val id: Long) : BloodPressureEvent()
    class DeletePressureEvent(val id: Long) : BloodPressureEvent()
    class UpdateTimeEvent(val time: Long) : BloodPressureEvent()
}

sealed class BloodPressureEffect : IEffect {
    class GoEditEffect(val id: Long) : BloodPressureEffect()
    class ShowDateEffect : BloodPressureEffect()
}