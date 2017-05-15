package com.augugrumi.ghioca.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;

import it.polpetta.libris.image.ReverseImageSearch;
import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;
import it.polpetta.libris.image.google.contract.IGoogleImageSearchResult;

import java.net.URL;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class SearchingUtility {
    final static String azureKey = "***REMOVED***";

    public static void searchImageWithGoogle(final String url,
                                             final GoogleReverseImageSearchListener listener) {
        new AsyncTask<Void, Void, Void>() {
            IGoogleImageSearchResult result = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    result = ReverseImageSearch
                            .getGoogleServices()
                            .imageSearchBuildQuery()
                            .setImage(new URL(url))
                            .build()
                            .search();
                    Log.i("SEARCH_RESULT", result.toJSONString());
                } catch (Exception error) {
                    listener.onFailure(error);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onSuccess(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }

    public static void searchImageWithAzure(final String url,
                                            final AzureReverseImageSearchListener listener) {
        new AsyncTask<Void, Void, Void>() {
            IAzureImageSearchResult result = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    result = ReverseImageSearch
                            .getAzureServices(azureKey)
                            .imageSearchBuildQuery()
                            .setImage(new URL(url))
                            .build()
                            .search();
                    Log.i("SEARCH_RESULT", result.toJSONString());
                } catch (Exception error) {
                    listener.onFailure(error);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onSuccess(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }
}
