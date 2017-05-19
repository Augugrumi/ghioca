package com.augugrumi.ghioca;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.augugrumi.ghioca.utility.NetworkingUtility;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class TurnOnWiFiFragment extends DialogFragment {
    @Bind(R.id.turn_on_wifi)
    Button wifiActivationButton;
    @Bind(R.id.cancel_button)
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.turnonwifi_dialogfragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.turn_on_wifi)
    public void onAccept() {
        NetworkingUtility.turnOnWiFi();
    }

    @OnClick(R.id.cancel_button)
    public void onCancel() {
        dismiss();
    }
}
