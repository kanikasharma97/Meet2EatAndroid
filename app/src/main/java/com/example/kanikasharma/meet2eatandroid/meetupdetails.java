package com.example.kanikasharma.meet2eatandroid;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Switch;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;

import mapping.MeetupMapping;
import utility.Alert;
import utility.Network;
import utility.SessionManagement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class meetupdetails extends AppCompatActivity {

    TextView title;
    TextView restaurantName;
    TextView address;
    TextView meetupDate;
    TextView duration;
    TextView description;
    Switch checkin;
    Switch confirm;
    RatingBar ratingBar;

    TextView minNumber;
    TextView maxNumber;

    int uid;
    final static int SHOW_ERROR=1;
    final static int SHOW_CHECKIN=2;
    final static int SHOW_RATING=3;
    boolean shouldCallCheckinApi = true;

    final Handler uiHandler=new Handler(){
        public void handleMessage(Message msg){

            if (msg.what == SHOW_RATING) {
                float value=Float.parseFloat((String)msg.obj);
                ratingBar.setRating(value);
            } else if(msg.what == SHOW_ERROR){
                Alert.showError(meetupdetails.this, msg.obj.toString());
                shouldCallCheckinApi = false;
                checkin.setChecked(false);
            } else if (msg.what == SHOW_CHECKIN) {
                boolean checkinVal = (Boolean)msg.obj;
                checkin.setChecked(checkinVal);
                if (!checkinVal) {
                    ratingBar.setVisibility(View.INVISIBLE);
                }

                checkin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final boolean checked = isChecked;
                        if (shouldCallCheckinApi == false) {
                            shouldCallCheckinApi = true;
                            return;
                        }
                        AsyncTask.execute(new Runnable(){

                            public void run () {
                                String data="checkin="+ checked + "&uid=" + uid;
                                SessionManagement session = new SessionManagement(getApplicationContext());
                                HttpURLConnection myConnection= Network.put("/meetup/checkin",data,session.getAuthToken());
                                try{
                                    int code=myConnection.getResponseCode();

                                    if (code == 400) {
                                        Message msg = uiHandler.obtainMessage();
                                        msg.what = SHOW_ERROR;
                                        msg.obj = "You can't checkin as meetup is full booked";
                                        uiHandler.sendMessage(msg);

                                    }
                                }catch (IOException e){
                                    System.out.println(e.getMessage());
                                }finally {
                                    myConnection.disconnect();
                                }
                            }

                        });

                    }
                });
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetupdetails);
        Intent obj=getIntent();
        title=(TextView)findViewById(R.id.txt_meetupTitle);
        meetupDate=(TextView)findViewById(R.id.txt_meetupDate);
        duration=(TextView)findViewById(R.id.txt_duration);
        description=(TextView)findViewById(R.id.txt_Description);
        address=(TextView) findViewById(R.id.txt_address);
        restaurantName=(TextView)findViewById(R.id.txt_restaurantName);
        checkin=(Switch)findViewById(R.id.checkin);
        confirm=(Switch)findViewById(R.id.confirm);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        minNumber=(TextView)findViewById(R.id.minNumber);
        maxNumber=(TextView)findViewById(R.id.maxNumber);


        int durationvalue=obj.getExtras().getInt("duration");
        int maxvalue=obj.getExtras().getInt("maxNoOfAttendee");
        int minvalue=obj.getExtras().getInt("minNoOfAttendee");
        uid = obj.getExtras().getInt("uid");
        boolean isConfirmed = obj.getExtras().getBoolean("confirmation");


        title.setText(obj.getStringExtra("title"));
        duration.setText(String.valueOf("duration"));
        description.setText(obj.getStringExtra("description"));
        restaurantName.setText(obj.getStringExtra("name"));
        address.setText(obj.getStringExtra("address"));
        duration.setText(String.valueOf(durationvalue + "hour(s)"));
        meetupDate.setText(obj.getStringExtra("meetupDate"));
        confirm.setChecked(isConfirmed);
        maxNumber.setText(String.valueOf(maxvalue));
        minNumber.setText(String.valueOf(minvalue));


        SessionManagement session = new SessionManagement(getApplicationContext());
        String type = session.getUserType();

        if (type.equals("restaurant")) {
            checkin.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);

        } else if (type.equals("user")) {
            confirm.setVisibility(View.INVISIBLE);


            Date strdate = null;
            SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yyyy  HH:mm");
            try {
                strdate=sdf.parse(obj.getStringExtra("meetupDate"));
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }

            if(strdate.before(new Date())){
             checkin.setClickable(false);
            }
            if(strdate.after(new Date())){
                ratingBar.setVisibility(View.INVISIBLE);
            }


            fetchCheckinInfo();
            fetchRating();

        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                setRating(v);
            }
        });




        confirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isConfirmed) {
                final boolean confirmed = isConfirmed;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String data="confirmation=" + confirmed + "&uid=" + uid;
                        SessionManagement session = new SessionManagement(getApplicationContext());
                        HttpURLConnection myConnection=Network.put("/meetup/confirm",data,session.getAuthToken());

                        try{
                            int code=myConnection.getResponseCode();
                            if (code != 200) {
                                System.out.println("Confirmation failed");
                            }
                        }catch (IOException e){
                            System.out.println(e.getMessage());
                        }finally {
                            myConnection.disconnect();
                        }

                    }
                });

            }
        });



    }

    public void setRating (final float value) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session=new SessionManagement(getApplicationContext());
                String data="uid=" + uid + "&value=" + value;
                HttpURLConnection myConnection = Network.put("/meetup/rating",data, session.getAuthToken());
                try{
                    int code=myConnection.getResponseCode();
                    if(code==200){
                        System.out.print("Success");
                    }
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }

            }
        });
    }

    public void fetchRating(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session = new SessionManagement(getApplicationContext());
                String data = "uid=" + uid;
                HttpURLConnection myConnection = Network.get("/meetup/rating", data, session.getAuthToken());
                try {
                    int code = myConnection.getResponseCode();
                    Message msg = uiHandler.obtainMessage();
                    String value = null;
                    if(code==200){
                        InputStream responsebody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody,"UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        while(jsonReader.hasNext()){
                           String key = jsonReader.nextName();
                           if(key.equals("value")){
                               value = jsonReader.nextString();
                           }
                        }
                        msg.what = SHOW_RATING;
                        msg.obj = value;
                        uiHandler.sendMessage(msg);

                    }
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        });

    }

    public void fetchCheckinInfo () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session = new SessionManagement(getApplicationContext());
                String data= "uid=" + uid;
                HttpURLConnection myConnection = Network.get("/meetup/checkin",data,session.getAuthToken());

                try{
                    int code=myConnection.getResponseCode();
                    Message msg=uiHandler.obtainMessage();

                    if(code==200){

                        InputStream responsebody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        boolean isUserCheckin = false;
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("isUserCheckin")) {
                                isUserCheckin = jsonReader.nextBoolean();
                            } else {
                                jsonReader.skipValue();
                            }

                        }

                        msg.what=SHOW_CHECKIN;
                        msg.obj=isUserCheckin;
                        uiHandler.sendMessage(msg);

                    }
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}
