package com.augugrumi.ghioca;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.championswimmer.libsocialbuttons.fabs.FABFacebook;

public class CreditsActivity extends AppCompatActivity {
    @BindView(R.id.facebook)
    FABFacebook fabFacebook;

    @BindView(R.id.github)
    ImageButton fabGithub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.facebook)
    public void goOnGhiocaFacebookPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Ghio-Ca-297296710681639/"));
        startActivity(browserIntent);
    }

    @OnClick(R.id.github)
    public void goOnGhiocaGithubPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Augugrumi/ghioca"));
        startActivity(browserIntent);
    }
}
