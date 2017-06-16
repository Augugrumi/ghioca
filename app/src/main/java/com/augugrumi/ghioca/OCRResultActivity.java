package com.augugrumi.ghioca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.augugrumi.ghioca.listener.TranslateListener;
import com.augugrumi.ghioca.listener.WatsonOCRListener;
import com.augugrumi.ghioca.translation.language.Language;
import com.augugrumi.ghioca.utility.TranslateUtility;
import com.facebook.CallbackManager;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener.FILE_PATH_INTENT_EXTRA;
import static com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener.URL_INTENT_EXTRA;

public class OCRResultActivity extends AppCompatActivity
        implements OCRDialogFragment.OcrStatusCallback {

    // BINDINGS

    @BindView(R.id.mainPhoto)
    KenBurnsView mainPhoto;

    @BindView(R.id.text_result)
    TextView textView;

    @BindView(R.id.share_fab)
    FloatingActionButton share;

    @BindView(R.id.spinner)
    AppCompatSpinner languagesSpinner;
    // END BINDINGS


    private String url;
    private String path;
    private DialogFragment shareFragment;
    private OCRDialogFragment searchingFragment;
    private ErrorDialogFragment errorDialogFragment;
    private WatsonOCRListener watsonListener;
    private ArrayList<String> text;
    private String language;
    private SparseArray<String> alreadyTranslatedText;
    private ProgressDialog searchProgressDialog;
    CallbackManager callbackManager;
    DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_result);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();

        // INITIALIZATIONS
        url = getIntent().getExtras().getString(URL_INTENT_EXTRA);
        path = getIntent().getStringExtra(FILE_PATH_INTENT_EXTRA);
        Picasso.with(this).load("file://" + path).into(mainPhoto);

        // END OF INITIALIZATIONS

        shareFragment = new ShareFragmentOcr();

        if(savedInstanceState == null) {
            text = new ArrayList<>();
            alreadyTranslatedText = new SparseArray<String>();
            language = "";
            FragmentManager fm = getSupportFragmentManager();
            searchingFragment = (OCRDialogFragment) fm
                    .findFragmentByTag(OCRDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
            if (searchingFragment == null) {
                searchingFragment = new OCRDialogFragment();
                fm.beginTransaction()
                        .add(searchingFragment, OCRDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT)
                        .addToBackStack(null)
                        .show(searchingFragment)
                        .commit();
            }
        } else {
            onRestoreInstanceState(savedInstanceState);
            refreshResultView();
        }

        ArrayList<String> supportedLanguages = Language.getAllSupportedLangagesCode();
        final ArrayList<String> languages = new ArrayList<>();
        for (String langCode : supportedLanguages) {
            languages.add(new Locale(langCode).getDisplayLanguage(Locale.getDefault()));
        }
        Collections.sort(languages);
        languages.add(0, getResources().getString(R.string.select_language));

        final ArrayAdapter<String> adapter  = new ArrayAdapter<String>(
                this,R.layout.spinner_item, languages);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        languagesSpinner.setAdapter(adapter);
        languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private HashMap<String, String> languagesMap = null;

            private void initMaps() {
                if(languagesMap == null) {
                    languagesMap = new HashMap<String, String>();
                    ArrayList<String> supportedLanguages = Language.getAllSupportedLangagesCode();
                    for (String langCode : supportedLanguages) {
                        languagesMap.put(new Locale(langCode).getDisplayLanguage(Locale.getDefault()),
                                langCode);
                        Log.i("PROVA_LANG", new Locale(langCode).getDisplayLanguage(Locale.getDefault()));
                    }
                    /*languages.remove(0);
                    adapter.notifyDataSetChanged();*/
                }
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                initMaps();
                boolean removed = false;
                if (position != 0 ||
                        (position == 0 && !languages.get(0).equals(getResources().getString(R.string.select_language)))) {
                    if (alreadyTranslatedText.get(position) == null) {
                        // Translation
                        TranslateListener yandexListener = new TranslateListener() {
                            @Override
                            public void onSuccess(String result) {
                                Log.i("ONSUCCESS", "YANDEX2");
                                //description = result;

                                String sanitized = result.substring(1, result.length() - 1);
                                alreadyTranslatedText.append(position, sanitized);
                                updateView(sanitized);
                            }

                            @Override
                            public void onStart() {
                                Log.i("START", "YANDEX2");
                            }

                            @Override
                            public void onFailure(Exception e) {

                                // don't do nothing? Boh
                                refreshResultView();
                                Log.i("FAILURE", "YANDEX2");
                            }
                        };

                        TranslateUtility.translateWithYandex(text.toString(),
                                Language.fromString(languagesMap.get(languages.get(position))),
                                yandexListener);
                    } else {
                        updateView(alreadyTranslatedText.get(position));
                    }

                    if (languages.get(0).equals(getResources().getString(R.string.select_language))) {
                        languages.remove(0);
                        adapter.notifyDataSetChanged();
                        removed = true;
                    }
                }

                if (removed)
                    languagesSpinner.setSelection(position-1);
                else
                    languagesSpinner.setSelection(position);
            }

            private int stringToPosition(String lang) {
                for (int i = 0; i < languages.size(); i++)
                    if (languages.get(i).equals(lang))
                        return i;
                return 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateView(String newText) {

        textView.setText(newText);
        text.clear();
        text.add(newText);

    }

    private void refreshResultView() {
        StringBuilder builder = new StringBuilder();
        for (String s : text) {
            builder.append(s);
            builder.append("\n");
        }
        textView.setText(builder.toString());
    }

    //TODO to change
    private void setText(ArrayList<String> text) {
        this.text = text;
        refreshResultView();
    }

    //TODO beautify the fragment
    @OnClick(R.id.share_fab)
    public void share() {
        shareFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPreExecute() {}

    @Override
    public void onPostExecute(ArrayList<String> text, String language) {
        FragmentManager fm = getSupportFragmentManager();
        searchingFragment = (OCRDialogFragment) fm
                .findFragmentByTag(OCRDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
        if (searchingFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(searchingFragment)
                    .remove(searchingFragment)
                    .commit();
        }
        setText(text);

    }

    @Override
    public void onError() {
        FragmentManager fm = getSupportFragmentManager();
        searchingFragment = (OCRDialogFragment) fm
                .findFragmentByTag(OCRDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
        if (searchingFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(searchingFragment)
                    .remove(searchingFragment)
                    .commit();
        }

        errorDialogFragment = (ErrorDialogFragment) fm
                .findFragmentByTag(ErrorDialogFragment.TAG_ERROR_FRAGMENT);
        if (errorDialogFragment == null) {
            errorDialogFragment = new ErrorDialogFragment();
            fm.beginTransaction()
                    .add(errorDialogFragment, ErrorDialogFragment.TAG_ERROR_FRAGMENT)
                    .show(errorDialogFragment)
                    .commit();
        } else
            errorDialogFragment.show(fm, ErrorDialogFragment.TAG_ERROR_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }

    public ArrayList<String> getText() {
        return text;
    }

    public String getLanguage() {
        return language;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("language", language);
        savedInstanceState.putStringArrayList("text", text);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        text = savedInstanceState.getStringArrayList("text");
        language = savedInstanceState.getString("language");
    }
}
