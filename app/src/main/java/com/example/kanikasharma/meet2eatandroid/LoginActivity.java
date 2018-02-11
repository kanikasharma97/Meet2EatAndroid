package com.example.kanikasharma.meet2eatandroid;

import android.content.Intent;
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



public class LoginActivity extends AppCompatActivity {
    EditText txtEmail;
    EditText txtpsw;
    Button   button;
    TextView txtforgotpassword;
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
        txtpsw=(EditText)findViewById(R.id.txt_psw);
        button = (Button)findViewById(R.id.btn_login);
        txtforgotpassword=(TextView)findViewById(R.id.txt_forgotpassword);


        button.setOnClickListener(
            new View.OnClickListener() {
                public void onClick (View view){
                    ArrayList <Boolean> validationResults = new ArrayList <Boolean>();
                    validationResults.add(Validation.handleEmptyField(txtEmail.getText(), txtEmail));
                    validationResults.add(Validation.handleSufficientLength(txtpsw.getText(),txtpsw, 6));

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
    }


    public  void submit () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                txtEmail = (EditText) findViewById(R.id.txt_email);
                txtpsw = (EditText) findViewById(R.id.txt_psw);

                String data = "email=" + txtEmail.getText() + "&password=" + txtpsw.getText();

                HttpURLConnection myConnection = Network.post("/login", data);

                try {

                    InputStream responseBody = null;
                    int code = myConnection.getResponseCode();
                    if (code == 200) {
                        responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        String type=null, authToken=null;

                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("type")) {
                                type = jsonReader.nextString();
                                System.out.println(type);
                            } else if (key.equals("authToken")) {
                                authToken = jsonReader.nextString();
                                System.out.println(authToken);
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        if(type.equals("user")) {
                            Intent myIntent = new Intent(LoginActivity.this,
                                    user_navigation.class);
                            startActivity(myIntent);
                        }else if(type.equals("blogger")) {
                            Intent myIntent=new Intent(LoginActivity.this,
                                    navigation_foodblogger.class);
                        }


                    } else if(code==401) {
                      Message msg = uiHandler.obtainMessage();
                        msg.what = SHOW_ERROR;
                      msg.obj = "Username/Password is incorrect";
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