package nl.mprog.rutger.aight;
/**
 * Rutger van de Lagemaat
 * Minor programmeren UvA
 * 10265732
 * zabilos@gmail.com
 */

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;


public class Application extends android.app.Application {
    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize parse database connection
        Parse.initialize(this, "41OkLo6j1hdKZsx1n1iGfvFtwRALWLerZ45glOZ8", "zXSgVFnOxCpRktMpvdTjGQ5YKObO69qqj9bFdNNm");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("all", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }
}
