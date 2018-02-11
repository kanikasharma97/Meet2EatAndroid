package utility;

import android.app.AlertDialog;

import android.content.Context;
import android.R;

/**
 * Created by kanikasharma on 05/02/18.
 */

public class Alert {
    public static void showError (Context context , String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error !");
        alertDialog.setIcon(R.drawable.alert_dark_frame);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}
