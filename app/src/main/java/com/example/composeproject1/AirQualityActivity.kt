package com.example.composeproject1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composeproject1.ui.MainScreen
import com.example.composeproject1.viewmodel.MainVm
import com.example.composeproject1.viewmodel.MainEvent
import com.example.composeproject1.ui.theme.ComposeProject1Theme

class AirQualityActivity : ComponentActivity() {
    private val mainVm by viewModels<MainVm>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeProject1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
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

