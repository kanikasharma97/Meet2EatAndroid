package com.example.kanikasharma.meet2eatandroid;

import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.*;
import android.content.*;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import android.util.JsonReader;
import android.os.Handler;
import validation.Validation;
import utility.Network;
import android.os.Message;

public class MainActivity extends AppCompatActivity {
    EditText txtEmail;
    EditText txtpsw;
    EditText txtconfirmpsw;
    EditText txtsecureans;
    Button btnsignup;
    Spinner spinnertype;
    final static int SHOW_ERROR=1;
    final Handler uihandler=new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == SHOW_ERROR)
                txtEmail.setError((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail=(EditText)findViewById(R.id.txt_email);
        txtconfirmpsw=(EditText)findViewById(R.id.txt_confirmpsw);
        txtpsw=(EditText)findViewById(R.id.txt_psw);
        btnsignup=(Button)findViewById(R.id.btn_signup);
        txtsecureans=(EditText) findViewById(R.id.txt_secureans);


        btnsignup.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        ArrayList <Boolean> validationResults = new ArrayList<Boolean>();

                        validationResults.add(Validation.handleEmptyField(txtEmail.getText(), txtEmail));
                        validationResults.add(Validation.handleRange(txtpsw.getText(),txtpsw,6,10,"Password"));
                        validationResults.add(Validation.handleRange(txtconfirmpsw.getText(), txtconfirmpsw,6, 10,"Confirm Password"));
                        validationResults.add(Validation.handleEmptyField(txtsecureans.getText(),txtsecureans));


                        if(txtpsw.getText().toString().equals(txtconfirmpsw.getText().toString()) == false) {
                            txtconfirmpsw.setError("Password & Confirm Password do not match");

                            validationResults.add(false);

                        }
                        if(validationResults.contains(false) == false){
                            submit();
                        }

                    }
                }

        );
    }

    public  void submit () {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                txtEmail=(EditText)findViewById(R.id.txt_email);
                txtpsw=(EditText)findViewById(R.id.txt_psw);
                txtsecureans=(EditText) findViewById(R.id.txt_secureans);
                Spinner securtiyQuestion =  (Spinner)findViewById(R.id.spinner_security_question);
                Spinner type = (Spinner)findViewById(R.id.spinner_type);

                HashMap <String, String> mapping = new HashMap<>();
                mapping.put("Food Blogger", "blogger");
                mapping.put("User", "user");
                mapping.put("Restaurant", "restaurant");

                String data = "email=" + txtEmail.getText() +
                        "&password=" + txtpsw.getText() +
                        "&securityQuestionAnswer=" + txtsecureans.getText() +
                        "&securityQuestion=" + securtiyQuestion.getSelectedItem().toString()
                        + "&type=" + mapping.get(type.getSelectedItem().toString());


                HttpURLConnection myConnection = Network.post("/registration", data, null);

                try {

                    int code = myConnection.getResponseCode();
                    if (code == 200) {
                        //show success dialoug
                        Intent myIntent = new Intent(MainActivity.this,
                                LoginActivity.class);
                        startActivity(myIntent);


                    } else if (code == 400){
                        InputStream responseBody = myConnection.getErrorStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("email")) {
                                Message msg=uihandler.obtainMessage();
                                msg.what=SHOW_ERROR;
                                msg.obj=jsonReader.nextString();
                                uihandler.sendMessage(msg);
                                break;
                            } else {
                                jsonReader.skipValue();
                            }
                        }

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
