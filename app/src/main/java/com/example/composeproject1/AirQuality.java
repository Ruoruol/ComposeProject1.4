package com.example.composeproject1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;


public class AirQuality extends AppCompatActivity {

    NavigationView nav_view2;
    DrawerLayout drawer_layout2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_air);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon);



        nav_view2 = findViewById(R.id.nav_view2);
        drawer_layout2 = findViewById(R.id.drawer_layout2);


        // 為navigatin_view設置點擊事件
        nav_view2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawer_layout2.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.bt01) {
                    // 按下「首頁」要做的事
                    Intent it = new Intent(AirQuality.this, Myhome.class);
                    startActivity(it);
                    Toast.makeText(AirQuality.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt02) {

                    Toast.makeText(AirQuality.this, "我的帳號", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt03) {

                    Intent it = new Intent(AirQuality.this, Basic.class);
                    startActivity(it);
                    Toast.makeText(AirQuality.this, "基本資料", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt04) {
                    Intent it = new Intent(AirQuality.this, AirQualityActivity.class);
                    startActivity(it);
                    Toast.makeText(AirQuality.this, "空氣品質", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt05) {
                    Intent it = new Intent(AirQuality.this, HeartSleep.class);
                    startActivity(it);
                    Toast.makeText(AirQuality.this, "血壓紀錄", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt06) {
                    Intent it = new Intent(AirQuality.this, Medications.class);
                    startActivity(it);
                    Toast.makeText(AirQuality.this, "用藥提醒", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.bt07) {
                    Intent it = new Intent(AirQuality.this, MedicationListActivity.class);
                    startActivity(it);
                    Toast.makeText(AirQuality.this, "提醒列表", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout2);
            drawerLayout.openDrawer(GravityCompat.START);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}