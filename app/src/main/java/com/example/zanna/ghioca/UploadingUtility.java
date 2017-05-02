package com.example.zanna.ghioca;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import io.filepicker.Filepicker;
import io.filepicker.FilepickerCallback;
import io.filepicker.models.FPFile;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class UploadingUtility {

    public static void uploadToServer(String path, final Context context, final UploadingListener listener) {
        Log.i("provaupload", "5");
        Filepicker.uploadLocalFile(Uri.parse(path), context, new FilepickerCallback() {
            @Override
            public void onFileUploadSuccess(final FPFile fpFile) {
                Log.i("provaupload", "6");
                listener.onFinish(fpFile.getUrl());
                /*Toast.makeText(context, fpFile.getUrl(), Toast.LENGTH_LONG).show();


                    new AsyncTask<Void, Void, Void>() {
                        URLSearcher searcher = null;
                        JSONObject res = null;

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                searcher = new URLSearcher(new URL(fpFile.getUrl()));
                                res = searcher.search();
                                Log.i("prova", res.toString(2));
                            } catch (Exception e) {}

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            try {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                                        .setTitle(res.getString("best_guess"));
                                dialog.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(null, null, null);*/
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
