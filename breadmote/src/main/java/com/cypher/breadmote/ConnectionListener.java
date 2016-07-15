package com.cypher.breadmote;

/**
 * Used to listen to connects and disconnects with a device
 *
 * @see BreadMote#addListener(ConnectionListener)
 * @see BreadMote#removeListener(ConnectionListener)
 */
public interface ConnectionListener {
    /**
     * Called when the connected device has disconnected
     */
    void onDisconnect();

    /**
     * Called when the device is connected to or when the listener is first added
     *
     * @param connection The Connection object used to interact with the device
     */
    void onConnect(Connection connection);
}
