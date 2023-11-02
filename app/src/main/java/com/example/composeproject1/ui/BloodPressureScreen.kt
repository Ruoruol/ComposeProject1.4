package com.example.composeproject1.ui

import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composeproject1.Action
import com.example.composeproject1.LineChartData
import com.example.composeproject1.database.BloodPressureData
import com.example.composeproject1.mvi.CommonEvent
import com.example.composeproject1.ui.theme.ComposeProject1Theme
import com.example.composeproject1.ui.theme.PrimaryColor
import com.example.composeproject1.ui.theme.Purple40
import com.example.composeproject1.viewmodel.BloodPressureEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun BloodPressureScreen(
    userId: Long,
    time: Long,
    systolicPointList: List<Pair<Float, Float>>,
    diastolicPointList: List<Pair<Float, Float>>,
    dataList: List<BloodPressureData>,
    onEvent: (CommonEvent) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(color = Purple40),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val year = remember(time) {
                Calendar.getInstance().apply {
                    timeInMillis = time
                }.get(Calendar.YEAR)
            }
            val month = remember(time) {
                Calendar.getInstance().apply {
                    timeInMillis = time
                }.get(Calendar.MONTH)
            }
            Spacer(modifier = Modifier.height(20.dp))
            BackOrForwardMonth(onEvent)
            Text(text = " $year 年  ${month + 1} 月", color = PrimaryColor, fontSize = 24.sp)
            BloodPressureChart(year, month, systolicPointList, diastolicPointList)
            BloodPressureList()
            ItemListScreen(dataList, onEvent)
        }
        val context = LocalContext.current
        FloatingActionButton(
            onClick = {
                context.startActivity(Intent(context, LineChartData::class.java).apply {
                    putExtra("action", Action.NEW)
                    // 將用戶的 ID 傳遞给 linechart 頁面
                    putExtra("user_id", userId)
                })
            }, backgroundColor = PrimaryColor, modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(end = 20.dp, bottom = 50.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "添加", tint = Color.White)
        }
    }
}

@Composable
fun ItemListScreen(dataList: List<BloodPressureData>, onEvent: (BloodPressureEvent) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(dataList) { bloodPressureData ->
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .border(1.dp, PrimaryColor, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "${bloodPressureData.year}年${bloodPressureData.month + 1}月${bloodPressureData.day}日 ${
                        if (bloodPressureData.bloodPressureDayDesc == 0) "上午" else if (bloodPressureData.bloodPressureDayDesc == 1) "中午" else "晚上"
                    } 血壓數據:", fontSize = 24.sp
                )
                Text(
                    text = "收縮壓 : ${bloodPressureData.bloodPressureHigh}",
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = 24.sp
                )
                Text(
                    text = "舒張壓 : ${bloodPressureData.bloodPressureLow}",
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = 24.sp
                )
                Text(
                    text = "心率 : ${bloodPressureData.heartBeat}",
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = 24.sp
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            onEvent(BloodPressureEvent.EditPressureEvent(bloodPressureData.bloodPressureId))
                        }, colors = ButtonDefaults.buttonColors(
                            contentColor = PrimaryColor
                        )
                    ) {
                        Text(text = "編輯", color = Color.White, fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            onEvent(BloodPressureEvent.DeletePressureEvent(bloodPressureData.bloodPressureId))
                        }, colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Red,
                            containerColor = Color.Red
                        )
                    ) {
                        Text(text = "刪除", color = Color.White, fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun BackOrForwardMonth(onEvent: (BloodPressureEvent) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                onEvent(BloodPressureEvent.BeforeMonthPressure)
            }, colors = ButtonDefaults.buttonColors(
                contentColor = PrimaryColor
            )
        ) {
            Text(text = "上個月", color = Color.White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                onEvent(BloodPressureEvent.NextMonthPressure)
            }, colors = ButtonDefaults.buttonColors(
                contentColor = PrimaryColor
            )
        ) {
            Text(text = "下個月", color = Color.White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                onEvent(BloodPressureEvent.OtherDatePressure)
            }, colors = ButtonDefaults.buttonColors(
                contentColor = PrimaryColor
            )
        ) {
            Text(text = "其他日期", color = Color.White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.weight(1f))


    }
}

@Composable
fun BloodPressureChart(
    year: Int,
    month: Int,
    systolicPointList: List<Pair<Float, Float>>,
    diastolicPointList: List<Pair<Float, Float>>
) {
    AndroidView(
        factory = {
            LineChart(it).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                initLineChart()
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        update = {
            it.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    // 假设X轴的值为日期的时间戳（毫秒）
                    val calendar = Calendar.getInstance()
                    val day = value.toInt()
                    val descFloat = ((value - day) * 10).toInt()
                    calendar.set(year, month, day)
                    val dateFormat = SimpleDateFormat("MM/dd")
                    return "${dateFormat.format(calendar.time)}${if (descFloat == 0) "上" else if (descFloat == 3) "中" else if (descFloat == 6) "晚" else ""}"
                }
            }
            val lineData = it.lineData
            var systolicDataSet = lineData.getDataSetByIndex(0) as? LineDataSet
            var diastolicDataSet = lineData.getDataSetByIndex(1) as? LineDataSet
            if (systolicDataSet == null) {
                systolicDataSet = LineDataSet(systolicPointList.toLineEntries(), "收縮壓")
                systolicDataSet.color = android.graphics.Color.RED
                systolicDataSet.setCircleColor(android.graphics.Color.RED)
                systolicDataSet.label = "收縮壓"
                lineData.addDataSet(systolicDataSet)
            } else {
                systolicDataSet.values = systolicPointList.toLineEntries()
            }
            if (diastolicDataSet == null) {
                diastolicDataSet = LineDataSet(diastolicPointList.toLineEntries(), "舒張壓")
                diastolicDataSet.color = android.graphics.Color.BLUE
                diastolicDataSet.setCircleColor(android.graphics.Color.BLUE)
                diastolicDataSet.label = "舒張壓"
                lineData.addDataSet(diastolicDataSet)
            } else {
                diastolicDataSet.values = diastolicPointList.toLineEntries()
            }
            lineData.notifyDataChanged()
            it.notifyDataSetChanged()
            it.invalidate()
        }
    )
}

private fun List<Pair<Float, Float>>.toLineEntries(): List<Entry> {
    return this.map { (x, y) ->
        Entry(x, y)
    }
}

private fun LineChart.initLineChart() {
    // 设置折线图的描述
    val description = Description()
    description.text = "血壓趨勢"
    this.description = description
    isDragEnabled = true // 允許拖動
    setScaleEnabled(true) // 允許縮放
    setPinchZoom(true) // 啟用縮放手勢
    // 設置X軸標籤的位置
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    // 自定義X軸標籤的間隔

    // 自定義收縮壓線的樣式
    val systolicDataSet = LineDataSet(ArrayList(), "收縮壓")
    systolicDataSet.color = android.graphics.Color.RED
    systolicDataSet.setCircleColor(android.graphics.Color.RED)

    // 自定義舒張壓線的樣式
    val diastolicDataSet = LineDataSet(ArrayList(), "舒張壓")
    diastolicDataSet.color = android.graphics.Color.BLUE
    diastolicDataSet.setCircleColor(android.graphics.Color.BLUE)


    // 添加Y軸的限制線，例如正常範圍
    val normalSystolicRange = LimitLine(120f, "正常範圍")
    normalSystolicRange.lineColor = android.graphics.Color.GREEN
    normalSystolicRange.lineWidth = 2f
    axisLeft.addLimitLine(normalSystolicRange)
    val normalDiastolicRange = LimitLine(80f, "正常範圍")
    normalDiastolicRange.lineColor = android.graphics.Color.GREEN
    normalDiastolicRange.lineWidth = 2f
    axisLeft.addLimitLine(normalDiastolicRange)
    data = com.github.mikephil.charting.data.LineData(systolicDataSet, diastolicDataSet)
    invalidate()
}


@Composable
fun BloodPressureList() {

}

@Preview
@Composable
fun BloodPressureScreenPreview() {

    BloodPressureScreen(
        1,
        System.currentTimeMillis(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf()
    ) {

    }

}