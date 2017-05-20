package com.augugrumi.ghioca.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;

import it.polpetta.libris.image.ReverseImageSearch;
import it.polpetta.libris.image.ibm.contract.IAbstractIBMImageFactoryReverseSearchProvider;
import it.polpetta.libris.image.ibm.contract.IIBMImageSearchResult;
import it.polpetta.libris.image.ibm.visualRecognition.URLIBMImageSearcher;

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
            URL newUrl = new URL(url);
            IAbstractIBMImageFactoryReverseSearchProvider factory = ReverseImageSearch.getIBMServices(watsonKey);
            URLIBMImageSearcher.Builder builder = factory.imageSearchBuildQuery();
            builder.setImage(newUrl);
            URLIBMImageSearcher searcher = builder.build();
            result = searcher.search();
            /*result = ReverseImageSearch
                    .getIBMServices(watsonKey)
                    .imageSearchBuildQuery()
                    .setImage(newUrl)
                    .build()
                    .search();*/
            Log.i("WATSON_SEARCH_RESULT", result.toJSONString());
        } catch (Exception error) {
            e = error;
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
