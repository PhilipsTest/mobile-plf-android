/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.ews.wifi;

import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.platform.ews.logger.EWSLogger;
import com.philips.platform.ews.util.TextUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("WeakerAccess")
@Singleton
public class WiFiUtil {

    public static final String DEVICE_SSID = "PHILIPS Setup";
    public static final String UNKNOWN_SSID = "<unknown ssid>";
    public static final int HOME_WIFI = 1;
    public static final int WRONG_WIFI = 2;
    public static final int UNKNOWN_WIFI = 3;
    public static final int DEVICE_HOTSPOT_WIFI = 4;
    private static final String TAG = "WiFiUtil";
    private static String lastWifiSSid;
    private static String selectedHomeWiFiSSID;
    @NonNull
    private WifiManager wifiManager;
    @NonNull
    private EWSLogger ewsLogger;

    @Inject
    public WiFiUtil(@NonNull WifiManager wifiManager, @NonNull EWSLogger ewsLogger) {
        this.wifiManager = wifiManager;
        this.ewsLogger = ewsLogger;
    }

    @Nullable
    public String getHomeWiFiSSD() {
        return lastWifiSSid;
    }

    public String getSelectedHomeWiFiSSID() {
        return selectedHomeWiFiSSID;
    }

    public void setSelectedHomeWiFiSSID(@NonNull final String selectedHomeWiFiSSID) {
        this.selectedHomeWiFiSSID = selectedHomeWiFiSSID;
    }

    public String getConnectedWiFiSSID() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            return getFormattedSSID(wifiInfo.getSSID());
        } else if (wifiInfo.getSupplicantState() == SupplicantState.DISCONNECTED) {
            lastWifiSSid = null;
            return null;
        }
        return null;
    }

    public String getFormattedSSID(@NonNull final String SSID) {
        return SSID.replace("\"", "");
    }

    @Nullable
    public String getCurrentWiFiSSID() {
        lastWifiSSid = getConnectedWiFiSSID();
        return lastWifiSSid;
    }

    public boolean isHomeWiFiEnabled() {
        return wifiManager.isWifiEnabled() && isWifiConnectedToNetwork() && (!DEVICE_SSID
                .equals(getCurrentWiFiSSID()));
    }

    public boolean isWifiConnectedToNetwork() {
        return getConnectedWiFiSSID() != null && !TextUtil.isEmpty(getConnectedWiFiSSID());
    }

    public boolean isConnectedToPhilipsSetup() {
        return getCurrentWiFiSSID() != null && DEVICE_SSID.equals(getCurrentWiFiSSID());
    }

    public
    @WiFiState
    int getCurrentWifiState() {
        String currentWifi = getConnectedWiFiSSID();
        ewsLogger.d(TAG, "Connected to:" + (currentWifi == null ? "Nothing" : currentWifi));

        if (lastWifiSSid == null) {
            lastWifiSSid = getConnectedWiFiSSID();
        }
        if (lastWifiSSid == null || currentWifi == null || currentWifi
                .equalsIgnoreCase(UNKNOWN_SSID)) {
            return UNKNOWN_WIFI;
        } else if (currentWifi.contains(DEVICE_SSID)) {
            return DEVICE_HOTSPOT_WIFI;
        } else if (currentWifi.contains(lastWifiSSid)) {
            return HOME_WIFI;
        } else if (!lastWifiSSid.equals(currentWifi)
                && !lastWifiSSid.equals(DEVICE_SSID)) {
            ewsLogger.d(TAG,
                    "Connected to wrong wifi, Current wifi " + currentWifi + " Home wifi " +
                            lastWifiSSid);
            return WRONG_WIFI;
        }
        return UNKNOWN_WIFI;
    }

    public void forgetHotSpotNetwork(String hotSpotWiFiSSID) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            ewsLogger.d(TAG, "Pre configured Wifi ssid " + config.SSID);
            String cleanSSID = config.SSID;
            cleanSSID = cleanSSID.replaceAll("^\"|\"$", "");
            if (cleanSSID.equals(hotSpotWiFiSSID)) {
                boolean success = wifiManager.removeNetwork(config.networkId);
                wifiManager.saveConfiguration();
                ewsLogger.i(TAG, "Removing network " + success);
            }
        }
    }

    @IntDef({HOME_WIFI, WRONG_WIFI, DEVICE_HOTSPOT_WIFI, UNKNOWN_WIFI})
    public @interface WiFiState {
    }
}
