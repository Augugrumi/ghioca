package com.augugrumi.ghioca.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.asyncTask.AsyncAzureReverseImageSearch;
import com.augugrumi.ghioca.asyncTask.AsyncGoogleReverseImageSearch;
import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;

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

    public static void searchImageWithGoogle(final String url,
                                             final GoogleReverseImageSearchListener listener) {

        new AsyncGoogleReverseImageSearch(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);

    }

    public static void searchImageWithAzure(final String url,
                                            final AzureReverseImageSearchListener listener) {
        new AsyncAzureReverseImageSearch(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);

    }

    public static void searchImageWithWatson(final String url, final WatsonReverseImageSearchListener listener){

    }

    public static void searchImageWithImagga(final String url, final ImaggaReverseImageSearchListener listener){

    }
}
