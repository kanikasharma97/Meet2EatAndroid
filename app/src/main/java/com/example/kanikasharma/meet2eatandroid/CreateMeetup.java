package com.example.kanikasharma.meet2eatandroid;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

import utility.Network;
import utility.SessionManagement;
import validation.Validation;

public class CreateMeetup extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{



    EditText title;
    EditText type;
    NumberPicker duration;
    NumberPicker maxnumber;
    NumberPicker minnumber;
    EditText description;
    Toolbar toolbar;
   TextView dateTime;
   Button btncreate;


   int day,month,year,hour,minute;
   int dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmeetup);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Meetup");
        dateTime=(TextView)findViewById(R.id.txt_dateTime);
        title=(EditText) findViewById(R.id.txt_title);
        type=(EditText) findViewById(R.id.txt_type);
        duration=(NumberPicker) findViewById(R.id.duration);
        maxnumber=(NumberPicker) findViewById(R.id.maxnumber);
        minnumber=(NumberPicker) findViewById(R.id.minnumber);
        description=(EditText)findViewById(R.id.txt_Description) ;
        btncreate=(Button) findViewById(R.id.btn_Create);

        minnumber.setMaxValue(10000);
        maxnumber.setMaxValue(10000);
        duration.setMaxValue(24);


        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c= Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);



                DatePickerDialog datetimepickerdialog=new DatePickerDialog(CreateMeetup.this,CreateMeetup.this,year,month,day );
                datetimepickerdialog.show();
            }
        });


        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Boolean> validationResults = new ArrayList<Boolean>();

                validationResults.add(Validation.handleEmptyField(title.getText(), title));
                validationResults.add(Validation.handleEmptyField(type.getText(), type));
                validationResults.add(Validation.handleEmptyField(description.getText(), description));


                if (validationResults.contains(false) == false) {

                    submit();
                }


            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal=i;
        monthFinal=i1+1;
        dayFinal=i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog=new TimePickerDialog(CreateMeetup.this,CreateMeetup.this,
                hour,minute, false);
        timePickerDialog.show();;


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        hourFinal=i;
        minuteFinal=i1;

        dateTime.setText(String.format("%d/%d/%d %d:%d", monthFinal, dayFinal, yearFinal, hourFinal, minuteFinal));
    }


    public  void submit () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                Intent obj=getIntent();
                int uid=obj.getExtras().getInt("uid");


                String data = "title=" + title.getText() +
                        "&type=" + type.getText() +
                        "&duration=" + duration.getValue() +
                        "&maxNoOfAttendee=" + maxnumber.getValue()
                        + "&minNoOfAttendee=" + minnumber.getValue() + "&description=" + description.getText() + "&restaurantUid=" + uid + "&meetupDate=" + dateTime.getText() + "&duration=" + duration.getValue();

                SessionManagement session = new SessionManagement(getApplicationContext());
                HttpURLConnection myConnection = Network.post("/meetup", data, session.getAuthToken());

                try {

                    int code = myConnection.getResponseCode();
                    if (code == 200) {
                       Intent myIntent=new Intent(CreateMeetup.this,meetup_list.class);
                       startActivity(myIntent);


                    } else if (code == 400){

                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                } catch (NullPointerException e){
                    System.out.println(e.getMessage());
                } finally {
                    myConnection.disconnect();
                }


            }
        });
    }

}
