package com.cypher.breadmote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by scypher on 3/9/16.
 */
class WifiReceiver extends BroadcastReceiver {

    private final RadioStatusListener<ScanResult> radioStatusListener;
    private final NetworkListener networkListener;

    public WifiReceiver(RadioStatusListener<ScanResult> radioStatusListener,
                        NetworkListener networkListener) {
        this.radioStatusListener = radioStatusListener;
        this.networkListener = networkListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

            if (supplicantState == SupplicantState.COMPLETED) {
                networkListener.onNetworkConnected();
            }
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int extra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (extra == WifiManager.WIFI_STATE_ENABLED) {
                radioStatusListener.onRadioStateChange(true);
            } else if (extra == WifiManager.WIFI_STATE_DISABLED) {
                radioStatusListener.onRadioStateChange(false);
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();

            Collections.sort(scanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    if (lhs.SSID.isEmpty() && rhs.SSID.isEmpty()) return 0;
                    if (lhs.SSID.isEmpty()) return 1;
                    if (rhs.SSID.isEmpty()) return -1;
                    return lhs.SSID.compareToIgnoreCase(rhs.SSID);
                }
            });

            for (ScanResult scanResult : scanResults) {
                radioStatusListener.onDeviceFound(scanResult);
            }

            radioStatusListener.onDiscoveryStateChange(false);
        }
    }

    /**
     * Created by scypher on 3/13/16.
     */
    interface NetworkListener {
        void onNetworkConnected();
    }
}
