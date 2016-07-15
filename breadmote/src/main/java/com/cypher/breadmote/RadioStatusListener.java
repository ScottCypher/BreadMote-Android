package com.cypher.breadmote;

/**
 * Created by scypher on 2/21/16.
 */
interface RadioStatusListener<T> {
    void onDisconnect();

    void onDeviceFound(T device);

    void onDiscoveryStateChange(boolean isDiscovering);

    void onRadioStateChange(boolean isOn);
}
