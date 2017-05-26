package com.augugrumi.ghioca.listener;

import it.polpetta.libris.opticalCharacterRecognition.ibm.contract.IIBMOcrResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface WatsonOCRListener extends SearchingListener {
    void onSuccess(IIBMOcrResult result);
}