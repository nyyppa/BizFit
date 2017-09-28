package com.bizfit.release.activities;

/**
 * Displays the Bizfit banner.
 * <p/>
 * Disabled for more convenient debugging and testing.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bizfit.release.R;

public class SplashScreen extends Activity {

    // Splash screen timer in ms.
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity2.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
