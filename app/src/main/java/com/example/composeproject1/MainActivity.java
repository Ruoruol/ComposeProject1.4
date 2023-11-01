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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText name = findViewById(R.id.name);
        EditText pwd = findViewById(R.id.pwd);
        View btnlogin = findViewById(R.id.login);
        View btnreg = findViewById(R.id.reg);
        final long userId = AppGlobalRepository.INSTANCE.getUserId();
        if (userId != -1) {
            goMainActivity(userId);
        }
        btnlogin.setOnClickListener(v -> {
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
        });


        btnreg.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "前往註冊！", Toast.LENGTH_SHORT).show();
        });
    }

    private void goMainActivity(long userId) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Myhome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXTRA_USER_ID", userId);
        startActivity(intent);
        finish();
    }

}
