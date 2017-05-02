package com.example.zanna.ghioca;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class SearchingUtility {
    public static void searchImage(final String url, final SearchingListener listener) {
        new AsyncTask<Void, Void, Void>() {
            URLSearcher searcher = null;
            JSONObject res = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    searcher = new URLSearcher(new URL(url));
                    res = searcher.search();
                    Log.i("prova", res.toString(2));
                } catch (Exception error) {
                    listener.onFailure(error);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onSuccess(res);
            }
        }.execute(null, null, null);
    }
}
