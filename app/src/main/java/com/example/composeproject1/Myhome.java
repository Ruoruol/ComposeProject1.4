package com.example.composeproject1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.composeproject1.model.AppGlobalRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Myhome extends AppCompatActivity {


    Button bt_medications, bt_air_quality, bt_heart_sleep, bt_logout;

    Gson gson = new Gson();

    DrawerLayout drawer_layout;
    NavigationView nav_view;

    RecyclerView rcv;
    RCVAdapter rcvAdapter;


    ArrayList<People> people = new ArrayList<>();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("alarm_receiver_log", "onNewIntent ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        Log.i("alarm_receiver_log", "onCreate ");
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final String title = bundle.getString("title");
                Log.i("alarm_receiver_log", "title " + title);
                if (!TextUtils.isEmpty(title)) {
                    final String desc = bundle.getString("desc");
                    new AlertDialog.Builder(this)
                            .setTitle(title)
                            .setMessage(
                                    desc
                            ).setPositiveButton("知道啦", (dialog, which) -> {

                            }).show();
                }
                bundle.clear();
            }
        }


//        sp = this.getSharedPreferences("username", this.MODE_PRIVATE);
//        textView_name = this.findViewById(R.id.textView_name);
//        textView_name.setText("欢迎你！"+sp.getString("Loginname",""));

        rcv = findViewById(R.id.rcv);

        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcvAdapter = new RCVAdapter(this, people);
        rcv.setAdapter(rcvAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon);

        People a = new People("159", "55", "?");
        people.add(a);


        bt_medications = findViewById(R.id.bt_medications);
        bt_medications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Myhome.this, Medications.class);
                startActivity(it);
            }
        });

        bt_air_quality = findViewById(R.id.bt_air_quality);
        bt_air_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Myhome.this, AirQualityActivity.class);
                startActivity(it);
            }
        });

        bt_heart_sleep = findViewById(R.id.bt_heart_sleep);
        bt_heart_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Myhome.this, HeartSleep.class);
                startActivity(it);
            }
        });

        bt_logout = findViewById(R.id.bt_logout);
        bt_logout.setOnClickListener(view -> {
            AppGlobalRepository.INSTANCE.logout();

            // 跳转到登录页面
            Intent intent1 = new Intent(Myhome.this, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
            finish(); // 结束当前活动
        });

        findViewById(R.id.bt_medications_list).setOnClickListener(v -> {
            Intent it = new Intent(Myhome.this, MedicationListActivity.class);
            startActivity(it);
        });


        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);

        // 為navigatin_view設置點擊事件
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            TextView textView_name;
            SharedPreferences sp;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawer_layout.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.bt01) {
                    // 按下「首頁」要做的事
                    Toast.makeText(Myhome.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt02) {

                    Toast.makeText(Myhome.this, "我的帳號", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt03) {
                    Intent it = new Intent(Myhome.this, Basic.class);
                    startActivity(it);
                    Toast.makeText(Myhome.this, "基本資料", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt04) {
                    Intent it = new Intent(Myhome.this, AirQualityActivity.class);
                    startActivity(it);
                    Toast.makeText(Myhome.this, "空氣品質", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt05) {
                    Intent it = new Intent(Myhome.this, HeartSleep.class);
                    startActivity(it);
                    Toast.makeText(Myhome.this, "心律睡眠", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt06) {
                    Intent it = new Intent(Myhome.this, Medications.class);
                    startActivity(it);
                    Toast.makeText(Myhome.this, "用藥提醒", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt07) {
                    Intent it = new Intent(Myhome.this, MedicationListActivity.class);
                    startActivity(it);
                    Toast.makeText(Myhome.this, "提醒列表", Toast.LENGTH_SHORT).show();
                    return true;
                }


                return false;
            }
        });


    }


    ActivityResultLauncher<Intent> newLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent it = result.getData();
                        String json = it.getStringExtra("json");
                        People p = gson.fromJson(json, People.class);
                        people.add(p);
                        rcvAdapter.notifyDataSetChanged();

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> editLuncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String json = data.getStringExtra("json");
                        int position = data.getIntExtra("position", -1);
                        people.remove(position);
                        people.add(position, gson.fromJson(json, People.class));
                        rcvAdapter.notifyDataSetChanged();
                    }
                }
            });

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.openDrawer(GravityCompat.START);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class RCVAdapter extends RecyclerView.Adapter<RCVAdapter.RCVHolder> {

        Context context;
        ArrayList<People> pList;

        public RCVAdapter(Context c_, ArrayList<People> p_) {
            context = c_;
            pList = p_;
        }


        @NonNull
        @Override
        public RCVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.people_basic, parent, false);
            return new RCVHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull RCVHolder holder, int position) {
            holder.tv_tall.setText(pList.get(position).tall);
            holder.tv_weight.setText(pList.get(position).weight);
            holder.tv_BMI.setText(pList.get(position).BMI);

        }

        @Override
        public int getItemCount() {
            return pList.size();
        }

        public class RCVHolder extends RecyclerView.ViewHolder {
            TextView tv_tall, tv_weight, tv_BMI;

            public RCVHolder(@NonNull View itemView) {
                super(itemView);
                tv_tall = itemView.findViewById(R.id.tv_tall);
                tv_weight = itemView.findViewById(R.id.tv_weight);
                tv_BMI = itemView.findViewById(R.id.tv_BMI);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int position = getAdapterPosition();
                        People p = pList.get(position);
                        String json = gson.toJson(p);
                        Intent it = new Intent(Myhome.this, Basic.class);
                        it.putExtra("json", json);
                        it.putExtra("position", position);
                        it.putExtra("action", Action.EDIT);
                        editLuncher.launch(it);
                    }
                });

            }

        }
    }

}
