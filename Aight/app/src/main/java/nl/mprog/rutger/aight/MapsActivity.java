package nl.mprog.rutger.aight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
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
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // set translucent statusbar
        Window window = this.getWindow();
        setColor(window);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "41OkLo6j1hdKZsx1n1iGfvFtwRALWLerZ45glOZ8", "zXSgVFnOxCpRktMpvdTjGQ5YKObO69qqj9bFdNNm");
        setUpMapIfNeeded();
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

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // make a fab button to locate current position
        ImageButton fabLocate;
        fabLocate = (ImageButton) findViewById(R.id.fablocate);

        fabLocate.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 getMyLocation();
             }
         });

        /** the following lines are based on this tutorial:
         *  http://bit.ly/1KoNLoA
         *
         *  Get LocationManager object from System Service LOCATION_SERVICE */
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = lm.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = lm.getLastKnownLocation(provider);

        // Get latitude of the current location
        final double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        final double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng UserLocation = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UserLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        // get list of events from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");

        // query.whereLessThan("createdAt", )

        final Date currentTime = new Date();
        final long currentTimeSecs = currentTime.getTime();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                for (int i = 0; i < list.size(); i++) {
                    // get event object
                    ParseObject object = list.get(i);

                    // get time of the event
                    final Date eventTime = object.getCreatedAt();
                    long eventTimeSecs = eventTime.getTime();

                    // get other data of event
                    ParseGeoPoint point = object.getParseGeoPoint("location");
                    String username = object.getString("username");
                    String description = object.getString("description");
                    Integer duration = object.getInt("duration");
                    Long eventAgeInSecs = ((currentTimeSecs - eventTimeSecs) / 1000);
                    int eventAgeInMins = (int) (eventAgeInSecs / 60);

                    if (eventAgeInSecs <= 4200 && eventAgeInMins < duration) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(),
                                point.getLongitude())).title((String) username)
                                .snippet((String) (description + " - " + (duration - eventAgeInMins) + "m left"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_red)));
                    }
                }
            }
        });


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
        mMap = null;
    }

    private void getMyLocation() {
        // Get location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        Location myLocation = lm.getLastKnownLocation(provider);
        final double latitude = myLocation.getLatitude();
        final double longitude = myLocation.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
    }

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
