package com.example.zanna.ghioca;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zanna.ghioca.listener.UploadingListener;
import com.example.zanna.ghioca.utility.ConvertUriToFilePath;
import com.example.zanna.ghioca.utility.UploadingUtility;
import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.widgets.CameraSettingsView;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public static final String FRAGMENT_TAG = "camera";

    private static final int SELECT_PICTURE = 100;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;

    @Bind(R.id.settings_view)
    CameraSettingsView settingsView;
    @Bind(R.id.flash_switch_view)
    FlashSwitchView flashSwitchView;
    @Bind(R.id.front_back_camera_switcher)
    CameraSwitchView cameraSwitchView;
    @Bind(R.id.record_button)
    RecordButton recordButton;
    @Bind(R.id.pick_file)
    ImageButton pickFile;

    @Bind(R.id.record_duration_text)
    TextView recordDurationText;
    @Bind(R.id.record_size_mb_text)
    TextView recordSizeText;

    @Bind(R.id.cameraLayout)
    View cameraLayout;
    @Bind(R.id.addCameraButton)
    View addCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // acquiring permission runtime
        final String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        final List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
        } else addCamera();

    }

    @OnClick(R.id.flash_switch_view)
    public void onFlashSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    @OnClick(R.id.front_back_camera_switcher)
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            final String name = photoName();
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                @Override
                public void onPhotoTaken(final byte[] bytes, String filePath) {
                    Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();

                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean b = false;
                            File f;
                            while (!b) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                f = new File(MyApplication.appFolderPath, name + ".jpg");
                                b = f.exists();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            final ProgressDialog uploadProgressDialog;
                            uploadProgressDialog = new ProgressDialog(MainActivity.this);
                            uploadProgressDialog.setCancelable(false);
                            uploadProgressDialog.setTitle("Uploading the image");
                            uploadProgressDialog.show();
                            UploadingListener listener = new UploadingListener() {

                                @Override
                                public void onProgressUpdate(int progress) {
                                    uploadProgressDialog.setProgress(progress);
                                }

                                @Override
                                public void onFinish(String url) {
                                    uploadProgressDialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                    intent.putExtra("url", url);
                                    intent.putExtra("path", MyApplication.appFolderPath +
                                            File.separator + name + ".jpg");
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Throwable error) {
                                    uploadProgressDialog.dismiss();
                                    AlertDialog errorDialog;
                                    errorDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    errorDialog.setCancelable(true);
                                    errorDialog.setTitle("Error");
                                    errorDialog.setMessage("An error occur during the uploading please try again");
                                    errorDialog.show();
                                }
                            };
                            Log.i("provaupload", MyApplication.appFolderPath + File.separator + name + ".jpg");
                            UploadingUtility.uploadToServer("file://" + MyApplication.appFolderPath +
                                File.separator + name + ".jpg", MainActivity.this, listener);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                }
            }, MyApplication.appFolderPath, name);
        }
    }

    @OnClick(R.id.settings_view)
    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }

    @OnClick(R.id.pick_file)
    public void onPickFileClicked() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    final String filePath = ConvertUriToFilePath.getPathFromURI(MainActivity.this,
                            selectedImageUri);

                        final ProgressDialog uploadProgressDialog;
                        uploadProgressDialog = new ProgressDialog(MainActivity.this);
                        uploadProgressDialog.setCancelable(false);
                        uploadProgressDialog.setTitle("Uploading the image");
                        uploadProgressDialog.show();
                        UploadingListener listener = new UploadingListener() {

                            @Override
                            public void onProgressUpdate(int progress) {
                                uploadProgressDialog.setProgress(progress);
                            }

                            @Override
                            public void onFinish(String url) {
                                uploadProgressDialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                intent.putExtra("url", url);
                                intent.putExtra("path", filePath);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                uploadProgressDialog.dismiss();
                                AlertDialog errorDialog;
                                errorDialog = new AlertDialog.Builder(MainActivity.this).create();
                                errorDialog.setCancelable(true);
                                errorDialog.setTitle("Error");
                                errorDialog.setMessage("An error occur during the uploading please try again");
                                errorDialog.show();
                            }
                        };
                        Log.i("provaupload", filePath);
                        UploadingUtility.uploadToServer("file://" + filePath, MainActivity.this, listener);
                    }
                }

            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            try{
                addCamera();
            } catch (SecurityException e){

            }
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() throws SecurityException{
        addCameraButton.setVisibility(View.GONE);
        cameraLayout.setVisibility(View.VISIBLE);

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            //cameraFragment.setResultListener(new CameraFragmentResultListener() {
            //    @Override
            //    public void onVideoRecorded(String filePath) {
            //        Intent intent = PreviewActivity.newIntentVideo(MainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            //    }
//
            //    @Override
            //    public void onPhotoTaken(byte[] bytes, String filePath) {
            //        Intent intent = PreviewActivity.newIntentPhoto(MainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            //    }
            //});

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    cameraSwitchView.displayBackCamera();
                }

                @Override
                public void onCurrentCameraFront() {
                    cameraSwitchView.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                    flashSwitchView.displayFlashAuto();
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(cameraSwitchView, degrees);
                    ViewCompat.setRotation(flashSwitchView, degrees);
                    ViewCompat.setRotation(recordDurationText, degrees);
                    ViewCompat.setRotation(recordSizeText, degrees);
                    ViewCompat.setRotation(pickFile, degrees);
                }


                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    cameraSwitchView.setEnabled(false);
                    recordButton.setEnabled(false);
                    settingsView.setEnabled(false);
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                    settingsView.setEnabled(true);
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }
            });

        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    private String photoName() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString().replaceAll(" ", "_");
    }


}
