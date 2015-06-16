package nl.mprog.rutger.aight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }


    // Allow user to create an event
    public void goMapsView(View view) {

        // create intent for create Event activity
        Intent go = new Intent(this, MapsActivity.class);

        // Go to MapsActivity
        startActivity(go);
        finish();
    }
}

