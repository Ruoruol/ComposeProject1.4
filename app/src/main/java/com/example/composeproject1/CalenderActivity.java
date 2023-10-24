package com.example.composeproject1;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button bt1, bt2, bt3;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> itemList;

    Gson gson = new Gson();
    ArrayList<MyData> fList = new ArrayList<>();

    SQLiteDatabaseHelper database;
    private String selectedDate = ""; // 用於保存日期

    String DB = "planB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendarView = findViewById(R.id.calendarView);
        bt1 = findViewById(R.id.bt_follow_up);
        bt2 = findViewById(R.id.bt_receive_medicine);
        bt3 = findViewById(R.id.bt_rehabilitation);
        recyclerView = findViewById(R.id.rcv_list);

        database = new SQLiteDatabaseHelper(this, DB, null, 1);
        itemList = new ArrayList<>();
        adapter = new MyAdapter(this, fList, selectedDate);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 設置 CalendarView 的日期選擇監聽器
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 直接更新外部的 selectedDate 变量
                selectedDate = formatDate(year, month, dayOfMonth);

                Calendar startDate = Calendar.getInstance();
                startDate.set(year, month, dayOfMonth, 0, 0, 0); // 设置时间为当天的开始时间

                Calendar endDate = Calendar.getInstance();
                endDate.set(year, month, dayOfMonth, 23, 59, 59); // 设置时间为当天的结束时间

                fList = database.getMyDataByMonth(startDate, endDate);
                adapter.pList = fList;
                adapter.notifyDataSetChanged();
            }
        });
        // 根據按鈕點擊情況添加日期和按鈕文字
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentType = "回診"; // 預約類型為 "回診"
                addItem(appointmentType , selectedDate);
                GetCurrentMonth();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentType = "拿藥"; // 預約類型為 "回診"
                addItem(appointmentType , selectedDate);
                GetCurrentMonth();
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentType = "復健"; // 預約類型為 "回診"
                addItem(appointmentType , selectedDate);
                GetCurrentMonth();
            }
        });
    }

    private void addItem(String newItem,String selectedDate) {
        itemList.add(newItem);

        // 添加项目到数据库
        database.addMyData(selectedDate, newItem);

        // 設定 CalendarView 的選擇日期以跳轉到相關的月份
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        try {
            date = dateFormat.parse(selectedDate);
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                long selectedDateInMillis = calendar.getTimeInMillis();
                calendarView.setDate(selectedDateInMillis, true, true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 重新查询数据库以获取最新数据
        GetCurrentMonth();
        adapter.setItems(fList, selectedDate);

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        Context context;
        ArrayList<MyData> pList;
        String selectedDate; // 用於保存日期

        public MyAdapter(Context c_, ArrayList<MyData> p_, String selectedDate) {
            context = c_;
            pList = p_;
            this.selectedDate = selectedDate;
        }

        // 創建新的 ViewHolder，並關聯 item_layout.xml 布局文件
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listt, parent, false);
            return new ViewHolder(view);
        }

        // 綁定數據到 ViewHolder 上
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            // 使用逆序排列的方法，将数据按日期从大到小排序
            Collections.sort(fList, Collections.reverseOrder(new Comparator<MyData>() {
                @Override
                public int compare(MyData item1, MyData item2) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date date1 = sdf.parse(item1.getDate());
                        Date date2 = sdf.parse(item2.getDate());
                        if (date1 != null && date2 != null) {
                            return date1.compareTo(date2);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            }));

            holder.tv_date.setText(pList.get(position).date); // 使用存储在数据对象中的日期
            holder.tv_item.setText(pList.get(position).item);
        }

        // 返回數據集的大小
        @Override
        public int getItemCount() {
            return pList.size();
        }

        public void setItems(ArrayList<MyData> items, String selectedDate) {
            pList = items;
            this.selectedDate = selectedDate;
            notifyDataSetChanged();
        }

        // 定義 ViewHolder 類別
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_date,tv_item;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_item = itemView.findViewById(R.id.tv_item);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CalenderActivity.this);
                        builder.setTitle("Delete Message");
                        int position = getAdapterPosition();
                        builder.setMessage("Are you sure to delete message of " + pList.get(position).item + "?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position = getAdapterPosition();
                                MyData p = pList.get(position);
                                database.deleteMyData(p);
                                pList.remove(position);
                                notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    }
                });
            }
        }
    }


    // 將日期格式化為字符串
    private String formatDate(int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(year - 1900, month, dayOfMonth); // 注意月份需要減1
        return sdf.format(date);
    }

    private void GetCurrentMonth() {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(calendarView.getDate());

        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);

        Calendar startDate = Calendar.getInstance();
        startDate.set(year, month, 1, 0, 0, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.set(year, month, selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

        // 获取数据
        fList = database.getMyDataByMonth(startDate, endDate);


        // 更新适配器
        adapter.setItems(fList, selectedDate);
        recyclerView.smoothScrollToPosition(0); // 滚动到顶部
    }


}
