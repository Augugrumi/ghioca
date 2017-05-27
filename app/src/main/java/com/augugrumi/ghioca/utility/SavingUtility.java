package com.augugrumi.ghioca.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

// TODO to implement
public class SavingUtility {

    /**
     * This class must not have instances
     */
    private SavingUtility(){}

    private static File getAppFolder() throws IOException {
        File folder = new File(MyApplication.appFolderPath);

        if (!folder.exists()) {
            if (!folder.mkdirs())
                throw new IOException("cannot create the folder");
        }

        return folder;
    }

    //
    public static void save(byte[] toSave, String fileName) throws IOException {
        File folder = getAppFolder();

        File file = new File(folder, fileName);
        if (file.exists()) {
            if (!file.delete())
                throw new IOException("file " + fileName + " exists and cannot be deleted");
        }

            FileOutputStream out = new FileOutputStream(file);
            out.write(toSave);
            out.flush();
            out.close();

            mediaScannerCall(MyApplication.getAppContext(), file);
    }

    public static void mediaScannerCall(final Context context, final File file) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                MediaScannerConnection.scanFile(context,
                        new String[]{file.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }


    public static void compressFileIfNeeded(String filePath) {
        File f = new File(filePath);

        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(filePath);
        int MAX_IMAGE_SIZE = 1000 * 1024;
        int streamLength = (int)f.length();
        if (streamLength > MAX_IMAGE_SIZE) {
            int compressQuality = 105;
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                try {
                    bmpStream.flush();//to avoid out of memory error
                    bmpStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compressQuality -= 5;
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
                byte[] bmpPicByteArray = bmpStream.toByteArray();
                streamLength = bmpPicByteArray.length;
                Log.d("test upload", "Quality: " + compressQuality);
                Log.d("test upload", "Size: " + streamLength);
            }

            FileOutputStream fo;

            try {
                f.delete();
                f = new File(filePath);
                fo = new FileOutputStream(f);
                fo.write(bmpStream.toByteArray());
                fo.flush();
                fo.close();
                ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "6");
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
