package nl.mprog.rutger.aight;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;


import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends FragmentActivity {

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

        // get all notifications
        ParsePush.subscribeInBackground("all");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        // set standard map UI settings
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


        // go to current location on the map
        goToMyLocation();

        createFABS();
        // refresh markers every 15 seconds
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(placeMarkers, 0, 15, TimeUnit.SECONDS);

    }
















    // run this every 15 seconds
    Runnable placeMarkers = new Runnable() {
        public void run() {

             // get list of events from parse
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");

            // get current time
            final Date currentTime = new Date();
            final long currentTimeSecs = currentTime.getTime();



            //query current events
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {

                    // get all markers off of map before creating current ones
                    mMap.clear();

                    int count = list.size();
                    for (int i = 0; i < count; i++) {
                        // get event object
                        ParseObject object = list.get(i);

                        // get time of the event
                        final Date eventTime = object.getCreatedAt();
                        long eventTimeSecs = eventTime.getTime();

                        // get other data of event
                        ParseGeoPoint point = object.getParseGeoPoint("location");
                        String description = object.getString("description");
                        Integer duration = object.getInt("duration");
                        String user = object.getString("username");
                        int eventAgeInSecs = (int) ((currentTimeSecs - eventTimeSecs) / 1000);

                        // only add active events
                        if (eventAgeInSecs <= 4200 && eventAgeInSecs < duration * 60) {
                            putMarker(point, description, duration, user, eventAgeInSecs);
                        }
                    }
                }
            });
        }
    };










    public void putMarker(ParseGeoPoint point, String description, Integer duration, String user,
                          int eventAgeInSecs) {
//        public final Marker addMarker (MarkerOptions options)
        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(),
                point.getLongitude())).title((String) user)
                .snippet(description + getString(R.string.time_left, (duration - eventAgeInSecs / 60)))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_red)));

        // look for notifications
        if (ParseUser.getCurrentUser() != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();

            if (eventAgeInSecs < 15 && !user.equals(currentUser.getUsername())) {
                ParsePush push = new ParsePush();
                push.setChannel("all");
                push.setMessage(user + " has created an event!");
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(">>>", "" + e);
                    }
                });

//            final ParseGeoPoint pointLocation = point;
//            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
//                @Override
//                public void gotLocation(Location location) {
//                    // got location!
//                }
//            };
//            MyLocation myLocation = new MyLocation();
//            myLocation.getLocation(this, locationResult);

//
//            double latDifference = Math.pow((location.getLatitude() - pointLocation.getLatitude()), 2);
//            double longDifference = Math.pow((location.getLongitude() - pointLocation.getLongitude()),2);
//            final double distance = Math.pow((latDifference + longDifference), 0.5);
//
            }
        }
    }




    // Allow user to create an event
    public void goCreateEvent(View view) {

        // keep the users patience
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.getting_location));
        dialog.show();

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                Intent go = new Intent(MapsActivity.this, CreateEvent.class);
                Bundle b = new Bundle();
                b.putDouble("latitude", location.getLatitude());
                b.putDouble("longitude", location.getLongitude());

                // Go to CreateEvent activity
                go.putExtras(b);
                startActivity(go);
                dialog.hide();

            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);

        // prompt user to turn location on if not found
        if (!myLocation.gps_enabled && !myLocation.network_enabled) {
            promptGPS(this);
        }
    }

    // get location and zoom in to it
    private void goToMyLocation() {

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
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



    // allow a user to log out
    private void logOut(View view) {
        ParseUser.logOut();

        // go to welcome activity
        Intent go = new Intent(this, Welcome.class);
        startActivity(go);
        finish();
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

    // create floating action buttons for current location and logout
    public void createFABS() {
        // make a fab button to locate current position
        ImageButton fabLocate = (ImageButton) findViewById(R.id.fablocate);
        fabLocate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {goToMyLocation();}
        });

        // make a fab button to log out
        Button fablogOut = (Button) findViewById(R.id.fablogout);
        fablogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logOut(v);
            }
        });
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
    public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

        @Override
        protected Notification getNotification(Context context, Intent intent) {
            Notification notification = super.getNotification(context, intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notification.color = context.getResources().getColor(R.color.ColorPrimary);
            }
            return notification;
        }
    }
}
