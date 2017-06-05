/*
 * Copyright 2013 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.augugrumi.ghioca.translation.translate;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.translation.YandexTranslatorAPI;
import com.augugrumi.ghioca.translation.language.Language;
import android.util.Log;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Makes calls to the Yandex machine translation web service API
 */
public final class Translate extends YandexTranslatorAPI {

  private static final String SERVICE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
  private static final String TRANSLATION_LABEL = "text";

  //prevent instantiation
  private Translate(){};

  /**
   * Translates text from a given Language to another given Language using Yandex.
   * 
   * @param text The String to translate.
   * @param from The language code to translate from.
   * @param to The language code to translate to.
   * @return The translated String.
   * @throws Exception on error.
   */
  public static String execute(final String text, final Language from, final Language to) throws Exception {
    validateServiceState(text);

    if (to == null) {

      // if from is null, error message. This save us a NPE
      // FIXME please change me :(
      return "Cannot translate in this language, sorry";
    }

    /*
    boolean print = to == null;
    Log.i("ISNULL", "Result: " + print);
    */

    final String params = 
        PARAM_API_KEY + URLEncoder.encode(MyApplication.getAppContext().getString(R.string.YANDEX_KEY),ENCODING)
        + PARAM_LANG_PAIR + URLEncoder.encode(from.toString(),ENCODING) + URLEncoder.encode("-",ENCODING) + URLEncoder.encode(to.toString(),ENCODING) 
        + PARAM_TEXT + URLEncoder.encode(text,ENCODING);
    final URL url = new URL(SERVICE_URL + params);
    return retrievePropArrString(url, TRANSLATION_LABEL).trim();
  }

  private static void validateServiceState(final String text) throws Exception {
    final int byteLength = text.getBytes(ENCODING).length;
    if(byteLength>10240) { // TODO What is the maximum text length allowable for Yandex?
      throw new RuntimeException("TEXT_TOO_LARGE");
    }
  }
}
