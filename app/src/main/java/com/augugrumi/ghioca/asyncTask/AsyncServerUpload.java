package com.augugrumi.ghioca.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.augugrumi.ghioca.listener.UploadingListener;
import com.augugrumi.ghioca.utility.UploadingUtility;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

// TODO change and manage failure when changed server for upload
public class AsyncServerUpload extends AsyncTask<Void, Void, Void>{
    private Activity activity;
    private String filePath;
    private UploadingListener listener;

    public AsyncServerUpload(String filePath, UploadingListener listener, Activity activity) {
        this.filePath = filePath;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStart();
    }

    @Override
    protected Void doInBackground(Void... params) {
        UploadingUtility.uploadToServer("file://" + filePath, activity, listener);
        return null;
    }

}
