package com.example.zanna.ghioca;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zanna.ghioca.listener.SearchingListener;
import com.example.zanna.ghioca.utility.SearchingUtility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultActivity extends AppCompatActivity {

    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.search_result)
    TextView searchResult;

    private String url;
    private String path;
    private SearchingListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        ButterKnife.bind(this);

        url = getIntent().getStringExtra("url");
        path = getIntent().getStringExtra("path");

        Picasso.with(this).load("file://" + path).into(imageView);

        final ProgressDialog searchProgressDialog;
        searchProgressDialog = new ProgressDialog(ResultActivity.this);
        searchProgressDialog.setCancelable(false);
        searchProgressDialog.setTitle("Searching");
        searchProgressDialog.show();

        listener = new SearchingListener() {
            @Override
            public void onFailure(Throwable error) {
                searchProgressDialog.dismiss();
                AlertDialog errorDialog;
                errorDialog = new AlertDialog.Builder(ResultActivity.this).create();
                errorDialog.setCancelable(true);
                errorDialog.setTitle("Error");
                errorDialog.setMessage("An error occur during the reverse search please try again");
                errorDialog.show();
            }

            @Override
            public void onSuccess(JSONObject answer) {
                try {
                    if (answer != null) {
                        String res = answer.getString("best_guess");
                        if (res != null)
                            searchResult.setText(res);
                    }
                    searchProgressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SearchingUtility.searchImage(url, listener);
    }
}
