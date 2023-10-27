package com.example.composeproject1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BloodPressure extends AppCompatActivity{




    String DB = "bb";
    Button bt_add;
    RecyclerView rcv;
    Gson gson = new Gson();
    DBHelper database;
    RCVAdapter rcvAdapter;
    ArrayList<MyData> fList = new ArrayList<>();
    LineChart lineChart;
    DatePicker datePicker;
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_sleep);

        rcv = findViewById(R.id.rcv_list);
        bt_add = findViewById(R.id.bt_add);
        lineChart = findViewById(R.id.lineChart);
        calendarView = findViewById(R.id.calendarView);
        long userId = getIntent().getLongExtra("EXTRA_USER_ID", -1);// -1是預設值，表示沒有傳遞學生ID時的情況

        database = new DBHelper(this);
        fList = database.getAllHBData();

        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcvAdapter = new RCVAdapter(this, fList);
        rcv.setAdapter(rcvAdapter);

        initializeLineChart();

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(BloodPressure.this, LineChartData.class);
                it.putExtra("action", Action.NEW);
                // 将用户的 ID 传递给 linechart 页面
                it.putExtra("user_id", userId);



                newLauncher.launch(it);
            }
        });


        // 监听日历选择事件
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 计算日期范围
                Calendar startDate = Calendar.getInstance();
                startDate.set(year, month, dayOfMonth, 0, 0, 0); // 设置时间为当天的开始时间

                Calendar endDate = Calendar.getInstance();
                endDate.set(year, month, dayOfMonth, 23, 59, 59); // 设置时间为当天的结束时间

                fList = database.getHBDataByMonth(startDate, endDate,userId);
                rcvAdapter.pList = fList;
                rcvAdapter.notifyDataSetChanged();

                // 执行数据库查询
                List<MyData> data = database.getHBDataByMonth(startDate, endDate,userId);
                // 转换数据格式
                Pair<List<Entry>, List<Entry>> entryPair = convertDataToEntries(data);
                // 更新折线图
                updateLineChart(entryPair.first, entryPair.second);
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
                        MyData p = gson.fromJson(json, MyData.class);
                        database.addHBData(p.date, p.high, p.low, p.hb, p.item,p.user_id);
                        // 获取新数据的日期，并设置为选定日期
                        String newDataDate = p.date; // 假设日期存储在 MyData 对象的 date 字段中
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date = dateFormat.parse(newDataDate);
                            long newDateInMillis = date.getTime();
                            calendarView.setDate(newDateInMillis);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // 更新折线图和列表
                        updateLineChartForCurrentMonth();
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String json = data.getStringExtra("json");
                        MyData p = gson.fromJson(json, MyData.class);
                        database.updateHBData(p);
                        rcvAdapter.pList = database.getAllHBData();
                        rcvAdapter.notifyDataSetChanged();
                        updateLineChartForCurrentMonth();
                    }
                }
            });

    class RCVAdapter extends RecyclerView.Adapter<RCVAdapter.RCVHolder> {
        Context context;
        ArrayList<MyData> pList;

        public RCVAdapter(Context c_, ArrayList<MyData> p_) {
            context = c_;
            pList = p_;
        }

        @NonNull
        @Override
        public RCVAdapter.RCVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
            return new RCVHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RCVAdapter.RCVHolder holder, int position) {

            // 在这里对数据进行排序
            Collections.sort(pList, new Comparator<MyData>() {
                @Override
                public int compare(MyData data1, MyData data2) {
                    // 假设 MyData 中有一个表示日期的字段叫做 date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date1 = sdf.parse(data1.date);
                        Date date2 = sdf.parse(data2.date);
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            holder.tv_date.setText(pList.get(position).date);
            holder.tv_item.setText(pList.get(position).item);
            holder.tv_h.setText(pList.get(position).high);
            holder.tv_l.setText(pList.get(position).low);
            holder.tv_hb.setText(pList.get(position).hb);
        }

        @Override
        public int getItemCount() {
            return pList.size();
        }

        public class RCVHolder extends RecyclerView.ViewHolder {
            TextView tv_date, tv_item, tv_h, tv_l, tv_hb;

            public RCVHolder(@NonNull View itemView) {
                super(itemView);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_item = itemView.findViewById(R.id.tv_time);
                tv_h = itemView.findViewById(R.id.tv_h);
                tv_l = itemView.findViewById(R.id.tv_l);
                tv_hb = itemView.findViewById(R.id.tv_hb);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        MyData p = pList.get(position);
                        String json = gson.toJson(p);
                        Intent it = new Intent(BloodPressure.this, LineChartData.class);
                        it.putExtra("json", json);
                        it.putExtra("action", Action.EDIT);
                        editLauncher.launch(it);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BloodPressure.this);
                        builder.setTitle("Delete Message");
                        int position = getAdapterPosition();
                        builder.setMessage("Are you sure to delete message of " + pList.get(position).item + "?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position = getAdapterPosition();
                                MyData p = pList.get(position);
                                database.deleteHBData(p);
                                pList.remove(position);
                                notifyDataSetChanged();
                                dialogInterface.dismiss();
                                updateLineChartForCurrentMonth();
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

    private void initializeLineChart() {
        // 设置折线图的描述
        Description description = new Description();
        description.setText("血壓趋势");
        lineChart.setDescription(description);
        lineChart.setDragEnabled(true); // 允许拖动
        lineChart.setScaleEnabled(true); // 允许缩放
        lineChart.setPinchZoom(true); // 启用缩放手势


        // 获取X轴和Y轴对象
        XAxis xAxis = lineChart.getXAxis();
        YAxis leftYAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        List<MyData> data = new ArrayList<>();
        data = database.getAllHBData();

        // 设置X轴标签的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 自定义X轴标签的间隔
        xAxis.setLabelCount(data.size(), true); // 设置标签数量为数据点的数量
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 假设X轴的值为日期的时间戳（毫秒）
                long timestamp = (long) value;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
                return dateFormat.format(calendar.getTime());
            }
        });

        // 自定义收缩压线的样式
        LineDataSet systolicDataSet = new LineDataSet(new ArrayList<>(), "收缩壓");
        systolicDataSet.setColor(Color.RED);
        systolicDataSet.setCircleColor(Color.RED);

        // 自定义舒张压线的样式
        LineDataSet diastolicDataSet = new LineDataSet(new ArrayList<>(), "舒张壓");
        diastolicDataSet.setColor(Color.BLUE);
        diastolicDataSet.setCircleColor(Color.BLUE);

        // 创建LineData对象并设置数据
        LineData lineData = new LineData(systolicDataSet, diastolicDataSet);
        lineChart.setData(lineData);

        // 添加Y轴的限制线，例如正常范围
        LimitLine normalSystolicRange = new LimitLine(120f, "正常范围");
        normalSystolicRange.setLineColor(Color.GREEN);
        normalSystolicRange.setLineWidth(2f);
        leftYAxis.addLimitLine(normalSystolicRange);

        LimitLine normalDiastolicRange = new LimitLine(80f, "正常范围");
        normalDiastolicRange.setLineColor(Color.GREEN);
        normalDiastolicRange.setLineWidth(2f);
        leftYAxis.addLimitLine(normalDiastolicRange);

        lineChart.invalidate();
    }

    private Pair<List<Entry>, List<Entry>> convertDataToEntries(List<MyData> data) {
        // 将数据库中的数据转换为Entry对象的列表
        List<Entry> systolicEntries = new ArrayList<>();
        List<Entry> diastolicEntries = new ArrayList<>();

        for (MyData item : data) {
            // 假设MyData对象中包含日期、高压和低压数据
            // 将日期的时间戳作为X轴值
            long timestamp = getTimestampFromStringDate(item.getDate());
            float highPressure = Float.parseFloat(item.getHighPressure());
            float lowPressure = Float.parseFloat(item.getLowPressure());

            systolicEntries.add(new Entry(timestamp, highPressure));
            diastolicEntries.add(new Entry(timestamp, lowPressure));
        }

        // 按日期排序
        Collections.sort(systolicEntries, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                return Float.compare(e1.getX(), e2.getX());
            }
        });

        Collections.sort(diastolicEntries, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                return Float.compare(e1.getX(), e2.getX());
            }
        });

        return new Pair<>(systolicEntries, diastolicEntries);
    }

    private long getTimestampFromStringDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void updateLineChart(List<Entry> systolicEntries, List<Entry> diastolicEntries) {
        LineData lineData = lineChart.getLineData();
        LineDataSet systolicDataSet = (LineDataSet) lineData.getDataSetByIndex(0);
        LineDataSet diastolicDataSet = (LineDataSet) lineData.getDataSetByIndex(1);

        if (systolicDataSet == null) {
            systolicDataSet = new LineDataSet(systolicEntries, "收缩壓");
            systolicDataSet.setColor(Color.RED);
            systolicDataSet.setCircleColor(Color.RED);
            systolicDataSet.setLabel("收缩壓");
            lineData.addDataSet(systolicDataSet);
        } else {
            systolicDataSet.setValues(systolicEntries);
        }

        if (diastolicDataSet == null) {
            diastolicDataSet = new LineDataSet(diastolicEntries, "舒张壓");
            diastolicDataSet.setColor(Color.BLUE);
            diastolicDataSet.setCircleColor(Color.BLUE);
            diastolicDataSet.setLabel("舒张壓");
            lineData.addDataSet(diastolicDataSet);
        } else {
            diastolicDataSet.setValues(diastolicEntries);
        }

        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void updateLineChartForCurrentMonth() {
        // 获取当前选定的日期
        long selectedDate = calendarView.getDate();
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(selectedDate);

        // 计算当前选定月份的开始和结束日期
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(selectedDate);
        startDate.set(Calendar.DAY_OF_MONTH, 1); // 设置为当前月份的第一天
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(selectedDate);
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH)); // 设置为当前月份的最后一天
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        long userId = getIntent().getLongExtra("EXTRA_USER_ID", -1);

        fList = database.getHBDataByMonth(startDate, endDate,userId);
        rcvAdapter.pList = fList;
        rcvAdapter.notifyDataSetChanged();

        // 执行数据库查询
        List<MyData> data = database.getHBDataByMonth(startDate, endDate,userId);

        // 转换数据格式
        Pair<List<Entry>, List<Entry>> entryPair = convertDataToEntries(data);

        // 更新折线图
        updateLineChart(entryPair.first, entryPair.second);
    }

}
