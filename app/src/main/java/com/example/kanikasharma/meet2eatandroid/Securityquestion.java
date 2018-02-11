package com.example.kanikasharma.meet2eatandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import utility.Network;
import validation.Validation;

import android.widget.Button;

public class Securityquestion extends AppCompatActivity {
    EditText txtnewpassword;
    EditText txtconfirmpassword;
    Button btnsubmit;
    EditText txtsecurityanswer;
    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_securityquestion);
        final String securityquestion = getIntent().getStringExtra("securityQuestion");
        final TextView txtsecurityquestion = (TextView)findViewById(R.id.txt_securityquestion);
        txtsecurityquestion.setText(securityquestion);
        final String securityanswerstring=getIntent().getStringExtra("securityQuestionAnswer");
        txtnewpassword=(EditText)findViewById(R.id.txt_newpsw);
        txtconfirmpassword=(EditText)findViewById(R.id.txt_confirmpsw);
        txtsecurityanswer=(EditText)findViewById(R.id.txt_secureans);
        btnsubmit=(Button)findViewById(R.id.btn_submit);
        email = getIntent().getStringExtra("email");

        btnsubmit.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        ArrayList<Boolean> validationResults = new ArrayList<Boolean>();

                        validationResults.add(Validation.handleEmptyField(txtnewpassword.getText(), txtnewpassword));
                        validationResults.add(Validation.handleSufficientLength(txtnewpassword.getText(), txtnewpassword, 6));
                        validationResults.add(Validation.handleSufficientLength(txtconfirmpassword.getText(), txtconfirmpassword, 6));
                        validationResults.add(Validation.handleEmptyField(txtsecurityanswer.getText(), txtsecurityanswer));

                        if (txtsecurityanswer.getText().toString().equals(securityanswerstring) == false) {
                            validationResults.add(false);
                        }

                        if (txtnewpassword.getText().toString().equals(txtconfirmpassword.getText().toString()) == false) {
                            txtconfirmpassword.setError("Password & Confirm Password do not match");
                            validationResults.add(false);
                        }

                        if (validationResults.contains(false) == false) {
                            submit();
                        }


                    }
                }

        );

    }

    public void submit(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {



                String data="email=" + email + "&password=" + txtnewpassword.getText();
                HttpURLConnection myConnection=Network.put("/password",data);

                try{
                    int code=myConnection.getResponseCode();
                    if(code == 200){
                        Intent myintent=new Intent(Securityquestion.this,LoginActivity.class);
                        startActivity(myintent);

                    }
                }catch (IOException e){

                } finally {
                    myConnection.disconnect();
                }



            }
        });
    }
}
