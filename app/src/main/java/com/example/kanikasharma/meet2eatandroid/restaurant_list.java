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
import utility.Network;

public class restaurant_list extends AppCompatActivity {

    final static int SHOW_DETAILS=1;
    final Handler uiHandler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==SHOW_DETAILS){
                ArrayList <RestaurantMapping> mappings = (ArrayList <RestaurantMapping>)msg.obj;
                RestaurantAdapter adapter = new RestaurantAdapter(restaurant_list.this, mappings);
                ListView listView = (ListView) findViewById(R.id.lvRestaurants);
                listView.setAdapter(adapter);
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list);
        fetchdetails();
    }


    public void fetchdetails(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection myConnection= Network.get("/restaurants",null, "123456");
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
            // fix this
            //capacity.setText(restaurant.seatingCapacity);

            String availDisplay;

            if (restaurant.availabilityWeekday && restaurant.availabilityWeekend) {
                availDisplay = "Mon to Sun";
            } else if (restaurant.availabilityWeekday){
                availDisplay = "Mon to Fri";
            } else if (restaurant.availabilityWeekend){
                availDisplay = "Sat to Sun";
            } else {
                availDisplay = "Not Available";
            }
            availability.setText(availDisplay);

            int startHour = restaurant.startTime / 60;
            int startMin = restaurant.startTime % 60;

            int endHour = restaurant.endTime / 60;
            int endMin = restaurant.endTime % 60;

            timings.setText(String.format("%d:%d - %d:%d", startHour, startMin, endHour, endMin));
            return convertView;
        }
    }
}
