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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class BloodPressureVm(application: Application) : BaseVm<BloodPressureEvent>(application) {
    var currentSelectTime by mutableLongStateOf(System.currentTimeMillis())
    var systolicPointList by mutableStateOf(listOf<Pair<Float, Float>>())
    var diastolicPointList by mutableStateOf(listOf<Pair<Float, Float>>())
    var dataList by mutableStateOf(listOf<BloodPressureData>())
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
        }.timeInMillis
        val minDayTimeInMillis = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        viewModelScope.launch {
            DatabaseRepository.getBloodPressureListBetween(
                userId,
                minDayTimeInMillis,
                maxDayTimeInMillis
            ).collectLatest {
                it.let { bloodPressureList ->
                    val list = bloodPressureList.toList().sortedBy { data ->
                        data.bloodPressureTime * 10 + data.bloodPressureDayDesc
                    }
                    dataList = list
                    systolicPointList = list.map { bloodPressureData ->
                        cleaner.timeInMillis = bloodPressureData.bloodPressureTime
                        val x = cleaner.get(Calendar.DAY_OF_MONTH).toFloat()
                        val y = bloodPressureData.bloodPressureHigh.toFloat()
                        Log.i("msgdddd","day $x")
                        x to y
                    }
                    diastolicPointList = list.map { bloodPressureData ->
                        cleaner.timeInMillis = bloodPressureData.bloodPressureTime
                        val x = cleaner.get(Calendar.DAY_OF_MONTH).toFloat()
                        val y = bloodPressureData.bloodPressureLow.toFloat()
                        x to y
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

            else -> {

            }
        }
    }

}

sealed class BloodPressureEvent : CommonEvent() {
    object GetBloodPressureInit : BloodPressureEvent()
    object NextMonthPressure : BloodPressureEvent()
    object BeforeMonthPressure : BloodPressureEvent()
}