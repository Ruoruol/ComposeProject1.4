package com.example.composeproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.composeproject1.ui.MedicationListScreen
import com.example.composeproject1.ui.WeTemplateScreen
import com.example.composeproject1.ui.theme.ComposeProject1Theme
import com.example.composeproject1.viewmodel.MedicationListEvent
import com.example.composeproject1.viewmodel.MedicationListVm

class MedicationListActivity : ComponentActivity() {
    private val vm by viewModels<MedicationListVm>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeTemplateScreen(topTitle = "提醒列表", clickBack = { finish() }) {
                MedicationListScreen(
                    vm.list,
                    vm.invalidList,
                    vm::dispatch
                )
            }
        }
        vm.dispatch(MedicationListEvent.Init)

    }
}