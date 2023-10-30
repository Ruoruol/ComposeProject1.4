package com.example.composeproject1;

import static com.example.composeproject1.model.Constant.DbKey.KEY_USER_ID;
import static com.example.composeproject1.model.Constant.DbKey.KEY_USER_NAME;
import static com.example.composeproject1.model.Constant.DbKey.KEY_USER_PASSWORD;
import static com.example.composeproject1.model.Constant.FileDbKey.KEY_SP_USER_ID;
import static com.example.composeproject1.model.Constant.FileDbKey.KEY_SP_USER_NAME;

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

import com.example.composeproject1.model.AppGlobalRepository;

import kotlin.Result;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    EditText name, pwd;
    Button btnlogin, btnreg;
    SharedPreferences sp1, sp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = findViewById(R.id.name);
        pwd = findViewById(R.id.pwd);
        btnlogin = findViewById(R.id.login);
        btnreg = findViewById(R.id.reg);
        sp1 = this.getSharedPreferences("useinfo", this.MODE_PRIVATE);
        sp2 = this.getSharedPreferences("username", this.MODE_PRIVATE);

        final long userId = AppGlobalRepository.INSTANCE.getUserId();
        if (userId != -1) {
            goMainActivity(userId);
        }
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String password = pwd.getText().toString();
                AppGlobalRepository.INSTANCE.login(
                        MainActivity.this,
                        username,
                        password,
                        longResult -> {
                            if (longResult == -1) {
                                Toast.makeText(MainActivity.this, "帳號或密碼錯誤！", Toast.LENGTH_LONG).show();
                            } else {
                                goMainActivity(longResult);
                            }
                            return Unit.INSTANCE;
                        }
                );

                // 查询用户名和密码相同的数据
               /* Cursor cursor = db.query("users", new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_PASSWORD}, "usname=? and uspassword=?", new String[]{username, password}, null, null, null);

                int flag = cursor.getCount();
                if (flag != 0) {
                    cursor.moveToFirst();
                    @SuppressLint("Range") long userId = cursor.getLong(cursor.getColumnIndex(KEY_USER_ID));

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Myhome.class);
                    intent.putExtra("EXTRA_USER_ID", userId);
                    SharedPreferences.Editor editor = sp2.edit();
                    @SuppressLint("Range") String loginname = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
                    editor.putString(KEY_SP_USER_NAME, loginname);
                    editor.putLong(KEY_SP_USER_ID, userId); // 将用户的 ID 存入 SharedPreferences
                    editor.commit();
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "帳號或密碼錯誤！", Toast.LENGTH_LONG).show();
                }*/
            }
        });


        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Register.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "前往註冊！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity(long userId) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Myhome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("EXTRA_USER_ID", userId);
        startActivity(intent);
    }

}
