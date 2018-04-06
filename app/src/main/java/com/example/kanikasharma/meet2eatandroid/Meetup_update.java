package com.example.kanikasharma.meet2eatandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import utility.Alert;
import utility.Network;
import utility.SessionManagement;
import validation.Validation;

public class Meetup_update extends AppCompatActivity {

    final static int SHOW_SUCCESS=1;
    final static int SHOW_ERROR=2;

    final Handler uihandler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==SHOW_SUCCESS){
                Alert.showSuccess(Meetup_update.this,(String)msg.obj);
            }else if(msg.what==SHOW_ERROR){
               Alert.showError(Meetup_update.this,msg.obj.toString());


            }
        }
    };

    EditText title;
    TextView restaurantName;
    TextView address;
    EditText maxPeople;
    EditText minPeople;
    EditText duration;
    TextView timings;
    EditText description;
    Button button;
    int uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_update);
        title=(EditText)findViewById(R.id.txt_meetupTitle);
        restaurantName=(TextView)findViewById(R.id.txt_restaurantName);
        address=(TextView)findViewById(R.id.txt_address);
        maxPeople=(EditText)findViewById(R.id.txt_maximumPeople);
        minPeople=(EditText)findViewById(R.id.txt_minimumPeople);
        duration=(EditText)findViewById(R.id.txt_duration);
        timings=(TextView)findViewById(R.id.txt_timings);
        description = (EditText)findViewById(R.id.txt_Description);
        button = (Button)findViewById(R.id.btn_updateMeetup);


        Intent obj=getIntent();

        int durationvalue=obj.getExtras().getInt("duration");
        uid = obj.getExtras().getInt("uid");


        final int minNoOfPeople = obj.getExtras().getInt("minNoOfAttendee");
        int maxNoOfPeople = obj.getExtras().getInt("maxNoOfAttendee");

        title.setText(obj.getStringExtra("title"));
        description.setText(obj.getStringExtra("description"));
        restaurantName.setText(obj.getStringExtra("name"));
        address.setText(obj.getStringExtra("address"));
        duration.setText(String.valueOf(durationvalue));
        timings.setText(obj.getStringExtra("meetupDate"));
        minPeople.setText(String.valueOf(minNoOfPeople));
        maxPeople.setText(String.valueOf(maxNoOfPeople));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Boolean> validationResults=new ArrayList<>();
                validationResults.add(Validation.handleEmptyField(title.getText(), title));
                validationResults.add(Validation.handleEmptyField(description.getText(), description));
                validationResults.add(Validation.handleEmptyField(duration.getText(), duration));
                validationResults.add(Validation.handleEmptyField(minPeople.getText(),minPeople));
                validationResults.add(Validation.handleEmptyField(maxPeople.getText(), maxPeople));

                if(validationResults.contains(false)==false){
                    update();

                }

            }
        });
    }




    public void update(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session = new SessionManagement(getApplicationContext());
                String data= "uid=" + uid + "&title="+title.getText() + "&duration=" + duration.getText() + "&minNoOfAttendee=" + minPeople.getText() + "&maxNoOfAttendee="+maxPeople.getText() + "&description="+description.getText();
                HttpURLConnection myConnection = Network.put("/meetup/update",data,session.getAuthToken());

                try{
                    int code=myConnection.getResponseCode();
                    Message msg=uihandler.obtainMessage();

                    if(code==200){
                        msg.what=SHOW_SUCCESS;
                        msg.obj="meetup updated successfully";
                        uihandler.sendMessage(msg);
                        Intent myIntet=new Intent(Meetup_update.this,meetup_list.class);
                        startActivity(myIntet);
                    } else if (code==400) {
                        msg.what=SHOW_ERROR;
                        msg.obj="max no of attendees should be greater than or equal to checked attendees";
                        uihandler.sendMessage(msg);
                    }
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}
