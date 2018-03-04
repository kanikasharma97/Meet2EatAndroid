package com.example.kanikasharma.meet2eatandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class user extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        BottomNavigationView bottomNavigationBar=(BottomNavigationView)findViewById(R.id.user_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                Intent myIntent;
                switch (id){
                    case R.id.nav_profile:
                        myIntent = new Intent(user.this,user_profile.class);
                        startActivity(myIntent);
                        return true;
                    case R.id.nav_account:
                        myIntent=new Intent(user.this,LoginActivity.class);
                        return true;

                }
                return true;
            }
        });
    }
}
