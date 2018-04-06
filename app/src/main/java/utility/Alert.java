package utility;

import android.app.AlertDialog;

import android.content.Context;
import android.R;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by kanikasharma on 05/02/18.
 */

public class Alert {
    public static void showError (Context context , String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error !");
        alertDialog.setIcon(R.drawable.stat_notify_error);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    public static void showSuccess(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Success !");
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    public static void logoutConfirmation(final Context context, String message, final SessionManagement session) {
        new AlertDialog.Builder(context).setTitle("Please confirm")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        session.logout();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
