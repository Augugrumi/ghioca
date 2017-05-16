package com.augugrumi.ghioca.listener;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface SavingListener extends Listener {
    void onSuccess();
    void onFailure(Throwable error);
}
