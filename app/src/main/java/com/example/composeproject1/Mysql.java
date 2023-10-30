package com.example.composeproject1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.composeproject1.model.Constant;

public class Mysql extends SQLiteOpenHelper {
    public Mysql(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =String.format( "create table tables(%s integer primary key autoincrement,%s text,%s text)",  Constant.DbKey.KEY_USER_ID,Constant.DbKey.KEY_USER_NAME, Constant.DbKey.KEY_USER_PASSWORD);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
