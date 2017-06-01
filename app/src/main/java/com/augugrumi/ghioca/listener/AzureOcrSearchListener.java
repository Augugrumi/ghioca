package com.augugrumi.ghioca.listener;

import it.polpetta.libris.opticalCharacterRecognition.azure.contract.IAzureOcrResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface AzureOcrSearchListener extends SearchingListener {
    void onSuccess(IAzureOcrResult result);
}

