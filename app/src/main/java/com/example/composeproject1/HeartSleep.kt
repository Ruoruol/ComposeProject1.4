package com.example.composeproject1

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.composeproject1.databinding.ActivityHeartSleepBinding
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.WeTemplateScreen
import com.github.mikephil.charting.data.Entry
import java.util.Random

class HeartSleep : AppCompatActivity() {

    private val random = Random()
    private val xData = ArrayList<String>()
    private val yData = ArrayList<Entry>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_sleep)
        setContent {
            WeTemplateScreen(topTitle = "心律睡眠", defaultIndex = remember {
                ResourceGlobalRepository.getIndexByName("心律睡眠")
            }, clickBack = { finish() }) {
                AndroidViewBinding(ActivityHeartSleepBinding::inflate) {
                    val lineChartData = LineChartData(bpmHistoryChart, this@HeartSleep)
                    for (i in 0..4) {
                        val randomValue = random.nextInt(31) + 70 // 生成70到100之间的随机整数
                        xData.add("第" + (i + 1) + "筆")
                        yData.add(Entry(i.toFloat(), randomValue.toFloat()))
                        if (i == 4) {
                            bpmValue.text = randomValue.toString()
                        }
                    }
                    lineChartData.initX(xData)
                    lineChartData.initY(0f, 10f)
                    lineChartData.initDataSet(yData)
                }
            }
        }


    }


}