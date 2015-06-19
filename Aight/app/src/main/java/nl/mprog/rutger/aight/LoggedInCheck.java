package nl.mprog.rutger.aight;


 /* taken from parse example app "anywall"
  * https://github.com/ParsePlatform/AnyWall/tree/master/AnyWall-android
  */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class LoggedInCheck extends Activity {
    public LoggedInCheck() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize parse database connection
        Parse.initialize(this, "41OkLo6j1hdKZsx1n1iGfvFtwRALWLerZ45glOZ8", "zXSgVFnOxCpRktMpvdTjGQ5YKObO69qqj9bFdNNm");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, MapsActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, Welcome.class));
        }
        finish();
    }

}