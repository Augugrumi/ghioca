package com.example.zanna.ghioca;

import android.os.AsyncTask;
import android.util.Log;

import it.polpetta.libris.ReverseImageSearch;
import it.polpetta.libris.google.imageSearch.SearchResult;

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
            JSONObject res = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SearchResult result = ReverseImageSearch
                            .getGoogleServices()
                            .imageSearchBuildQuery()
                            .setPhoto(new URL(url))
                            .runQuery()
                            .getContest();
                    res = new JSONObject(result.toJSONString());
                    Log.i("prova", res.toString(2));
                    Log.i("prova", res.get("best_guess").toString());
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
