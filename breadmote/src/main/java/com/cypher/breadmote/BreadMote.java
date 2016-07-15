package com.cypher.breadmote;

import android.content.Context;

/**
 * This class is the starting point for BreadMote operations. After it has been initialized, it can be
 * used to scan for nearby devices or connect to nearby/remote devices.
 */
public final class BreadMote {
    private static BreadMote breadMote;
    private final ConnectionManager connectionManager;

    private BreadMote(Context context) {
        connectionManager = new ConnectionManager(context);
    }

    private static BreadMote getBreadMote() {
        if (breadMote == null) {
            throw new RuntimeException("initialize(Context) must be called first");
        } else {
            return breadMote;
        }
    }

    /**
     * Prepares the BreadMote SDK. This should be called only once, such as in your application's
     * {@code onCreate}.
     *
     * @param context The Android context used to initialize the SDK
     */
    public static void initialize(Context context) {
        if (breadMote == null) {
            breadMote = new BreadMote(context);
        } else {
            throw new RuntimeException("BreadMote should only be initialized once");
        }
    }

    /**
     * Begins a scan for nearby devices. Scans end on their own after the default time period
     * (determined by the system) or when initiating a connection.
     *
     * @param connectionType The type of scan to be started
     * @see #addListener(ScanListener)
     * @see #removeListener(ScanListener)
     */
    public static void scan(ConnectionType connectionType) {
        getBreadMote().connectionManager.startScan(connectionType);
    }

    /**
     * Cleans up all resources related to scanning, connecting, or interacting with a device
     */
    public static void terminate() {
        getBreadMote().connectionManager.terminate();
    }

    /**
     * Connects to the specified device over Bluetooth
     *
     * @param device The device to connect to
     * @return {@code true} if the connection process could be successfully started, {@code false} otherwise
     * @see #scan(ConnectionType)
     * @see #addListener(ConnectionListener)
     * @see #removeListener(ConnectionListener)
     */
    public static boolean connectBluetooth(Device device) {
        return getBreadMote().connectionManager.connect(device.getAddress());
    }

    /**
     * Connects to host on the specified WiFi network
     * @param device The network to connect to
     * @param password The password needed to connect to the network
     * @param host The host to connect to
     * @param port The port to use for connecting to the host
     * @return {@code true} if the connection process could be successfully started, {@code false} otherwise
     * @see #scan(ConnectionType)
     * @see #addListener(ConnectionListener)
     * @see #removeListener(ConnectionListener)
     */
    public static boolean connectWiFi(Device device, String password, String host, int port) {
        CustomWifiManager.ConnectionInfo connectionInfo = new CustomWifiManager.ConnectionInfo(device, host, port, password);
        return getBreadMote().connectionManager.connect(connectionInfo);
    }

    /**
     * Connects to the specified host on the current network
     *
     * @param host The host to connect to
     * @param port The port to use for connecting to the host
     * @return {@code true} if the connection process could be successfully started, {@code false} otherwise
     * @see #addListener(ConnectionListener)
     * @see #removeListener(ConnectionListener)
     */
    public static boolean connectWiFi(String host, int port) {
        CustomWifiManager.ConnectionInfo connectionInfo = new CustomWifiManager.ConnectionInfo(port, host);
        return getBreadMote().connectionManager.connect(connectionInfo);
    }

    /**
     * Registers a listener to observe {@link #scan(ConnectionType)} events
     * @param scanListener The listener to be registered
     */
    public static void addListener(ScanListener scanListener) {
        getBreadMote().connectionManager.addListener(scanListener);
    }

    /**
     * Unregisteres a listener from {@link #scan(ConnectionType)} events
     * @param scanListener The listener to be unregistered
     * @return {@code true} if the listener was successfully removed, {@code false} otherwise
     */
    public static boolean removeListener(ScanListener scanListener) {
        return getBreadMote().connectionManager.removeListener(scanListener);
    }

    /**
     * Registers a listener to observe connection events
     * @param connectionListener The listener to be registered
     */
    public static void addListener(ConnectionListener connectionListener) {
        getBreadMote().connectionManager.addListener(connectionListener);
    }

    /**
     * Unregisters a listener from connection events
     * @param connectionListener The listener to be unregistered
     * @return {@code true} if the listener was successfully removed, {@code false} otherwise
     */
    public static boolean removeListener(ConnectionListener connectionListener) {
        return getBreadMote().connectionManager.removeListener(connectionListener);
    }
}
