package com.augugrumi.ghioca.utility;

import android.os.AsyncTask;

import com.augugrumi.ghioca.asyncTask.AsyncTranslate;
import com.augugrumi.ghioca.listener.TranslateListener;

import it.augugrumi.libtranslate.conctract.Language;

/**
 * Created by davide on 30/05/17.
 */

public class TranslateUtility {

    public static void translateWithYandex(final String text,
                                           final Language to,
                                           final TranslateListener listener) {
        new AsyncTranslate(text, to,listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);

    }
}
