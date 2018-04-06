package mapping;

import android.content.Intent;
import android.util.JsonReader;

import com.example.kanikasharma.meet2eatandroid.CreateMeetup;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by kanikasharma on 15/03/18.
 */

public class MeetupMapping {
    public String title;
    public String type;
    public Long meetupDate;
    public Integer maxNoOfAtendee;
    public Integer duration;
    public Integer minNoOfAtendee;
    public String  description;
    public String  status;
    public Boolean restaurantConfirmed;
    public Integer uid;
    public String name;
    public String address;


    public MeetupMapping(String title,String type,Long meetupDate,Integer maxNoOfAtendee,Integer duration,Integer minNoOfAtendee,String description,String status,Boolean restaurantConfirmed,Integer uid, String name, String address){
        this.title=title;
        this.type=type;
        this.meetupDate=meetupDate;
        this.minNoOfAtendee=minNoOfAtendee;
        this.maxNoOfAtendee=maxNoOfAtendee;
        this.description=description;
        this.duration=duration;
        this.status=status;
        this.restaurantConfirmed=restaurantConfirmed;
        this.uid=uid;
        this.name = name;
        this.address = address;

    }
    public static MeetupMapping getObject(JsonReader reader){
        String title=null,type=null,status=null,description=null;
        int maxNoOfAttendee=0,minNoOfAttendee=0,uid=0,duration=0;
        Boolean restaurantConfirmed=null;
        Long meetupDate=null;
        String name = null, address=null;

        try{
            reader.beginObject();
            while (reader.hasNext()){
                String key=reader.nextName();
                if(key.equals("title")){
                    title=reader.nextString();
                }else if(key.equals("type")){
                    type=reader.nextString();
                }else if(key.equals("meetupDate")){
                    meetupDate=reader.nextLong();
                }else if(key.equals("maxNoOfAttendee")){
                    maxNoOfAttendee=reader.nextInt();
                }else if(key.equals("minNoOfAttendee")){
                    minNoOfAttendee=reader.nextInt();
                }else if(key.equals("description")){
                    description=reader.nextString();
                }else if(key.equals("duration")){
                    duration=reader.nextInt();
                }else if(key.equals("status")){
                    status=reader.nextString();
                }else if(key.equals("restaurantConfirmed")){
                    restaurantConfirmed=reader.nextBoolean();
                }else if(key.equals("uid")){
                    uid= reader.nextInt();
                } else if (key.equals("restaurant")){
                    reader.beginObject();

                    while (reader.hasNext()){
                        key=reader.nextName();
                        if (key.equals("name")) {
                            name = reader.nextString();
                        } else if (key.equals("address")) {
                            address = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }

                    reader.endObject();
                } else  {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }catch (IOException e){

        }

        return  new MeetupMapping(title,type,meetupDate,maxNoOfAttendee,duration, minNoOfAttendee, description,status, restaurantConfirmed,uid, name, address);
    }

    public static ArrayList<MeetupMapping> fromJson(JsonReader reader){
        ArrayList<MeetupMapping> meetups=new ArrayList<MeetupMapping>();
        try{
            reader.beginArray();
            while (reader.hasNext()){
                meetups.add(getObject(reader));
            }
            reader.endArray();
        }catch (IOException e){

        }return meetups;
   }


}
