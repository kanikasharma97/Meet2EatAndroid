package com.example.kanikasharma.meet2eatandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import mapping.RestaurantMapping;
import utility.Alert;
import utility.Network;
import utility.SessionManagement;
import java.util.*;

public class foodblogger extends AppCompatActivity {
    TextView lastWeek;
    TextView lastMonth;


    final static int SHOW_DETAILS=1;
    final Handler uiHandler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==SHOW_DETAILS) {
                HashMap <String, Integer> obj = (HashMap<String, Integer>) msg.obj;
                lastWeek.setText(obj.get("lastWeek").toString());
                lastMonth.setText(obj.get("lastMonth").toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodblogger);
        lastWeek=(TextView)findViewById(R.id.txt_lastWeek);
        lastMonth=(TextView)findViewById(R.id.txt_lastMonth);

        SessionManagement session =  new SessionManagement(getApplicationContext());
        session.checkLogin();
        fetch();

        BottomNavigationView bottomNavigationBar=(BottomNavigationView)findViewById(R.id.foodblogger_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected ( MenuItem item) {
                int id = item.getItemId();
                Intent myIntent;
                SessionManagement session = new SessionManagement(getApplicationContext());
                switch(id){
                    case R.id.nav_profile:
                         myIntent=new Intent(foodblogger.this,foodblogger_profile.class);
                        startActivity(myIntent);
                        return true;
                    case R.id.nav_restaurant:
                        if (!session.getProfileComplete()) {
                            Alert.showError(foodblogger.this, "Please update your profile");
                            return false;
                        }
                        myIntent=new Intent(foodblogger.this,restaurant_list.class);
                        startActivity(myIntent);
                        return true;
                    case R.id.nav_account:
                        Alert.logoutConfirmation(foodblogger.this, "Do you want to really log out?", session);
                        return true;

                    case R.id.meetups:
                        if (!session.getProfileComplete()) {
                            Alert.showError(foodblogger.this, "Please update your profile");
                            return false;
                        }
                        myIntent=new Intent(foodblogger.this,meetup_list.class);
                        startActivity(myIntent);
                        return true;
                        

                }
                return true;
            }
        });
    }

    void fetch () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session = new SessionManagement(getApplicationContext());
                HttpURLConnection myConnection = Network.get("/meetup/statstics",null, session.getAuthToken());
                try{
                    int code=myConnection.getResponseCode();
                    if(code==200){
                        InputStream responsebody=myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        HashMap <String, Integer> stats = new HashMap<String, Integer>();

                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("lastWeek")) {
                                stats.put("lastWeek", jsonReader.nextInt());
                            } else if (key.equals("lastMonth")) {
                                stats.put("lastMonth", jsonReader.nextInt());
                            } else {
                                jsonReader.skipValue();
                            }

                        }
                        jsonReader.endObject();
                        Message msg=uiHandler.obtainMessage();
                        msg.what=SHOW_DETAILS;
                        msg.obj=stats;
                        uiHandler.sendMessage(msg);
                    }
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }catch (NullPointerException e){
                    System.out.println(e.getMessage());
                }finally {
                    myConnection.disconnect();
                }
            }
        });
    }
}
