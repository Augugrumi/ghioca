package com.example.zanna.ghioca;

import org.json.JSONObject;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface SearchingListener {
    void onFailure(Throwable error);
    void onSuccess(JSONObject answer);
}
