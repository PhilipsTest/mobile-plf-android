package com.philips.platform.ews.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.philips.platform.ews.connectionsuccessful.ConnectionSuccessfulFragment;
import com.philips.platform.ews.homewificonnection.ConnectingDeviceWithWifiFragment;
import com.philips.platform.ews.hotspotconnection.ConnectingWithDeviceFragment;
import com.philips.platform.ews.settingdeviceinfo.ConnectWithPasswordFragment;
import com.philips.platform.ews.setupsteps.FirstSetupStepsFragment;
import com.philips.platform.ews.setupsteps.SecondSetupStepsFragment;
import com.philips.platform.ews.startconnectwithdevice.StartConnectWithDeviceFragment;
import com.philips.platform.ews.troubleshooting.connecttowrongphone.ConnectToWrongPhoneTroubleshootingFragment;
import com.philips.platform.ews.troubleshooting.resetconnection.ResetConnectionTroubleshootingFragment;
import com.philips.platform.ews.troubleshooting.resetdevice.ResetDeviceTroubleshootingFragment;
import com.philips.platform.ews.troubleshooting.setupaccesspointmode.SetupAccessPointModeTroubleshootingFragment;
import com.philips.platform.ews.troubleshooting.wificonnectionfailure.WifiConnectionUnsuccessfulFragment;
import com.philips.platform.ews.troubleshooting.wificonnectionfailure.WrongWifiNetworkFragment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NavigatorTest {

    @InjectMocks
    private Navigator subject;

    @Mock
    private FragmentNavigator mockFragmentNavigator;

    private ArgumentCaptor<Fragment> captor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        captor = ArgumentCaptor.forClass(Fragment.class);
    }

    @Test
    public void itShouldGetFragmentNavigatorWhichIsGiven() throws Exception{
        assertSame(subject.getFragmentNavigator(),mockFragmentNavigator);
    }

    @Test
    public void itShouldPushGettingStartingFragmentWhenNavigating() throws Exception {
        subject.navigateToGettingStartedScreen();

        verifyFragmentPushed(StartConnectWithDeviceFragment.class);
    }

    @Test
    public void itShouldPushDevicePoweredOnConfirmationScreenWhenNavigating() throws Exception {
        subject.navigateToDevicePoweredOnConfirmationScreen();

        verifyFragmentPushed(FirstSetupStepsFragment.class);
    }

    @Test
    public void itShouldPushCompletingDeviceSetupScreenWhenNavigating() throws Exception {
        subject.navigateToCompletingDeviceSetupScreen();

        verifyFragmentPushed(SecondSetupStepsFragment.class);
    }

    @Test
    public void itShouldPushConnectToDeviceWithPasswordScreenWhenNavigating() throws Exception {
        subject.navigateToConnectToDeviceWithPasswordScreen("deviceFriendlyName");

        verifyFragmentPushed(ConnectWithPasswordFragment.class);
    }

    @Test
    public void itShouldPushPairingSuccessScreenWhenNavigating() throws Exception {
        subject.navigateToPairingSuccessScreen();

        verifyFragmentPushed(ConnectionSuccessfulFragment.class);
    }

    @Test
    public void itShouldPushResetDeviceTroubleShootingScreenWhenNavigating() throws Exception {
        subject.navigateToResetDeviceTroubleShootingScreen();

        verifyFragmentPushed(ResetDeviceTroubleshootingFragment.class);
    }

    @Test
    public void itShouldPushConnectToWrongPhoneTroubleShootingScreenWhenNavigating() throws Exception {
        subject.navigateToConnectToWrongPhoneTroubleShootingScreen();

        verifyFragmentPushed(ConnectToWrongPhoneTroubleshootingFragment.class);
    }

    @Test
    public void itShouldPushSetupAccessPointModeScreenWhenNavigating() throws Exception {
        subject.navigateSetupAccessPointModeScreen();

        verifyFragmentPushed(SetupAccessPointModeTroubleshootingFragment.class);
    }

    @Test
    public void itShouldPushResetConnectionScreenWhenNotPresentInStack() throws Exception {
        subject.navigateToResetConnectionTroubleShootingScreen();

        verifyFragmentPushed(ResetConnectionTroubleshootingFragment.class);
    }

    @Test
    public void itShouldPushPowerOnScreenWhenNotPresentInStack() throws Exception {
        subject.navigateToDevicePoweredOnConfirmationScreen();

        verifyFragmentPushed(FirstSetupStepsFragment.class);
    }

    @Test
    public void itShouldNavigateToWifiConnectionUnsuccessfulScreen() throws Exception {
        subject.navigateToWIFIConnectionUnsuccessfulTroubleShootingScreen("deviceName", "homeWifiSssid");

        verifyFragmentPushed(WifiConnectionUnsuccessfulFragment.class);
    }

    @Test
    public void itShouldNavigateToConnectingPhoneToHotspotWifiFragment() throws Exception {
        subject.navigateToConnectingPhoneToHotspotWifiScreen();

        verifyFragmentPushed(ConnectingWithDeviceFragment.class);
    }

    @Test
    public void itShouldNavigateToConnectingDeviceWithWifiScreenWithArgs() throws Exception {
        subject.navigateToConnectingDeviceWithWifiScreen("homeWifiSssid",
                "password", "deviceName", "deviceFriendlyName");

        verifyFragmentPushed(ConnectingDeviceWithWifiFragment.class);
    }

    @Test
    public void itShouldNavigateToConnectingDeviceWithWifiScreenWithBundle() throws Exception {
        Bundle data = mock(Bundle.class);
        when(data.containsKey(anyString())).thenReturn(true);
        when(data.getString(anyString())).thenReturn("dummyValue");

        subject.navigateToConnectingDeviceWithWifiScreen(data, true);

        verifyFragmentPushed(ConnectingDeviceWithWifiFragment.class);
    }

    @Test
    public void itShouldNavigateToEWSWiFiPairedScreen() throws Exception {
        subject.navigateToEWSWiFiPairedScreen();

        verifyFragmentPushed(ConnectionSuccessfulFragment.class);
    }

    @Test
    public void itShouldNavigateToWrongWifiNetworkScreen() throws Exception {
        subject.navigateToWrongWifiNetworkScreen(new Bundle());

        verifyFragmentPushed(WrongWifiNetworkFragment.class);
    }

    @Test
    public void itShouldVerifyFragmentNavigatorPopIsCalledWhenNavigateBack() throws Exception {
        subject.navigateBack();

        verify(mockFragmentNavigator).pop();
    }

    private void verifyFragmentPushed(@NonNull Class fragmentClass) {
        verify(mockFragmentNavigator).push(captor.capture(), anyInt());
        assertEquals(fragmentClass, captor.getValue().getClass());
    }
}