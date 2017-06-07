package com.augugrumi.ghioca;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.augugrumi.ghioca.utility.MyPermissionChecker;
import com.augugrumi.ghioca.utility.NetworkingUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class WiFiFragment extends DialogFragment {

    public static final String TAG_WIFI_FRAGMENT = "wifiFragmentTag";

    @BindView(R.id.turn_on_wifi)
    Button wifiActivationButton;
    @BindView(R.id.cancel)
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyPermissionChecker.checkPermissions(getActivity());
        setRetainInstance(true);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_dialogfragment, container, false);
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



    @OnClick(R.id.turn_on_wifi)
    public void onAccept() {
        NetworkingUtility.turnOnWiFi();
        dismiss();
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        dismiss();
    }

}