package com.example.kanikasharma.meet2eatandroid;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import utility.Display;

public class restaurantDetails extends AppCompatActivity {
    TextView contactNo;
    TextView address;
    TextView name;
    TextView timings;
    TextView availability;
    TextView website;
    TextView capacity;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        Intent obj = getIntent();
        contactNo = (TextView)findViewById(R.id.txt_ContactNo);
        address = (TextView)findViewById(R.id.txt_address);
        name = (TextView)findViewById(R.id.txt_restaurantName);
        capacity = (TextView)findViewById(R.id.txt_capacity);
        website = (TextView)findViewById(R.id.txt_website);
        timings=(TextView)findViewById(R.id.txt_timings);
        availability = (TextView)findViewById(R.id.txt_availability);





        boolean weekday = obj.getExtras().getBoolean("availabilityWeekday");
        boolean weekend = obj.getExtras().getBoolean("availabilityWeekend");
        int startTime=obj.getExtras().getInt("startTime");
        int endtime=obj.getExtras().getInt("endTime");
        int capacityVal = obj.getExtras().getInt("seatingCapacity");
        final int uidvalue=obj.getExtras().getInt("uid");





        timings.setText(Display.timeDisplay(startTime, endtime));
        availability.setText(Display.availDisplay(weekday, weekend));
        contactNo.setText(obj.getStringExtra("contactNo"));
        name.setText(obj.getStringExtra("name"));
        website.setText(obj.getStringExtra("website"));
        capacity.setText(String.valueOf(capacityVal));
        address.setText(obj.getStringExtra("address"));


        FloatingActionButton add=findViewById(R.id.btn_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent=new Intent(restaurantDetails.this,CreateMeetup.class);
                myintent.putExtra("uid",uidvalue);
                startActivity(myintent);
            }
        });

    }

}
