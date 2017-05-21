package com.augugrumi.ghioca.utility;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.listener.UploadingListener;

import io.filepicker.Filepicker;
import io.filepicker.FilepickerCallback;
import io.filepicker.models.FPFile;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class UploadingUtility {
    public static final String MY_API_KEY =
            MyApplication.getAppContext().getString(R.string.FILESTACK_KEY);

    static {
        // TODO -> put in more appropriate place
        Filepicker.setKey(MY_API_KEY);
    }

    public static void uploadToServer(String path, final Context context, final UploadingListener listener) {
        Log.i("provaupload", "5 path:" + path);
        listener.onStart();
        Filepicker.uploadLocalFile(Uri.parse(path), context, new FilepickerCallback() {
            @Override
            public void onFileUploadSuccess(final FPFile fpFile) {
                Log.i("provaupload", "6" + fpFile.getUrl().toString());
                Log.i("provaupload", "7 url->" + fpFile.getUrl().toString());
                listener.onFinish(fpFile.getUrl());
            }

            @Override
            public void onFileUploadError(Throwable error) {
                Log.i("provaupload", "8 " + error.toString());
                listener.onFailure(error);
            }

            @Override
            public void onFileUploadProgress(Uri uri, float progress) {
                Log.i("provaupload", "--progress" + progress);
                listener.onProgressUpdate(Math.round(progress));
            }
        });
    }
}
