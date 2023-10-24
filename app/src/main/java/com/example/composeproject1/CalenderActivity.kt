package com.example.composeproject1

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.composeproject1.databinding.ActivityCalenderBinding
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.WeTemplateScreen
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale

class CalenderActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView

    private lateinit var recyclerView: RecyclerView
    private var adapter: MyAdapter? = null
    private var itemList: MutableList<String>? = null
    var gson = Gson()
    var fList = arrayListOf<MyData>()

    var database: SQLiteDatabaseHelper? = null
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
        database = SQLiteDatabaseHelper(this, DB, null, 1)
        itemList = ArrayList()
        adapter = MyAdapter(this, fList, selectedDate)
        recyclerView.setAdapter(adapter)
        recyclerView.setLayoutManager(LinearLayoutManager(this))

        // 設置 CalendarView 的日期選擇監聽器
        calendarView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth -> // 直接更新外部的 selectedDate 变量
            selectedDate = formatDate(year, month, dayOfMonth)
            val startDate = Calendar.getInstance()
            startDate[year, month, dayOfMonth, 0, 0] = 0 // 设置时间为当天的开始时间
            val endDate = Calendar.getInstance()
            endDate[year, month, dayOfMonth, 23, 59] = 59 // 设置时间为当天的结束时间
            fList = database!!.getMyDataByMonth(startDate, endDate)
            adapter!!.pList = fList
            adapter!!.notifyDataSetChanged()
        })
        // 根據按鈕點擊情況添加日期和按鈕文字
        binding.btFollowUp.setOnClickListener(View.OnClickListener {
            val appointmentType = "回診" // 預約類型為 "回診"
            addItem(appointmentType, selectedDate)
            GetCurrentMonth()
        })
        binding.btReceiveMedicine.setOnClickListener(View.OnClickListener {
            val appointmentType = "拿藥" // 預約類型為 "回診"
            addItem(appointmentType, selectedDate)
            GetCurrentMonth()
        })
        binding.btRehabilitation.setOnClickListener(View.OnClickListener {
            val appointmentType = "復健" // 預約類型為 "回診"
            addItem(appointmentType, selectedDate)
            GetCurrentMonth()
        })
    }

    private fun addItem(newItem: String, selectedDate: String) {
        itemList!!.add(newItem)

        // 添加项目到数据库
        database!!.addMyData(selectedDate, newItem)

        // 設定 CalendarView 的選擇日期以跳轉到相關的月份
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date?
        try {
            date = dateFormat.parse(selectedDate)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                val selectedDateInMillis = calendar.timeInMillis
                calendarView!!.setDate(selectedDateInMillis, true, true)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // 重新查询数据库以获取最新数据
        GetCurrentMonth()
        adapter!!.setItems(fList, selectedDate)
    }

    internal inner class MyAdapter(
        var context: Context, var pList: ArrayList<MyData>, // 用於保存日期
        var selectedDate: String
    ) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        // 創建新的 ViewHolder，並關聯 item_layout.xml 布局文件
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.listt, parent, false)
            return ViewHolder(view)
        }

        // 綁定數據到 ViewHolder 上
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            // 使用逆序排列的方法，将数据按日期从大到小排序
            Collections.sort(fList, Collections.reverseOrder(java.util.Comparator { item1, item2 ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                try {
                    val date1 = sdf.parse(item1.getDate())
                    val date2 = sdf.parse(item2.getDate())
                    if (date1 != null && date2 != null) {
                        return@Comparator date1.compareTo(date2)
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                0
            }))
            holder.tv_date.text = pList[position].date // 使用存储在数据对象中的日期
            holder.tv_item.text = pList[position].item
        }

        // 返回數據集的大小
        override fun getItemCount(): Int {
            return pList.size
        }

        fun setItems(items: ArrayList<MyData>, selectedDate: String) {
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
                    builder.setTitle("Delete Message")
                    val position = adapterPosition
                    builder.setMessage("Are you sure to delete message of " + pList[position].item + "?")
                    builder.setPositiveButton("Yes") { dialogInterface, i ->
                        val position = adapterPosition
                        val p = pList[position]
                        database!!.deleteMyData(p)
                        pList.removeAt(position)
                        notifyDataSetChanged()
                        dialogInterface.dismiss()
                    }
                    builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
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

        // 获取数据
        fList = database!!.getMyDataByMonth(startDate, endDate)


        // 更新适配器
        adapter!!.setItems(fList, selectedDate)
        recyclerView.smoothScrollToPosition(0) // 滚动到顶部
    }
}
