package com.augugrumi.ghioca.listener;

import it.polpetta.libris.image.ibm.contract.IIBMImageSearchResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface WatsonReverseImageSearchListener extends SearchingListener {
    void onSuccess(IIBMImageSearchResult result);
}