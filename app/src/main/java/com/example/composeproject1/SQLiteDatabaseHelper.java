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




}
