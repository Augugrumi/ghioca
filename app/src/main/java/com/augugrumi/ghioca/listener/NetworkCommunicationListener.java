package com.augugrumi.zanna.ghioca.listener;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public interface NetworkCommunicationListener extends Listener {
    void onStart();
    void onFailure(Exception e);
}
