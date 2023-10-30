package com.example.composeproject1;

import static com.example.composeproject1.model.Constant.DbKey.DB_USER_TABLE_NAME;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.composeproject1.model.Constant;

public class Register extends AppCompatActivity {
    EditText usename, usepwd, usepwd2;
    Button submit;
    DBHelper mysql;
    SQLiteDatabase db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usename = this.findViewById(R.id.usename);
        usepwd = this.findViewById(R.id.usepwd);
        usepwd2 = this.findViewById(R.id.usepwd2);
        submit = this.findViewById(R.id.submit);
        mysql = new DBHelper(this);
        db = mysql.getWritableDatabase();
        sp = this.getSharedPreferences("useinfo", this.MODE_PRIVATE);

        submit.setOnClickListener(new View.OnClickListener() {
            boolean flag = true;

            @Override
            public void onClick(View v) {
                String name = usename.getText().toString().trim();
                String pwd01 = usepwd.getText().toString().trim();
                String pwd02 = usepwd2.getText().toString().trim();
                String sex = "";
                if (name.equals("") || pwd01.equals("") || pwd02.equals("")) {
                    Toast.makeText(Register.this, "帳號或密碼不能為空!！", Toast.LENGTH_LONG).show();
                } else {
                    Cursor cursor = db.query(DB_USER_TABLE_NAME, new String[]{Constant.DbKey.KEY_USER_NAME}, null, null, null, null, null);

                    while (cursor.moveToNext()) {
                        if (cursor.getString(0).equals(name)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag == true) {
                        if (pwd01.equals(pwd02)) {
                            ContentValues cv = new ContentValues();
                            cv.put(Constant.DbKey.KEY_USER_NAME, name);
                            cv.put(Constant.DbKey.KEY_USER_PASSWORD, pwd01);
                            db.insert(DB_USER_TABLE_NAME, null, cv);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Constant.DbKey.KEY_USER_NAME, name);
                            editor.putString(Constant.DbKey.KEY_USER_PASSWORD, pwd01);
                            editor.commit();
                            Intent intent = new Intent();
                            intent.setClass(Register.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(Register.this, "註冊成功！", Toast.LENGTH_LONG).show();
                            db.close();
                        } else {
                            Toast.makeText(Register.this, "密碼不一致！", Toast.LENGTH_LONG).show();            //提示密码不一致
                        }
                    } else {
                        Toast.makeText(Register.this, "帳號已存在！", Toast.LENGTH_LONG).show();            //提示密码不一致
                    }

                }
            }

        });
    }
}

