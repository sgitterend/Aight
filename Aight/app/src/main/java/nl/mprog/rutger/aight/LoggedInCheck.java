package nl.mprog.rutger.aight;
/**
 * Rutger van de Lagemaat
 * Minor programmeren UvA
 * 10265732
 * zabilos@gmail.com
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

        // add pointer to user to installation to enable notifications
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", currentUser);
            installation.saveInBackground();
        }

        // Check if there is current user info
        if (currentUser != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, MapsActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        finish();
    }

}