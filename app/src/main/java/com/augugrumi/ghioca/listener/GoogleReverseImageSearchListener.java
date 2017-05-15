package com.augugrumi.ghioca.listener;

import it.polpetta.libris.image.google.contract.IGoogleImageSearchResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface GoogleReverseImageSearchListener extends SearchingListener {
    void onSuccess(IGoogleImageSearchResult result);
}
