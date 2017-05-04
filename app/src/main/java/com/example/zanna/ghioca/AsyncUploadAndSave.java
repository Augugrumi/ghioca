package com.example.zanna.ghioca;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class AsyncUploadAndSave extends AsyncTask<String, Void, Void> {
    private ProgressDialog uploadProgressDialog;
    private AlertDialog resultDialog;
    private SearchingListener searchingListener;
    private UploadingListener uploadingListener;
    private Activity activity;

    public AsyncUploadAndSave setContextActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    protected void onPreExecute() {
        uploadProgressDialog = new ProgressDialog(activity);
        uploadProgressDialog.setCancelable(false);
        uploadProgressDialog.setTitle("Uploading the image");
        uploadProgressDialog.show();

        resultDialog = new AlertDialog.Builder(activity).create();
        resultDialog.setTitle("Result");


        uploadingListener = new UploadingListener() {
            @Override
            public void onProgressUpdate(int progress) {
                uploadProgressDialog.setProgress(progress);
            }

            @Override
            public void onFinish(String url) {
                uploadProgressDialog.dismiss();
                SearchingUtility.searchImage(url, searchingListener);
            }

            @Override
            public void onFailure(Throwable error) {
                error.printStackTrace();
                uploadProgressDialog.dismiss();
            }
        };

        searchingListener = new SearchingListener() {
            @Override
            public void onFailure(Throwable error) {
                error.printStackTrace();
                resultDialog.dismiss();
            }

            @Override
            public void onSuccess(JSONObject answer) {
                try {
                    String s = answer.getString("best_guess");
                    if (s == null)
                        s = "No result found";
                    resultDialog.setMessage(s);
                    resultDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            File file = new File(params[0]);
            byte[] jpeg = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(jpeg); //read file into bytes[]
            fis.close();

            UploadingUtility.uploadToServer("file://" + params[0],MyApplication.getAppContext(), uploadingListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
