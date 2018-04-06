package mapping;
import android.util.JsonReader;

import org.json.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by kanikasharma on 03/03/18.
 */

public class RestaurantMapping {
    public String name;
    public String contactNo;
    public String address;
    public String type;
    public String website;
    public Boolean availabilityWeekday;
    public Boolean availabilityWeekend;
    public Integer startTime;
    public Integer endTime;
    public Integer seatingCapacity;
    public Integer uid;



    public RestaurantMapping(String name,int uid , String contactNo, String address, String type, String website, int startTime, int endTime, int seatingCapacity, boolean availabilityWeekday, boolean availabilityWeekend){

        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
        this.type = type;
        this.website = website;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seatingCapacity = seatingCapacity;
        this.availabilityWeekday = availabilityWeekday;
        this.availabilityWeekend = availabilityWeekend;
        this.uid = uid;
    }

    public static RestaurantMapping getObject(JsonReader reader) {
        String name=null,contactNo=null,address=null, type=null, website=null;
        int startTime=0, endTime=0, seatingCapacity=0, uid=0;
        Boolean availabilityWeekday=null, availabilityWeekend=null;


        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("name")) {
                    name = reader.nextString();
                } else if (key.equals("contactNo")) {
                    contactNo = reader.nextString();
                } else if (key.equals("address")) {
                    address = reader.nextString();
                } else if (key.equals("type")) {
                    type = reader.nextString();
                } else if (key.equals("website")) {
                    website = reader.nextString();
                } else if (key.equals("startTime")) {
                    startTime = reader.nextInt();
                } else if (key.equals("endTime")) {
                    endTime = reader.nextInt();
                } else if (key.equals("seatingCapacity")){
                    seatingCapacity = reader.nextInt();
                } else if (key.equals("availabilityWeekday")) {
                    availabilityWeekday = reader.nextBoolean();
                } else if (key.equals("availabilityWeekend")) {
                    availabilityWeekend = reader.nextBoolean();
                } else if (key.equals("uid")) {
                    uid = reader.nextInt();
                }else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch(IOException e) {

        }

        return new RestaurantMapping(name,uid, contactNo, address, type, website, startTime, endTime, seatingCapacity, availabilityWeekday, availabilityWeekend);
    }

    public static ArrayList<RestaurantMapping> fromJson(JsonReader reader) {
        ArrayList<RestaurantMapping> restaurants = new ArrayList<RestaurantMapping>();

        try {
            reader.beginArray();
            while(reader.hasNext()) {
                restaurants.add(getObject(reader));
            }
            reader.endArray();
        }catch(IOException e){

        }
        return restaurants;
    }
}

