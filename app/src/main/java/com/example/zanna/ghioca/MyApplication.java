package com.example.zanna.ghioca;

import android.app.Application;
import android.content.Context;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class MyApplication extends Application {

    private static Context instance;
    public static String MY_API_KEY = "AkHqkinKScahBDKyXuqzQz";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance;
    }
}