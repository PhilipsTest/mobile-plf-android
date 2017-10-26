/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.philips.cdp2.ews.logger.EWSLogger;
import com.philips.cdp2.ews.util.TextUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.philips.cdp2.ews.view.EWSActivity.EWS_STEPS;
import static com.philips.cdp2.ews.wifi.WiFiUtil.DEVICE_SSID;

@SuppressWarnings("WeakerAccess")
@Singleton
public class WiFiConnectivityManager {

    private static final int DELAY_IN_EACH_NEW_SCAN = 500;
    private static int maxAttempts = 10;
    private final WifiManager wifiManager;
    private Wifi wifi;
    private WiFiUtil wiFiUtil;

    @Inject
    public WiFiConnectivityManager(@NonNull final WifiManager wifiManager,
                                   @NonNull final Wifi wifi,
                                   @NonNull final WiFiUtil wiFiUtil) {
        this.wifiManager = wifiManager;
        this.wifi = wifi;
        this.wiFiUtil = wiFiUtil;
    }

    public void connectToHomeWiFiNetwork(@NonNull final String ssid) {
        EWSLogger.d(EWS_STEPS, "Step 5 : Connecting to home network");
        connectToNetwork(ssid);
    }

    public void connectToApplianceHotspotNetwork(final String deviceHotspotSSID) {
        wiFiUtil.forgetHotSpotNetwork(deviceHotspotSSID);
        configureOpenNetwork();

        if (!TextUtil.isEmpty(wiFiUtil.getCurrentWiFiSSID())) {
            connectToNetwork(DEVICE_SSID);
        }
    }

    private void configureOpenNetwork() {
        EWSLogger.d(EWS_STEPS, "1.1 Configuring open network");
        WifiConfiguration wfc = new WifiConfiguration();
        wfc.SSID = "\"" + DEVICE_SSID + "\"";
        wfc.status = WifiConfiguration.Status.ENABLED;
        wfc.priority = 40;
        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wfc.allowedAuthAlgorithms.clear();
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        wifiManager.addNetwork(wfc);
        wifiManager.saveConfiguration();
    }

    private void connectToNetwork(@NonNull final String ssid) {
        EWSLogger.d(EWS_STEPS, "Trying to connect to network " + ssid);
        ScanResult accessPoint = null;
        int attempt = 0;

        while (attempt++ < maxAttempts) {
            accessPoint = findNetworkAccessPoint(ssid);

            if (accessPoint != null) {
                EWSLogger.d(EWS_STEPS, "Found access point ");
                break;
            } else {
                wifiManager.startScan();
                try {
                    // TODO Re-consider please!
                    Thread.sleep(DELAY_IN_EACH_NEW_SCAN);
                } catch (InterruptedException ignored) {

                }
            }
        }

        if (accessPoint == null) {
            EWSLogger.d(EWS_STEPS, "Unable to connect to access point given ");
            return;
        }

        EWSLogger.d(EWS_STEPS, "Connecting to  " + accessPoint);
        wifi.connectToConfiguredNetwork(wifiManager, accessPoint);
    }

    private ScanResult findNetworkAccessPoint(@NonNull final String ssid) {
        List<ScanResult> foundNetworks = wifiManager.getScanResults();
        if (foundNetworks != null) {
            for (ScanResult foundNetwork : foundNetworks) {
                if (foundNetwork.SSID.equals(ssid)) {
                    return foundNetwork;
                }
            }
        }

        EWSLogger.e(EWS_STEPS, "Access point not found ");
        return null;
    }

    @VisibleForTesting
    public void setMaxScanAttempts(final int maxAttempts) {
        WiFiConnectivityManager.maxAttempts = maxAttempts;
    }
}
