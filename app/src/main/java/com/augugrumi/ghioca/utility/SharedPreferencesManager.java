package com.augugrumi.ghioca.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class SharedPreferencesManager {

    private static final String SHARED_PREFERENCES_FILE = "sharedPreferencesFile";
    public static final String WIFI_PREFERENCE = "rememberToTurnOnWifi";
    public static final String SEARCH_PREFERENCE = "rememberSearchType";

    public static boolean getUserWiFiPreference() {
        Log.i("SHARED_PREFERENCE_MAN", "getWifi");

        Context context = MyApplication.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);

        return sharedPref.getBoolean(WIFI_PREFERENCE, true);
    }

    public static void setUserWiFiPreference(boolean choice) {
        Log.i("SHARED_PREFERENCE_MAN", "setWifi " + choice);
        Context context = MyApplication.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(WIFI_PREFERENCE, choice);
        editor.apply();
    }

    public static SearchType getUserSearchPreference() {
        Context context = MyApplication.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);

        int choice = sharedPref.getInt(SEARCH_PREFERENCE, 0);

        Log.i("SHARED_PREFERENCES_MAN", "choice:" + choice + " " + SearchType.values()[choice]);

        return SearchType.values()[choice];
    }

    public static void setUserSearchPreference(SearchType choice) {
        Context context = MyApplication.getAppContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        Log.i("SHARED_PREFERENCES_MAN", "choice:" + choice.ordinal() + " " + choice);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SEARCH_PREFERENCE, choice.ordinal());
        editor.apply();
    }
}
