package com.augugrumi.ghioca;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class UploadingErrorDialog extends Dialog {

    public UploadingErrorDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_dialogfragment);
        setCancelable(false);

        Button ok = (Button) findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}
