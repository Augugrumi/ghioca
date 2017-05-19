package com.augugrumi.ghioca.listener;

import it.polpetta.libris.image.contract.IImageSearchResult;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface ImaggaReverseImageSearchListener extends SearchingListener {
    void onSuccess(IImageSearchResult result);
}
