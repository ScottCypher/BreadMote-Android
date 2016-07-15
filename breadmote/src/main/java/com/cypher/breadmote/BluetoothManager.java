package com.cypher.breadmote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by scypher on 2/21/16.
 */
class BluetoothManager implements
        RadioStatusListener<BluetoothDevice>,
        Manager<String>,
        IOConnectionListener {

    private final List<BluetoothDevice> bluetoothDevices;
    private final IOConnectionListener ioConnectionListener;
    private final ScanListener scanListener;
    private final BluetoothReceiver bluetoothReceiver;
    private final boolean hasAdapter;
    private final Context context;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isScanning;
    private boolean discoverWhenEnabled;
    private boolean hasDisconnected;

    public BluetoothManager(Context context, IOConnectionListener ioConnectionListener, ScanListener scanListener) {
        bluetoothDevices = new ArrayList<>();
        this.ioConnectionListener = ioConnectionListener;
        this.scanListener = scanListener;
        this.context = context;

        bluetoothReceiver = new BluetoothReceiver(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        hasAdapter = BluetoothAdapter.getDefaultAdapter() != null;
        context.registerReceiver(bluetoothReceiver, filter);
    }

    @Override
    public void onConnect(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        ioConnectionListener.onConnect(inputStream, outputStream);
        hasDisconnected = false;
    }

    @Override
    public void onDisconnect() {
        if (!hasDisconnected) {
            hasDisconnected = true;
            closeSocket();
            ioConnectionListener.onDisconnect();
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice bluetoothDevice) {
        boolean found = false;
        for (BluetoothDevice currentDevice : bluetoothDevices) {
            if (currentDevice.getAddress().equals(bluetoothDevice.getAddress())) {
                found = true;
                break;
            }
        }

        if (!found) {
            bluetoothDevices.add(bluetoothDevice);
            Device device = toDevice(bluetoothDevice);
            scanListener.onDeviceFound(device);
        }
    }

    private Device toDevice(BluetoothDevice bluetoothDevice) {
        String address = bluetoothDevice.getAddress();
        String name = bluetoothDevice.getName();
        return ConnectionManager.toDevice(context, address, name);
    }

    @Override
    public void onDiscoveryStateChange(boolean isDiscovering) {
        if (this.isScanning != isDiscovering) {
            onDiscoveryStateChangedHelper(isDiscovering);
        }
    }

    @Override
    public void onRadioStateChange(boolean isOn) {
        if (!isOn) {
            bluetoothDevices.clear();
            onDisconnect();
        } else if (discoverWhenEnabled) {
            startScan();
        }
    }

    @Override
    public boolean hasDisconnected() {
        return hasDisconnected;
    }

    @Override
    public boolean connect(String macAddress) {
        cancelDiscovery();
        hasDisconnected = false;

        BluetoothDevice bluetoothDevice = lookupBluetoothDevice(macAddress);
        if (bluetoothDevice != null) {
            BluetoothThread bluetoothThread = new BluetoothThread(bluetoothDevice, this);
            bluetoothThread.start();
            return true;
        } else {
            return false;
        }
    }

    private BluetoothDevice lookupBluetoothDevice(String deviceAddress) {
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            if (bluetoothDevice.getAddress().equals(deviceAddress)) {
                return bluetoothDevice;
            }
        }
        return null;
    }

    @Override
    public void disconnect() {
        onDisconnect();
        //hasDisconnected represents a more 'natural' disconnect
        hasDisconnected = false;
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
    public void terminate() {
        bluetoothDevices.clear();
        //TODO handle more gracefully
        try {
            context.unregisterReceiver(bluetoothReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        cancelDiscovery();
    }

    @Override
    public void enableRadio(boolean discoverWhenEnabled) {
        if (hasAdapter) {
            this.discoverWhenEnabled = discoverWhenEnabled;
            BluetoothAdapter.getDefaultAdapter().enable();
        } else {
            onRadioStateChange(false);
        }
    }

    @Override
    public void startScan() {
        if (hasAdapter) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.startDiscovery();
                onDiscoveryStateChangedHelper(true);
                bluetoothDevices.clear();
            }
        } else {
            onRadioStateChange(false);
        }
    }

    @Override
    public boolean isRadioEnabled() {
        if (hasAdapter) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return bluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    @Override
    public boolean isScanning() {
        return isScanning;
    }

    @Override
    public List<Device> getDevices() {
        return toDevice(bluetoothDevices);
    }

    private List<Device> toDevice(List<BluetoothDevice> bluetoothDeviceList) {
        List<Device> devices = new LinkedList<>();

        for (BluetoothDevice device : bluetoothDeviceList) {
            devices.add(toDevice(device));
        }

        return Collections.unmodifiableList(devices);
    }

    private void cancelDiscovery() {
        if (hasAdapter) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
        } else {
            onDiscoveryStateChangedHelper(false);
        }
    }

    private void onDiscoveryStateChangedHelper(boolean isDiscovering) {
        this.isScanning = isDiscovering;
        scanListener.onScanStateChange(isDiscovering);
    }

    /**
     * Created by cypher1 on 1/31/16.
     */
    private static class BluetoothThread extends Thread {
        private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        private final IOConnectionListener connectionListener;
        private BluetoothSocket socket;

        public BluetoothThread(BluetoothDevice device, IOConnectionListener connectionListener) {
            this.connectionListener = connectionListener;

            UUID uuid = UUID.fromString(SPP_UUID);
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            if (socket != null) {
                try {
                    socket.connect();
                    final InputStream inputStream = socket.getInputStream();
                    final OutputStream outputStream = socket.getOutputStream();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            connectionListener.onConnect(inputStream, outputStream);
                        }
                    });

                    return;
                } catch (IOException exception) {
                    //unable to connect
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    exception.printStackTrace();
                }
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    connectionListener.onDisconnect();
                }
            });
        }
    }
}
