package com.augugrumi.ghioca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;
import com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener;
import com.augugrumi.ghioca.utility.SearchingUtility;
import com.facebook.CallbackManager;
import com.squareup.picasso.Picasso;

import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;
import it.polpetta.libris.image.contract.IImageSearchResult;
import it.polpetta.libris.image.google.contract.IGoogleImageSearchResult;
import it.polpetta.libris.image.ibm.contract.IIBMImageSearchResult;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity {

    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.search_result)
    TextView searchResult;
    @Bind(R.id.description_result)
    TextView descriptionResult;
    @Bind(R.id.share_fab)
    FloatingActionButton share;

    private String url;
    private String path;
    private GoogleReverseImageSearchListener googleListener;
    private AzureReverseImageSearchListener azureListener;
    private WatsonReverseImageSearchListener watsonListener;
    private ImaggaReverseImageSearchListener imaggaListener;
    private int numberOfSearch;
    private ArrayList<String> results;
    private String description;
    CallbackManager callbackManager;
    DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result_activity);
        ButterKnife.bind(this);

        callbackManager = CallbackManager.Factory.create();

        results = new ArrayList<>();
        description = "";

        searchResult.setMovementMethod(new ScrollingMovementMethod());
        results = new ArrayList<>();

        url = getIntent().getExtras().getString(DefaultUploadingListener.URL_INTENT_EXTRA);
        path = getIntent().getStringExtra(DefaultUploadingListener.FILE_PATH_INTENT_EXTRA);
        Log.i("WATSON_ONCREATEACTIVITY", url + " ");

        Picasso.with(this).load("file://" + path).into(imageView);

        FragmentManager fm = getSupportFragmentManager();
        if(savedInstanceState == null) {
            newFragment = new ShareFragment();
        }

        final ProgressDialog searchProgressDialog;
        searchProgressDialog = new ProgressDialog(ResultActivity.this);
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.setTitle("Searching");
        searchProgressDialog.show();

        numberOfSearch = 1;

        googleListener = new GoogleReverseImageSearchListener() {
            @Override
            public void onSuccess(final IGoogleImageSearchResult result) {
                if (result != null) {
                    String res = result.getBestGuess();
                    if (res != null)
                        if (!results.contains(res))
                            results.add(res);
                        if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                            searchResult.setText(res);
                        else
                            searchResult.append("\n" + res);
                }

                numberOfSearch -= 1;
                if (numberOfSearch <= 0) {
                    searchProgressDialog.dismiss();
                    searchResult.setText("");
                    for (String s : results)
                        searchResult.append(s + "\n");
                }
            }

            @Override
            public void onStart() {}

            @Override
            public void onFailure(Exception e) {
                if (e instanceof IOException) {
                    searchProgressDialog.dismiss();
                    AlertDialog errorDialog;
                    errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                    errorDialog.setCancelable(true);
                    errorDialog.setTitle("Error");
                    errorDialog.setMessage("An error occur during the reverse search please try again");
                    errorDialog.show();
                    numberOfSearch -= 1;
                    if (numberOfSearch <= 0) {
                        searchProgressDialog.dismiss();
                        searchResult.setText("");
                        for (String s : results)
                            searchResult.append(s + "\n");
                    }
                }
            }
        };

        azureListener = new AzureReverseImageSearchListener() {
            @Override
            public void onSuccess(final IAzureImageSearchResult result) {
                if (result != null) {
                    String res = result.getBestGuess();
                    if (!results.contains(res))
                        results.add(res);
                    if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                        searchResult.setText(res);
                    else
                        searchResult.append("\n" + res);
                    ArrayList<String> tags = result.getTags();
                    if (tags != null)
                        for (String tag : tags)
                            if (!results.contains(tag))
                                results.add(tag);
                    description = result.getDescription();
                    if (description != null)
                        descriptionResult.setText(description);
                }
                numberOfSearch -= 1;
                if (numberOfSearch <= 0) {
                    searchProgressDialog.dismiss();
                    searchResult.setText("");
                    for (String s : results)
                        searchResult.append(s + "\n");
                }
            }

            @Override
            public void onStart() {}

            @Override
            public void onFailure(Exception e) {
                if (e instanceof IOException) {
                    searchProgressDialog.dismiss();
                    AlertDialog errorDialog;
                    errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                    errorDialog.setCancelable(true);
                    errorDialog.setTitle("Error");
                    errorDialog.setMessage("An error occur during the reverse search please try again");
                    errorDialog.show();
                    numberOfSearch -= 1;
                    if (numberOfSearch <= 0) {
                        searchProgressDialog.dismiss();
                        searchResult.setText("");
                        for (String s : results)
                            searchResult.append(s + "\n");
                    }
                }
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
                    if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                        searchResult.setText(res);
                    else
                        searchResult.append("\n" + res);

                    ArrayList<String> tags = result.getTags();
                    if (tags != null)
                        for (String tag : tags)
                            if (!results.contains(tag))
                                results.add(tag);

                }

                numberOfSearch -= 1;
                if (numberOfSearch <= 0) {
                    searchProgressDialog.dismiss();
                    searchResult.setText("");
                    for (String s : results)
                        searchResult.append(s + "\n");
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(Exception e) {
                if (e instanceof IOException) {
                    searchProgressDialog.dismiss();
                    AlertDialog errorDialog;
                    errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                    errorDialog.setCancelable(true);
                    errorDialog.setTitle("Error");
                    errorDialog.setMessage("An error occur during the reverse search please try again");
                    errorDialog.show();
                    numberOfSearch -= 1;
                    if (numberOfSearch <= 0) {
                        searchProgressDialog.dismiss();
                        searchResult.setText("");
                        for (String s : results)
                            searchResult.append(s + "\n");
                    }
                }
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
                    if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                        searchResult.setText(res);
                    else
                        searchResult.append("\n" + res);
                    ArrayList<String> tags = result.getTags();
                    if (tags != null)
                        for (String tag : tags)
                            if (!results.contains(tag))
                                results.add(tag);
                }

                numberOfSearch -= 1;
                if (numberOfSearch <= 0) {
                    searchProgressDialog.dismiss();
                    searchResult.setText("");
                    for (String s : results)
                        searchResult.append(s + "\n");
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(Exception e) {
                if (e instanceof IOException) {
                    searchProgressDialog.dismiss();
                    AlertDialog errorDialog;
                    errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                    errorDialog.setCancelable(true);
                    errorDialog.setTitle("Error");
                    errorDialog.setMessage("An error occur during the reverse search please try again");
                    errorDialog.show();
                    numberOfSearch -= 1;
                    if (numberOfSearch <= 0) {
                        searchProgressDialog.dismiss();
                        searchResult.setText("");
                        for (String s : results)
                            searchResult.append(s + "\n");
                    }
                }
            }
        };


        SearchingUtility.searchImageWithGoogle(url, googleListener);
        //SearchingUtility.searchImageWithAzure(url, azureListener);
        SearchingUtility.searchImageWithWatson(url, watsonListener);
        SearchingUtility.searchImageWithImagga(url, imaggaListener);

    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getResults() {
        return results;
    }

    //TODO beautify the fragment
    @OnClick(R.id.share_fab)
    public void share() {

       /* FragmentTransaction ft = getFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentById(R.id.share_dialogfragment);
        ft.addToBackStack(null);

        // Create and show the dialog.
        newFragment = ShareFragment.newInstance(1);*/
        newFragment.show(getSupportFragmentManager(), "dialog");


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
