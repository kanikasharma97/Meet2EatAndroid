package com.example.kanikasharma.meet2eatandroid;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import utility.Alert;
import utility.Network;
import utility.SessionManagement;
import validation.Validation;

public class user_profile extends AppCompatActivity {


    final static int SHOW_DETAILS=1;
    final static int SHOW_ERROR=2;
    final static int SHOW_SUCCESS=3;



    final Handler uihandler=new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == SHOW_DETAILS) {

                HashMap mapping  = (HashMap)msg.obj;

                for (Object key : mapping.keySet()) {

                    if (key.equals("email")) {
                        txtemail.setText(mapping.get(key).toString());
                    } else if (key.equals("name")) {
                        txtname.setText(mapping.get(key).toString());
                    } else if (key.equals("contactNo")) {
                        txtcontactno.setText(mapping.get(key).toString());
                    } else if(key.equals("age")) {
                        txtage.setText(mapping.get(key).toString());
                    }

                }


            }else if (msg.what == SHOW_ERROR){
                String message =(String)msg.obj;
                txtemail.setError((String)msg.obj);
            } else if (msg.what == SHOW_SUCCESS) {
                Alert.showSuccess(user_profile.this, (String)msg.obj);
            }

        }
    };




    Toolbar toolbar;
    EditText txtemail;
    EditText txtname;
    EditText txtcontactno;
    EditText txtage;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        button = (Button) findViewById(R.id.btn_Update);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtemail = (EditText) findViewById(R.id.txt_email);
        txtname = (EditText) findViewById(R.id.txt_name);
        txtcontactno = (EditText) findViewById(R.id.txt_ContactNo);
        txtage = (EditText) findViewById(R.id.txt_Age);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Profile");
        fetchdetails();


        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {

                                          ArrayList<Boolean> validationResults = new ArrayList<Boolean>();

                                          validationResults.add(Validation.handleEmptyField(txtemail.getText(), txtemail));
                                          validationResults.add(Validation.handleEmptyField(txtname.getText(), txtname));
                                          validationResults.add(Validation.handleEmptyField(txtcontactno.getText(), txtcontactno));
                                          validationResults.add(Validation.handleEmptyField(txtage.getText(), txtage));


                                          if (validationResults.contains(false) == false) {
                                              update();
                                          }

                                      }


                                  }

        );

    }
    public void fetchdetails() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                // get authToken
                SessionManagement session = new SessionManagement(getApplicationContext());
                HttpURLConnection myConnection= Network.get("/user",null, session.getAuthToken());
                try
                {
                    int code = myConnection.getResponseCode();
                    if(code == 200){
                        InputStream responsebody=myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        HashMap<String,Object> mapping =new HashMap<String,Object>();

                        jsonReader.beginObject();

                        while(jsonReader.hasNext()) {
                            String key = jsonReader.nextName();

                            if (key == "age") {
                                mapping.put(key, jsonReader.nextInt());
                            } else {
                                mapping.put(key, jsonReader.nextString());
                            }
                        }

                        Message msg=uihandler.obtainMessage();
                        msg.what=SHOW_DETAILS;
                        msg.obj=mapping;
                        uihandler.sendMessage(msg);

                    }
                }catch (IOException e) {
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
                SessionManagement session = new SessionManagement(getApplicationContext());
                String data="email=" + txtemail.getText() + "&age=" + txtage.getText() + "&contactNo=" + txtcontactno.getText() + "&name=" + txtname.getText();
                HttpURLConnection myConnection=Network.put("/user",data, session.getAuthToken());

                try{
                    int code= myConnection.getResponseCode();

                    Message msg = uihandler.obtainMessage();
                    if(code==400){
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


                    } else if (code == 200) {
                        msg.what = SHOW_SUCCESS;
                        msg.obj = "profile update successfully";
                        uihandler.sendMessage(msg);
                    }

                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }finally {
                    myConnection.disconnect();

                }
            }


        });
    }

}




