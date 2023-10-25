package com.example.composeproject1

import android.annotation.SuppressLint
import android.app.*
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.lifecycleScope
import com.example.composeproject1.AlarmTimer.TIMER_ACTION
import com.example.composeproject1.databinding.ActivityMedicationsBinding
import com.example.composeproject1.model.Constant.TYPE_MEDICATION
import com.example.composeproject1.model.DatabaseRepository
import com.example.composeproject1.model.ResourceGlobalRepository.getIndexByName
import com.example.composeproject1.ui.WeTemplateScreen
import com.example.composeproject1.utils.ToastUtils
import com.example.composeproject1.utils.requestAlarmPermission
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat


class Medications : AppCompatActivity() {
    private lateinit var binding: ActivityMedicationsBinding

    companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicationsBinding.inflate(layoutInflater)
        setContent {
            WeTemplateScreen(topTitle = "用藥提醒", defaultIndex = remember {
                getIndexByName("用藥提醒")
            }, clickBack = { finish() }) {
                AndroidViewBinding(ActivityMedicationsBinding::inflate) {
                    binding = this
                    binding.submitButton.setOnClickListener { scheduleNotification() }
                }
            }
        }

    }


    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification() {
        val title = binding.titleET.text.toString()
        val desc = binding.messageET.text.toString()
        val time = getTime()

        showAlert(time, title, desc)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        //Log.d("CCC", "Scheduled notification with title: $title, message: $message, time: $time")
        val date = Date(time)
        val dateFormat = DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = DateFormat.getTimeFormat(applicationContext)

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + formatter.format(date)
            )

            .setPositiveButton("確定!") { _, _ ->
                requestAlarmPermission(this) {
                    if (it) {
                        lifecycleScope.launch {
                            val data = DatabaseRepository.createMedicationData(title, message, time)
                            if (data != null) {
                                val bundle = Bundle().apply {
                                    putString("title", title)
                                    putInt("id", data.id)
                                    putString("desc", message)
                                    putLong("time", time)
                                    putInt("type",TYPE_MEDICATION)
                                }
                                val isSuccess = AlarmTimer.setAlarmTimer(
                                    this@Medications,
                                    data.id,
                                    bundle,
                                    time,
                                    TIMER_ACTION,
                                    AlarmManager.RTC_WAKEUP
                                )
                                if (isSuccess) {
                                    Toast.makeText(this@Medications, "新增成功", Toast.LENGTH_SHORT)
                                        .show()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@Medications,
                                        "新增失敗,不能進行通知",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@Medications,
                                    "資料庫新增失敗",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    } else {
                        ToastUtils.shortToast("沒有鬧鐘權限,無法進行提醒")
                    }
                }

            }
            .show()


        Log.d("CCC", "Scheduled notification with title: $title, message: $message, time: $time")
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)
        return calendar.timeInMillis
    }


}