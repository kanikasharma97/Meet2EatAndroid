package com.example.kanikasharma.meet2eatandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.*;
import android.widget.*;
import android.content.*;
import android.view.*;
import android.os.*;

import mapping.RestaurantMapping;
import utility.*;

public class restaurant_list extends AppCompatActivity {
    ListView listView;
    ArrayList <RestaurantMapping> mappings;

    final static int SHOW_DETAILS=1;
    final Handler uiHandler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==SHOW_DETAILS){
                mappings = (ArrayList <RestaurantMapping>)msg.obj;
                RestaurantAdapter adapter = new RestaurantAdapter(restaurant_list.this, mappings);

                listView.setAdapter(adapter);

            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list);
        fetchdetails();
        listView = (ListView) findViewById(R.id.lvRestaurants);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestaurantMapping item = mappings.get(i);
                Intent myIntent=new Intent(restaurant_list.this,restaurantDetails.class);
                myIntent.putExtra("name",item.name);
                myIntent.putExtra("contactNo",item.contactNo);
                myIntent.putExtra("address",item.address);
                myIntent.putExtra("website",item.website);
                myIntent.putExtra("availabilityWeekday",item.availabilityWeekday);
                myIntent.putExtra("availabilityWeekend",item.availabilityWeekend);
                myIntent.putExtra("startTime",item.startTime);
                myIntent.putExtra("endTime",item.endTime);
                myIntent.putExtra("seatingCapacity",item.seatingCapacity);
                myIntent.putExtra("uid", item.uid);
                startActivity(myIntent);
            }
        });

    }


    public void fetchdetails(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session = new SessionManagement(getApplicationContext());
                HttpURLConnection myConnection= Network.get("/restaurants",null, session.getAuthToken());
                try{
                    int code=myConnection.getResponseCode();
                    if(code==200){
                        InputStream responsebody=myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);


                        ArrayList<RestaurantMapping> restaurantMappings = RestaurantMapping.fromJson(jsonReader);
                        Message msg=uiHandler.obtainMessage();
                        msg.what=SHOW_DETAILS;
                        msg.obj=restaurantMappings;
                        uiHandler.sendMessage(msg);
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

    public class RestaurantAdapter extends ArrayAdapter<RestaurantMapping> {
        public RestaurantAdapter(Context context, ArrayList<RestaurantMapping> restaurants) {
            super(context, 0, restaurants);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RestaurantMapping restaurant = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.resturant_template, parent, false);
            }
            TextView name = convertView.findViewById(R.id.restaurantName);
            TextView address = convertView.findViewById(R.id.restaurantAddress);
            TextView type=convertView.findViewById(R.id.restaurantType);
            TextView capacity=convertView.findViewById(R.id.restaurantCapacity);
            TextView availability = convertView.findViewById(R.id.restaurantAvl);
            TextView timings = convertView.findViewById(R.id.restaurantTimings);

            name.setText(restaurant.name);
            address.setText(restaurant.address);
            type.setText(restaurant.type);
            capacity.setText(String.valueOf(restaurant.seatingCapacity));

            String availDisplay;
            availability.setText(utility.Display.availDisplay(restaurant.availabilityWeekday, restaurant.availabilityWeekend));

            timings.setText(utility.Display.timeDisplay(restaurant.startTime,restaurant.endTime));
            return convertView;
        }
    }
}
