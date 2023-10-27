package com.example.composeproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText name,pwd;
    Button btnlogin,btnreg;
    DBHelper mysql;
    SQLiteDatabase db;
    SharedPreferences sp1,sp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = findViewById(R.id.name);
        pwd = findViewById(R.id.pwd);
        btnlogin = findViewById(R.id.login);
        btnreg = findViewById(R.id.reg);
        sp1 =  this.getSharedPreferences("useinfo",this.MODE_PRIVATE);
        sp2 = this.getSharedPreferences("username",this.MODE_PRIVATE);

        // 初始化数据库连接
        mysql = new DBHelper(this);
        db = mysql.getWritableDatabase();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String password = pwd.getText().toString();

                // 查询用户名和密码相同的数据
                Cursor cursor = db.query("users", new String[]{"id","usname", "uspassword" }, "usname=? and uspassword=?", new String[]{username, password}, null, null, null);

                int flag = cursor.getCount();
                if (flag != 0) {
                    cursor.moveToFirst();
                    @SuppressLint("Range") long userId = cursor.getLong(cursor.getColumnIndex("id"));

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Myhome.class);
                    intent.putExtra("EXTRA_USER_ID", userId);
                    SharedPreferences.Editor editor = sp2.edit();
                    @SuppressLint("Range") String loginname = cursor.getString(cursor.getColumnIndex("usname"));
                    editor.putString("Loginname", loginname);
                    editor.putLong("user_id", userId); // 将用户的 ID 存入 SharedPreferences
                    editor.commit();
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "帳號或密碼錯誤！", Toast.LENGTH_LONG).show();
                }
            }
        });


        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Register.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"前往註冊！",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
