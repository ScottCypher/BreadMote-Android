package com.cypher.breadmote;

import android.content.Context;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cypher1 on 1/27/16.
 */
class ConnectionManager implements IOConnectionListener, ScanListener {
    private final List<ConnectionListener> connectionListeners;
    private final Context context;
    private Connection connection;
    private Thread readThread, writeThread;
    private Manager manager;
    private final List<ScanListener> scanListeners;

    public ConnectionManager(Context context) {
        this.context = context;
        connectionListeners = new LinkedList<>();
        scanListeners = new LinkedList<>();
    }

    public static Device toDevice(Context context, String address, String name) {
        String displayName = getActualName(context, name);
        return new Device(address, displayName);
    }

    private static String getActualName(Context context, String name) {
        if (name == null || name.trim().isEmpty()) {
            name = context.getString(R.string.connect_device_nameless);
        }
        return name;
    }

    public void addListener(ConnectionListener connectionListener) {
        connectionListeners.add(connectionListener);
        if (manager != null && manager.hasDisconnected()) {
            connectionListener.onDisconnect();
        } else if (connection != null) {
            connectionListener.onConnect(connection);
        }
    }

    public boolean removeListener(ConnectionListener connectionListener) {
        return connectionListeners.remove(connectionListener);
    }

    public void addListener(ScanListener scanListener) {
        scanListeners.add(scanListener);
        scanListener.onScanStateChange(manager.isScanning());
        scanListener.setCurrentDevices(manager.getDevices());
    }

    public boolean removeListener(ScanListener scanListener) {
        return scanListeners.remove(scanListener);
    }

    public void terminate() {
        if (manager != null) {
            manager.terminate();
            manager = null;
        }
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    @Override
    public void onConnect(InputStream inputStream, OutputStream outputStream) {
        connection = new Connection(context, manager);
        MessageWriter messageWriter = new MessageWriter(outputStream, manager);
        MessageReader messageReader = new MessageReader(inputStream, connection, manager);

        readThread = new Thread(messageReader);
        readThread.start();

        writeThread = new Thread(messageWriter);
        writeThread.start();

        connection.setReadWrite(messageReader, messageWriter);


        for (ConnectionListener connectionListener : connectionListeners) {
            connectionListener.onConnect(connection);
        }
    }

    @Override
    public void onDisconnect() {
        if (writeThread != null) {
            writeThread.interrupt();
            writeThread = null;
        }

        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }

        for (ConnectionListener connectionListener : connectionListeners) {
            connectionListener.onDisconnect();
        }
    }

    public void startScan(ConnectionType connectionType) {
        if (manager == null) {
            switch (connectionType) {
                case BLUETOOTH:
                    manager = new BluetoothManager(context, this, this);
                    break;
                case WIFI:
                    manager = new CustomWifiManager(context, this, this);
                    break;
                default:
                    throw new RuntimeException("Unhandled connection type: " + connectionType);
            }
        }

        if (manager.isRadioEnabled()) {
            manager.startScan();
        } else {
            manager.enableRadio(true);
        }
    }

    public boolean connect(String macAddress) {
        return ((BluetoothManager) manager).connect(macAddress);
    }

    public boolean connect(CustomWifiManager.ConnectionInfo connectionInfo) {
        return ((CustomWifiManager) manager).connect(connectionInfo);
    }

    @Override
    public void onScanStateChange(boolean isScanning) {
        for (ScanListener scanListener : scanListeners) {
            scanListener.onScanStateChange(isScanning);
        }
    }

    @Override
    public void onDeviceFound(Device device) {
        for (ScanListener scanListener : scanListeners) {
            scanListener.onDeviceFound(device);
        }
    }

    @Override
    public void setCurrentDevices(List<Device> devices) {
        for (ScanListener scanListener : scanListeners) {
            scanListener.setCurrentDevices(devices);
        }
    }
}
