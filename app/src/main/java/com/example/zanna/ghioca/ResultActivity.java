package com.example.zanna.ghioca;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zanna.ghioca.listener.AzureReverseImageSearchListener;
import com.example.zanna.ghioca.listener.GoogleReverseImageSearchListener;
import com.example.zanna.ghioca.utility.SearchingUtility;
import com.squareup.picasso.Picasso;

import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;
import it.polpetta.libris.image.google.contract.IGoogleImageSearchResult;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultActivity extends AppCompatActivity {

    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.search_result)
    TextView searchResult;
    @Bind(R.id.description_result)
    TextView descriptionResult;

    private String url;
    private String path;
    private GoogleReverseImageSearchListener googleListener;
    private AzureReverseImageSearchListener azureListener;
    private int numberOfSearch;
    private ArrayList<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result_activity);
        ButterKnife.bind(this);

        searchResult.setMovementMethod(new ScrollingMovementMethod());
        results = new ArrayList<>();

        url = getIntent().getStringExtra("url");
        path = getIntent().getStringExtra("path");

        Picasso.with(this).load("file://" + path).into(imageView);

        final ProgressDialog searchProgressDialog;
        searchProgressDialog = new ProgressDialog(ResultActivity.this);
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.setTitle("Searching");
        searchProgressDialog.show();

        numberOfSearch = 2;

        googleListener = new GoogleReverseImageSearchListener() {
            @Override
            public void onSuccess(final IGoogleImageSearchResult result) {
                if (result != null) {
                    String res = result.getBestGuess();
                    if (res != null)
                        if (!results.contains(res))
                            results.add(res);/*
                        if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                            searchResult.setText(res);
                        else
                            searchResult.append("\n" + res);*/
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
                searchProgressDialog.dismiss();
                AlertDialog errorDialog;
                errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                errorDialog.setCancelable(true);
                errorDialog.setTitle("Error");
                errorDialog.setMessage("An error occur during the reverse search please try again");
                errorDialog.show();
            }
        };

        azureListener = new AzureReverseImageSearchListener() {
            @Override
            public void onSuccess(final IAzureImageSearchResult result) {
                if (result != null) {
                    String res = result.getBestGuess();
                    if (!results.contains(res))
                        results.add(res);
                    /*if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                        searchResult.setText(res);
                    else
                        searchResult.append("\n" + res);*/
                    ArrayList<String> tags = result.getTags();
                    if (tags != null)
                        for (String tag : tags)
                            if (!results.contains(tag))
                                results.add(tag);
                    String description = result.getDescription();
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
                searchProgressDialog.dismiss();
                AlertDialog errorDialog;
                errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                errorDialog.setCancelable(true);
                errorDialog.setTitle("Error");
                errorDialog.setMessage("An error occur during the reverse search please try again");
                errorDialog.show();
            }
        };

        SearchingUtility.searchImageWithGoogle(url, googleListener);
        SearchingUtility.searchImageWithAzure(url, azureListener);
    }
}
