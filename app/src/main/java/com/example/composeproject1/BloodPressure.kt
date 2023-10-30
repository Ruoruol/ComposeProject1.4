package com.example.composeproject1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.composeproject1.databinding.ActivityCalenderBinding
import com.example.composeproject1.databinding.ActivityHeartSleepBinding
import com.example.composeproject1.model.AppGlobalRepository
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.WeTemplateScreen
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections

class BloodPressure : AppCompatActivity() {
    private lateinit var bt_add: Button
    private lateinit var rcv: RecyclerView
    var gson = Gson()
    private lateinit var database: DBHelper
    var rcvAdapter: RCVAdapter? = null
    var fList = ArrayList<MyData>()
    var lineChart: LineChart? = null
    private lateinit var datePicker: DatePicker
    private lateinit var calendarView: CalendarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_heart_sleep)
        setContent {
            WeTemplateScreen(
                topTitle = "心律睡眠",
                drawerGesturesEnabled = false,
                defaultIndex = remember {
                    ResourceGlobalRepository.getIndexByName("心律睡眠")
                },
                clickBack = { finish() }) {
                AndroidViewBinding(ActivityHeartSleepBinding::inflate) {
                    rcv = rcvList
                    bt_add = btAdd
                    this@BloodPressure.lineChart = lineChart
                    this@BloodPressure.calendarView = calendarView
                    val userId = AppGlobalRepository.userId // -1是預設值，表示沒有傳遞學生ID時的情況
                    database = DBHelper(this@BloodPressure)
                    rcv.setLayoutManager(LinearLayoutManager(this@BloodPressure))
                    rcvAdapter = RCVAdapter(this@BloodPressure, fList)
                    rcv.setAdapter(rcvAdapter)
                    initializeLineChart()
                    bt_add.setOnClickListener(View.OnClickListener {
                        val it = Intent(this@BloodPressure, LineChartData::class.java)
                        it.putExtra("action", Action.NEW)
                        // 将用户的 ID 传递给 linechart 页面
                        it.putExtra("user_id", userId)
                        newLauncher.launch(it)
                    })


                    // 监听日历选择事件
                    calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth -> // 计算日期范围
                        val startDate = Calendar.getInstance()
                        startDate[year, month, dayOfMonth, 0, 0] = 0 // 设置时间为当天的开始时间
                        val endDate = Calendar.getInstance()
                        endDate[year, month, dayOfMonth, 23, 59] = 59 // 设置时间为当天的结束时间
                        fList = database!!.getHBDataByMonth(startDate, endDate, userId)
                        rcvAdapter!!.pList = fList
                        rcvAdapter!!.notifyDataSetChanged()

                        // 执行数据库查询
                        val data: List<MyData> =
                            database!!.getHBDataByMonth(startDate, endDate, userId)
                        // 转换数据格式
                        val entryPair = convertDataToEntries(data)
                        // 更新折线图
                        updateLineChart(entryPair.first, entryPair.second)
                    })
                    lifecycleScope.launch(Dispatchers.IO) {
                        val data = database!!.getAllHBDataByUserId(AppGlobalRepository.userId)
                        withContext(Dispatchers.Main) {
                            fList.clear()
                            fList.addAll(data)
                            rcvAdapter?.notifyDataSetChanged()
                            updateLineChat(data)
                        }
                    }
                }

            }
        }

    }

    private fun updateLineChat(data: List<MyData>) {
        val entryPair = convertDataToEntries(data)
        // 更新折线图
        updateLineChart(entryPair.first, entryPair.second)
    }

    var newLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                if (result.resultCode == RESULT_OK) {
                    val it = result.data
                    val json = it!!.getStringExtra("json")
                    val p = gson.fromJson(json, MyData::class.java)
                    database!!.addHBData(p.date, p.high, p.low, p.hb, p.item, p.user_id)
                    // 获取新数据的日期，并设置为选定日期
                    val newDataDate = p.date // 假设日期存储在 MyData 对象的 date 字段中
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    try {
                        val date = dateFormat.parse(newDataDate)
                        val newDateInMillis = date.time
                        calendarView!!.date = newDateInMillis
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    // 更新折线图和列表
                    updateLineChartForCurrentMonth()
                }
            }
        }
    )
    var editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult?) {
                result ?: return
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val json = data!!.getStringExtra("json")
                    val p = gson.fromJson(json, MyData::class.java)
                    database!!.updateHBData(p)
                    updateLineChartForCurrentMonth()
                }
            }
        })

    inner class RCVAdapter(var context: Context, var pList: ArrayList<MyData>) :
        RecyclerView.Adapter<RCVAdapter.RCVHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.list, parent, false)
            return RCVHolder(view)
        }

        override fun onBindViewHolder(holder: RCVHolder, position: Int) {

            // 在这里对数据进行排序
            Collections.sort(
                pList,
                java.util.Comparator { data1, data2 -> // 假设 MyData 中有一个表示日期的字段叫做 date
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    try {
                        val date1 = sdf.parse(data1.date)
                        val date2 = sdf.parse(data2.date)
                        return@Comparator date1.compareTo(date2)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                        return@Comparator 0
                    }
                })
            holder.tv_date.text = pList[position].date
            holder.tv_item.text = pList[position].item
            holder.tv_h.text = pList[position].high
            holder.tv_l.text = pList[position].low
            holder.tv_hb.text = pList[position].hb
        }

        override fun getItemCount(): Int {
            return pList.size
        }

        inner class RCVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_date: TextView
            var tv_item: TextView
            var tv_h: TextView
            var tv_l: TextView
            var tv_hb: TextView

            init {
                tv_date = itemView.findViewById(R.id.tv_date)
                tv_item = itemView.findViewById(R.id.tv_time)
                tv_h = itemView.findViewById(R.id.tv_h)
                tv_l = itemView.findViewById(R.id.tv_l)
                tv_hb = itemView.findViewById(R.id.tv_hb)
                itemView.setOnClickListener {
                    val position = adapterPosition
                    val p = pList[position]
                    val json = gson.toJson(p)
                    val it = Intent(this@BloodPressure, LineChartData::class.java)
                    it.putExtra("json", json)
                    it.putExtra("action", Action.EDIT)
                    editLauncher.launch(it)
                }
                itemView.setOnLongClickListener {
                    val builder = AlertDialog.Builder(this@BloodPressure)
                    builder.setTitle("Delete Message")
                    val position = adapterPosition
                    builder.setMessage("Are you sure to delete message of " + pList[position].item + "?")
                    builder.setPositiveButton("Yes") { dialogInterface, i ->
                        val position = adapterPosition
                        val p = pList[position]
                        database!!.deleteHBData(p)
                        pList.removeAt(position)
                        notifyDataSetChanged()
                        dialogInterface.dismiss()
                        updateLineChartForCurrentMonth()
                    }
                    builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
                    builder.show()
                    true
                }
            }
        }
    }

    private fun initializeLineChart() {
        // 设置折线图的描述
        val description = Description()
        description.text = "血壓趋势"
        lineChart!!.description = description
        lineChart!!.isDragEnabled = true // 允许拖动
        lineChart!!.setScaleEnabled(true) // 允许缩放
        lineChart!!.setPinchZoom(true) // 启用缩放手势


        // 获取X轴和Y轴对象
        val xAxis = lineChart!!.xAxis
        val leftYAxis = lineChart!!.axisLeft
        val rightYAxis = lineChart!!.axisRight
        var data: List<MyData?> = ArrayList()
        data = database!!.allHBData

        // 设置X轴标签的位置
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // 自定义X轴标签的间隔
        xAxis.setLabelCount(data.size, true) // 设置标签数量为数据点的数量
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // 假设X轴的值为日期的时间戳（毫秒）
                val timestamp = value.toLong()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timestamp
                val dateFormat = SimpleDateFormat("MM/dd")
                return dateFormat.format(calendar.time)
            }
        }

        // 自定义收缩压线的样式
        val systolicDataSet = LineDataSet(ArrayList(), "收缩壓")
        systolicDataSet.color = Color.RED
        systolicDataSet.setCircleColor(Color.RED)

        // 自定义舒张压线的样式
        val diastolicDataSet = LineDataSet(ArrayList(), "舒张壓")
        diastolicDataSet.color = Color.BLUE
        diastolicDataSet.setCircleColor(Color.BLUE)

        // 创建LineData对象并设置数据
        val lineData = LineData(systolicDataSet, diastolicDataSet)
        lineChart!!.data = lineData

        // 添加Y轴的限制线，例如正常范围
        val normalSystolicRange = LimitLine(120f, "正常范围")
        normalSystolicRange.lineColor = Color.GREEN
        normalSystolicRange.lineWidth = 2f
        leftYAxis.addLimitLine(normalSystolicRange)
        val normalDiastolicRange = LimitLine(80f, "正常范围")
        normalDiastolicRange.lineColor = Color.GREEN
        normalDiastolicRange.lineWidth = 2f
        leftYAxis.addLimitLine(normalDiastolicRange)
        lineChart!!.invalidate()
    }

    private fun convertDataToEntries(data: List<MyData>): Pair<List<Entry>, List<Entry>> {
        // 将数据库中的数据转换为Entry对象的列表
        val systolicEntries: MutableList<Entry> = ArrayList()
        val diastolicEntries: MutableList<Entry> = ArrayList()
        for (item in data) {
            // 假设MyData对象中包含日期、高压和低压数据
            // 将日期的时间戳作为X轴值
            val timestamp = getTimestampFromStringDate(item.getDate())
            val highPressure = item.highPressure.toFloat()
            val lowPressure = item.lowPressure.toFloat()
            systolicEntries.add(Entry(timestamp.toFloat(), highPressure))
            diastolicEntries.add(Entry(timestamp.toFloat(), lowPressure))
        }

        // 按日期排序
        Collections.sort(systolicEntries) { e1, e2 -> java.lang.Float.compare(e1.x, e2.x) }
        Collections.sort(diastolicEntries) { e1, e2 -> java.lang.Float.compare(e1.x, e2.x) }
        return Pair(systolicEntries, diastolicEntries)
    }

    private fun getTimestampFromStringDate(dateString: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.parse(dateString)
            date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            -1
        }
    }

    private fun updateLineChart(systolicEntries: List<Entry>, diastolicEntries: List<Entry>) {
        val lineData = lineChart!!.lineData
        var systolicDataSet = lineData.getDataSetByIndex(0) as LineDataSet
        var diastolicDataSet = lineData.getDataSetByIndex(1) as LineDataSet
        if (systolicDataSet == null) {
            systolicDataSet = LineDataSet(systolicEntries, "收缩壓")
            systolicDataSet.color = Color.RED
            systolicDataSet.setCircleColor(Color.RED)
            systolicDataSet.label = "收缩壓"
            lineData.addDataSet(systolicDataSet)
        } else {
            systolicDataSet.values = systolicEntries
        }
        if (diastolicDataSet == null) {
            diastolicDataSet = LineDataSet(diastolicEntries, "舒张壓")
            diastolicDataSet.color = Color.BLUE
            diastolicDataSet.setCircleColor(Color.BLUE)
            diastolicDataSet.label = "舒张壓"
            lineData.addDataSet(diastolicDataSet)
        } else {
            diastolicDataSet.values = diastolicEntries
        }
        lineData.notifyDataChanged()
        lineChart!!.notifyDataSetChanged()
        lineChart!!.invalidate()
    }

    private fun updateLineChartForCurrentMonth() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = database!!.getAllHBDataByUserId(AppGlobalRepository.userId)
            withContext(Dispatchers.Main) {
                // 获取当前选定的日期
                val selectedDate = calendarView!!.date
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.timeInMillis = selectedDate

                // 计算当前选定月份的开始和结束日期
                val startDate = Calendar.getInstance()
                startDate.timeInMillis = selectedDate
                startDate[Calendar.DAY_OF_MONTH] = 1 // 设置为当前月份的第一天
                startDate[Calendar.HOUR_OF_DAY] = 0
                startDate[Calendar.MINUTE] = 0
                startDate[Calendar.SECOND] = 0
                val endDate = Calendar.getInstance()
                endDate.timeInMillis = selectedDate
                endDate[Calendar.DAY_OF_MONTH] =
                    endDate.getActualMaximum(Calendar.DAY_OF_MONTH) // 设置为当前月份的最后一天
                endDate[Calendar.HOUR_OF_DAY] = 23
                endDate[Calendar.MINUTE] = 59
                endDate[Calendar.SECOND] = 59
                val userId = AppGlobalRepository.userId
                fList = data
                rcvAdapter!!.pList = fList
                rcvAdapter!!.notifyDataSetChanged()

                // 执行数据库查询
                val data: List<MyData> = fList

                // 转换数据格式
                val entryPair = convertDataToEntries(data)

                // 更新折线图
                updateLineChart(entryPair.first, entryPair.second)
            }

        }

    }
}
