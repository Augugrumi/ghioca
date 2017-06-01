package com.augugrumi.ghioca.listener;

import com.augugrumi.ghioca.asyncTask.asynkTaskResult.FreeOcrSpaceOCRResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface FreeOcrSpaceOCRListener extends SearchingListener {
    void onSuccess(FreeOcrSpaceOCRResult result);
}