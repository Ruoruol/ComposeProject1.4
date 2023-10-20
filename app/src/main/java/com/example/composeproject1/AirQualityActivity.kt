package com.example.composeproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.MainScreen
import com.example.composeproject1.ui.WeTemplateScreen
import com.example.composeproject1.viewmodel.MainEvent
import com.example.composeproject1.viewmodel.MainVm

class AirQualityActivity : ComponentActivity() {
    private val mainVm by viewModels<MainVm>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
            WeTemplateScreen(
                "空氣品質",
                defaultIndex = ResourceGlobalRepository.getIndexByName("空氣品質"),
                clickBack = {
                    finish()
                }) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = it),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        mainVm.cityList,
                        mainVm.curSelectCity,
                        mainVm.curSelectArea,
                        mainVm.curSelectAreaList,
                        mainVm.curQuality,
                        mainVm::dispatchEvent
                    )
                }
            }


        }
        mainVm.dispatchEvent(MainEvent.Init)
    }
}

