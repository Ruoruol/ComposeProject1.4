package com.example.composeproject1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HDB.db";
    private static final int DATABASE_VERSION = 1;
    // 表格名稱
    private static final String TABLE_USERS = "users";
    private static final String TABLE_HEARTB = "heartb";


    // user表格欄位
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "usname";
    private static final String COLUMN_USER_PASSWORD = "uspassword";

    // heartbeat格欄位
    private static final String HB_ID = "id";
    private static final String HB_DATE = "hb_date";
    private static final String HB_HIGH = "hb_high";
    private static final String HB_LOW = "hb_low";
    private static final String HB = "hb";
    private static final String HB_ITEM = "hb_item";
    private static final String USER_ID = "user_id";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT"
            + ")";

    // 建立成績表格的SQL指令，並加入學生表格的外鍵關聯
    private static final String CREATE_TABLE_HEARTB = "CREATE TABLE " + TABLE_HEARTB + "("
            + HB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + HB_DATE + " TEXT,"
            + HB_HIGH + " TEXT,"
            + HB_LOW + " TEXT,"
            + HB + " TEXT,"
            + HB_ITEM + " TEXT,"
            + USER_ID + " INTEGER," // 新增用於外鍵關聯的欄位
            + "FOREIGN KEY(" + USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_HEARTB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 當資料庫版本變更時，處理升級邏輯
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEARTB);

        onCreate(db);
    }

    void addHBData(String date, String high, String low, String hb,String item,long userID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HB_DATE, date);
        values.put(HB_HIGH, high);
        values.put(HB_LOW, low);
        values.put(HB, hb);
        values.put(HB_ITEM, item);
        values.put(USER_ID, userID);

        db.insert(TABLE_HEARTB, null, values);
    }

    public ArrayList<MyData> getAllHBData() {
        ArrayList<MyData> peopleList = new ArrayList<MyData>();

        String selectQuery = "SELECT  * FROM " + TABLE_HEARTB;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MyData p = new MyData(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        Long.parseLong(cursor.getString(6))
                );
                peopleList.add(p);
            } while (cursor.moveToNext());
        }
        return peopleList;
    }

    public int updateHBData(MyData mydata) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HB_ID, mydata.id);
        values.put(HB_DATE, mydata.date);
        values.put(HB_HIGH, mydata.high);
        values.put(HB_LOW, mydata.low);
        values.put(HB, mydata.hb);
        values.put(HB_ITEM, mydata.item);

        return db.update(TABLE_HEARTB, values, HB_ID + " = ?",
                new String[] { String.valueOf(mydata.id) });
    }

    // 刪除
    public void deleteHBData(MyData mydata) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HEARTB, HB_ID + " = ?",
                new String[]{String.valueOf(mydata.id)});

    }
    public ArrayList<MyData> getHBDataByMonth(Calendar year, Calendar month,long user_id) {
        ArrayList<MyData> dataList = new ArrayList<>();

        int yearValue = year.get(Calendar.YEAR);
        int monthValue = month.get(Calendar.MONTH) + 1; // Calendar 中的月份是从 0 开始的，所以要加 1


        // 构建查询条件
        String startDate = String.format("%04d-%02d-01", yearValue, monthValue);
        String endDate = String.format("%04d-%02d-31", yearValue, monthValue);

        String selectQuery = "SELECT * FROM " + TABLE_HEARTB +
                " WHERE " + USER_ID + " = " + user_id +
                " AND " + HB_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MyData p = new MyData(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        Long.parseLong(cursor.getString(6))
                );
                dataList.add(p);
            } while (cursor.moveToNext());
        }

        cursor.close();


        return dataList;
    }
}

