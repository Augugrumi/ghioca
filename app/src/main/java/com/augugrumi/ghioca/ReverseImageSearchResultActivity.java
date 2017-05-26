package com.augugrumi.ghioca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;
import com.augugrumi.ghioca.listener.GoogleReverseImageSearchListener;
import com.augugrumi.ghioca.listener.ImaggaReverseImageSearchListener;
import com.augugrumi.ghioca.listener.WatsonReverseImageSearchListener;
import com.facebook.CallbackManager;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.pchmn.materialchips.ChipView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener.FILE_PATH_INTENT_EXTRA;
import static com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener.URL_INTENT_EXTRA;

public class ReverseImageSearchResultActivity extends AppCompatActivity
        implements ImageSearchingDialogFragment.ImageSearchingStatusCallback {

    // BINDINGS

    @BindView(R.id.mainPhoto)
    KenBurnsView mainPhoto;

    @BindView(R.id.best_guess)
    TextView bestGuess;

    @BindView(R.id.share_fab)
    FloatingActionButton share;

    @BindView(R.id.chipList)
    LinearLayout chipListManager;

    // END BINDINGS


    private String url;
    private String path;
    private DialogFragment shareFragment;
    private ImageSearchingDialogFragment searchingFragment;
    private ErrorDialogFragment errorDialogFragment;
    private GoogleReverseImageSearchListener googleListener;
    private AzureReverseImageSearchListener azureListener;
    private WatsonReverseImageSearchListener watsonListener;
    private ImaggaReverseImageSearchListener imaggaListener;
    private volatile ArrayList<String> results;
    private ArrayList<String> resultsToShare;
    private ArrayList<String> selectedChips;
    private String description;
    private ProgressDialog searchProgressDialog;
    CallbackManager callbackManager;
    DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search_result);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();

        // INITIALIZATIONS
        url = getIntent().getExtras().getString(URL_INTENT_EXTRA);
        path = getIntent().getStringExtra(FILE_PATH_INTENT_EXTRA);
        Picasso.with(this).load("file://" + path).into(mainPhoto);

        // END OF INITIALIZATIONS

        shareFragment = new ShareFragment();

        if(savedInstanceState == null) {
            results = new ArrayList<>();
            resultsToShare = new ArrayList<>();
            description = "";
            FragmentManager fm = getSupportFragmentManager();
            searchingFragment = (ImageSearchingDialogFragment) fm
                    .findFragmentByTag(ImageSearchingDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT);
            if (searchingFragment == null) {
                searchingFragment = new ImageSearchingDialogFragment();
                fm.beginTransaction()
                        .add(searchingFragment, ImageSearchingDialogFragment.TAG_IMAGE_SEARCHING_FRAGMENT)
                        .addToBackStack(null)
                        .show(searchingFragment)
                        .commit();
            }
        } else {
            onRestoreInstanceState(savedInstanceState);
            setDescription(description);
            refreshResultView();
        }

    }

    private void addResults(ArrayList<String> newResults) {
        Log.d("ADDINGRESULTS", newResults.toString());
        results.addAll(newResults);
        resultsToShare.addAll(newResults);
    }

    @SuppressWarnings("deprecated")
    private void refreshResultView() {

        Log.d("ADDINGRESULTS", "View refreshed");

        // TODO create chips and put it in the view
        // check out: http://stackoverflow.com/questions/6661261/adding-content-to-a-linear-layout-dynamically

        /*searchResult.setText("");
        for (String s : results)
            searchResult.append(s + "\n");*/

        cleanDuplicates();
        chipListManager.removeAllViews();
        final int defaultChipsPerNumber = 2;

        for (int j = 0; j < results.size(); j=j+defaultChipsPerNumber) {

            LinearLayout line = new LinearLayout(this, null);
            line.setPadding(0,5,0,5);

            int chipsPerLine = defaultChipsPerNumber;
            if ((results.size() - j) < 3) {

                chipsPerLine = results.size() - j;
            }

            for (int i = j; i < chipsPerLine + j; i++) {

                // Put Chips
                final String resultToDisplay = results.get(i);
                final ChipView chip = new ChipView(this, null);
                //chip.setDeletable(true);
                //chip.setDeleteIconColor(getResources().getColor(android.R.color.white));
                if (resultsToShare.contains(resultToDisplay)) {
                    chip.setChipBackgroundColor(
                            ResourcesCompat.getColor(getResources(), android.R.color.holo_green_light, null));
                    chip.setAvatarIcon(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.tick, null));
                } else {
                    chip.setChipBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.gray, null));
                    chip.setAvatarIcon(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.cross, null));
                }
                chip.setLabel(resultToDisplay);
                chip.setOnChipClicked(new View.OnClickListener() {
                    boolean enabled = resultsToShare.contains(resultToDisplay);

                    @Override
                    public void onClick(View v) {
                        if (enabled) {
                            chip.setChipBackgroundColor(
                                    ResourcesCompat.getColor(getResources(), R.color.gray, null));
                            chip.setAvatarIcon(
                                    ResourcesCompat.getDrawable(getResources(), R.drawable.cross, null));
                            resultsToShare.remove(resultToDisplay);

                        }
                        else {
                            chip.setChipBackgroundColor(
                                    ResourcesCompat.getColor(getResources(), android.R.color.holo_green_light, null));
                            chip.setAvatarIcon(
                                    ResourcesCompat.getDrawable(getResources(), R.drawable.tick, null));
                            resultsToShare.add(resultToDisplay);
                        }
                        enabled = !enabled;
                    }
                });
                line.addView(chip);
            }

            chipListManager.addView(line);

            line.bringToFront();
        }
        bestGuess.setText("..."+description+"!");
    }

    private void cleanDuplicates() {

        // Eliminating duplicates...
        Set<String> tmp = new HashSet<>();
        tmp.addAll(results);
        results.clear();
        // With a set we loose the elements order in the list, but we don't care
        results.addAll(tmp);
    }

    private void setDescription(String newDescription) {
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
    public void onPostExecute(String description, final ArrayList<String> tags) {
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
            setDescription(description);

        addResults(tags);
        refreshResultView();

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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getResults() {
        return resultsToShare;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("description", description);
        savedInstanceState.putStringArrayList("results", results);
        savedInstanceState.putStringArrayList("resultsToShare", resultsToShare);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        results = savedInstanceState.getStringArrayList("results");
        resultsToShare = savedInstanceState.getStringArrayList("resultsToShare");
        description = savedInstanceState.getString("description");
    }
}
