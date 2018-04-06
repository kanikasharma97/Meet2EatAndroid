package utility;

/**
 * Created by kanikasharma on 10/03/18.
 */

public class Display {
    public static String availDisplay (boolean availabilityWeekday, boolean availabilityWeekend) {
        String display = "";
        if (availabilityWeekday && availabilityWeekend) {
            display = "Mon to Sun";
        } else if (availabilityWeekday){
            display = "Mon to Fri";
        } else if (availabilityWeekend){
            display = "Sat to Sun";
        } else {
            display = "Not Available";
        }
        return display;
    }
    public static String timeDisplay(int startTime, int endTime){
        int startHour = startTime / 60;
        int startMin = startTime % 60;

        int endHour = endTime / 60;
        int endMin = endTime % 60;

        return String.format("%d:%d - %d:%d", startHour, startMin, endHour, endMin);
    }
}
