package com.example.kanikasharma.meet2eatandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import mapping.MeetupMapping;
import mapping.RestaurantMapping;
import utility.Alert;
import utility.Network;
import utility.SessionManagement;

import android.view.View;
import android.widget.TextView;
import java.util.Date;
import java.text.*;

import org.w3c.dom.Text;

public class meetup_list extends AppCompatActivity {


    final static int SHOW_DETAILS=1;
    ListView listView;
    ArrayList<MeetupMapping> mappings;

    final Handler uiHandler=new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==SHOW_DETAILS){

                mappings = (ArrayList <MeetupMapping>)msg.obj;
                if (mappings.size() == 0) {
                    Alert.showError(meetup_list.this, "There are no meetups to show.");
                } else {
                    meetup_list.MeetupAdapter adapter = new MeetupAdapter(meetup_list.this, mappings);
                    listView.setAdapter(adapter);
                }

            }
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetup_list);
        listView = (ListView) findViewById(R.id.lvMeetups);
        final SessionManagement session = new SessionManagement(getApplicationContext());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MeetupMapping item=mappings.get(i);
                Intent myIntent= session.getUserType().equals("blogger") ?  new Intent(meetup_list.this, Meetup_update.class) : new Intent(meetup_list.this,meetupdetails.class);
                myIntent.putExtra("title",item.title);
                myIntent.putExtra("type",item.type);
                myIntent.putExtra("meetupDate",item.meetupDate);
                myIntent.putExtra("duration",item.duration);
                myIntent.putExtra("description",item.description);
                myIntent.putExtra("name",item.name);
                myIntent.putExtra("address",item.address);
                myIntent.putExtra("duration",item.duration);
                myIntent.putExtra("uid", item.uid);
                myIntent.putExtra("confirmation", item.restaurantConfirmed);
                myIntent.putExtra("minNoOfAttendee", item.minNoOfAtendee);
                myIntent.putExtra("maxNoOfAttendee", item.maxNoOfAtendee);

                DateFormat df = new SimpleDateFormat("dd MMM yyyy  HH:mm");
                myIntent.putExtra("meetupDate", df.format(item.meetupDate));


                startActivity(myIntent);


            }
        });
        fetchdetails();
    }

    public void fetchdetails() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SessionManagement session = new SessionManagement(getApplicationContext());
                HttpURLConnection myConnection = Network.get("/meetups", null, session.getAuthToken());
                try {
                    int code = myConnection.getResponseCode();
                    if (code == 200) {
                        InputStream responsebody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responsebody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);


                        ArrayList<MeetupMapping> meetupMappings = MeetupMapping.fromJson(jsonReader);
                        Message msg = uiHandler.obtainMessage();
                        msg.what = SHOW_DETAILS;
                        msg.obj = meetupMappings;
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

    public class MeetupAdapter extends ArrayAdapter<MeetupMapping> {
        public MeetupAdapter(Context context, ArrayList<MeetupMapping> meetups) {
            super(context, 0, meetups);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MeetupMapping meetup = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.meetup_template, parent, false);
            }

            TextView title = convertView.findViewById(R.id.meetupTitle);
            TextView name = convertView.findViewById(R.id.restaurantName);
            TextView address = convertView.findViewById(R.id.restaurantAddress);
            TextView meetupdatetime = convertView.findViewById(R.id.meetupDateTime);
            TextView maxNoOfAtendee = convertView.findViewById(R.id.maxNoOfAtendee);
            TextView status = convertView.findViewById(R.id.status);
            final Switch checkin = convertView.findViewById(R.id.checkin);
            Switch confirm=convertView.findViewById(R.id.confirm);

            DateFormat df = new SimpleDateFormat("dd MMM yyyy  HH:mm");

            title.setText(meetup.title);
            meetupdatetime.setText(df.format(meetup.meetupDate));
            maxNoOfAtendee.setText(String.valueOf(meetup.maxNoOfAtendee));
            status.setText(meetup.status);
            address.setText(meetup.address);
            name.setText(meetup.name);
            return convertView;
        }

    }
}