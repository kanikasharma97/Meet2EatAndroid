package utility;

/**
 * Created by kanikasharma on 25/02/18.
 */

import java.util.HashMap;
import com.example.kanikasharma.meet2eatandroid.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagement {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "pref";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String KEY_AUTH_TOKEN =  "authToken";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_PROFILE_COMPLETE = "profileComplete";

    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String authToken, String userType, boolean isProfileComplete) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putBoolean(KEY_PROFILE_COMPLETE, isProfileComplete);
        editor.commit();
    }


    public void checkLogin() {
        if (!this.isLoggedIn()){
            startLoginActivity();
        }
    }

    public void startLoginActivity () {
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void logout () {
        editor.clear();
        editor.commit();
        startLoginActivity();
    }

    public String getAuthToken () {
        return pref.getString(KEY_AUTH_TOKEN, null);
    }
    public String getUserType () {return pref.getString(KEY_USER_TYPE, null);}

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
    public boolean getProfileComplete () {return pref.getBoolean(KEY_PROFILE_COMPLETE, false);}
    public void setProfileComplete (boolean iscomplete) {
        editor.putBoolean(KEY_PROFILE_COMPLETE, iscomplete);
        editor.commit();
    }
}
