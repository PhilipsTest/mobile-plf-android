package com.philips.cdp2.ews.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.homewificonnection.ConnectingDeviceWithWifiFragment;
import com.philips.cdp2.ews.hotspotconnection.ConnectingPhoneToHotspotWifiFragment;
import com.philips.cdp2.ews.settingdeviceinfo.ConnectWithPasswordFragment;
import com.philips.cdp2.ews.troubleshooting.connecttowrongphone.ConnectToWrongPhoneTroubleshootingFragment;
import com.philips.cdp2.ews.troubleshooting.resetconnection.ResetConnectionTroubleshootingFragment;
import com.philips.cdp2.ews.troubleshooting.resetdevice.ResetDeviceTroubleshootingFragment;
import com.philips.cdp2.ews.troubleshooting.setupaccesspointmode.SetupAccessPointModeTroubleshootingFragment;
import com.philips.cdp2.ews.troubleshooting.wificonnectionfailure.WifiConnectionUnsuccessfulFragment;
import com.philips.cdp2.ews.troubleshooting.wificonnectionfailure.WrongWifiNetworkFragment;
import com.philips.cdp2.ews.view.EWSDevicePowerOnFragment;
import com.philips.cdp2.ews.view.EWSGettingStartedFragment;
import com.philips.cdp2.ews.view.EWSHomeWifiDisplayFragment;
import com.philips.cdp2.ews.view.EWSPressPlayAndFollowSetupFragment;
import com.philips.cdp2.ews.view.EWSWiFiPairedFragment;

public class Navigator {

    @NonNull
    private final FragmentNavigator fragmentNavigator;

    public Navigator(@NonNull final FragmentNavigator fragmentNavigator) {
        this.fragmentNavigator = fragmentNavigator;
    }

    public void navigateToGettingStartedScreen() {
        pushFragment(new EWSGettingStartedFragment());
    }

    public void navigateToHomeNetworkConfirmationScreen() {
        boolean isPresentInStack = fragmentNavigator
                .popToFragment(EWSHomeWifiDisplayFragment.class.getCanonicalName());
        if (!isPresentInStack) {
            pushFragment(new EWSHomeWifiDisplayFragment());
        }
    }

    public void navigateToDevicePoweredOnConfirmationScreen() {
        boolean isPresentInStack =
                fragmentNavigator.popToFragment(EWSDevicePowerOnFragment.class.getCanonicalName());
        if (!isPresentInStack) {
            pushFragment(new EWSDevicePowerOnFragment());
        }
    }

    public void navigateToCompletingDeviceSetupScreen() {
        boolean isPresentInStack = fragmentNavigator
                .popToFragment(EWSPressPlayAndFollowSetupFragment.class.getCanonicalName());
        if (!isPresentInStack) {
            pushFragment(new EWSPressPlayAndFollowSetupFragment());
        }
    }

    public void navigateToConnectToDeviceWithPasswordScreen(String friendlyName) {
        pushFragment(ConnectWithPasswordFragment.newInstance(friendlyName));
    }

    public void navigateToPairingSuccessScreen() {
        pushFragment(new EWSWiFiPairedFragment());
    }

    public void navigateToResetConnectionTroubleShootingScreen() {
        boolean isPresentInStack = fragmentNavigator
                .popToFragment(ResetConnectionTroubleshootingFragment.class.getCanonicalName());
        if (!isPresentInStack) {
            pushFragment(new ResetConnectionTroubleshootingFragment());
        }
    }

    public void navigateToResetDeviceTroubleShootingScreen() {
        pushFragment(new ResetDeviceTroubleshootingFragment());
    }

    public void navigateToConnectToWrongPhoneTroubleShootingScreen() {
        pushFragment(new ConnectToWrongPhoneTroubleshootingFragment());
    }

    public void navigateSetupAccessPointModeScreen() {
        pushFragment(new SetupAccessPointModeTroubleshootingFragment());
    }

    public void navigateToWIFIConnectionUnsuccessfulTroubleShootingScreen(@NonNull String deviceName, @NonNull String wifiSSID) {
        pushFragment(WifiConnectionUnsuccessfulFragment.newInstance(deviceName, wifiSSID));
    }

    public void navigateToConnectingPhoneToHotspotWifiScreen() {
        pushFragment(new ConnectingPhoneToHotspotWifiFragment());
    }

    public void navigateToConnectingDeviceWithWifiScreen(@NonNull final String homeWiFiSSID, @NonNull final String homeWiFiPassword, @NonNull final String deviceName,@NonNull final String deviceFriendlyName) {
        pushFragment(ConnectingDeviceWithWifiFragment.newInstance(homeWiFiSSID, homeWiFiPassword, deviceName,deviceFriendlyName));
    }

    public void navigateToConnectingDeviceWithWifiScreen(@Nullable Bundle bundle) {
        boolean isPresentInStack = fragmentNavigator.popToFragment(ConnectingDeviceWithWifiFragment.class.getCanonicalName());
        if (!isPresentInStack) {
            pushFragment(ConnectingDeviceWithWifiFragment.newInstance(bundle));
        }
    }

    public void navigateToEWSWiFiPairedScreen() {
        pushFragment(new EWSWiFiPairedFragment());
    }

    public void navigateToWrongWifiNetworkScreen(Bundle bundle) {
        pushFragment(WrongWifiNetworkFragment.newInstance(bundle));
    }

    public void navigateBack() {
        fragmentNavigator.pop();
    }

    private void pushFragment(@NonNull Fragment fragment) {
        fragmentNavigator.push(fragment, R.id.contentFrame);
    }
}