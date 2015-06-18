package nl.mprog.rutger.aight;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


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

        createLocationRequest();

        // build map
        setUpMapIfNeeded();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        ...
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
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

        // make a fab button to locate current position
        ImageButton fabLocate = (ImageButton) findViewById(R.id.fablocate);
        fabLocate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getMyLocation();
            }
        });

        // make a fab button to log out
        Button fablogOut = (Button) findViewById(R.id.fablogout);
        fablogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logOut(v);
            }
        });

        // get current location on the map
        getMyLocation();

        // refresh markers every 3 seconds
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(placeMarkers, 0, 50, TimeUnit.SECONDS);

    }

    Runnable placeMarkers = new Runnable() {
        public void run() {
            // get list of events from parse
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");

            // get current time
            final Date currentTime = new Date();
            final long currentTimeSecs = currentTime.getTime();

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {

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

                        // only show current events
                        if (eventAgeInSecs <= 4200 && eventAgeInSecs < duration * 60) {
                            addMarker(point, description, duration, user, eventAgeInSecs);
                        }
                    }
                }
            });
        }
    };

    public void addMarker(ParseGeoPoint point, String description, Integer duration, String user,
                          int eventAgeInSecs) {


//        public final Marker addMarker (MarkerOptions options)
        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(),
                point.getLongitude())).title((String) user)
                .snippet(description + getString(R.string.time_left, (duration - eventAgeInSecs / 60)))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_red)));
    }

    // Allow user to create an event
    public void goCreateEvent(View view) {


        // create intent for create Event activity
        Intent go = new Intent(this, CreateEvent.class);

        // Get location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        Location myLocation = lm.getLastKnownLocation(provider);
        final double latitude = myLocation.getLatitude();
        final double longitude = myLocation.getLongitude();

        // give location along
        Bundle b = new Bundle();
        b.putDouble("latitude", latitude);
        b.putDouble("longitude", longitude);
        go.putExtras(b);

        // Go to CreateEvent activity
        startActivity(go);
    }

    // get location and zoom in to it
    private void getMyLocation() {


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            Log.d(">>>", " lm = " + lm);
        }
        else {
            Criteria criteria = new Criteria();
            String provider = lm.getBestProvider(criteria, true);
            Location myLocation = lm.getLastKnownLocation(provider);
            if (myLocation != null) {
                final double latitude = myLocation.getLatitude();
                final double longitude = myLocation.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
            }
            else {
                promptGPS(this);
            }
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


    protected void createLocationRequest() {

        // request location from google location services
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public static void promptGPS(final Activity activity)
    {
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



    // make status bar transparent on android 5.0 and higher
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    final void setColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.argb((int) (0.2 * 255.0f), 0, 0, 0));
        }
    }
}
