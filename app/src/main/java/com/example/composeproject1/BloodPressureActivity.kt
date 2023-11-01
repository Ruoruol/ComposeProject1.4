package com.example.composeproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.BloodPressureScreen
import com.example.composeproject1.ui.WeTemplateScreen
import com.example.composeproject1.viewmodel.BloodPressureEvent
import com.example.composeproject1.viewmodel.BloodPressureVm

class BloodPressureActivity : ComponentActivity() {
    private val vm: BloodPressureVm by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeTemplateScreen(
                topTitle = "心律睡眠",
                drawerGesturesEnabled = false,
                defaultIndex = remember {
                    ResourceGlobalRepository.getIndexByName("心律睡眠")
                },
                clickBack = { finish() }) {
                BloodPressureScreen(
                    vm.userId,
                    vm.currentSelectTime,
                    vm.systolicPointList,
                    vm.diastolicPointList,
                    vm.dataList
                ) {
                    if (it is BloodPressureEvent) {
                        vm.dispatchEvent(it)
                    } else {

                    }
                }
            }
        }
        vm.dispatchEvent(BloodPressureEvent.GetBloodPressureInit)
    }

}