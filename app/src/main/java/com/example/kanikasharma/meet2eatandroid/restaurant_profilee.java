package com.example.kanikasharma.meet2eatandroid;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import utility.Alert;
import utility.Network;
import validation.Validation;
import android.widget.Spinner;
import android.widget.TimePicker;

public class restaurant_profilee extends AppCompatActivity {


    final static int SHOW_DETAILS=1;
    final static int SHOW_ERROR=2;
    final static int SHOW_SUCCESS=3;
    final static String [] types = {"Cafe", "pub", "Family Restaurant", "Others"};

    final Handler uihandler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==SHOW_DETAILS){
                HashMap mapping=(HashMap)msg.obj;
                for(Object key : mapping.keySet()){
                    Object value = mapping.get(key);
                    int time;
                    boolean available;
                    if(key.equals("email")) {
                        txtemail.setText(value.toString());
                    }else if(key.equals("contactNo")) {
                        txtcontactno.setText(value.toString());
                    }else if(key.equals("name")){
                        txtname.setText(value.toString());
                    }else if(key.equals("website")){
                        txtwebsite.setText(value.toString());
                    }else if(key.equals("address")){
                        txtaddress.setText(value.toString());
                    } else if (key.equals("seatingCapacity")) {
                        txtseatingCapacity.setText(value.toString());
                    } else if (key.equals("type")) {
                        restaurantType.setSelection(Arrays.asList(types).indexOf(value.toString()));
                    } else if (key.equals("availabilityWeekday")) {
                        available = (Boolean)value;
                        availabilityWeekday.setSelection(available ? 0 : 1);
                    } else if (key.equals("availabilityWeekend")) {
                        available = (Boolean)value;
                        availabilityWeekend.setSelection(available ? 0 : 1);
                    } else if (key.equals("startTime")) {
                        time = (int) value;
                        timePickerStartTime.setCurrentHour(time/60);
                        timePickerStartTime.setCurrentMinute(time%60);
                    } else if (key.equals("endTime")) {
                        time = (int) value;
                        timePickerEndTime.setCurrentHour(time/60);
                        timePickerEndTime.setCurrentMinute(time%60);
                    }
                }
            }else if(msg.what==SHOW_ERROR){
                String message=(String)msg.obj;
                txtemail.setError((String)msg.obj);

            }else if(msg.what==SHOW_SUCCESS){
                Alert.showSuccess(restaurant_profilee.this,(String)msg.obj);
            }
        }
    };

    Toolbar toolbar;
    EditText txtemail;
    EditText txtname;
    EditText txtaddress;
    EditText txtcontactno;
    EditText txtwebsite;
    Button button;
    Spinner availabilityWeekday;
    Spinner availabilityWeekend;
    TimePicker timePickerStartTime;
    TimePicker timePickerEndTime;
    Spinner restaurantType;
    EditText txtseatingCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profilee);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Profile");
        txtemail=(EditText) findViewById(R.id.txt_email);
        txtname=(EditText)findViewById(R.id.txt_name);
        txtaddress=(EditText)findViewById(R.id.txt_address);
        txtcontactno=(EditText)findViewById(R.id.txt_ContactNo);
        txtwebsite=(EditText)findViewById(R.id.txt_website);
        button=(Button)findViewById(R.id.btn_Update);
        availabilityWeekday=(Spinner)findViewById(R.id.spinner_avilaibilityweekday);
        availabilityWeekend=(Spinner)findViewById(R.id.spinner_availaibilityweekend);
        timePickerStartTime=(TimePicker)findViewById(R.id.TimePicker_starttime);
        timePickerEndTime=(TimePicker)findViewById(R.id.TimePicker_endttime);
        restaurantType=(Spinner)findViewById(R.id.spinner_restaurantType);
        txtseatingCapacity=(EditText)findViewById(R.id.txt_seatingcapacity);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Boolean> validationResults=new ArrayList<>();
                validationResults.add(Validation.handleEmptyField(txtemail.getText(), txtemail));
                validationResults.add(Validation.handleEmptyField(txtname.getText(), txtname));
                validationResults.add(Validation.handleEmptyField(txtcontactno.getText(), txtcontactno));
                validationResults.add(Validation.handleEmptyField(txtaddress.getText(), txtaddress));
                validationResults.add(Validation.handleEmptyField(txtwebsite.getText(), txtwebsite));

                if(validationResults.contains(false)==false){
                    update();

                }

            }
        });

        fetchdetails();

    }



   public void fetchdetails(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection myConnection= Network.get("/restaurant",null,"123456");
                try {
                    int code = myConnection.getResponseCode();
                    if (code == 200) {
                        InputStream responsebody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        HashMap<String, Object> mapping = new HashMap<String, Object>();

                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("seatingCapacity") || key.equals("startTime") || key.equals("endTime")) {
                                mapping.put(key, jsonReader.nextInt());
                            } else if (key.equals("availabilityWeekday") || key.equals("availabilityWeekend")) {
                                mapping.put(key, jsonReader.nextBoolean());
                            } else {
                                mapping.put(key, jsonReader.nextString());
                            }

                        }



                        Message msg = uihandler.obtainMessage();
                        msg.what = SHOW_DETAILS;
                        msg.obj = mapping;
                        uihandler.sendMessage(msg);

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

    public void update(){
       AsyncTask.execute(new Runnable() {
           @Override
           public void run() {
               boolean availabilityWeekdayValue = availabilityWeekday.getSelectedItemId() == 0  ? true : false;
               boolean  availabilityWeekendValue = availabilityWeekend.getSelectedItemId() == 0  ? true : false;
               int startTimeValue = timePickerStartTime.getCurrentHour() * 60 + timePickerStartTime.getCurrentMinute();
               int endTimeValue = timePickerEndTime.getCurrentHour() * 60 + timePickerEndTime.getCurrentMinute();
               String data="email=" + txtemail.getText() +
                       "&name=" + txtname.getText() +
                       "&address=" + txtaddress.getText() +
                       "&contactNo=" + txtcontactno.getText() +
                       "&website=" + txtwebsite.getText() +
                       "&seatingCapacity=" + txtseatingCapacity.getText() +
                       "&availabilityWeekday=" + availabilityWeekdayValue +
                       "&availabilityWeekend="+ availabilityWeekendValue +
                       "&startTime=" + startTimeValue +
                       "&endTime=" + endTimeValue +
                       "&type=" + restaurantType.getSelectedItem().toString();
               HttpURLConnection myConnection = Network.put("/restaurant",data,"123456");

               try{
                    int code=myConnection.getResponseCode();
                    Message msg=uihandler.obtainMessage();
                    if (code==400){
                        InputStream responseBody = myConnection.getErrorStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("email")) {
                                msg.what = SHOW_ERROR;
                                msg.obj = jsonReader.nextString();
                                uihandler.sendMessage(msg);
                                break;

                            } else {

                                jsonReader.skipValue();

                            }
                        }

                    }else if(code==200){
                        msg.what=SHOW_SUCCESS;
                        msg.obj="profile update successfully";
                        uihandler.sendMessage(msg);
                    }
               }catch (IOException e){
                   System.out.println(e.getMessage());
               }finally {
                   myConnection.disconnect();
               }
           }
       });
    }


}





