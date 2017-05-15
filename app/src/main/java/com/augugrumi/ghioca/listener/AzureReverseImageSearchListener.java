package com.augugrumi.zanna.ghioca.listener;

import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface AzureReverseImageSearchListener extends SearchingListener {
    void onSuccess(IAzureImageSearchResult result);
}
