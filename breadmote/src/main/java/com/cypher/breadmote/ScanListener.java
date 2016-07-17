package com.cypher.breadmote;

import java.util.List;

/**
 * Used to actively listen to a device scan
 * @see BreadMote#scan(ConnectionType)
 * @see BreadMote#addListener(ScanListener)
 * @see BreadMote#removeListener(ScanListener)
 */
public interface ScanListener {
    /**
     * Called when the scan state has changed or when the listener is first added
     *
     * @param isScanning {@code true} if the scan is currently active, {@code false} otherwise
     */
    void onScanStateChange(boolean isScanning);

    /**
     * Called when the scan discovers a new device
     *
     * @param device A newly discovered device
     */
    void onDeviceFound(Device device);

    /**
     * Called when the listener is first added
     *
     * @param devices The current list of devices in the active or previous scan. This collection
     *                cannot be modified
     */
    void setCurrentDevices(List<Device> devices);
}
