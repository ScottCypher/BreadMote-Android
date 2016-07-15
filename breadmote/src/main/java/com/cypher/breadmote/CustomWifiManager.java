package com.cypher.breadmote;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by scypher on 3/9/16.
 */
class CustomWifiManager
        implements Manager<CustomWifiManager.ConnectionInfo>, RadioStatusListener<ScanResult>, IOConnectionListener,
        WifiReceiver.NetworkListener {

    private final WifiReceiver wifiReceiver;
    private final WifiManager wifiManager;
    private final List<ScanResult> scanResults;
    private final ScanListener scanListener;
    private final IOConnectionListener IOioConnectionListeneronnectionListener;
    private final boolean hasAdapter;
    private final Context context;
    private ConnectionInfo onConnectedInfo;
    private boolean discoverWhenEnabled;
    private boolean isScanning;
    private boolean hasDisconnected;
    private InputStream inputStream;
    private OutputStream outputStream;

    public CustomWifiManager(Context context, IOConnectionListener IOioConnectionListeneronnectionListener, ScanListener scanListener) {
        scanResults = new ArrayList<>();
        this.scanListener = scanListener;
        this.IOioConnectionListeneronnectionListener = IOioConnectionListeneronnectionListener;
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiReceiver(this, this);

        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

        hasAdapter = context.registerReceiver(wifiReceiver, filter) != null;
    }

    @Override
    public boolean connect(ConnectionInfo connectionInfo) {
        if (connectionInfo.getMacAddress() == null) {
            onNetworkConnected();
            return true;
        } else {
            ScanResult scanResult = lookupScanResult(connectionInfo.getMacAddress());
            hasDisconnected = false;

            if (scanResult != null) {
                this.onConnectedInfo = connectionInfo;

                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", scanResult.SSID);
                wifiConfig.preSharedKey = String.format("\"%s\"", connectionInfo.getPassword());

                int netId = wifiManager.addNetwork(wifiConfig);
                if (netId != -1) {
                    return wifiManager.enableNetwork(netId, true);
                }
            }
            return false;
        }
    }

    private ScanResult lookupScanResult(String deviceAddress) {
        for (ScanResult scanResult : scanResults) {
            if (scanResult.BSSID.equals(deviceAddress)) {
                return scanResult;
            }
        }
        return null;
    }

    @Override
    public void disconnect() {
        onDisconnect();
        wifiManager.disconnect();

        //hasDisconnected represents a more 'natural' disconnect
        hasDisconnected = false;
    }

    @Override
    public boolean hasDisconnected() {
        return hasDisconnected;
    }

    @Override
    public void terminate() {
        scanResults.clear();
        //TODO handle more gracefully
        try {
            context.unregisterReceiver(wifiReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enableRadio(boolean discoverWhenEnabled) {
        this.discoverWhenEnabled = true;
        wifiManager.setWifiEnabled(true);
    }

    @Override
    public void startScan() {
        onDiscoveryStateChange(true);
        scanResults.clear();
        wifiManager.startScan();
    }

    @Override
    public boolean isRadioEnabled() {
        return wifiManager.isWifiEnabled();
    }

    @Override
    public boolean isScanning() {
        return isScanning;
    }

    @Override
    public List<Device> getDevices() {
        return toDevice(scanResults);
    }

    private List<Device> toDevice(List<ScanResult> scanResults) {
        List<Device> devices = new LinkedList<>();

        for (ScanResult device : scanResults) {
            devices.add(toDevice(device));
        }

        return Collections.unmodifiableList(devices);
    }

    @Override
    public void onDisconnect() {
        if (!hasDisconnected) {
            hasDisconnected = true;
            closeSocket();
            IOioConnectionListeneronnectionListener.onDisconnect();
        }
    }

    private void closeSocket() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
    }

    @Override
    public void onDeviceFound(ScanResult scanResult) {
        if (isScanning) {
            scanResults.add(scanResult);
            Device device = toDevice(scanResult);
            scanListener.onDeviceFound(device);
        }
    }

    private Device toDevice(ScanResult scanResult) {
        String address = scanResult.BSSID;
        String name = scanResult.SSID;
        return ConnectionManager.toDevice(context, address, name);
    }

    @Override
    public void onDiscoveryStateChange(boolean isDiscovering) {
        this.isScanning = isDiscovering;
        scanListener.onScanStateChange(isScanning);
    }

    @Override
    public void onRadioStateChange(boolean isOn) {
        if (!isOn) {
            scanResults.clear();
            onDisconnect();
        } else if (discoverWhenEnabled) {
            startScan();
        }
    }

    @Override
    public void onNetworkConnected() {
        if (onConnectedInfo != null) {
            WifiThread wifiThread = new WifiThread(this, onConnectedInfo.getHost(), onConnectedInfo.getPort());
            onConnectedInfo = null;
            wifiThread.start();
        }
    }

    @Override
    public void onConnect(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        IOioConnectionListeneronnectionListener.onConnect(inputStream, outputStream);
        hasDisconnected = false;
    }

    /**
     * Created by scypher on 3/13/16.
     */
    private static class WifiThread extends Thread {
        private static final String TAG = "WifiThread";
        //TODO effective way to avoid need this?
        private static final int CONNECT_DELAY = 2500;

        private final String host;
        private final int port;
        private final IOConnectionListener ioConnectionListener;

        public WifiThread(IOConnectionListener ioConnectionListener, String host, int port) {
            this.ioConnectionListener = ioConnectionListener;
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(CONNECT_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Socket socket = new Socket(host, port);
                final InputStream inputStream = socket.getInputStream();
                final OutputStream outputStream = socket.getOutputStream();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "success");
                        ioConnectionListener.onConnect(inputStream, outputStream);
                    }
                });
                return;
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "failure");
                    ioConnectionListener.onDisconnect();
                }
            });
        }
    }

    /**
     * Created by scypher on 4/17/16.
     */
    static class ConnectionInfo {
        private final String host;
        private final int port;
        private final String macAddress;
        private final String password;

        public ConnectionInfo(Device device, String host, int port, String password) {
            this.macAddress = device.getAddress();
            this.host = host;
            this.port = port;
            this.password = password;
        }

        public ConnectionInfo(int port, String host) {
            this.port = port;
            this.host = host;
            macAddress = null;
            password = null;
        }

        public String getPassword() {
            return password;
        }

        public int getPort() {
            return port;
        }

        public String getHost() {
            return host;
        }

        public String getMacAddress() {
            return macAddress;
        }

    }
}
