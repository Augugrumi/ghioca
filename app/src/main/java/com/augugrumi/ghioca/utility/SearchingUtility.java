package com.augugrumi.ghioca.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.asyncTask.AsyncAzureOcrSearch;
import com.augugrumi.ghioca.asyncTask.AsyncAzureReverseImageSearch;
import com.augugrumi.ghioca.asyncTask.AsyncFreeOcrSpaceOCR;
import com.augugrumi.ghioca.asyncTask.AsyncGoogleReverseImageSearch;
import com.augugrumi.ghioca.asyncTask.AsyncImaggaReverseImageSearch;
import com.augugrumi.ghioca.asyncTask.AsyncWatsonOCR;
import com.augugrumi.ghioca.asyncTask.AsyncWatsonReverseImageSearch;
import com.augugrumi.ghioca.listener.AzureOcrSearchListener;
import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.FreeOcrSpaceOCRListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.WatsonOCRListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;

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
        new AsyncWatsonReverseImageSearch(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);;
    }

    public static void searchImageWithImagga(final String url, final ImaggaReverseImageSearchListener listener){
        new AsyncImaggaReverseImageSearch(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);;
    }

    public static void searchOCRWithWatson(final String url, final WatsonOCRListener listener){
        new AsyncWatsonOCR(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);;
    }

    public static void searchOCRWithFreeOcrSpace(final String url, final FreeOcrSpaceOCRListener listener){
        new AsyncFreeOcrSpaceOCR(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);;
    }

    public static void searchOCRWithAzure(final String url, final AzureOcrSearchListener listener){
        Log.i("searchOCRWithAzure", url);
        new AsyncAzureOcrSearch(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);;
    }
}
