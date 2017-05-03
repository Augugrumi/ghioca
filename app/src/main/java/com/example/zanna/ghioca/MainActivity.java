package com.example.zanna.ghioca;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;

import io.filepicker.Filepicker;

import static com.example.zanna.ghioca.MyApplication.MY_API_KEY;
import static com.example.zanna.ghioca.SavingUtility.folderPath;

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
        super.onPause();
        cameraView.stop();
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
                new AsyncTask<Void, Void, Void>(){

                    ProgressDialog uploadProgressDialog;
                    AlertDialog resultDialog;
                    SearchingListener searchingListener;
                    UploadingListener uploadingListener;

                    @Override
                    protected void onPreExecute() {
                        uploadProgressDialog = new ProgressDialog(MainActivity.this,
                                ProgressDialog.STYLE_HORIZONTAL);
                        uploadProgressDialog.setCancelable(false);
                        uploadProgressDialog.setTitle("Uploading the image");
                        uploadProgressDialog.show();

                        resultDialog = new AlertDialog.Builder(MainActivity.this).create();
                        resultDialog.setTitle("Result");


                        uploadingListener = new UploadingListener() {
                            @Override
                            public void onProgressUpdate(int progress) {
                                uploadProgressDialog.setProgress(progress);
                            }

                            @Override
                            public void onFinish(String url) {
                                uploadProgressDialog.dismiss();
                                SearchingUtility.searchImage(url, searchingListener);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                error.printStackTrace();
                                uploadProgressDialog.dismiss();
                            }
                        };

                        searchingListener = new SearchingListener() {
                            @Override
                            public void onFailure(Throwable error) {
                                error.printStackTrace();
                                resultDialog.dismiss();
                            }

                            @Override
                            public void onSuccess(JSONObject answer) {
                                try {
                                    resultDialog.setMessage(answer.getString("best_guess"));
                                    resultDialog.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        Log.i("provaupload", "1");
                        name = photoName();
                        Log.i("provaupload", "2");
                        SavingUtility.saveFile(jpeg, name, MainActivity.this);
                        Log.i("provaupload", "3");
                        UploadingUtility.uploadToServer("file://" + folderPath
                                + "/" + name, MainActivity.this, uploadingListener);
                        Log.i("provaupload", "4");
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    Log.i("asdkjvbgfòadgba", "Image Path : " +
                            selectedImageUri);
                    new AsyncTask<Void, Void, Void>(){

                        ProgressDialog uploadProgressDialog;
                        AlertDialog resultDialog;
                        SearchingListener searchingListener;
                        UploadingListener uploadingListener;

                        @Override
                        protected void onPreExecute() {
                            uploadProgressDialog = new ProgressDialog(MainActivity.this,
                                    ProgressDialog.STYLE_HORIZONTAL);
                            uploadProgressDialog.setCancelable(false);
                            uploadProgressDialog.setTitle("Uploading the image");
                            uploadProgressDialog.show();

                            resultDialog = new AlertDialog.Builder(MainActivity.this).create();
                            resultDialog.setTitle("Result");


                            uploadingListener = new UploadingListener() {
                                @Override
                                public void onProgressUpdate(int progress) {
                                    uploadProgressDialog.setProgress(progress);
                                }

                                @Override
                                public void onFinish(String url) {
                                    uploadProgressDialog.dismiss();
                                    SearchingUtility.searchImage(url, searchingListener);
                                }

                                @Override
                                public void onFailure(Throwable error) {
                                    error.printStackTrace();
                                    uploadProgressDialog.dismiss();
                                }
                            };

                            searchingListener = new SearchingListener() {
                                @Override
                                public void onFailure(Throwable error) {
                                    error.printStackTrace();
                                    resultDialog.dismiss();
                                }

                                @Override
                                public void onSuccess(JSONObject answer) {
                                    try {
                                        resultDialog.setMessage(answer.getString("best_guess"));
                                        resultDialog.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                String filePath = ConvertUriToFilePath.getPathFromURI(MainActivity.this, selectedImageUri);
                                File file = new File(filePath);
                                byte[] jpeg = new byte[(int) file.length()];

                                FileInputStream fis = new FileInputStream(file);
                                fis.read(jpeg); //read file into bytes[]
                                fis.close();
                                UploadingUtility.uploadToServer("file://" + filePath,
                                        MainActivity.this, uploadingListener);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute(null, null, null);
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        Filepicker.unregistedLocalFileUploadCallbacks();
        super.onDestroy();
    }
}
