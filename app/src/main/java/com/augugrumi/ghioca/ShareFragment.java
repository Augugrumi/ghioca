package com.augugrumi.ghioca;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.championswimmer.libsocialbuttons.fabs.FABDropbox;
import in.championswimmer.libsocialbuttons.fabs.FABFacebook;
import in.championswimmer.libsocialbuttons.fabs.FABGoogleplus;
import in.championswimmer.libsocialbuttons.fabs.FABInstagram;
import in.championswimmer.libsocialbuttons.fabs.FABLinkedin;
import in.championswimmer.libsocialbuttons.fabs.FABTumblr;
import in.championswimmer.libsocialbuttons.fabs.FABTwitter;
import in.championswimmer.libsocialbuttons.fabs.FABWhatsapp;


/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class ShareFragment extends DialogFragment {
    @Bind(R.id.fab_facebook)
    FABFacebook fabFacebook;
    @Bind(R.id.fab_twitter)
    FABTwitter fabTwitter;
    @Bind(R.id.fab_instagram)
    FABInstagram fabInstagram;
    @Bind(R.id.fab_whatsapp)
    FABWhatsapp fabWhatsapp;
    @Bind(R.id.fab_tumblr)
    FABTumblr fabTumblr;
    @Bind(R.id.fab_googleplus)
    FABGoogleplus fabGooglePlus;
    @Bind(R.id.fab_dropbox)
    FABDropbox fabDropbox;
    @Bind(R.id.fab_linkedin)
    FABLinkedin fabLinkedin;
    @Bind(R.id.fab_other)
    FloatingActionButton fabOther;

    private String path;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllowReturnTransitionOverlap(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.share_dialogfragment, container, false);
        ButterKnife.bind(this, view);
        path = getActivity().getIntent().getStringExtra("path");
        return view;



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
        /*ArrayList<String> results = ((ResultActivity)getActivity()).getResults();
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
        startActivity(Intent.createChooser(share, "Share Image"));*/
    }

    @OnClick(R.id.fab_facebook) //TODO: IMPLEMENTARE UNA SHARE DIALOG??
    public void facebookShare(){
        ShareDialog shareDialog = new ShareDialog(this);
        if(shareDialog.canShow(SharePhotoContent.class)){
            Log.d("FACEBOOK","file://" + path);

            ContentResolver contentResolver = getContext().getContentResolver();
            ArrayList<String> results = ((ResultActivity)getActivity()).getResults();
            String description = ((ResultActivity)getActivity()).getDescription();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse("file://" + path));
            } catch (IOException e) {
                e.printStackTrace();
            }

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();

            ShareHashtag.Builder hashtags = new ShareHashtag.Builder();

            for (String res : results) {
                String s = "#";
                hashtags.setHashtag(s+WordUtils.uncapitalize((WordUtils.capitalize(res)).replaceAll(" ", "")));

            }

            hashtags.setHashtag("#GhioCa #Ciao");

            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .setShareHashtag(hashtags.build())
                    .build();

            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }

    }

    @OnClick(R.id.fab_twitter)
    public void twitterShare(){

    }

    @OnClick(R.id.fab_instagram)
    public void instagramShare(){

    }

    @OnClick(R.id.fab_whatsapp)
    public void whatsAppShare(){

    }

    @OnClick(R.id.fab_tumblr)
    public void tumblrShare(){

    }

    @OnClick(R.id.fab_googleplus)
    public void googlePlusShare(){

    }

    @OnClick(R.id.fab_other)
    public void dropboxShare(){

    }

    @OnClick(R.id.fab_other)
    public void linkedinShare(){

    }


}