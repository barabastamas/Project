package com.engineering.software.sapi.project;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import com.engineering.software.sapi.project.LoginRegister.LoginActivity;

public class SplashScreenActivity extends Activity {

    private static final int SPLASH_TIME_OUT = 3000;

    /*
     * Verify if there is network available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwordInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetwordInfo != null && activeNetwordInfo.isConnected();
    }

    /*
     * Hide Status bar
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initParse() {
        try {
            Parse.initialize(getApplication(), "NiPzJoksbo1Hjp8VO5qiTJf0heB2ZHTvsbfgF2Gg", "TaHxiUvGn5dICdDllvDlMJLD56NEO3chJPkFI5b2");
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        hideStatusBar();

        /*
         *  Initialize Parse
         */
        initParse();

        /*
         *  Create TestObject
         */
        /*ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();*/

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
                if (isNetworkAvailable()) {
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
