package com.example.composeproject1.model

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Person2
import com.example.composeproject1.AirQualityActivity
import com.example.composeproject1.CalenderActivity
import com.example.composeproject1.EmptyActivity
import com.example.composeproject1.HeartSleep
import com.example.composeproject1.MedicationListActivity
import com.example.composeproject1.Medications
import com.example.composeproject1.Myhome
import com.example.composeproject1.bean.DrawerData

object ResourceGlobalRepository {
    private val DRAWER_LIST = listOf(
        DrawerData(
            "首頁",
            Myhome::class.java,
            imageVector = Icons.Default.Home,
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        ),
        DrawerData(
            "我的帳號", imageVector = Icons.Default.AccountBox,
            targetClazz = EmptyActivity::class.java
        ),
        DrawerData(
            "基本資料", imageVector = Icons.Default.Person2,
            targetClazz = EmptyActivity::class.java
        ),
        DrawerData(
            "空氣品質", imageVector = Icons.Default.Bedtime,
            targetClazz = AirQualityActivity::class.java
        ),
        DrawerData(
            "心律睡眠",
            imageVector = Icons.Default.ModeNight,
            targetClazz = CalenderActivity::class.java
        ),
        DrawerData(
            "用藥提醒",
            imageVector = Icons.Default.MedicalServices,
            targetClazz = Medications::class.java
        ),
        DrawerData(
            "提醒列表", imageVector = Icons.Default.AlarmAdd,
            targetClazz = MedicationListActivity::class.java
        )
    )

    fun getIndexByName(name: String): Int {
        return DRAWER_LIST.indexOfFirst { it.title == name }
    }
    fun getDrawableDataList() = DRAWER_LIST.toList()
}