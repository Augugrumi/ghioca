package com.augugrumi.ghioca;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener.FILE_PATH_INTENT_EXTRA;
import static com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener.URL_INTENT_EXTRA;

public class ResultActivity extends AppCompatActivity
        implements ImageSearchingDialogFragment.ImageSearchingStatusCallback {

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
    private CallbackManager callbackManager;
    private DialogFragment shareFragment;
    private ImageSearchingDialogFragment searchingFragment;
    private ErrorDialogFragment errorDialogFragment;

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

        url = getIntent().getExtras().getString(URL_INTENT_EXTRA);
        path = getIntent().getStringExtra(FILE_PATH_INTENT_EXTRA);

        Picasso.with(this).load("file://" + path).into(imageView);

        FragmentManager fm = getSupportFragmentManager();
        if(savedInstanceState == null) {
            shareFragment = new ShareFragment();
        }

        searchingFragment = (ImageSearchingDialogFragment) fm
                .findFragmentByTag(ImageSearchingDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
        if (searchingFragment == null) {
            searchingFragment = new ImageSearchingDialogFragment();
            fm.beginTransaction()
                    .add(searchingFragment, ImageSearchingDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT)
                    .show(searchingFragment)
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
    public void onPostExecute(String description, ArrayList<String> tags) {
        FragmentManager fm = getSupportFragmentManager();
        searchingFragment = (ImageSearchingDialogFragment) fm
                .findFragmentByTag(ImageSearchingDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
        if (searchingFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(searchingFragment)
                    .remove(searchingFragment)
                    .commit();
        }
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
        FragmentManager fm = getSupportFragmentManager();
        searchingFragment = (ImageSearchingDialogFragment) fm
                .findFragmentByTag(ImageSearchingDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            super.onBackPressed();
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
