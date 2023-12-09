package com.example.composeproject1

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.composeproject1.database.MyHistoryData
import com.example.composeproject1.databinding.ActivityCalenderBinding
import com.example.composeproject1.model.Constant
import com.example.composeproject1.model.DatabaseRepository
import com.example.composeproject1.model.DatabaseRepository.fetchHistoryDataList
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.WeTemplateScreen
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.Cleaner
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Calendar.SECOND
import java.util.Date
import java.util.Locale

class CalenderActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView

    private lateinit var recyclerView: RecyclerView
    private var adapter: MyAdapter? = null
    private var itemList: MutableList<String>? = null

    private var selectedDate = "" // 用於保存日期
    var DB = "planB"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)
        setContent {
            WeTemplateScreen(
                topTitle = "行事曆",
                drawerGesturesEnabled = false,
                defaultIndex = remember {
                    ResourceGlobalRepository.getIndexByName("行事曆")
                },
                clickBack = { finish() }) {
                AndroidViewBinding(ActivityCalenderBinding::inflate) {
                    initView(this)
                }
            }
        }


    }

    private fun initView(binding: ActivityCalenderBinding) {
        calendarView = binding.calendarView
        recyclerView = binding.rcvList
        itemList = ArrayList()
        adapter = MyAdapter(this, arrayListOf(), selectedDate)
        recyclerView.setAdapter(adapter)
        recyclerView.setLayoutManager(LinearLayoutManager(this))

        // 設置 CalendarView 的日期選擇監聽器
        calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth -> // 直接更新外部的 selectedDate 变量
            dateChanged(year, month, dayOfMonth)
        })
        calendarView.date = System.currentTimeMillis()
        // 根據按鈕點擊情況添加日期和按鈕文字
        binding.btFollowUp.setOnClickListener(View.OnClickListener {
            val appointmentType = "回診" // 預約類型為 "回診"
            addItem(appointmentType, selectedDate)
            //GetCurrentMonth()
        })
        binding.btReceiveMedicine.setOnClickListener(View.OnClickListener {
            val appointmentType = "拿藥" // 預約類型為 "拿藥"
            addItem(appointmentType, selectedDate)
            //  GetCurrentMonth()
        })
        binding.btRehabilitation.setOnClickListener(View.OnClickListener {
            val appointmentType = "復健" // 預約類型為 "復健"
            addItem(appointmentType, selectedDate)
            // GetCurrentMonth()
        })
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarView.date
        dateChanged(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    private fun dateChanged(year: Int, month: Int, dayOfMonth: Int) {
        selectedDate = formatDate(year, month, dayOfMonth)
        val startDate = Calendar.getInstance()
        startDate[year, month, dayOfMonth, 0, 0] = 0 // 設置時間為當天的開始時間
        val endDate = Calendar.getInstance()
        endDate[year, month, dayOfMonth, 23, 59] = 59 // 設置時間為當天的結束時間
        lifecycleScope.launch {
            val list = DatabaseRepository.fetchHistoryDataList(
                startDate.timeInMillis,
                endDate.timeInMillis
            )
            adapter!!.pList = list.toMutableList()
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun addItem(newItem: String, selectedDate: String) {
        itemList!!.add(newItem)

        // 添加项目到資料庫
        lifecycleScope.launch {
            // 設定 CalendarView 的選擇日期以跳轉到相關的月份
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date: Date?
            try {
                date = dateFormat.parse(selectedDate)
                if (date != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.set(HOUR_OF_DAY, 6)
                    calendar.set(MINUTE, 0)
                    calendar.set(SECOND, 0)
                    val selectedDateInMillis = calendar.timeInMillis

                    val data = DatabaseRepository.createHistoryData(selectedDateInMillis, newItem)
                    if (data != null) {
                        AlarmTimer.setAlarmTimer(
                            this@CalenderActivity,
                            data.id,
                            Bundle().apply {
                                putInt("type", Constant.TYPE_HISTORY)
                                putString("title", newItem)
                                putInt("id", data.id)
                            },
                            selectedDateInMillis,
                            AlarmTimer.TIMER_ACTION,
                            AlarmManager.RTC_WAKEUP
                        )
                    }
                    calendarView.setDate(selectedDateInMillis, true, true)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main.immediate) {
                // 重新查尋資料庫以獲取最新數據
                this@CalenderActivity.selectedDate = selectedDate
                GetCurrentMonth()
            }

        }
    }

    internal inner class MyAdapter(
        var context: Context, var pList: MutableList<MyHistoryData>, // 用於保存日期
        var selectedDate: String
    ) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        // 創建新的 ViewHolder，並關聯 item_layout.xml 布局文件
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.listt, parent, false)
            return ViewHolder(view)
        }

        // 綁定數據到 ViewHolder 上
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val item = pList[position]
            holder.tv_date.text = sdf.format(Date(item.date).apply {
                time
            }) // 使用儲存在數據對象中的日期
            holder.tv_item.text = item.item
        }

        // 返回數據集的大小
        override fun getItemCount(): Int {
            return pList.size
        }

        fun setItems(items: MutableList<MyHistoryData>, selectedDate: String) {
            pList = items
            this.selectedDate = selectedDate
            notifyDataSetChanged()
        }

        // 定義 ViewHolder 類別
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_date: TextView
            var tv_item: TextView

            init {
                tv_date = itemView.findViewById(R.id.tv_date)
                tv_item = itemView.findViewById(R.id.tv_item)
                itemView.setOnLongClickListener {
                    val builder = AlertDialog.Builder(this@CalenderActivity)
                    builder.setTitle("刪除")
                    val position = adapterPosition
                    builder.setMessage("是否確定刪除 " + pList[position].item + "?")
                    builder.setPositiveButton("確定") { dialogInterface, i ->
                        val position = adapterPosition
                        val p = pList[position]
                        DatabaseRepository.deleteHistoryData(p)
                        pList.removeAt(position)
                        notifyDataSetChanged()
                        dialogInterface.dismiss()
                    }
                    builder.setNegativeButton("取消") { dialogInterface, i -> dialogInterface.dismiss() }
                    builder.show()
                    true
                }
            }
        }
    }

    // 將日期格式化為字符串
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date(year - 1900, month, dayOfMonth) // 注意月份需要減1
        return sdf.format(date)
    }

    private fun GetCurrentMonth() {
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.timeInMillis = calendarView.date
        val year = selectedCalendar[Calendar.YEAR]
        val month = selectedCalendar[Calendar.MONTH]
        val startDate = Calendar.getInstance()
        startDate[year, month, 1, 0, 0] = 0
        val endDate = Calendar.getInstance()
        endDate[year, month, selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59] =
            59

        // 獲取數據
        lifecycleScope.launch {
            val list = fetchHistoryDataList(startDate.timeInMillis, endDate.timeInMillis)
            withContext(Dispatchers.Main) {
                // 更新適配器
                adapter!!.setItems(list.toMutableList(), selectedDate)
                recyclerView.smoothScrollToPosition(0) // 滾動到頂部
            }
        }


    }
}
