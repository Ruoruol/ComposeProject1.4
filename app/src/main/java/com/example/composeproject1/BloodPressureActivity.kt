package com.example.composeproject1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.remember
import com.example.composeproject1.model.Constant.BundleKey.KEY_BUNDLE_BLOOD_PRESSURE_ID
import com.example.composeproject1.model.Constant.BundleKey.KEY_BUNDLE_USER_ID
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.mvi.observeEffect
import com.example.composeproject1.ui.BloodPressureScreen
import com.example.composeproject1.ui.WeTemplateScreen
import com.example.composeproject1.viewmodel.BloodPressureEffect
import com.example.composeproject1.viewmodel.BloodPressureEvent
import com.example.composeproject1.viewmodel.BloodPressureVm
import com.github.mikephil.charting.charts.LineChart

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
                    }
                }
            }
        }
        vm.dispatchEvent(BloodPressureEvent.GetBloodPressureInit)
        vm.effect.observeEffect(this) {
            if (it is BloodPressureEffect.GoEditEffect) {
                startActivity(Intent(this, LineChartData::class.java).apply {
                    putExtra(KEY_BUNDLE_USER_ID, vm.userId)
                    putExtra(KEY_BUNDLE_BLOOD_PRESSURE_ID, it.id)
                })
            }
        }
    }

}