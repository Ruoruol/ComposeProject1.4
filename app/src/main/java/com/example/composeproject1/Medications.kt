package com.example.composeproject1

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.composeproject1.AlarmTimer.TIMER_ACTION
import com.example.composeproject1.databinding.ActivityMedicationsBinding
import com.example.composeproject1.model.DatabaseRepository
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat


class Medications : AppCompatActivity() {
    private lateinit var binding: ActivityMedicationsBinding
    private lateinit var drawer_layout4: DrawerLayout
    private lateinit var nav_view4: NavigationView

    companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitButton.setOnClickListener { scheduleNotification() }

        /** setContentView(R.layout.activity_medications) */
        //        Appbar建立標題
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.toolbar_m)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icon)
        drawer_layout4 = findViewById(R.id.drawer_layout4);
        nav_view4 = findViewById(R.id.nav_view4);

        // 為navigatin_view設置點擊事件
        nav_view4.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item -> // 點選時收起選單
            drawer_layout4.closeDrawer(GravityCompat.START)

            // 取得選項id
            val id = item.itemId

            // 依照id判斷點了哪個項目並做相應事件
            val b = when (id) {
                R.id.bt01 -> {
                    // 按下「首頁」要做的事
                    val it = Intent(this@Medications, Myhome::class.java)
                    startActivity(it)
                    Toast.makeText(this@Medications, "首頁", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bt02 -> {
                    Toast.makeText(this@Medications, "我的帳號", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bt03 -> {
                    val it = Intent(this@Medications, Basic::class.java)
                    startActivity(it)
                    Toast.makeText(this@Medications, "基本資料", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bt04 -> {
                    val it = Intent(this@Medications, AirQualityActivity::class.java)
                    startActivity(it)
                    Toast.makeText(this@Medications, "空氣品質", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bt05 -> {
                    val it = Intent(this@Medications, HeartSleep::class.java)
                    startActivity(it)
                    Toast.makeText(this@Medications, "心律睡眠", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bt06 -> {
                    val it = Intent(this@Medications, Medications::class.java)
                    startActivity(it)
                    Toast.makeText(this@Medications, "用藥提醒", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.bt07 -> {
                    val it = Intent(this@Medications, MedicationListActivity::class.java)
                    startActivity(it)
                    Toast.makeText(this@Medications, "提醒列表", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }

                else -> false
            }
            b
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout4)
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
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
                lifecycleScope.launch {
                    val data = DatabaseRepository.createMedicationData(title, message, time)
                    if (data != null) {
                        val isSuccess = AlarmTimer.setAlarmTimer(
                            this@Medications,
                            data.id,
                            title,
                            message,
                            time,
                            TIMER_ACTION,
                            AlarmManager.RTC_WAKEUP
                        )
                        if (isSuccess) {
                            Toast.makeText(this@Medications, "新增成功", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@Medications,
                                "新增失敗,不能进行通知",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@Medications, "新增失敗", Toast.LENGTH_SHORT).show()
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
        calendar.set(year, month, day, hour, minute,0)
        return calendar.timeInMillis
    }


}