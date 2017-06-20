package com.augugrumi.ghioca.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 07/06/17.
 */
public class MyPermissionChecker {

    private static final int REQUEST_CAMERA_PERMISSIONS = 931;

    public static boolean checkPermissions (Activity activity) {
        final String[] permissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE};

        final List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
        }

        boolean res = true;

        for (int i = 0; i < permissions.length && res; i++) {
            if (ActivityCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                res = false;
            }
        }

        return res;
    }
}
