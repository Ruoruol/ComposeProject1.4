package com.example.composeproject1.model

import com.example.composeproject1.AirQualityActivity
import com.example.composeproject1.EmptyActivity
import com.example.composeproject1.HeartSleep
import com.example.composeproject1.MedicationListActivity
import com.example.composeproject1.Medications
import com.example.composeproject1.Myhome
import com.example.composeproject1.bean.DrawerData

object ResourceGlobalRepository {
    private val DRAWER_LIST = listOf(
        DrawerData("首頁", Myhome::class.java),
        DrawerData("我的帳號", EmptyActivity::class.java),
        DrawerData("基本資料", EmptyActivity::class.java),
        DrawerData("空氣品質", AirQualityActivity::class.java),
        DrawerData(
            "心律睡眠", HeartSleep::
            class.java
        ),
        DrawerData(
            "用藥提醒", Medications::
            class.java
        ),
        DrawerData(
            "提醒列表", MedicationListActivity::
            class.java
        )
    )


    fun getDrawableDataList() = DRAWER_LIST.toList()
}