package com.augugrumi.ghioca;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.augugrumi.ghioca.asyncTask.asynkTaskResult.FreeOcrSpaceOCRResult;
import com.augugrumi.ghioca.listener.AzureOcrSearchListener;
import com.augugrumi.ghioca.listener.FreeOcrSpaceOCRListener;
import com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener;
import com.augugrumi.ghioca.utility.NetworkingUtility;
import com.augugrumi.ghioca.utility.SavingUtility;
import com.augugrumi.ghioca.utility.SearchingUtility;

import it.polpetta.libris.opticalCharacterRecognition.azure.contract.IAzureOcrResult;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class OCRDialogFragment extends DialogFragment {

    public static final String TAG_IMAGE_SEARCHING_FRAGMENT = "headlessOcrFragmentTag";

    public static interface OcrStatusCallback {
        void onPreExecute();
        void onPostExecute(ArrayList<String> text, String language);
        void onError();
    }

    private void onSearcherSuccess() {
        numberOfActiveSearchers--;
        numberOfSuccesses++;
        if (numberOfActiveSearchers == 0)
            callback.onPostExecute(text, language);
    }

    private void onSearcherFailure() {
        numberOfActiveSearchers--;
        numberOfFailures++;
        if (numberOfActiveSearchers == 0 && numberOfSuccesses == 0)
            callback.onError();
        else if (numberOfActiveSearchers == 0)
            callback.onPostExecute(text, language);
    }


    private static final int NUMBER_OF_SEARCHERS = 1;
    private int numberOfActiveSearchers = 0;
    private int numberOfSuccesses = 0;
    private int numberOfFailures = 0;
    private OcrStatusCallback callback;
    //private WatsonOCRListener watsonListener;
    private AzureOcrSearchListener azureListener;
    private FreeOcrSpaceOCRListener freeListener;
    private ArrayList<String> text;
    private String language;
    private String url;
    private String filePath;

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
        callback = (OcrStatusCallback) activity;
        search();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        text = new ArrayList<>();
        language = "";
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


    public void search() {
        callback.onPreExecute();
        if (NetworkingUtility.isConnectivityAvailable()) {
            url = getActivity().getIntent().getStringExtra(DefaultUploadingListener.URL_INTENT_EXTRA);
            filePath = getActivity().getIntent().getStringExtra(DefaultUploadingListener.FILE_PATH_INTENT_EXTRA);
            if (!SavingUtility.isFileBiggerThan1MB(filePath)) {
                freeListener = new FreeOcrSpaceOCRListener() {
                    @Override
                    public void onSuccess(FreeOcrSpaceOCRResult result) {
                        if (result != null) {
                            text = result.getBestGuess();
                            language = "unk";
                            Log.i("SUCCESS", "FREEOCR " + result);
                        }
                        onSearcherSuccess();
                    }

                    @Override
                    public void onStart() {
                        numberOfActiveSearchers++;
                        Log.i("START", "FREEOCR");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        onSearcherFailure();
                        Log.i("FAILURE", "FREEOCR");
                    }
                };
                SearchingUtility.searchOCRWithFreeOcrSpace(url, freeListener);
            } else {
                azureListener = new AzureOcrSearchListener() {
                    @Override
                    public void onSuccess(IAzureOcrResult result) {
                        if (result != null) {
                            text = result.getBestGuess();
                            language = result.getLanguage();
                            Log.i("SUCCESS", "FREEOCR " + result);
                        }
                        onSearcherSuccess();
                    }

                    @Override
                    public void onStart() {
                        numberOfActiveSearchers++;
                        Log.i("START", "FREEOCR");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        onSearcherFailure();
                        Log.i("FAILURE", "FREEOCR");
                    }
                };
                SearchingUtility.searchOCRWithAzure(url, azureListener);
            }
            /*watsonListener = new WatsonOCRListener() {
                @Override
                public void onSuccess(IIBMOcrResult result) {
                    if (result != null) {
                        text = result.getBestGuess();
                        language = result.getLanguage();
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

            SearchingUtility.searchOCRWithWatson(url, watsonListener);*/

        } else
            callback.onError();
    }
}
