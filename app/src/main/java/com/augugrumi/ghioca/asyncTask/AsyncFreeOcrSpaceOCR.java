package com.augugrumi.ghioca.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.asyncTask.asynkTaskResult.FreeOcrSpaceOCRResult;
import com.augugrumi.ghioca.listener.FreeOcrSpaceOCRListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class  AsyncFreeOcrSpaceOCR extends AsyncTask<Void, Void, Void> {
    private static final String ENDPOINT = "https://api.ocr.space/parse/image";
    private static final String METHOD = "POST";
    private static final String USER_AGENT_ATTRIBUTE = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0";
    private static final String LANGUAGE_ACCEPTED_ATTRIBUTE = "Accept-Language";
    private static final String LANGUAGE_ACCEPTED_VALUE = "en-US,en;q=0.5";
    private static final String API_KEY_ATTRIBUTE = "apikey";
    private static final String IS_OVERLAYED_ATTRIBUTE = "isOverlayRequired";
    private static final String URL_ATTRIBUTE = "url";
    private static String freeOcrKey =
            MyApplication.getAppContext().getString(R.string.FREE_OCR_SPACE_KEY);

    private FreeOcrSpaceOCRListener listener;
    private FreeOcrSpaceOCRResult result;
    private boolean error;
    private String url;
    private Exception e;

    public AsyncFreeOcrSpaceOCR(String url, FreeOcrSpaceOCRListener listener) {
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // TODO think if it could be the right thing to do
        if (error)
            listener.onFailure(e);
        else
            listener.onSuccess(result);
    }

    private String sendPost() throws Exception {

        URL obj = new URL(ENDPOINT); // OCR API Endpoints
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod(METHOD);
        con.setRequestProperty(USER_AGENT_ATTRIBUTE, USER_AGENT_VALUE);
        con.setRequestProperty(LANGUAGE_ACCEPTED_ATTRIBUTE, LANGUAGE_ACCEPTED_VALUE);


        JSONObject postDataParams = new JSONObject();

        postDataParams.put(API_KEY_ATTRIBUTE, freeOcrKey);
        postDataParams.put(IS_OVERLAYED_ATTRIBUTE, false);
        postDataParams.put(URL_ATTRIBUTE, url);


        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(getPostDataString(postDataParams));
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //return result
        return String.valueOf(response);
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String res = sendPost();
            result = new FreeOcrSpaceOCRResult(res);
            Log.i("STRING_RESULT_OCR", result.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*private static String freeOcrKey =
            MyApplication.getAppContext().getString(R.string.FREE_OCR_SPACE_KEY);

    private FreeOcrSpaceOCRListener listener;
    private FreeOCRSpaceResult result;
    private boolean error;
    private String url;
    private Exception e;

    public AsyncFreeOcrSpaceOCR(String url, FreeOcrSpaceOCRListener listener) {
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
                    .getFreeOcrServices(freeOcrKey)
                    .imageSearchBuildQuery()
                    .setImage(new URL(url))
                    .build()
                    .search();
            Log.i("FREEOCR_OCR_RESULT", result.toJSONString());
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
    }*/
}
