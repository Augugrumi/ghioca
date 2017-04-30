package com.example.zanna.ghioca;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class SavingUtility {
    public static final String folderName = "GHiO-CA";
    public static final String folderPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + folderName;

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void runTimePermissionAcquirement(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private static File getAppFolder() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), folderName);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    public static boolean saveFile(byte[] imageToSave, String fileName, Context context) {
        Assert.assertTrue(isExternalStorageWritable());

        File folder = getAppFolder();

        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(imageToSave);
            out.flush();
            out.close();

            mediaScannerCall(context, file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void mediaScannerCall(Context context, File file) {
        MediaScannerConnection.scanFile(context,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }

                });
    }
}
