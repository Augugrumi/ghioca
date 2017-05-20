package com.augugrumi.ghioca.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.augugrumi.ghioca.MyApplication;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class NetworkingUtility extends BroadcastReceiver{

    private static boolean wifiEnabled;
    private static boolean connectivityAvailable;

    static {
        wifiEnabled = wifiCheck();
        connectivityAvailable = connectivityCheck();
    }

    public static boolean isWifiEnabled() {
        return wifiEnabled;
    }

    public static boolean isConnectivityAvailable() {
        return connectivityAvailable;
    }

    private static boolean connectivityCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static boolean wifiCheck() {
        WifiManager wifi = (WifiManager) MyApplication.getAppContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public static void turnOnWiFi() {
        WifiManager wifiManager = (WifiManager) MyApplication.getAppContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        wifiEnabled = wifiCheck();
        connectivityAvailable = connectivityCheck();
    }
}
