package com.example.composeproject1

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.composeproject1.model.Constant.BundleKey.KEY_BUNDLE_BLOOD_PRESSURE_ID
import com.example.composeproject1.model.Constant.BundleKey.KEY_BUNDLE_USER_ID
import com.example.composeproject1.model.DatabaseRepository.getBloodPressureById
import com.example.composeproject1.model.DatabaseRepository.saveBloodPressureData
import com.example.composeproject1.utils.ToastUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class LineChartData : AppCompatActivity() {
    private lateinit var et_high: EditText
    private lateinit var et_low: EditText
    private lateinit var et_hb: EditText
    private lateinit var tv_time: TextView
    private lateinit var spinner: Spinner
    var time = arrayOf("早上", "中午", "晚上")
    private lateinit var bt_save: Button
    private lateinit var bt_Cancel: Button
    private lateinit var bt_date: Button
    private val gson = Gson()
    private val calendar = Calendar.getInstance()
    var selectedDate: Long = 0
    var action: String? = ""
    var id = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linechart)

        // 初始化视图组件
        et_high = findViewById(R.id.et_high)
        et_low = findViewById(R.id.et_low)
        et_hb = findViewById(R.id.et_hb)
        spinner = findViewById(R.id.spinner)
        bt_save = findViewById(R.id.bt_save)
        bt_Cancel = findViewById(R.id.bt_Cancel)
        bt_date = findViewById(R.id.bt_date)
        tv_time = findViewById(R.id.tv_time)
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.sp_time, time)
        spinner.setAdapter(adapter)
        var user_id = intent.getLongExtra(KEY_BUNDLE_USER_ID, -1)
        val bloodPressureId = intent.getLongExtra(KEY_BUNDLE_BLOOD_PRESSURE_ID, -1)
        if (bloodPressureId != -1L) {
            lifecycleScope.launchWhenResumed {
                bt_date.isEnabled = false
                spinner.isEnabled = false
                getBloodPressureById(bloodPressureId)?.let {
                    withContext(Dispatchers.Main) {
                        et_high.setText(it.bloodPressureHigh.toString())
                        et_low.setText(it.bloodPressureLow.toString())
                        et_hb.setText(it.heartBeat.toString())
                        spinner.setSelection(it.bloodPressureDayDesc)
                        selectedDate = it.bloodPressureTime
                        tv_time.text = getFormatString(it.bloodPressureTime)
                    }
                }
            }
        } else {
            selectedDate = currentDate
        }
        val it = intent
        action = it.getStringExtra("action")
        if (action == Action.EDIT) {
            val json = it.getStringExtra("json")
            val p = gson.fromJson(json, MyData::class.java)
            id = p.id
            user_id = p.user_id // 獲取用户的 ID
            spinner.getSelectedItem()
            et_high.setText(p.high)
            et_low.setText(p.low)
            et_hb.setText(p.hb)
            tv_time.setText(p.date)
        }


        // 设置保存按钮的点击事件
        val finalUser_id = user_id
        bt_save.setOnClickListener {
            val high = et_high.text.toString()
            val low = et_low.text.toString()
            val heartRate = et_hb.text.toString()
            if (high.isEmpty() || low.isEmpty() || heartRate.isEmpty()) {
                ToastUtils.shortToast("请输入完整信息")
                return@setOnClickListener
            }
            val item = spinner.selectedItem.toString()
            if (action == Action.NEW) {
                id = -1
            }
            val desc = when (item) {
                "早上" -> 0
                "中午" -> 1
                "晚上" -> 2
                else -> -1
            }
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selectedDate
                set(Calendar.HOUR_OF_DAY, if (desc == 0) 8 else if (desc == 1) 12 else 18)
            }
            saveBloodPressureData(
                high,
                low,
                heartRate,
                desc,
                calendar.timeInMillis,
                finalUser_id
            )
            finish()
        }
        bt_Cancel.setOnClickListener {
            val it = Intent()
            it.putExtra("action", Action.CANCEL)
            setResult(RESULT_CANCELED, it)
            finish()
        }
        bt_date.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@LineChartData,
                object : OnDateSetListener {
                    override fun onDateSet(
                        datePicker: DatePicker,
                        year: Int,
                        month: Int,
                        day: Int
                    ) {
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)
                        selectedDate = currentDate
                        tv_time.setText(getFormatString(selectedDate))
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private val currentDate: Long
        private get() {
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]

            // 獲取星期
            val calendar = Calendar.getInstance()
            calendar[year, month - 1] = day
            return calendar.timeInMillis
        }

    private fun getFormatString(time: Long): String {
        calendar.timeInMillis = time
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] + 1 // 0 ~ 11
        val day = calendar[Calendar.DAY_OF_MONTH]
        val dayOfWeekString = getDayOfWeekString(day)
        return String.format("%04d-%02d-%02d (%s)", year, month, day, dayOfWeekString)
    }

    private fun getDayOfWeekString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "星期日"
            Calendar.MONDAY -> "星期一"
            Calendar.TUESDAY -> "星期二"
            Calendar.WEDNESDAY -> "星期三"
            Calendar.THURSDAY -> "星期四"
            Calendar.FRIDAY -> "星期五"
            Calendar.SATURDAY -> "星期六"
            else -> ""
        }
    }
}