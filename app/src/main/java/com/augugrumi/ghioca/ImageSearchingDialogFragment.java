package com.augugrumi.ghioca;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.TranslateListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;
import com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener;
import com.augugrumi.ghioca.utility.MyPermissionChecker;
import com.augugrumi.ghioca.utility.NetworkingUtility;
import com.augugrumi.ghioca.utility.SearchingUtility;
import com.augugrumi.ghioca.utility.TranslateUtility;

import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;
import it.polpetta.libris.image.contract.IImageSearchResult;
import it.polpetta.libris.image.google.contract.IGoogleImageSearchResult;
import it.polpetta.libris.image.ibm.contract.IIBMImageSearchResult;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class ImageSearchingDialogFragment extends DialogFragment {

    public static final String TAG_IMAGE_SEARCHING_FRAGMENT = "headlessSearchingFragmentTag";

    public static interface ImageSearchingStatusCallback {
        void onPreExecute();
        void onPostExecute(String description, ArrayList<String> tags);
        void onError();
    }

    // IMAGE SEARCHING

    private void onSearcherSuccess() {
        numberOfActiveSearchers--;
        numberOfSuccesses++;
        if (numberOfActiveSearchers == 0) {
            translate();
        }
    }

    private void onSearcherFailure() {
        numberOfActiveSearchers--;
        numberOfFailures++;
        if (numberOfActiveSearchers == 0 && numberOfSuccesses == 0)
            callback.onError();
        else if (numberOfActiveSearchers == 0) {
            translate();
        }
    }

    // END IMAGE SEARCHING

    // TEXT TRANSLATION

    private void onTranslateSuccess() {
        callback.onPostExecute(description, results);
    }

    private void onTranslateFailure() {
        callback.onError();
    }

    // END TEXT TRANSLATION

    private static final int NUMBER_OF_SEARCHERS = 4;
    private int numberOfActiveSearchers = 0;
    private int numberOfSuccesses = 0;
    private int numberOfFailures = 0;
    private ImageSearchingStatusCallback callback;
    private GoogleReverseImageSearchListener googleListener;
    private AzureReverseImageSearchListener azureListener;
    private WatsonReverseImageSearchListener watsonListener;
    private ImaggaReverseImageSearchListener imaggaListener;
    private ArrayList<String> results;
    private String description;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.searching_dialogfragment, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);
        return view;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ImageSearchingStatusCallback)activity;
        search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyPermissionChecker.checkPermissions(getActivity());
        setRetainInstance(true);
        results = new ArrayList<>();
        description = "";

    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private void translate() {

        TranslateListener yandexListener = new TranslateListener() {
            @Override
            public void onSuccess(String result) {
                Log.i("ONSUCCESS", "YANDEX");
                description = result;

                onTranslateSuccess();
            }

            @Override
            public void onStart() {

                Log.i("START", "YANDEX");

            }

            @Override
            public void onFailure(Exception e) {
                onTranslateFailure();
                Log.i("FAILURE", "YANDEX");
            }
        };

        TranslateUtility.translateWithYandex(description,
            com.augugrumi.ghioca.translation.language.Language.fromString(Locale.getDefault().getLanguage()),
            yandexListener);
    }


    public void search() {
        callback.onPreExecute();
        if (NetworkingUtility.isConnectivityAvailable()) {
            url = getActivity().getIntent().getStringExtra(DefaultUploadingListener.URL_INTENT_EXTRA);
            googleListener = new GoogleReverseImageSearchListener() {
                @Override
                public void onSuccess(IGoogleImageSearchResult result) {
                    if (result != null) {
                        String res = result.getBestGuess();
                        if (res != null)
                            if (!results.contains(res))
                                results.add(res);
                    }
                    onSearcherSuccess();
                    for(String s : results) {
                        if (s == null || s.trim().equals("")) {
                            results.remove(s);
                        }
                    }
                    Log.i("SUCCESS", "GOOGLE");
                }

                @Override
                public void onStart() {
                    numberOfActiveSearchers++;
                    Log.i("START", "GOOGLE");
                }

                @Override
                public void onFailure(Exception e) {
                    onSearcherFailure();
                    Log.i("FAILURE", "GOOGLE");
                }
            };

            azureListener = new AzureReverseImageSearchListener() {
                @Override
                public void onSuccess(IAzureImageSearchResult result) {
                    if (result != null) {
                        String res = result.getBestGuess();
                        if (!results.contains(res))
                            results.add(res);
                        ArrayList<String> tags = result.getTags();
                        if (tags != null)
                            for (String tag : tags)
                                if (!results.contains(tag))
                                    results.add(tag);
                        description = result.getDescription();
                    }
                    for(String s : results) {
                        if (s == null || s.trim().equals("")) {
                            results.remove(s);
                        }
                    }
                    onSearcherSuccess();
                    Log.i("SUCCESS", "AZURE");
                }

                @Override
                public void onStart() {
                    numberOfActiveSearchers++;
                    Log.i("START", "AZURE");
                }

                @Override
                public void onFailure(Exception e) {
                    onSearcherFailure();
                    Log.i("FAILURE", "AZURE");
                }
            };

            watsonListener = new WatsonReverseImageSearchListener() {
                @Override
                public void onSuccess(IIBMImageSearchResult result) {
                    if (result != null) {
                        String res = result.getBestGuess();
                        if (res != null)
                            if (!results.contains(res))
                                results.add(res);
                        ArrayList<String> tags = result.getTags();
                        if (tags != null)
                            for (String tag : tags)
                                if (!results.contains(tag))
                                    results.add(tag);

                    }
                    for(String s : results) {
                        if (s == null || s.trim().equals("")) {
                            results.remove(s);
                        }
                    }
                    onSearcherSuccess();
                    Log.i("SUCCESS", "WATSON");
                }

                @Override
                public void onStart() {
                    numberOfActiveSearchers++;
                    Log.i("START", "WATSON");
                }

                @Override
                public void onFailure(Exception e) {
                    onSearcherFailure();
                    Log.i("FAILURE", "WATSON");
                }
            };

            imaggaListener = new ImaggaReverseImageSearchListener() {
                @Override
                public void onSuccess(IImageSearchResult result) {
                    if (result != null) {
                        String res = result.getBestGuess();
                        if (res != null)
                            if (!results.contains(res))
                                results.add(res);
                        ArrayList<String> tags = result.getTags();
                        if (tags != null)
                            for (String tag : tags)
                                if (!results.contains(tag))
                                    results.add(tag);
                    }
                    for(String s : results) {
                        if (s == null || s.trim().equals("")) {
                            results.remove(s);
                        }
                    }
                    onSearcherSuccess();
                    Log.i("SUCCESS", "IMAGGA");
                }

                @Override
                public void onStart() {
                    numberOfActiveSearchers++;
                    Log.i("START", "IMMAGA");
                }

                @Override
                public void onFailure(Exception e) {
                    onSearcherFailure();
                    Log.i("FAILURE", "IMMAGA");
                }
            };

            SearchingUtility.searchImageWithGoogle(url, googleListener);
            SearchingUtility.searchImageWithAzure(url, azureListener);
            SearchingUtility.searchImageWithWatson(url, watsonListener);
            SearchingUtility.searchImageWithImagga(url, imaggaListener);
        } else
            callback.onError();
    }
}
