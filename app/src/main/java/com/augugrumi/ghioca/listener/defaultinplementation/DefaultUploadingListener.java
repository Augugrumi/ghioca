package com.augugrumi.ghioca.listener.defaultinplementation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import com.augugrumi.ghioca.ResultActivity;
import com.augugrumi.ghioca.listener.UploadingListener;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

//TODO -> this could use fragments instead of alerts
public class DefaultUploadingListener implements UploadingListener {

    public static String URL_INTENT_EXTRA = "url";
    public static String FILE_PATH_INTENT_EXTRA = "path";

    private ProgressDialog uploadProgressDialog;
    private String filePath;
    private Activity activity;

    public DefaultUploadingListener(String filePath, Activity activity) {
        this.filePath = filePath;
        this.activity = activity;
        uploadProgressDialog = new ProgressDialog(activity);
        uploadProgressDialog.setCancelable(false);
        uploadProgressDialog.setTitle("Uploading the image");
    }

    @Override
    public void onStart() {
        uploadProgressDialog.show();
    }

    @Override
    public void onProgressUpdate(int progress) {
        uploadProgressDialog.setProgress(progress);
    }

    @Override
    public void onFinish(String url) {
        uploadProgressDialog.dismiss();
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra(URL_INTENT_EXTRA, url);
        intent.putExtra(FILE_PATH_INTENT_EXTRA, filePath);
        activity.startActivity(intent);
    }

    @Override
    public void onFailure(Throwable error) {
        uploadProgressDialog.dismiss();
        AlertDialog errorDialog;
        errorDialog = new AlertDialog.Builder(activity).create();
        errorDialog.setCancelable(true);
        errorDialog.setTitle("Error");
        errorDialog.setMessage("An error occur during the uploading please try again");
        errorDialog.show();
    }
}
