package nl.mprog.rutger.aight;
/**
 * Rutger van de Lagemaat
 * Minor programmeren UvA
 * 10265732
 * zabilos@gmail.com
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends FragmentActivity {

    static float PUSH_SEARCH_DISTANCE = 3500;

    static  int MAX_AIGHTS = 100;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // set translucent statusbar
        Window window = this.getWindow();
        setColor(window);

        // build map
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        placeMarkers.run();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // set standard map UI settings
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // go to current location on the map
        goToMyLocation();

        // put buttons on the map
        createFABS();

        // refresh markers every 15 seconds
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(placeMarkers, 0, 15, TimeUnit.SECONDS);
    }



    // run this every 15 seconds
    Runnable placeMarkers = new Runnable() {
        public void run() {
            // look for current events
            parseQuery();

            // update user location
            storeCurrentLocation();
        }
    };

    public void parseQuery() {
        // get current time
        final Date currentTime = new Date();
        final long currentTimeSecs = currentTime.getTime();

        // get latest user location
        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint currentLocation = user.getParseGeoPoint("userLocation");

        // get list of events from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.orderByDescending("createdAt");
        query.whereWithinKilometers("location", currentLocation, PUSH_SEARCH_DISTANCE / 1000);
        query.setLimit(MAX_AIGHTS);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                // get all markers off of map before creating current ones
                mMap.clear();

                for (int i = 0; i < list.size(); i++) {
                    // get event activity
                    ParseObject activity = list.get(i);

                    // get time of the event
                    final Date eventTime = activity.getCreatedAt();
                    long eventTimeSecs = eventTime.getTime();

                    // get other data of event
                    ParseGeoPoint point = activity.getParseGeoPoint("location");
                    String description = activity.getString("description");
                    Integer duration = activity.getInt("duration");
                    String activityUser = activity.getString("username");

                    // calculate time since activity was created
                    int eventAgeInSecs = (int) ((currentTimeSecs - eventTimeSecs) / 1000);

                    // only add active events
                    if (eventAgeInSecs <= 5400 && eventAgeInSecs < duration * 60) {
                        putMarker(point, description, duration, activityUser, eventAgeInSecs);
                    }
                }
            }
        });
    }

    // add markers to the map and calculate distance
    public void putMarker(final ParseGeoPoint point, String description, Integer duration, final String user,
                          final int eventAgeInSecs) {

        // add event marker
        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(),
                point.getLongitude())).title((String) user)
                .snippet(description + getString(R.string.time_left, (duration - eventAgeInSecs / 60)))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_red)));
    }


    // Allow user to create an event
    public void goCreateEvent(View view) {
        Intent go = new Intent(MapsActivity.this, CreateEventActivity.class);
        startActivity(go);
    }

    // get location and zoom in to it
    private void goToMyLocation() {
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                // zoom map to current location
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);

        // prompt user to turn location on if not found
        if (!myLocation.gps_enabled && !myLocation.network_enabled) {
            promptGPS(this);
        }
    }

    // called periodically to update user location on parse
    private void storeCurrentLocation() {
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                // Update last known user location
                ParseUser user = ParseUser.getCurrentUser();
                if (user == null) {
                    return;
                }
                ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(),
                        location.getLongitude());
                user.put("userLocation", userLocation);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(">>>>", "save location error " + e);
                    }
                });
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }

    // prompts user to turn location on
    public static void promptGPS(final Activity activity) {
        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Your location is unavailable. \nTurn GPS on?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    // create floating action buttons (FABs) for current location and logout
    public void createFABS() {
        ImageButton fabLocate = (ImageButton) findViewById(R.id.fablocate);
        fabLocate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToMyLocation();
            }
        });
        Button fablogOut = (Button) findViewById(R.id.fablogout);
        fablogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logOut(v);
            }
        });
    }

    // allow a user to log out
    private void logOut(View view) {
        ParseUser.logOut();

        // go to welcome activity
        Intent go = new Intent(this, WelcomeActivity.class);
        startActivity(go);
        finish();
    }


    // make status bar transparent on android 5.0 and higher
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    final void setColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.argb((int) (0.2 * 255.0f), 0, 0, 0));
        }
    }

}
