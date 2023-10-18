package com.example.composeproject1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Random;

public class HeartSleep extends AppCompatActivity {

    DrawerLayout drawer_layout3;
    NavigationView nav_view3;
    LineChartData lineChartData;
    LineChart lineChart;

    TextView bpm_value;
    ArrayList<String> xData = new ArrayList<>();
    ArrayList<Entry> yData = new ArrayList<>();
    Random random = new Random();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_sleep);

        Toolbar toolbar = findViewById(R.id.toolbar);
        lineChart = findViewById(R.id.bpm_history_chart);
        lineChartData = new LineChartData(lineChart,this);
        bpm_value = findViewById(R.id.bpm_value);

        for(int i = 0;i<5;i++) {
            int randomValue = random.nextInt(31) + 70; // 生成70到100之间的随机整数
            xData.add("第" + (i + 1) + "筆");
            yData.add(new Entry(i, randomValue));
            if (i == 4) {
                bpm_value.setText(String.valueOf(randomValue));
            }
        }

        lineChartData.initX(xData);
        lineChartData.initY(0F,10F);
        lineChartData.initDataSet(yData);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_hs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon);

        drawer_layout3 = findViewById(R.id.drawer_layout3);
        nav_view3 = findViewById(R.id.nav_view3);

        // 為navigatin_view設置點擊事件
        nav_view3.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawer_layout3.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.bt01) {
                    // 按下「首頁」要做的事
                    Intent it = new Intent(HeartSleep.this, Myhome.class);
                    startActivity(it);
                    Toast.makeText(HeartSleep.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt02) {

                    Toast.makeText(HeartSleep.this, "我的帳號", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt03) {
                    Intent it = new Intent(HeartSleep.this, Basic.class);
                    startActivity(it);
                    Toast.makeText(HeartSleep.this, "基本資料", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt04) {
                    Intent it = new Intent(HeartSleep.this, AirQualityActivity.class);
                    startActivity(it);
                    Toast.makeText(HeartSleep.this, "空氣品質", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt05) {
                    Intent it = new Intent(HeartSleep.this, HeartSleep.class);
                    startActivity(it);
                    Toast.makeText(HeartSleep.this, "心律睡眠", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt06) {
                    Intent it = new Intent(HeartSleep.this, Medications.class);
                    startActivity(it);
                    Toast.makeText(HeartSleep.this, "用藥提醒", Toast.LENGTH_SHORT).show();
                    return true;
                }


                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout3);
            drawerLayout.openDrawer(GravityCompat.START);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}