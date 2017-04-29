package com.example.zanna.ghioca;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {

    CameraView cameraView;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SavingUtility.runTimePermissionAcquirement(this);

        cameraView = (CameraView)findViewById(R.id.camera);

        final ImageButton flash = (ImageButton)findViewById(R.id.btn_flash);
        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);

        flash.setOnClickListener(new View.OnClickListener() {
            boolean flashOn = true;

            @Override
            public void onClick(View v) {
                flashOn = !flashOn;
                if (flashOn){
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    flash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                } else {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    flash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                }
            }
        });

        ImageButton camera = (ImageButton)findViewById(R.id.btn_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(final byte[] jpeg) {
                super.onPictureTaken(jpeg);
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {

                        SavingUtility.saveFile(jpeg, photoName(), MainActivity.this);

                        /*String url = "";
                        try {
                            final String response = PostUtility.postRequest(url, jpeg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                        return null;
                    }
                }.execute(null, null, null);
            }

            private String photoName() {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                return timestamp.toString().replaceAll(" ", "_") + ".jpeg";
            }
        });
    }
}
