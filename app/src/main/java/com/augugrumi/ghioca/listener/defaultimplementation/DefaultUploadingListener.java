package com.augugrumi.ghioca.listener.defaultimplementation;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.augugrumi.ghioca.ErrorDialogFragment;
import com.augugrumi.ghioca.ResultActivity;
import com.augugrumi.ghioca.UploadingDialogFragment;
import com.augugrumi.ghioca.listener.UploadingListener;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

//TODO -> this could use fragments instead of alerts
public class DefaultUploadingListener implements UploadingListener {

    public static String URL_INTENT_EXTRA = "url";
    public static String FILE_PATH_INTENT_EXTRA = "path";

    private String filePath;
    private FragmentActivity activity;
    private static UploadingDialogFragment uploadFragment;
    private static ErrorDialogFragment errorDialogFragment;

    public DefaultUploadingListener(String filePath, FragmentActivity activity) {
        this.filePath = filePath;
        this.activity = activity;

    }

    @Override
    public void onStart() {
        FragmentManager fm = activity.getSupportFragmentManager();
        uploadFragment = (UploadingDialogFragment) fm
                .findFragmentByTag(UploadingDialogFragment.TAG_UPLOADING_FRAGMENT);
        if (uploadFragment == null) {
            uploadFragment = new UploadingDialogFragment();
            fm.beginTransaction()
                    .add(uploadFragment, UploadingDialogFragment.TAG_UPLOADING_FRAGMENT)
                    .addToBackStack(null)
                    .show(uploadFragment)
                    .commit();

        } else
            uploadFragment.show(fm, UploadingDialogFragment.TAG_UPLOADING_FRAGMENT);
    }

    @Override
    public void onProgressUpdate(int progress) {}

    @Override
    public void onFinish(String url) {
        uploadFragment.dismiss();

        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra(URL_INTENT_EXTRA, url);
        intent.putExtra(FILE_PATH_INTENT_EXTRA, filePath);
        activity.startActivity(intent);
    }

    @Override
    public void onFailure(Throwable error) {
        FragmentManager fm = activity.getSupportFragmentManager();
        uploadFragment.setCancelable(true);
        uploadFragment.dismiss();

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
}
