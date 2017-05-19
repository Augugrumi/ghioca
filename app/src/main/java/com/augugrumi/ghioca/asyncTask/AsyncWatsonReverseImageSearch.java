package com.augugrumi.ghioca.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;

import it.polpetta.libris.image.ReverseImageSearch;
import it.polpetta.libris.image.ibm.contract.IIBMImageSearchResult;

import java.net.URL;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class AsyncWatsonReverseImageSearch extends AsyncTask<Void, Void, Void> {

    private static String watsonKey =
            MyApplication.getAppContext().getString(R.string.WATSON_KEY);

    private WatsonReverseImageSearchListener listener;
    private IIBMImageSearchResult result;
    private boolean error;
    private String url;
    private Exception e;

    public AsyncWatsonReverseImageSearch(String url, WatsonReverseImageSearchListener listener) {
        this.listener = listener;
        this.url = url;
        error = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listener.onStart();
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            result = ReverseImageSearch
                    .getIBMServices(watsonKey)
                    .imageSearchBuildQuery()
                    .setImage(new URL(url))
                    .build()
                    .search();
            Log.i("SEARCH_RESULT", result.toJSONString());
        } catch (Exception error) {
            e = error;
            Log.e("WATSON_ERROR",e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // TODO think if it could be the right thing to do
        if (error)
            listener.onFailure(e);
        else
            listener.onSuccess(result);
    }
}
