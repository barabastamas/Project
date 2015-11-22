package com.engineering.software.sapi.project;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Atti Zold on 2015. 11. 21..
 */
public class SimpleApplication extends Application{

    public void onCreate(){
        Parse.initialize(this,getString(R.string.parse_app_id),getString(R.string.parse_client_id));
    }

}
