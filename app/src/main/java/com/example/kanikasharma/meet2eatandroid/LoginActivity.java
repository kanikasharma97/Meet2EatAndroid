package com.example.kanikasharma.meet2eatandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.*;
import java.io.IOException;
import java.lang.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import utility.*;
import validation.Validation;
import android.os.*;
import android.content.Context;
import utility.SessionManagement;



public class LoginActivity extends AppCompatActivity {
    EditText txtEmail;
    EditText txtpsw;
    Button   button;
    TextView txtforgotpassword;
    TextView txtregister;


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String AuthToken = "AuthTokenKey";
    //public SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

    final static int SHOW_ERROR = 1;
    final Handler uiHandler = new Handler () {

        @Override
        public void handleMessage (Message msg) {
            if (msg.what == SHOW_ERROR) {
                Alert.showError(LoginActivity.this, (String) msg.obj);
            }
            super.handleMessage(msg);
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail=(EditText)findViewById(R.id.txt_email);
        txtregister=(TextView) findViewById(R.id.txt_registerhere);
        txtpsw=(EditText)findViewById(R.id.txt_psw);
        button = (Button)findViewById(R.id.btn_login);
        txtforgotpassword=(TextView)findViewById(R.id.txt_forgotpassword);


        button.setOnClickListener(
            new View.OnClickListener() {
                public void onClick (View view){

                    ArrayList <Boolean> validationResults = new ArrayList <Boolean>();
                    validationResults.add(Validation.handleEmptyField(txtEmail.getText(), txtEmail));
                    validationResults.add(Validation.handleEmptyField(txtpsw.getText(),txtpsw));
                    validationResults.add(Validation.handleSufficientLength(txtpsw.getText(),txtpsw, 6, "Password"));

                    if(validationResults.contains(false) == false){
                        submit();
                    }

                }


            }

        );

         txtforgotpassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent myIntent = new Intent(LoginActivity.this,
                         forgotpassword_activity.class);
                 startActivity(myIntent);
             }
         });
         txtregister.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent myIntent=new Intent(LoginActivity.this,MainActivity.class);
                 startActivity(myIntent);
             }
         });


    }


    public  void submit () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                txtEmail = (EditText) findViewById(R.id.txt_email);
                txtpsw = (EditText) findViewById(R.id.txt_psw);

                String data = "email=" + txtEmail.getText() + "&password=" + txtpsw.getText();

                HttpURLConnection myConnection = Network.post("/login", data, null);

                try {

                    InputStream responseBody = null;
                    int code = myConnection.getResponseCode();
                    if (code == 200) {
                        responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        String type=null, authToken=null;
                        boolean isProfileComplete=false;

                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("type")) {
                                type = jsonReader.nextString();
                            } else if (key.equals("authToken")) {
                                authToken = jsonReader.nextString();
                            } else if (key.equals("isProfileComplete")){
                                isProfileComplete = jsonReader.nextBoolean();
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        SessionManagement session = new SessionManagement(getApplicationContext());
                        session.createLoginSession(authToken, type, isProfileComplete);
                        Intent myIntent = null;
                        if(type.equals("user")) {
                            myIntent = new Intent(LoginActivity.this,
                                    user.class);

                        }else if(type.equals("blogger")) {
                            myIntent=new Intent(LoginActivity.this,
                                    foodblogger.class);
                        } else if (type.equals("restaurant")){
                            myIntent=new Intent(LoginActivity.this,
                                    restaurant.class);
                        }
                        startActivity(myIntent);

                    } else if(code==401) {
                      Message msg = uiHandler.obtainMessage();
                      msg.what = SHOW_ERROR;
                      msg.obj = "Username/Password is incorrect";
                      uiHandler.sendMessage(msg);
                    } else if (code == 400) {
                      Message msg = uiHandler.obtainMessage();
                      msg.what = SHOW_ERROR;
                      msg.obj = "Please enter valid email address";
                      uiHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                } finally {
                    myConnection.disconnect();

                }
            }
        });

        }
    }
