package com.augugrumi.ghioca.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.listener.WatsonOCRListener;

import it.polpetta.libris.opticalCharacterRecognition.OpticalCharacterRecognitionSearch;
import it.polpetta.libris.opticalCharacterRecognition.ibm.contract.IIBMOcrResult;

import java.net.URL;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class AsyncWatsonOCR extends AsyncTask<Void, Void, Void> {

    private static String watsonKey =
            MyApplication.getAppContext().getString(R.string.WATSON_KEY);

    private WatsonOCRListener listener;
    private IIBMOcrResult result;
    private boolean error;
    private String url;
    private Exception e;

    public AsyncWatsonOCR(String url, WatsonOCRListener listener) {
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
            result = OpticalCharacterRecognitionSearch
                    .getIBMServices(watsonKey)
                    .imageSearchBuildQuery()
                    .setImage(new URL(url))
                    .build()
                    .search();
            Log.i("WATSON_OCR_RESULT", result.toJSONString());
        } catch (Exception exception) {
            e = exception;
            e.printStackTrace();
            error = true;
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
