package com.augugrumi.ghioca.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.augugrumi.ghioca.MyApplication;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class NetworkingUtility {
    public static boolean isWifiEnabled() {
        WifiManager wifi = (WifiManager) MyApplication.getAppContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public static boolean isConnectivityAvailable() {
        ConnectivityManager conMgr = (ConnectivityManager)MyApplication.getAppContext()
            .getApplicationContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        return netInfo == null;
    }

    public static void turnOnWiFi() {
        WifiManager wifiManager = (WifiManager) MyApplication.getAppContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }
}
