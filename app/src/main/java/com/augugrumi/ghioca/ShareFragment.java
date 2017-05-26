package com.augugrumi.ghioca;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.robertsimoes.shareable.Shareable;

import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
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
    @BindView(R.id.fab_facebook)
    FABFacebook fabFacebook;
    @BindView(R.id.fab_twitter)
    FABTwitter fabTwitter;
    @BindView(R.id.fab_instagram)
    FABInstagram fabInstagram;
    @BindView(R.id.fab_whatsapp)
    FABWhatsapp fabWhatsapp;
    @BindView(R.id.fab_tumblr)
    FABTumblr fabTumblr;
    @BindView(R.id.fab_googleplus)
    FABGoogleplus fabGooglePlus;
    @BindView(R.id.fab_dropbox)
    FABDropbox fabDropbox;
    @BindView(R.id.fab_linkedin)
    FABLinkedin fabLinkedin;
    @BindView(R.id.fab_other)
    FloatingActionButton fabOther;

    private String path;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.share_dialogfragment, container, false);
        ButterKnife.bind(this, view);
        path = getActivity().getIntent().getStringExtra("path");
        //TODO try with a mock url
        url = getActivity().getIntent().getStringExtra("url");

        return view;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /*static ShareFragment newInstance(int num) {
        ShareFragment f = new ShareFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }*/

    @OnClick(R.id.fab_other)
    public void otherShare() {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");

        String toShare = shareContent();

        share.putExtra(Intent.EXTRA_TEXT, toShare);
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    private String shareContent() {
        ArrayList<String> results = ((ReverseImageSearchResultActivity)getActivity()).getResults();
        String description = ((ReverseImageSearchResultActivity)getActivity()).getDescription();

        StringBuilder toShare = new StringBuilder(description);
        toShare.append("\n");
        for (String res : results) {
            toShare.append("#");
            toShare.append(WordUtils.uncapitalize((WordUtils.capitalize(res)).replaceAll(" ", "")));
            toShare.append(" ");
        }

        toShare.append("#GhioCa");

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

    @OnClick(R.id.fab_facebook)
    public void facebookShare(){

        String toShare = shareContent();
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

                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }
        } else {
            if(shareDialog.canShow(ShareLinkContent.class)){
                ShareLinkContent link = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(url))
                        .build();

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .readFrom(link)
                        .build();

                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }
        }


    }

    @OnClick(R.id.fab_twitter)
    public void twitterShare(){

        String packageName = "com.twitter.android";
        if(AppInstallationChecker.isPackageInstalled(packageName, getContext().getPackageManager())){
            String toShare = shareContent();

            Shareable imageShare = new Shareable.Builder(this.getActivity())
                    .message(toShare)
                    .image(Uri.parse("file://" + path))
                    .socialChannel(Shareable.Builder.TWITTER)
                    .build();

            imageShare.share();
        }
        else{
            redirectToGooglePlay(packageName);
        }



    }

    @OnClick(R.id.fab_instagram)
    public void instagramShare(){

        String packageName = "com.instagram.android";
        String toShare = shareContent();
        copyToClipboard(toShare);
        if(AppInstallationChecker.isPackageInstalled(packageName, getContext().getPackageManager())){
            // Create the new Intent using the 'Send' action.
            Intent share = new Intent(Intent.ACTION_SEND);

            // Set the MIME type
            share.setType("image/*");

            share.setPackage(packageName);

            // Add the URI to the Intent.
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));

            // Broadcast the Intent.
            startActivity(Intent.createChooser(share,""));
        }
        else{
            redirectToGooglePlay(packageName);
        }
    }

    @OnClick(R.id.fab_whatsapp)
    public void whatsAppShare(){
        String packageName = "com.whatsapp";
        PackageManager pm = this.getActivity().getPackageManager();
        if(AppInstallationChecker.isPackageInstalled(packageName,getContext().getPackageManager())){
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("image/*");
            waIntent.setPackage(packageName);

            String toShare = shareContent();

            waIntent.putExtra(Intent.EXTRA_TEXT, toShare);
            waIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
            startActivity(Intent.createChooser(waIntent, "Share Image"));

        } else {
            redirectToGooglePlay(packageName);
        }
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

    private void redirectToGooglePlay(String appPackageName){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


}