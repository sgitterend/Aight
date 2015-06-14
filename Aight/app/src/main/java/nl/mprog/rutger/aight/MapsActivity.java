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
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

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

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bur");
        testObject.saveInBackground();


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
        String UserLocationString = String.valueOf(UserLocation.toString());

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UserLocation));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                //Log.d("locations", list.toString());
                for (int i = 0; i < list.size(); i++) {

                    // krijg dit nog niet aan de praat
//                    double lati = Double.parseDouble(list.get(i).getParseGeoPoint("lat").toString());
//                    double longi = Double.parseDouble(list.get(i).getParseGeoPoint("long").toString());
//                    String user = list.get(i).getParseGeoPoint("User").toString();
//                    String description = list.get(i).getParseGeoPoint("Description").toString();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(i, i)).title((String) "ja").snippet((String) "hoor"));
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
