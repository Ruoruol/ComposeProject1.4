package com.example.composeproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.composeproject1.ui.MedicationListScreen
import com.example.composeproject1.viewmodel.MedicationListEvent
import com.example.composeproject1.viewmodel.MedicationListVm

class MedicationListActivity : ComponentActivity() {
    private val vm by viewModels<MedicationListVm>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicationListScreen(
                vm.list,
                vm.invalidList,
                vm::dispatch
            )
        }
        vm.dispatch(MedicationListEvent.Init)
    }
}