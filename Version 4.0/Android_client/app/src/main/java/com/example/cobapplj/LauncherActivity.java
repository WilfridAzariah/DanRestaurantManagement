package com.example.cobapplj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        final SharedPreferences user_pref = getSharedPreferences("USER_PREF", MODE_PRIVATE);

        String username_pref = user_pref.getString("username","");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (username_pref.equals("")) {
                    Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    MainActivity.StartRefresh(getApplicationContext());

                    int saldo = user_pref.getInt("saldo",0);
                    Intent intent = new Intent(LauncherActivity.this, HomeActivity.class);
                    intent.putExtra("saldo",saldo);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}