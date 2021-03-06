package nl.mprog.rutger.aight;
/**
 * Rutger van de Lagemaat
 * Minor programmeren UvA
 * 10265732
 * zabilos@gmail.com
 */

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class CreateEventActivity extends Activity {

    final int MAX_CHARS = 40;

    final int TIME_STEPS_MINUTES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // create slider and textview for intended aight length
        SeekBar timeSlider = (SeekBar) findViewById(R.id.timeslider);
        final TextView currentTime = (TextView) findViewById(R.id.chosentime);

        // show the standard time (min time = 1 min, steps of 5 min)
        currentTime.setText(timeSlider.getProgress() * TIME_STEPS_MINUTES + " minutes");

        timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    currentTime.setText("1 minute");
                } else {
                    currentTime.setText(String.valueOf(progress * TIME_STEPS_MINUTES) + " minutes");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        // Make sure user input is limited to 40 chars
        final TextView charCount = (TextView) findViewById(R.id.charCount);
        final EditText eventDescription = (EditText) findViewById(R.id.event_description);

        // display characters left to describe aight
        charCount.setText(Integer.toString(MAX_CHARS));

        eventDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // display live changes of number of chars
                charCount.setText(Integer.toString(MAX_CHARS - start - count));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void sendEventToParse(View view) {

        // get aight description
        EditText editText = (EditText) findViewById(R.id.event_description);
        String message = editText.getText().toString();

        // get user duration
        SeekBar timeSlider = (SeekBar) findViewById(R.id.timeslider);
        int duration = timeSlider.getProgress();

        // translate slider value to actual time
        if (duration > 0) {
            duration = duration * TIME_STEPS_MINUTES;
        }
        else {
            duration = 1;
        }

        // store event variables
        ParseObject placeObject = new ParseObject("Event");

        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            return;
        }

        // store data in parse object
        ParseGeoPoint point = user.getParseGeoPoint("userLocation");
        String userName = user.getUsername();
        placeObject.put("username", userName);
        placeObject.put("duration", duration);
        placeObject.put("description", message);
        placeObject.put("location", point);

        // save object to parse
        placeObject.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // things are going fine
                } else {
                    done(e);
                }
            }
        });
        finish();
    }

}

