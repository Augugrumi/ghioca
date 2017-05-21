package com.augugrumi.ghioca.utility;

import android.content.pm.PackageManager;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class AppInstallationChecker {
    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
