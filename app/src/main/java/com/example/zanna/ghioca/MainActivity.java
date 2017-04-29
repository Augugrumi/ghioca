package com.example.zanna.ghioca;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        cameraView = (CameraView)findViewById(R.id.camera);

        final ImageButton flash = (ImageButton)findViewById(R.id.btn_flash);
        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);

        flash.setOnClickListener(new View.OnClickListener() {
            boolean flashOn = true;

            @Override
            public void onClick(View v) {
                Log.i("sjdvsdfò", "" + flashOn);
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
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        createDirectoryAndSaveFile(jpeg, timestamp.toString().trim() + ".jpeg");
                        String url = "";
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
                        }
                        return null;
                    }
                }.execute(null, null, null);
            }

            private void createDirectoryAndSaveFile(byte[] imageToSave, String fileName) {

                if (!isExternalStorageWritable()) {
                    return;
                }

                String folder_main = "Ghioca";
                File folder = new File(Environment.getExternalStorageDirectory().toString()+"/" +folder_main);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File file = new File(folder, fileName);
                if (file.exists()) {
                    file.delete();
                }

                Assert.assertTrue(folder.exists());

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(imageToSave);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            private boolean isExternalStorageWritable() {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    return true;
                }
                return false;
            }

        });
    }
}
