package com.example.composeproject1

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.composeproject1.Medications
import com.example.composeproject1.databinding.ActivityMainBinding
import com.example.composeproject1.model.AppGlobalRepository
import com.example.composeproject1.model.AppGlobalRepository.logout
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.HomeTemplateScreen
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson

class Myhome : AppCompatActivity() {

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i("alarm_receiver_log", "onNewIntent ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_main)
        setContent {
            HomeTemplateScreen(
                topTitle = "首頁",
                defaultIndex = remember {
                    ResourceGlobalRepository.getIndexByName("首頁")
                },
                clickLoginOut = {
                    logout()
                    val intent1 = Intent(this@Myhome, MainActivity::class.java)
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent1)
                    finish()
                }) {
                // 跳轉到登入頁面
                AndroidViewBinding(ActivityMainBinding::inflate) {
                    initView(this)
                }

            }
        }
        val intent = intent
        Log.i("alarm_receiver_log", "onCreate ")
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {
                val title = bundle.getString("title")
                Log.i("alarm_receiver_log", "title $title")
                if (!TextUtils.isEmpty(title)) {
                    val desc = bundle.getString("desc")
                    AlertDialog.Builder(this)
                        .setTitle(title)
                        .setMessage(
                            desc
                        ).setPositiveButton("知道啦") { dialog: DialogInterface?, which: Int -> }
                        .show()
                }
                bundle.clear()
            }
        }

    }

    private fun initView(binding: ActivityMainBinding) {

        binding.btMedications.setOnClickListener {
            startActivity(Intent(this@Myhome, Medications::class.java))
        }
        binding.btAirQuality.setOnClickListener {
            startActivity(Intent(this@Myhome, AirQualityActivity::class.java))
        }
        binding.btHeartSleep.setOnClickListener {
            startActivity(Intent(this@Myhome, BloodPressureActivity::class.java))
        }

        binding.btMedicationsList.setOnClickListener {
            startActivity(Intent(this@Myhome, MedicationListActivity::class.java))
        }
        binding.btHistory.setOnClickListener {
            startActivity(Intent(this@Myhome, CalenderActivity::class.java))
        }
    }

}
