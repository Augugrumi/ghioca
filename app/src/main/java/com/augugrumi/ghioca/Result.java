package com.augugrumi.ghioca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;
import com.augugrumi.ghioca.utility.SearchingUtility;
import com.facebook.CallbackManager;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.robertlevonyan.views.chip.Chip;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;
import it.polpetta.libris.image.contract.IImageSearchResult;
import it.polpetta.libris.image.google.contract.IGoogleImageSearchResult;
import it.polpetta.libris.image.ibm.contract.IIBMImageSearchResult;

public class Result extends AppCompatActivity {

    // BINDINGS

    @Bind(R.id.mainPhoto)
    KenBurnsView mainPhoto;

    @Bind(R.id.best_guess)
    TextView bestGuess;

    @Bind(R.id.share_fab)
    FloatingActionButton share;

    @Bind(R.id.chipList)
    LinearLayout chipListManager;

    // END BINDINGS


    private String url;
    private String path;
    private GoogleReverseImageSearchListener googleListener;
    private AzureReverseImageSearchListener azureListener;
    private WatsonReverseImageSearchListener watsonListener;
    private ImaggaReverseImageSearchListener imaggaListener;
    private volatile ArrayList<String> results;
    private String description;
    private ProgressDialog searchProgressDialog;
    CallbackManager callbackManager;
    DialogFragment newFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        ButterKnife.bind(this);

        callbackManager = CallbackManager.Factory.create();

        // INITIALIZATIONS

        results = new ArrayList<>();
        description = "";
        url = getIntent().getExtras().getString("url");
        path = getIntent().getStringExtra("path");
        Picasso.with(this).load("file://" + path).into(mainPhoto);

        // END OF INITIALIZATIONS

        if(savedInstanceState == null) {
            newFragment = new ShareFragment();
        }

        searchProgressDialog = new ProgressDialog(Result.this);
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.setTitle("Searching");
        searchProgressDialog.show();

        // SEARCHERS

        googleListener = new GoogleReverseImageSearchListener() {
            @Override
            public void onSuccess(final IGoogleImageSearchResult result) {

                addNewResults(result);
                refreshResultView();
            }

            @Override
            public void onStart() {}

            @Override
            public void onFailure(Exception e) {
                onSearchFailure(e);
            }
        };

        azureListener = new AzureReverseImageSearchListener() {
            @Override
            public void onSuccess(final IAzureImageSearchResult result) {

                addNewResults(result);

                if (result.getDescription() != null) {
                    setDescription(result.getDescription());
                }

                refreshResultView();
            }

            @Override
            public void onStart() {}

            @Override
            public void onFailure(Exception e) {
                onSearchFailure(e);
            }
        };

        watsonListener = new WatsonReverseImageSearchListener() {
            @Override
            public void onSuccess(IIBMImageSearchResult result) {

                addNewResults(result);
                refreshResultView();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(Exception e) {
                onSearchFailure(e);
            }
        };

        imaggaListener = new ImaggaReverseImageSearchListener() {
            @Override
            public void onSuccess(IImageSearchResult result) {

                addNewResults(result);
                refreshResultView();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(Exception e) {
                onSearchFailure(e);
            }
        };

        // END OF SEARCHERS


        SearchingUtility.searchImageWithGoogle(url, googleListener);
        SearchingUtility.searchImageWithAzure(url, azureListener);


        SearchingUtility.searchImageWithWatson(url, watsonListener);
        SearchingUtility.searchImageWithImagga(url, imaggaListener);

    }

    private void onSearchFailure(Exception e) {
        if (e instanceof IOException) {
            searchProgressDialog.dismiss();
            AlertDialog errorDialog;
            errorDialog = new AlertDialog.Builder(Result.this).create();
            errorDialog.setCancelable(true);
            errorDialog.setTitle("Error");
            errorDialog.setMessage("An error occur during the reverse search please try again");
            errorDialog.show();

            refreshResultView();
        }
    }

    private void addNewResults(IImageSearchResult result) {
        if (result != null) {
            ArrayList<String> toAdd = new ArrayList<>();
            String res = result.getBestGuess();

            if (res != null) {
                toAdd.add(res);
            }

            ArrayList<String> newTags = result.getTags();

            if (newTags != null) {

                toAdd.addAll(newTags);
                addResults(toAdd);
            }

        }
    }

    private synchronized void addResults(ArrayList<String> newResults) {

        Log.d("ADDINGRESULTS", newResults.toString());

        results.addAll(newResults);

        // Eliminating duplicates...
        Set<String> tmp = new HashSet<>();
        tmp.addAll(results);
        results.clear();
        // With a set we loose the elements order in the list, but we don't care
        results.addAll(tmp);
    }

    private synchronized void refreshResultView() {

        Log.d("ADDINGRESULTS", "View refreshed");

        searchProgressDialog.dismiss();

        // TODO create chips and put it in the view
        // check out: http://stackoverflow.com/questions/6661261/adding-content-to-a-linear-layout-dynamically

        /*searchResult.setText("");
        for (String s : results)
            searchResult.append(s + "\n");*/
    }

    private synchronized void setDescription(String newDescription) {

        StringBuilder stringBuilder = new StringBuilder()
                .append("...")
                .append(newDescription)
                .append("!");

        bestGuess.setText(stringBuilder.toString());

        String capitalizedNewDescription= newDescription.substring(0, 1)
                .toUpperCase() + newDescription.substring(1);

        description = capitalizedNewDescription;
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

    public synchronized String getDescription() {
        return description;
    }

    public synchronized ArrayList<String> getResults() {

        return results;
    }
}
