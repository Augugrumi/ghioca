package com.augugrumi.ghioca;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

import static android.R.attr.path;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class ShareFragment extends DialogFragment {
    @Bind(R.id.fab_facebook)
    FloatingActionButton fabFacebook;
    @Bind(R.id.fab_twitter)
    FloatingActionButton fabTwitter;
    @Bind(R.id.fab_instagram)
    FloatingActionButton fabInstagram;
    @Bind(R.id.fab_whatsapp)
    FloatingActionButton fabWhatsapp;
    @Bind(R.id.fab_tumblr)
    FloatingActionButton fabTumblr;
    @Bind(R.id.fab_googleplus)
    FloatingActionButton fabGooglePlus;
    @Bind(R.id.fab_dropbox)
    FloatingActionButton fabDropbox;
    @Bind(R.id.fab_linkedin)
    FloatingActionButton fabLinkedin;
    @Bind(R.id.fab_other)
    FloatingActionButton fabOther;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllowReturnTransitionOverlap(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.share_dialogfragment, container, false);
    }

    static ShareFragment newInstance(int num) {
        ShareFragment f = new ShareFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @OnClick(R.id.fab_other)
    public void otherShare() {
        ArrayList<String> results = ((ResultActivity)getActivity()).getResults();
        String description = ((ResultActivity)getActivity()).getDescription();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        StringBuilder textToShare = new StringBuilder(description);
        textToShare.append("\n");
        for (String res : results) {
            textToShare.append("#");
            textToShare.append(WordUtils.uncapitalize((WordUtils.capitalize(res)).replaceAll(" ", "")));
            textToShare.append(" ");
        }
        textToShare.append("#GhioCa");
        share.putExtra(Intent.EXTRA_TEXT, textToShare.toString());
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
        startActivity(Intent.createChooser(share, "Share Image"));
    }

}