package com.example.csie_indoor_navigation;

import android.app.Application;
import android.content.Context;

public class GetApplicationContext extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
    public static Context getAppContext()
    {
        return appContext;
    }
}

