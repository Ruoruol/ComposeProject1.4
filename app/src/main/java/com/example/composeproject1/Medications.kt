package com.example.composeproject1

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import com.example.composeproject1.AlarmTimer.TIMER_ACTION
import com.example.composeproject1.databinding.ActivityMedicationsBinding
import com.example.composeproject1.model.Constant.TYPE_MEDICATION
import com.example.composeproject1.model.DataRepository
import com.example.composeproject1.model.DatabaseRepository
import com.example.composeproject1.model.ResourceGlobalRepository.getIndexByName
import com.example.composeproject1.ui.WeTemplateScreen
import com.example.composeproject1.ui.theme.PrimaryColor
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
                var isShow by remember {
                    mutableStateOf(false)
                }
                ShowSelectMedication(isShow, {
                    isShow = false
                }) {
                    binding.messageET.setText(it)
                }
                AndroidViewBinding(ActivityMedicationsBinding::inflate) {
                    binding = this
                    binding.submitButton.setOnClickListener { scheduleNotification() }
                    this.bnSelectMediaction.setOnClickListener {
                        isShow = true
                    }
                    bnA.setOnClickListener {
                        changeCount(true, count)
                    }
                    bnS.setOnClickListener {
                        changeCount(false, count)
                    }
                }
            }
        }

    }

    private fun changeCount(isAdd: Boolean, editText: TextView) {
        val before = editText.text.toString().toIntOrNull() ?: 1
        editText.text = kotlin.math.max(if (isAdd) before + 1 else before - 1, 1).toString()
    }

    @Composable
    fun ShowSelectMedication(isShow: Boolean, dismiss: () -> Unit, selectFunc: (String) -> Unit) {
        val data = DataRepository.getMedicationList()
        if (isShow) {
            Dialog(
                onDismissRequest = {
                    dismiss.invoke()
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            PrimaryColor
                        )
                ) {
                    items(data) {
                        Text(text = it,
                            color = Color.Black,
                            fontSize = 25.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .background(
                                    Color(App.appContext.getColor(R.color.bt_color))
                                )
                                .padding(12.dp)
                                .clickable {
                                    dismiss.invoke()
                                    selectFunc.invoke(it)
                                }
                                .wrapContentSize()
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification() {
        val title = binding.titleET.text.toString()
        val desc = binding.messageET.text.toString()
        val time = getTime()

        showAlert(
            time,
            title,
            if (binding.loopSwitch.isChecked) Int.MAX_VALUE else binding.count.text.toString()
                .toIntOrNull() ?: 1,
            desc
        )
    }

    private fun showAlert(time: Long, title: String, count: Int, message: String) {
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
                            val data =
                                DatabaseRepository.createMedicationData(title, message, count, time)
                            if (data != null) {
                                val bundle = Bundle().apply {
                                    putString("title", title)
                                    putInt("id", data.id)
                                    putString("desc", message)
                                    putLong("time", time)
                                    putInt("type", TYPE_MEDICATION)
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
                                    val intent =
                                        Intent(this@Medications, MedicationListActivity::class.java)
                                    startActivity(intent)
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