package com.cypher.breadmote;

import java.util.List;

/**
 * Created by scypher on 2/21/16.
 */
interface Manager<ConnectionInfo> {
    boolean connect(ConnectionInfo connectionInfo);

    void disconnect();

    boolean hasDisconnected();

    void terminate();

    void enableRadio(boolean discoverWhenEnabled);

    void startScan();

    boolean isRadioEnabled();

    boolean isScanning();

    List<Device> getDevices();

}
