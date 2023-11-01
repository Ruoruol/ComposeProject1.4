package com.example.composeproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.composeproject1.model.DatabaseRepository;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;

public class LineChartData extends AppCompatActivity {

    EditText et_high, et_low, et_hb;
    TextView tv_time;

    Spinner spinner;
    String[] time = {"早上", "中午", "晚上"};

    Button bt_save, bt_Cancel, bt_date;
    Gson gson = new Gson();
    Calendar calendar;
    long selectedDate;
    String action = "";
    int id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);

        // 初始化视图组件
        et_high = findViewById(R.id.et_high);
        et_low = findViewById(R.id.et_low);
        et_hb = findViewById(R.id.et_hb);
        spinner = findViewById(R.id.spinner);
        bt_save = findViewById(R.id.bt_save);
        bt_Cancel = findViewById(R.id.bt_Cancel);
        bt_date = findViewById(R.id.bt_date);
        tv_time = findViewById(R.id.tv_time);
        long user_id = getIntent().getLongExtra("user_id", -1);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.sp_time, time);
        spinner.setAdapter(adapter);

        Intent it = getIntent();
        action = it.getStringExtra("action");
        if (action.equals(Action.EDIT)) {
            String json = it.getStringExtra("json");
            MyData p = gson.fromJson(json, MyData.class);
            id = p.id;
            user_id = p.user_id;  // 获取用户的 ID
            spinner.getSelectedItem();
            et_high.setText(p.high);
            et_low.setText(p.low);
            et_hb.setText(p.hb);
            tv_time.setText(p.date);
        }


        // 设置保存按钮的点击事件
        long finalUser_id = user_id;
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String high = et_high.getText().toString();
                String low = et_low.getText().toString();
                String heartRate = et_hb.getText().toString();
                String item = spinner.getSelectedItem().toString();
                if (action.equals(Action.NEW)) {
                    id = -1;
                }

                int desc = switch (item) {
                    case "早上" -> 0;
                    case "中午" -> 1;
                    case "晚上" -> 2;
                    default -> -1;
                };

                DatabaseRepository.INSTANCE.saveBloodPressureData(
                        high,
                        low,
                        heartRate,
                        desc,
                        selectedDate,
                        finalUser_id
                );

                finish();
            }
        });


        bt_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.putExtra("action", Action.CANCEL);
                setResult(RESULT_CANCELED, it);
                finish();
            }
        });


        calendar = Calendar.getInstance();
        selectedDate = getCurrentDate();


        bt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(LineChartData.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                selectedDate = getCurrentDate();
                                tv_time.setText(getFormatString(selectedDate));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }

    private long getCurrentDate() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 獲取星期
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTimeInMillis();
    }

    private String getFormatString(long time) {
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 0 ~ 11
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayOfWeekString = getDayOfWeekString(day);
        return String.format("%04d-%02d-%02d (%s)", year, month, day, dayOfWeekString);
    }

    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "星期日";
            case Calendar.MONDAY:
                return "星期一";
            case Calendar.TUESDAY:
                return "星期二";
            case Calendar.WEDNESDAY:
                return "星期三";
            case Calendar.THURSDAY:
                return "星期四";
            case Calendar.FRIDAY:
                return "星期五";
            case Calendar.SATURDAY:
                return "星期六";
            default:
                return "";
        }

    }

}