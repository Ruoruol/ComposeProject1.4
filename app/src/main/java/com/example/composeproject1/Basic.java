package com.example.composeproject1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class Basic extends AppCompatActivity {

    EditText et_tall, et_weight, et_BMI;
    Button bt_OK,bt_Cancel;

    Gson gson = new Gson();

    NavigationView nav_view1;
    DrawerLayout drawer_layout1;

    int position = 0;
    String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        et_tall = findViewById(R.id.et_tall);
        et_weight = findViewById(R.id.et_weight);
        et_BMI = findViewById(R.id.et_BMI);

        bt_OK = findViewById(R.id.bt_OK);
        bt_Cancel = findViewById(R.id.bt_Cancel);

        Intent it = getIntent();
        String action = it.getStringExtra("action");


        Toolbar toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_basic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon);


        nav_view1 = findViewById(R.id.nav_view1);
        drawer_layout1 = findViewById(R.id.drawer_layout1);

        // 為navigatin_view設置點擊事件
        nav_view1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawer_layout1.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.bt01) {
                    Intent it = new Intent(Basic.this, Myhome.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt02) {
                    // 按下「使用說明」要做的事
                    Toast.makeText(Basic.this, "我的帳號", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt03) {
                    // 按下「使用說明」要做的事
                    Intent it = new Intent(Basic.this, Basic.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "基本資料", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt04) {
                    Intent it = new Intent(Basic.this, AirQualityActivity.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "空氣品質", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt05) {
                    Intent it = new Intent(Basic.this, HeartSleep.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "心律睡眠", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt06) {
                    Intent it = new Intent(Basic.this, Medications.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "用藥提醒", Toast.LENGTH_SHORT).show();
                    return true;
                }else if (id == R.id.bt07) {
                    Intent it = new Intent(Basic.this, MedicationListActivity.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "提醒列表", Toast.LENGTH_SHORT).show();
                    return true;
                }else if (id == R.id.bt08) {
                    Intent it = new Intent(Basic.this, CalenderActivity.class);
                    startActivity(it);
                    Toast.makeText(Basic.this, "行事曆", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

        if(action.equals(Action.EDIT)) {
            String json = it.getStringExtra("json");
            position = it.getIntExtra("position",-1);
            People p = gson.fromJson(json,People.class);
            et_tall.setText(p.tall);
            et_weight.setText(p.weight);
            et_BMI.setText(p.BMI);
        }

        bt_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tall = et_tall.getText().toString();
                String weight  = et_weight.getText().toString();
                String BMI = et_BMI.getText().toString();
                People p = new People(tall,weight,BMI);
                String json = gson.toJson(p);
                Intent it = new Intent();
                it.putExtra("json",json);
                if(action.equals(Action.EDIT))
                    it.putExtra("position",position);
                setResult(RESULT_OK,it);
                finish();
            }
        });


        Button btCancel = findViewById(R.id.bt_Cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.putExtra("action",Action.CANCEL);
                setResult(RESULT_CANCELED,it);
                finish();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout1);
            drawerLayout.openDrawer(GravityCompat.START);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}