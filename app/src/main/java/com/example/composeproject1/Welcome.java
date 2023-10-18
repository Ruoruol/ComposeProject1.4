package com.example.composeproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {
    SharedPreferences sp;
    TextView showhello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sp = this.getSharedPreferences("username", this.MODE_PRIVATE);
        showhello = this.findViewById(R.id.mainword);
        showhello.setText("欢迎你！"+sp.getString("Loginname",""));
    }
}
