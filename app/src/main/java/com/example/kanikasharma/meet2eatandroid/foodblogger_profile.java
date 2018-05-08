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
import java.util.HashMap;

import utility.Alert;
import utility.Network;
import utility.SessionManagement;
import validation.Validation;

public class foodblogger_profile extends AppCompatActivity {


    final static int SHOW_DETAILS=1;
    final static int SHOW_ERROR=2;
    final static int SHOW_SUCCESS=3;

    final Handler uihandler=new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == SHOW_DETAILS) {

                HashMap mapping = (HashMap)msg.obj;
                for(Object key : mapping.keySet()){

                    String value = mapping.get(key).toString();

                    if(key.equals("email")) {
                        txtemail.setText(value);
                    }else if(key.equals("contactNo")){
                        txtcontactno.setText(value);
                     }else if(key.equals("age")) {
                        txtage.setText(value);
                    } else if(key.equals("address")){
                        txtaddress.setText(value);
                    } else if(key.equals("experience")){
                        txtexperience.setText(value);
                    } else if(key.equals("socialProfile")){
                        txtsocialprofile.setText(value);
                    } else if(key.equals("name")) {
                        txtname.setText(value);
                    }
                }



            } else if(msg.what == SHOW_ERROR)  {
                String message=(String)msg.obj;
                txtemail.setError((String)msg.obj);
            }else if(msg.what == SHOW_SUCCESS)  {
                Alert.showSuccess(foodblogger_profile.this,(String)msg.obj);
            }


        }
    };


    Toolbar toolbar;
    EditText txtname;
    EditText txtemail;
    EditText txtcontactno;
    EditText txtsocialprofile;
    EditText txtexperience;
    EditText txtage;
    EditText txtaddress;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodblogger_profile);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        txtname=(EditText)findViewById(R.id.txt_name);
        txtemail=(EditText)findViewById(R.id.txt_email);
        txtcontactno=(EditText)findViewById(R.id.txt_ContactNo);
        txtsocialprofile=(EditText)findViewById(R.id.txt_socialprofile);
        txtexperience=(EditText)findViewById(R.id.txt_experience);
        txtage=(EditText)findViewById(R.id.txt_Age);
        txtaddress=(EditText)findViewById(R.id.txt_address) ;
        button=(Button)findViewById(R.id.btn_Update);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Profile");
        fetchdetails();;


          button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {


                  ArrayList<Boolean> validationResults = new ArrayList<Boolean>();

                  validationResults.add(Validation.handleEmptyField(txtemail.getText(), txtemail));
                  validationResults.add(Validation.handleEmptyField(txtname.getText(), txtname));
                  //validationResults.add(Validation.handleEmptyField(txtcontactno.getText(), txtcontactno));
                  validationResults.add(Validation.handleEmptyField(txtage.getText(), txtage));
                  validationResults.add(Validation.handleEmptyField(txtaddress.getText(), txtaddress));
                  validationResults.add(Validation.handleEmptyField(txtexperience.getText(), txtexperience));
                  validationResults.add(Validation.handleEmptyField(txtsocialprofile.getText(),txtsocialprofile));
                  validationResults.add(Validation.handleExactLength(txtcontactno.getText(),txtcontactno,10, "Contact No"));

                  if (validationResults.contains(false) == false) {
                      update();
                  }


              }
          });



    }

    public void fetchdetails() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                SessionManagement session = new SessionManagement(getApplicationContext());
                HttpURLConnection myConnection= Network.get("/blogger",null, session.getAuthToken());
                try
                {
                    int code = myConnection.getResponseCode();
                    if(code == 200){
                        InputStream responsebody=myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                         HashMap<String,Object> mapping=new HashMap<String, Object>() ;

                         jsonReader.beginObject();
                         while(jsonReader.hasNext()){

                             String key = jsonReader.nextName();
                             if(key.equals("age") || key.equals("experience")){
                                 mapping.put(key,jsonReader.nextInt());
                             } else{
                                 mapping.put(key,jsonReader.nextString());
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
                String data="email=" + txtemail.getText() + "&age=" + txtage.getText() + "&contactNo=" + txtcontactno.getText() + "&name=" + txtname.getText() + "&socialProfile=" + txtsocialprofile.getText() + "&experience=" + txtexperience.getText() + "&address=" + txtaddress.getText();
                HttpURLConnection myConnection = Network.put("/blogger",data,session.getAuthToken());

                try{
                    int code= myConnection.getResponseCode();
                    Message msg = uihandler.obtainMessage();
                    if(code == 400) {
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
                        session.setProfileComplete(true);
                    }



                    } catch (IOException e){
                    System.out.println(e.getMessage());
                } finally {
                   myConnection.disconnect();
                }


            }
        });
    }


}
