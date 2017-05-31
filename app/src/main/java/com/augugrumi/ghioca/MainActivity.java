package com.augugrumi.ghioca;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.augugrumi.ghioca.listener.UploadingListener;
import com.augugrumi.ghioca.listener.defaultimplementation.DefaultUploadingListener;
import com.augugrumi.ghioca.utility.ConvertUriToFilePath;
import com.augugrumi.ghioca.utility.NetworkingUtility;
import com.augugrumi.ghioca.utility.SavingUtility;
import com.augugrumi.ghioca.utility.SearchType;
import com.augugrumi.ghioca.utility.SharedPreferencesManager;
import com.augugrumi.ghioca.utility.UploadingUtility;
import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.internal.manager.impl.Camera1Manager;
import com.github.florent37.camerafragment.internal.ui.BaseAnncaFragment;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.florent37.camerafragment.internal.utils.CameraHelper.hasCamera2;

public class MainActivity extends AppCompatActivity {

    private static boolean isStarting = true;

    public static final String FRAGMENT_TAG = "camera";
    private static final int SELECT_PICTURE = 100;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;

    @BindView(R.id.flash_switch_view)
    FlashSwitchView flashSwitchView;
    @BindView(R.id.front_back_camera_switcher)
    CameraSwitchView cameraSwitchView;
    @BindView(R.id.record_button)
    RecordButton recordButton;
    @BindView(R.id.pick_file)
    ImageButton pickFile;
    @BindView(R.id.menu_button)
    ImageButton buttonMenu;

    @BindView(R.id.record_duration_text)
    TextView recordDurationText;
    @BindView(R.id.record_size_mb_text)
    TextView recordSizeText;

    @BindView(R.id.cameraLayout)
    View cameraLayout;
    @BindView(R.id.addCameraButton)
    View addCameraButton;

    private DialogFragment wifiFragment;
    private Drawer menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        if (permissionsToRequest.isEmpty())
            addCamera();

        if (isStarting && SharedPreferencesManager.getUserWiFiPreference()) {
            FragmentManager fm = getSupportFragmentManager();
            if (savedInstanceState == null) {
                wifiFragment = new WiFiFragment();
            }
            if (wifiFragment != null && !NetworkingUtility.isWifiEnabled())
                wifiFragment.show(fm, WiFiFragment.TAG_WIFI_FRAGMENT);
            isStarting = false;
        }

        setUpDrawerMenu();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.addCameraButton)
    public void onAddCameraClicked() {
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
                    //Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                    ((BaseAnncaFragment) cameraFragment).reSetZoom();
                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            boolean b = false;
                            File f = new File("");
                            while (!b) {
                                try {
                                    Thread.sleep(700);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                f = new File(MyApplication.appFolderPath, name + ".jpg");
                                b = f.exists();
                            }
                            SavingUtility.mediaScannerCall(MainActivity.this, f);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            final String filePath = MyApplication.appFolderPath +
                                    File.separator + name + ".jpg";
                            setUpUpload(filePath);
                            if (!hasCamera2(MainActivity.this) && Camera1Manager.getCameraInstance() != null) {
                                Camera1Manager.getCameraInstance().startPreview();
                            }
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                }
            }, MyApplication.appFolderPath, name);
        }
    }

    @OnClick(R.id.pick_file)
    public void onPickFileClicked() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    final String filePath = ConvertUriToFilePath.getPathFromURI(MainActivity.this,
                            selectedImageUri);
                    setUpUpload(filePath);
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
                e.printStackTrace();
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
                    if (((degrees>88 && degrees <92) || (degrees>178 && degrees <182) ||
                        (degrees>268 && degrees <272) || (degrees > 358 || degrees < 2)) &&
                        degrees % 2 == 0 ) {

                        ViewCompat.setRotation(cameraSwitchView, degrees);
                        ViewCompat.setRotation(flashSwitchView, degrees);
                        ViewCompat.setRotation(recordDurationText, degrees);
                        ViewCompat.setRotation(recordSizeText, degrees);
                        ViewCompat.setRotation(pickFile, degrees);
                    }
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
                    //settingsView.setEnabled(false);
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                    //settingsView.setEnabled(true);
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
        return timestamp.toString().replaceAll(" ", "_").replaceAll(":","_");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @OnClick(R.id.menu_button)
    public void showMenu() {
        menu.openDrawer();
    }

    private void setUpUpload(String filePath) {
        Class toStart = null;
        if (SharedPreferencesManager.getUserSearchPreference() == SearchType.REVERSE_IMAGE_SEARCH)
            toStart = ReverseImageSearchResultActivity.class;
        else if (SharedPreferencesManager.getUserSearchPreference() == SearchType.OCR_SEARCH)
            toStart = OCRResultActivity.class;
        UploadingListener listener =
                new DefaultUploadingListener(filePath, MainActivity.this, toStart);
        if (NetworkingUtility.isConnectivityAvailable()) {
            listener.onStart();
            UploadingUtility.uploadToServer("file://" + filePath, MainActivity.this, listener);

        } else {
            listener.onFailure(null);
        }
    }

    private void setUpDrawerMenu() {
        menu = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.photo_size).withIcon(R.drawable.ic_photo_size)
                                .withIdentifier(1).withSelectable(false),
                        new SwitchDrawerItem().withName(R.string.wifi_reminder).withIcon(R.drawable.ic_wifi)
                                .withIdentifier(2).withSwitchEnabled(true).withSelectable(false).withSetSelected(false)
                                .withCheckable(false).withChecked(SharedPreferencesManager.getUserWiFiPreference())
                                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                                        SharedPreferencesManager.setUserWiFiPreference(isChecked);
                                    }
                                }),
                        new PrimaryDrawerItem().withName(R.string.change_camera_type).withIcon(R.drawable.ic_search)
                                .withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withSelectable(true).withName(R.string.ris)
                                        .withIdentifier(SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13)
                                        .withSetSelected(SharedPreferencesManager.getUserSearchPreference().ordinal()
                                                == SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13),
                                new SecondaryDrawerItem().withSelectable(true).withName(R.string.ocr)
                                        .withIdentifier(SearchType.OCR_SEARCH.ordinal() + 13)
                                        .withSetSelected(SharedPreferencesManager.getUserSearchPreference().ordinal()
                                                == SearchType.OCR_SEARCH.ordinal()  + 13)
                        ),
                        new PrimaryDrawerItem().withName(R.string.credits).withIcon(R.drawable.ic_credits)
                                .withIdentifier(4).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int)drawerItem.getIdentifier()){
                            case 1:
                                menu.closeDrawer();
                                final CameraFragmentApi cameraFragment = getCameraFragment();
                                if (cameraFragment != null) {
                                    cameraFragment.openSettingDialog();
                                }
                                break;
                            case 2: break; // ok
                            case 3:
                                List<IDrawerItem> subItems = ((PrimaryDrawerItem) drawerItem).getSubItems();
                                int toSelect = 0;
                                if (SharedPreferencesManager.getUserSearchPreference() == SearchType.REVERSE_IMAGE_SEARCH)
                                    toSelect = SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13;
                                else if (SharedPreferencesManager.getUserSearchPreference() == SearchType.OCR_SEARCH)
                                    toSelect = SearchType.OCR_SEARCH.ordinal() + 13;
                                for (IDrawerItem item : subItems) {
                                    item.withSetSelected(false);
                                    if (item.getIdentifier() == toSelect)
                                        item.withSetSelected(true);
                                    menu.updateItem(item);
                                }

                                break;
                            case 4: // TODO create credits activity
                                menu.closeDrawer();
                                break;
                            default:
                                if ((int)drawerItem.getIdentifier() == SearchType.REVERSE_IMAGE_SEARCH.ordinal() + 13)
                                    SharedPreferencesManager.setUserSearchPreference(SearchType.REVERSE_IMAGE_SEARCH);
                                else if ((int)drawerItem.getIdentifier() == SearchType.OCR_SEARCH.ordinal() + 13)
                                    SharedPreferencesManager.setUserSearchPreference(SearchType.OCR_SEARCH);
                        }
                        return false;
                    }
                })
                .withTranslucentStatusBar(true)
                .withSelectedItem(-1)
                .withCloseOnClick(false)
                .build();
    }
}
