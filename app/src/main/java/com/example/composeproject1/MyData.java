package com.example.composeproject1;

public class MyData {
    int id;
    long user_id;
    String date,high,low,hb,item;

    public MyData(int id, String date, String high, String low, String hb, String item, long userId) {
        this.id = id;
        this.date = date;
        this.high = high;
        this.low = low;
        this.hb = hb;
        this.item = item;
        this.user_id = userId; // 将用户 ID 设置为类的成员变量
    }

    public String getHighPressure() {
        return high;
    }
    public String getLowPressure() {
        return low;
    }
    public String getDate() {
        return date;
    }
}
