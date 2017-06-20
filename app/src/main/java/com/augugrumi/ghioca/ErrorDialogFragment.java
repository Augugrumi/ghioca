package com.augugrumi.ghioca;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class ErrorDialogFragment extends DialogFragment {

    public static final String TAG_ERROR_FRAGMENT = "errorFragmentTag";

    @BindView(R.id.ok_button)
    Button wifiActivationButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyPermissionChecker.checkPermissions(getActivity());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.error_dialogfragment, container, false);
        ButterKnife.bind(this, view);
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

    @OnClick(R.id.ok_button)
    public void onAccept() {
        dismiss();
    }

}