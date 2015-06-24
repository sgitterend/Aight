package nl.mprog.rutger.aight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }


    // Either let user log in or create an account
    public void goLogIn(View view) {

        // create intent for create Event activity
        Intent go = new Intent(this, LogInActivity.class);

        // Go to login screen
        startActivity(go);
    }
    public void goSignUp(View view) {

        // create intent for create Event activity
        Intent go = new Intent(this, SignUpActivity.class);

        // Go to go to sign up screen
        startActivity(go);
    }
}

