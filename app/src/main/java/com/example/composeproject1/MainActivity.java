package com.example.composeproject1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.composeproject1.model.AppGlobalRepository;

import kotlin.Unit;


public class MainActivity extends AppCompatActivity {

    EditText name, pwd;
    Button btnlogin, btnreg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = this.findViewById(R.id.name);
        pwd = this.findViewById(R.id.pwd);
        btnlogin = this.findViewById(R.id.login);
        btnreg = this.findViewById(R.id.reg);
        if (AppGlobalRepository.INSTANCE.isLogin()) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(MainActivity.this, Myhome.class);
            startActivity(intent);
        }
        btnlogin.setOnClickListener(v -> {
            String username = name.getText().toString();
            String password = pwd.getText().toString();
            AppGlobalRepository.INSTANCE.login(MainActivity.this, username, password, isLogin -> {
                if (isLogin) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setClass(MainActivity.this, Myhome.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "帳號或密碼錯誤！", Toast.LENGTH_LONG).show();
                }
                return Unit.INSTANCE;
            });
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


}

