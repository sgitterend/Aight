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


    // Either let user log in or create an account
    public void goLogIn(View view) {

        // create intent for create Event activity
        Intent go = new Intent(this, LogIn.class);

        // Go to login screen
        startActivity(go);
    }
    public void goSignUp(View view) {

        // create intent for create Event activity
        Intent go = new Intent(this, SignUp.class);

        // Go to go to sign up screen
        startActivity(go);
    }
}

