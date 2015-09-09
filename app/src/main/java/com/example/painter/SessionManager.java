package com.example.painter;

/**
 * Created by §d¶®µ® on 2015/8/20.
 */

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidGreeve";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_NAME = "name";
    public static final String KEY_GALLERYID = "galleryID";
    public static final String KEY_GALLERYPUBLIC = "galleryPublic";
    public static final String KEY_FriendListID = "friendListID";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String password, String name, String galleryID, String galleryPublic) {

//       , String name, String galleryID, String galleryPublic, String friendlistID

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        editor.putString(KEY_NAME, name);

        editor.putString(KEY_GALLERYPUBLIC, galleryPublic);
//        editor.putString(KEY_FriendListID, friendlistID);

        // commit changes
        editor.commit();
    }

    /* Check login method wil check user login status
             If false it will redirect user to login page
            Else won't do anything  */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LogInActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    //Get stored session data

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        // user email
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_GALLERYID, pref.getString(KEY_GALLERYID, null));
        user.put(KEY_GALLERYPUBLIC, pref.getString(KEY_GALLERYPUBLIC, null));
//        user.put(KEY_FriendListID, pref.getString(KEY_FriendListID, null));


        // return user
        return user;
    }

    // Clear session details

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LogInActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    // Quick check for login

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}


