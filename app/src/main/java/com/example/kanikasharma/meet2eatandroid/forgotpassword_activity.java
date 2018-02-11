package com.example.kanikasharma.meet2eatandroid;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import utility.Network;

public class forgotpassword_activity extends AppCompatActivity {

    Button button;
    EditText txtemail;
    final static int SHOW_ERROR=1;
    final Handler uihandler=new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == SHOW_ERROR)
                txtemail.setError((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword_activity);

        button=(Button)findViewById(R.id.btn_next);
        txtemail=(EditText)findViewById(R.id.txt_email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }


    public void submit(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                String data="email=" + txtemail.getText();
                HttpURLConnection myConnection= Network.get("/registration",data);
                try
                {
                    int code=myConnection.getResponseCode();
                    if(code==200){
                        Intent myintent=new Intent(forgotpassword_activity.this,Securityquestion.class);
                        myintent.putExtra("email", txtemail.getText().toString());


                        InputStream responsebody=myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject();
                        while (jsonReader.hasNext()){
                            String key=jsonReader.nextName();
                            if(key.equals("securityQuestion") || key.equals("securityQuestionAnswer")){
                                myintent.putExtra(key,jsonReader.nextString());

                            }else{
                                jsonReader.skipValue();
                            }
                        }
                        startActivity(myintent);
                    }else if(code==400){

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
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }catch(NullPointerException e){
                    System.out.println(e.getMessage());
                }finally {
                    myConnection.disconnect();
                }
            }
        });
    }

}
