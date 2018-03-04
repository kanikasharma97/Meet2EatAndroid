package utility;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kanikasharma on 04/02/18.
 */

public class Network {
    public static final String hostAddress = "http://10.0.2.2:9000";

    public static HttpURLConnection post (String url, String data, String authToken) {
        HttpURLConnection myConnection = null;
        url = hostAddress + url;

        try {
            URL endPoint = new URL(url);
            myConnection = (HttpURLConnection) endPoint.openConnection();
            myConnection.setRequestMethod("POST");
            if (authToken != null) {
                myConnection.setRequestProperty("authToken", authToken);
            }
            myConnection.setDoOutput(true);
            myConnection.getOutputStream().write(data.getBytes());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return myConnection;
    }

    public static HttpURLConnection get(String url, String data, String authToken) {
        HttpURLConnection myConnection = null;
        if (data != null) {
            url = hostAddress + url + "?" + data;
        } else {
            url = hostAddress + url;
        }

        try {
            URL endPoint = new URL(url);
            myConnection = (HttpURLConnection) endPoint.openConnection();
            myConnection.setRequestProperty("authToken", authToken);
            //myConnection.setUseCaches(false);
            //myConnection.setAllowUserInteraction(false);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return myConnection;
    }

    public static HttpURLConnection put(String url, String data, String authToken) {
        HttpURLConnection myConnection = null;
        url = hostAddress + url;

        try {
            URL endPoint = new URL(url);
            myConnection = (HttpURLConnection) endPoint.openConnection();
            myConnection.setRequestMethod("PUT");

            if (authToken != null) {
                myConnection.setRequestProperty("authToken", authToken);
            }

            myConnection.setDoOutput(true);
            myConnection.getOutputStream().write(data.getBytes());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return myConnection;
    }
}
