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
import android.widget.ImageView;
import android.widget.TextView;

import com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener;
import com.facebook.CallbackManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity
        implements HeadlessImageSearchingFragment.TaskStatusCallback {

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
    private int numberOfSearch;
    private ArrayList<String> results;
    private String description;
    CallbackManager callbackManager;
    DialogFragment shareFragment;
    HeadlessImageSearchingFragment searchingFragment;

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

        Picasso.with(this).load("file://" + path).into(imageView);

        FragmentManager fm = getSupportFragmentManager();
        if(savedInstanceState == null) {
            shareFragment = new ShareFragment();
        }

        searchingFragment = (HeadlessImageSearchingFragment) fm
                .findFragmentByTag(HeadlessImageSearchingFragment.TAG_HEADLESS_SEARCHING_FRAGMENT);
        if (searchingFragment == null) {
            searchingFragment = new HeadlessImageSearchingFragment();
            fm.beginTransaction()
                    .add(searchingFragment, HeadlessImageSearchingFragment.TAG_HEADLESS_SEARCHING_FRAGMENT)
                    .commit();
        }

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
        shareFragment = ShareFragment.newInstance(1);*/
        shareFragment.show(getSupportFragmentManager(), "dialog");


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    ProgressDialog searchProgressDialog;
    @Override
    public void onPreExecute() {
        searchProgressDialog = new ProgressDialog(ResultActivity.this);
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.setTitle("Searching");
        searchProgressDialog.show();
    }

    @Override
    public void onPostExecute(String description, ArrayList<String> tags) {
        searchProgressDialog.dismiss();
        if (!description.equals(""))
            descriptionResult.setText(description);
        for (String tag : tags) {
            if (searchResult.getText().toString().equalsIgnoreCase("No results found"))
                searchResult.setText(tag);
            else
                searchResult.append("\n" + tag);
        }
    }

    @Override
    public void onError() {
        AlertDialog errorDialog;
        errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
        errorDialog.setCancelable(true);
        errorDialog.setTitle("Error");
        errorDialog.setMessage("An error occur during the reverse search please try again");
        errorDialog.show();
    }
}
