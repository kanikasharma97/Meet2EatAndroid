package com.example.kanikasharma.meet2eatandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class foodblogger extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodblogger);

        BottomNavigationView bottomNavigationBar=(BottomNavigationView)findViewById(R.id.foodblogger_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected ( MenuItem item) {
                int id = item.getItemId();
                Intent myIntent;
                switch(id){
                    case R.id.nav_profile:
                         myIntent=new Intent(foodblogger.this,foodblogger_profile.class);
                        startActivity(myIntent);
                        return true;
                    case R.id.nav_restaurant:
                        myIntent=new Intent(foodblogger.this,restaurant_list.class);
                        startActivity(myIntent);
                        return true;
                    case R.id.nav_account:
                        myIntent =new Intent(foodblogger.this,LoginActivity.class);
                        startActivity(myIntent);
                        return true;
                        

                }
                return true;
            }
        });
    }
}
