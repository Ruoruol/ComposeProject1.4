package com.example.composeproject1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_C = "cc";
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String ITEM = "item";

    public SQLiteDatabaseHelper(@Nullable Context context, @Nullable String low, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, low, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_Add_TABLE = "CREATE TABLE if not exists "
                + TABLE_C + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE + " TEXT, "
                + ITEM +" TEXT )";
        db.execSQL(CREATE_Add_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String drop = "DROP TABLE IF EXISTS " + TABLE_C;
        db.execSQL(drop);

    }

    void addMyData(String date,String item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DATE, date);

        values.put(ITEM, item);

        db.insert(TABLE_C, null, values);
    }

    public ArrayList<MyData> getAllMyData() {
        ArrayList<MyData> peopleList = new ArrayList<MyData>();

        String selectQuery = "SELECT  * FROM " + TABLE_C;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MyData p = new MyData(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2)

                );
                peopleList.add(p);
            } while (cursor.moveToNext());
        }
        return peopleList;
    }

    public ArrayList<MyData> getMyDataByMonth(Calendar year, Calendar month) {
        ArrayList<MyData> dataList = new ArrayList<>();

        int yearValue = year.get(Calendar.YEAR);
        int monthValue = month.get(Calendar.MONTH) + 1; // Calendar 中的月份是从 0 开始的，所以要加 1

        // 构建查询条件
        String startDate = String.format("%04d-%02d-01", yearValue, monthValue);
        String endDate = String.format("%04d-%02d-31", yearValue, monthValue);

        String selectQuery = "SELECT * FROM " + TABLE_C +
                " WHERE " + DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MyData p = new MyData(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2)
                );
                dataList.add(p);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return dataList;
    }



    public void deleteMyData(MyData mydata) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_C, ID + " = ?",
                new String[]{String.valueOf(mydata.id)});

    }


}
