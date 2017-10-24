package com.philips.cdp2.ews.hotspotconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.philips.cdp2.ews.navigation.Navigator;
import com.philips.cdp2.ews.settingdeviceinfo.DeviceFriendlyNameFetcher;
import com.philips.cdp2.ews.troubleshooting.hotspotconnectionfailure.ConnectionUnsuccessfulViewModel;
import com.philips.cdp2.ews.wifi.WiFiConnectivityManager;
import com.philips.cdp2.ews.wifi.WiFiUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

public class ConnectingPhoneToHotspotWifiViewModel implements DeviceFriendlyNameFetcher.Callback {

    public interface ConnectingPhoneToHotSpotCallback {
        void registerReceiver(@NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter);

        void unregisterReceiver(@NonNull BroadcastReceiver receiver);

        Fragment getFragment();

        int requestCode();
    }

    private static final long DEVICE_CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

    @NonNull
    private final WiFiConnectivityManager wiFiConnectivityManager;

    @NonNull
    private final DeviceFriendlyNameFetcher deviceFriendlyNameFetcher;
    @NonNull
    private final WiFiUtil wiFiUtil;
    @NonNull
    private final Navigator navigator;
    @NonNull
    private final Handler handler;

    @Nullable
    private ConnectingPhoneToHotSpotCallback fragmentCallback;

    @NonNull
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
                int currentWifiState = wiFiUtil.getCurrentWifiState();
                Log.d("CONNECT", "Current Wifi State: " + wiFiUtil.getCurrentWifiState());
                if (currentWifiState == WiFiUtil.DEVICE_HOTSPOT_WIFI) {
                    onPhoneConnectedToHotspotWifi();
                    unregisterBroadcastReceiver();
                }
            }
        }
    };

    @NonNull
    private final Runnable timeOutAction = new Runnable() {
        @Override
        public void run() {
            onConnectionAttemptTimedOut();
        }
    };

    @Inject ConnectingPhoneToHotspotWifiViewModel(@NonNull WiFiConnectivityManager wiFiConnectivityManager,
                                                  @NonNull DeviceFriendlyNameFetcher deviceFriendlyNameFetcher,
                                                  @NonNull WiFiUtil wiFiUtil,
                                                  @NonNull Navigator navigator,
                                                  @NonNull @Named("mainLooperHandler") Handler handler) {
        this.wiFiConnectivityManager = wiFiConnectivityManager;
        this.deviceFriendlyNameFetcher = deviceFriendlyNameFetcher;
        this.wiFiUtil = wiFiUtil;
        this.navigator = navigator;
        this.handler = handler;
    }

    public void connectToHotSpot() {
        if (fragmentCallback != null) {
            fragmentCallback.registerReceiver(broadcastReceiver, createIntentFilter());
        }
        handler.postDelayed(timeOutAction, DEVICE_CONNECTION_TIMEOUT);
        wiFiConnectivityManager.connectToApplianceHotspotNetwork(WiFiUtil.DEVICE_SSID);
    }

    public void handleCancelButtonClicked() {
        // TODO cancel whatever is going on now
        navigator.navigateBack();
    }

    public void onResultReceived(int result) {
        if (result == ConnectionUnsuccessfulViewModel.HELP_NEEDED_RESULT) {
            navigator.navigateToResetConnectionTroubleShootingScreen();
        } else if (result == ConnectionUnsuccessfulViewModel.HELP_NOT_NEEDED_RESULT) {
            navigator.navigateToCompletingDeviceSetupScreen();
        }
    }

    public void clear() {
        if (fragmentCallback != null) {
            fragmentCallback.unregisterReceiver(broadcastReceiver);
        }
        setFragmentCallback(null);
    }

    private void onPhoneConnectedToHotspotWifi() {
        deviceFriendlyNameFetcher.setNameFetcherCallback(this);
        deviceFriendlyNameFetcher.fetchFriendlyName();
    }

    private void showUnsuccessfulDialog() {
        if (fragmentCallback != null) {
            navigator.navigateToUnsuccessfulConnectionDialog(fragmentCallback.getFragment(),
                    fragmentCallback.requestCode());
        }
    }

    private void onConnectionAttemptTimedOut() {
        showUnsuccessfulDialog();
    }

    private IntentFilter createIntentFilter() {
        return new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    }

    private void unregisterBroadcastReceiver() {
        if (fragmentCallback != null) {
            fragmentCallback.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override public void onFriendlyNameFetchingSuccess(@NonNull String friendlyName) {
        navigator.navigateToConnectToDeviceWithPasswordScreen(friendlyName);
    }

    @Override public void onFriendlyNameFetchingFailed() {
        showUnsuccessfulDialog();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    @Nullable
    ConnectingPhoneToHotSpotCallback getFragmentCallback() {
        return fragmentCallback;
    }

    public void setFragmentCallback(@Nullable ConnectingPhoneToHotSpotCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }
}