package com.augugrumi.ghioca.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.listener.TranslateListener;
import com.augugrumi.ghioca.translation.detect.Detect;

import it.augugrumi.libtranslate.conctract.Language;

import java.util.Locale;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class AsyncTranslate extends AsyncTask<Void, Void, Void> {
    private static String yandexKey =
            MyApplication.getAppContext().getString(R.string.YANDEX_KEY);

    private TranslateListener listener;
    private String result;
    private boolean error;
    private String text;
    private Language translateTo;
    private Exception e;

    public AsyncTranslate(String text, Language translateTo, TranslateListener listener) {
        this.listener = listener;
        this.text = text;
        this.translateTo = translateTo;
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
            com.augugrumi.ghioca.translation.language.Language lang = Detect.
                    execute(text);
            Log.e("TRANSLATE_RESULT lang", lang + "");
            Log.e("TRANSLATE_RESULT solang", Locale.getDefault().getLanguage() + "");
            result = com.augugrumi.ghioca.translation.translate.Translate.execute(text, lang,
                    com.augugrumi.ghioca.translation.language.Language.fromString(Locale.getDefault().getLanguage()));
            Log.i("TRANSLATE_RESULT", result);
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
