package com.engineering.software.sapi.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {

    private static final int SPLASH_TIME_OUT = 3000;

    private boolean isNetworkAvaible() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwordInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetwordInfo != null && activeNetwordInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView loading = (ImageView) findViewById(R.id.loading_animation);
        loading.setBackgroundResource(R.drawable.loading_animation);

        final AnimationDrawable loadingAnimation = (AnimationDrawable) loading.getBackground();
        loadingAnimation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*  This method will be executed when the timer is over
                 *  Start the main activity
                 */

                //  Check for network connection
                if (isNetworkAvaible()) {
                    // stop loading animation
                    loadingAnimation.stop();

                    // Start the main activity
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(SplashScreenActivity.this, "No network connection!", Toast.LENGTH_LONG).show();
                }

                // Close activity
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_TIME_OUT);


    }
}
