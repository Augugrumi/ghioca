package com.augugrumi.ghioca.utility;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

// TODO to implement
public class SavingUtility {

    /**
     * This class must not have instances
     */
    private SavingUtility(){}

    private static File getAppFolder() throws IOException {
        File folder = new File(MyApplication.appFolderPath);

        if (!folder.exists()) {
            if (!folder.mkdirs())
                throw new IOException("cannot create the folder");
        }

        return folder;
    }

    //
    public static void save(byte[] toSave, String fileName) throws IOException {
        File folder = getAppFolder();

        File file = new File(folder, fileName);
        if (file.exists()) {
            if (!file.delete())
                throw new IOException("file " + fileName + " exists and cannot be deleted");
        }

            FileOutputStream out = new FileOutputStream(file);
            out.write(toSave);
            out.flush();
            out.close();

            mediaScannerCall(MyApplication.getAppContext(), file);
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
