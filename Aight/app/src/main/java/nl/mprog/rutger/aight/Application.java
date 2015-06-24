package nl.mprog.rutger.aight;

/**
 * Created by Rutger on 23-6-2015.
 * Initializing parse here should prevent crashes
 */

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.parse.LocationCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;



public class Application extends android.app.Application {

    public Application() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

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

        ParsePush.subscribeInBackground("all");


        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                // update user location on parse
                ParseUser user = ParseUser.getCurrentUser();
                user.put("lastKnownLocation", location);
                user.put("test", "test");
                user.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // Saved successfully
                        } else {
                            Log.d(">>>", "" + e);
                        }
                    }
                });
            }
        };
    }
}
