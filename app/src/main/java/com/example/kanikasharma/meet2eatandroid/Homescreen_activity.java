package com.example.kanikasharma.meet2eatandroid;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class Homescreen_activity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeintent=new Intent(Homescreen_activity.this,LoginActivity.class);
                startActivity(homeintent);
                finish();

            }
        },SPLASH_TIME_OUT);
    }
}
