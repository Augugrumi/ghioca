package com.augugrumi.ghioca.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.listener.UploadingListener;
import com.augugrumi.ghioca.utility.UploadingUtility;

import java.io.File;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

// TODO change and manage failure when changed server for upload
public class AsyncServerUpload extends AsyncTask<Void, Void, Void>{
    private Activity activity;
    private String filename;
    private UploadingListener listener;


    @Override
    protected Void doInBackground(Void... params) {
        UploadingUtility.uploadToServer("file://" + MyApplication.appFolderPath +
                File.separator + filename, activity, listener);
        return null;
    }

}
