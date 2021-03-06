package com.augugrumi.ghioca.listener;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface UploadingListener {
    void onStart();
    void onProgressUpdate(int progress);
    void onFinish(String url);
    void onFailure(Throwable error);
}
