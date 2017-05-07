package com.example.zanna.ghioca.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.zanna.ghioca.listener.UploadingListener;

import io.filepicker.Filepicker;
import io.filepicker.FilepickerCallback;
import io.filepicker.models.FPFile;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class UploadingUtility {
    public static final String MY_API_KEY = "AkHqkinKScahBDKyXuqzQz";
    static {
        // TODO -> put in more appropriate place
        Filepicker.setKey(MY_API_KEY);
    }

    public static void uploadToServer(String path, final Context context, final UploadingListener listener) {
        Log.i("provaupload", "5");
        Filepicker.uploadLocalFile(Uri.parse(path), context, new FilepickerCallback() {
            @Override
            public void onFileUploadSuccess(final FPFile fpFile) {
                Log.i("provaupload", "6");
                listener.onFinish(fpFile.getUrl());
            }

            @Override
            public void onFileUploadError(Throwable error) {
                listener.onFailure(error);
            }

            @Override
            public void onFileUploadProgress(Uri uri, float progress) {
                listener.onProgressUpdate(Math.round(progress));
            }
        });
    }
}
