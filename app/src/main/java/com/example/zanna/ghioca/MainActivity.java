package com.example.zanna.ghioca;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;
import java.sql.Timestamp;

import io.filepicker.Filepicker;

import static com.example.zanna.ghioca.MyApplication.MY_API_KEY;

public class MainActivity extends AppCompatActivity {
    final int SELECT_PICTURE = 100;
    CameraView cameraView;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Filepicker.setKey(MY_API_KEY);
        setContentView(R.layout.activity_main);
        SavingUtility.runTimePermissionAcquirement(this);

        Button imgPicker = (Button)findViewById(R.id.pick_file);
        imgPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

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
            String name;

            @Override
            public void onPictureTaken(final byte[] jpeg) {
                super.onPictureTaken(jpeg);
                name = photoName();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        SavingUtility.saveFile(jpeg, name, MainActivity.this);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        new AsyncUploadAndSave()
                                .setContextActivity(MainActivity.this)
                                .execute(SavingUtility.folderPath + File.separator + name, null, null);
                    }
                }.execute(null, null, null);



            }


        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    new AsyncUploadAndSave()
                            .setContextActivity(MainActivity.this)
                            .execute(ConvertUriToFilePath.getPathFromURI(MainActivity.this,
                                    selectedImageUri), null, null);
                }

            }
        }
    }

    private String photoName() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString().replaceAll(" ", "_") + ".jpeg";
    }

    @Override
    protected void onDestroy() {
        Filepicker.unregistedLocalFileUploadCallbacks();
        super.onDestroy();
    }
}
