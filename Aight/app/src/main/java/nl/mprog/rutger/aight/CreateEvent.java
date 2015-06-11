package nl.mprog.rutger.aight;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


public class CreateEvent extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // create slider and textview that is updated with current slider value
        SeekBar timeSlider = (SeekBar) findViewById(R.id.timeslider);
        final TextView currentTime = (TextView) findViewById(R.id.chosentime);
        final int stepfive = 5;

        // show the standard time (min time = 1 min, steps of 5 min)
        currentTime.setText(timeSlider.getProgress() * stepfive + " minutes");

        timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    currentTime.setText("1 minute");
                } else {
                    currentTime.setText(String.valueOf(progress * stepfive) + " minutes");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        EditText eventDescription = (EditText) findViewById(R.id.event_description);
        final TextView descriptionCount = (TextView) findViewById(R.id.charCount);

        descriptionCount.setText(140 - eventDescription.length());

        eventDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                descriptionCount.setText(String.valueOf(s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        });


    }
}

