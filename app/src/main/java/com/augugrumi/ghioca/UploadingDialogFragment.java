package com.augugrumi.ghioca;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class UploadingDialogFragment extends DialogFragment {

    public static final String TAG_UPLOADING_FRAGMENT = "uploadingFragmentTag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.uploading_dialogfragment, container, false);
        ButterKnife.bind(this, view);
        setCancelable(false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

}
