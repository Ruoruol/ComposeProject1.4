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
import com.example.composeproject1.Myhome.RCVAdapter.RCVHolder
import com.example.composeproject1.databinding.ActivityMainBinding
import com.example.composeproject1.model.AppGlobalRepository
import com.example.composeproject1.model.AppGlobalRepository.logout
import com.example.composeproject1.model.ResourceGlobalRepository
import com.example.composeproject1.ui.HomeTemplateScreen
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson

class Myhome : AppCompatActivity() {
    private val gson = Gson()
    private lateinit var rcvAdapter: RCVAdapter
    private val people = ArrayList<People>()
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
                // 跳转到登录页面
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
        binding.rcv.layoutManager = LinearLayoutManager(this)
        val rcvAdapter = RCVAdapter(this, people)
        binding.rcv.adapter = rcvAdapter
        val a = People("159", "55", "?")
        people.add(a)
        binding.btMedications.setOnClickListener {
            startActivity(Intent(this@Myhome, Medications::class.java))
        }
        binding.btAirQuality.setOnClickListener {
            startActivity(Intent(this@Myhome, AirQualityActivity::class.java))
        }
        binding.btHeartSleep.setOnClickListener {
            startActivity(Intent(this@Myhome, CalenderActivity::class.java))
        }

        binding.btMedicationsList.setOnClickListener {
            startActivity(Intent(this@Myhome, MedicationListActivity::class.java))
        }
    }

    var newLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult?) {
                result ?: return
                if (result.resultCode == RESULT_OK) {
                    val it = result.data
                    val json = it!!.getStringExtra("json")
                    val p = gson.fromJson(json, People::class.java)
                    people.add(p)
                    rcvAdapter!!.notifyDataSetChanged()
                }
            }

        }
    )
    var editLuncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult?) {
                result ?: return
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val json = data!!.getStringExtra("json")
                    val position = data.getIntExtra("position", -1)
                    people.removeAt(position)
                    people.add(position, gson.fromJson(json, People::class.java))
                    rcvAdapter.notifyDataSetChanged()
                }
            }
        })


    inner class RCVAdapter(var context: Context, val pList: ArrayList<People>) :
        RecyclerView.Adapter<RCVHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.people_basic, parent, false)
            return RCVHolder(view)
        }

        override fun onBindViewHolder(holder: RCVHolder, position: Int) {
            holder.tv_tall.text = pList[position].tall
            holder.tv_weight.text = pList[position].weight
            holder.tv_BMI.text = pList[position].BMI
        }

        override fun getItemCount(): Int {
            return pList.size
        }

        inner class RCVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_tall: TextView
            var tv_weight: TextView
            var tv_BMI: TextView

            init {
                tv_tall = itemView.findViewById(R.id.tv_tall)
                tv_weight = itemView.findViewById(R.id.tv_weight)
                tv_BMI = itemView.findViewById(R.id.tv_BMI)
                itemView.setOnClickListener {
                    val position = adapterPosition
                    val p = pList[position]
                    val json = gson.toJson(p)
                    val it = Intent(this@Myhome, Basic::class.java)
                    it.putExtra("json", json)
                    it.putExtra("position", position)
                    it.putExtra("action", Action.EDIT)
                    editLuncher.launch(it)
                }
            }
        }
    }
}
