package com.augugrumi.ghioca;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.augugrumi.ghioca.utility.AppInstallationChecker;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.robertsimoes.shareable.Shareable;

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
    private String url;

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
        //TODO try with a mock url
        //url = getActivity().getIntent().getStringExtra("url");
        url = "https://previews.123rf.com/images/burakowski/burakowski1202/burakowski120200227/" +
                "12222018-Example-rubber-stamp-Stock-Vector-sample.jpg";
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

    private String shareContent() {
        ArrayList<String> results = ((ResultActivity)getActivity()).getResults();
        String description = ((ResultActivity)getActivity()).getDescription();

        StringBuilder toShare = new StringBuilder(description);
        toShare.append("\n");
        for (String res : results) {
            toShare.append("#");
            toShare.append(WordUtils.uncapitalize((WordUtils.capitalize(res)).replaceAll(" ", "")));
            toShare.append(" ");
        }

        return toShare.toString();
    }

    private void copyToClipboard(String toCopy) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip =
                android.content.ClipData.newPlainText("Copied Text", toCopy);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this.getActivity(),
                "Hastags and description copied to the clipboard", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.fab_facebook) //TODO: IMPLEMENTARE UNA SHARE DIALOG??
    public void facebookShare(){
        //TODO remove this line (debug purposes) and uncomment the line below!
        String toShare = "una bella descrizione della foto\n#prova #prova #prova #prova ";
        //String toShare = shareContent();
        copyToClipboard(toShare);

        ShareDialog shareDialog = new ShareDialog(this);
        ContentResolver contentResolver = getContext().getContentResolver();
        if (AppInstallationChecker
                .isPackageInstalled("com.facebook.katana", this.getActivity().getPackageManager())) {
            if(shareDialog.canShow(SharePhotoContent.class)){
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse("file://" + path));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();

                ShareHashtag.Builder hashtags = new ShareHashtag.Builder()
                        .setHashtag("#GhioCa");

                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .setShareHashtag(hashtags.build())
                        .build();

                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }
        } else {
            if(shareDialog.canShow(ShareLinkContent.class)){
                ShareLinkContent link = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(url))
                        .build();

                ShareHashtag.Builder hashtags = new ShareHashtag.Builder()
                        .setHashtag("#GhioCa");

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .readFrom(link)
                        .setShareHashtag(hashtags.build())
                        .build();

                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }
        }


    }

    @OnClick(R.id.fab_twitter)
    public void twitterShare(){
        Shareable imageShare = new Shareable.Builder(this.getActivity())
                .message("try share on twitter")
                .image(Uri.parse("file://" + path))
                .socialChannel(Shareable.Builder.TWITTER)
                .build();
        imageShare.share();
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

    @OnClick(R.id.fab_dropbox)
    public void dropboxShare(){

    }

    @OnClick(R.id.fab_linkedin)
    public void linkedinShare(){

    }


}