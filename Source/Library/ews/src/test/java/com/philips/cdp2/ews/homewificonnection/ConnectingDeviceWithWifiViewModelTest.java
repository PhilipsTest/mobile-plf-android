package com.philips.cdp2.ews.homewificonnection;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.ews.appliance.ApplianceAccessManager;
import com.philips.cdp2.ews.communication.DiscoveryHelper;
import com.philips.cdp2.ews.microapp.EWSDependencyProvider;
import com.philips.cdp2.ews.microapp.EWSInterface;
import com.philips.cdp2.ews.navigation.Navigator;
import com.philips.cdp2.ews.tagging.EWSTagger;
import com.philips.cdp2.ews.wifi.WiFiConnectivityManager;
import com.philips.cdp2.ews.wifi.WiFiUtil;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EWSTagger.class)
public class ConnectingDeviceWithWifiViewModelTest {

    private static final String HOME_SSID = "homeSsid";
    private static final String HOME_SSID_PASSWORD = "homeSsidPw";
    private static final String DEVICE_NAME = "deviceName";

    private ConnectingDeviceWithWifiViewModel subject;

    @Mock private ApplianceAccessManager mockApplianceAccessManager;
    @Mock private Navigator mockNavigator;
    @Mock private WiFiConnectivityManager mockWiFiConnectivityManager ;
    @Mock private WiFiUtil mockWiFiUtil;
    @Mock private Handler mockHandler;
    @Mock private DiscoveryHelper mockDiscoveryHelper;
    @Mock private ConnectingDeviceWithWifiViewModel.ConnectingDeviceToWifiCallback mockFragmentCallback;

    @Mock private Intent mockIntent;
    @Mock private NetworkInfo mockNetworkInfo;
    @Mock private Appliance mockAppliance;

    @Captor private ArgumentCaptor<ApplianceAccessManager.SetPropertiesCallback> putPropsCallbackCaptor;
    @Captor private ArgumentCaptor<BroadcastReceiver> receiverArgumentCaptor;
    @Captor private ArgumentCaptor<DiscoveryHelper.DiscoveryCallback> discoveryCallbackArgumentCaptor;
    @Captor private ArgumentCaptor<Runnable> timeoutRunnableCaptor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(EWSTagger.class);

        AppInfraInterface mockAppInfraInterface = mock(AppInfraInterface.class);
        AppTaggingInterface mockTaggingInterface = mock(AppTaggingInterface.class);
        when(mockAppInfraInterface.getTagging()).thenReturn(mockTaggingInterface);
        Map<String, String> mockMap = new HashMap<>();
        mockMap.put(EWSInterface.PRODUCT_NAME, DEVICE_NAME);
        EWSDependencyProvider.getInstance().initDependencies(mockAppInfraInterface, mockMap);

        subject = new ConnectingDeviceWithWifiViewModel(mockApplianceAccessManager, mockNavigator,
                mockWiFiConnectivityManager, mockWiFiUtil, mockHandler, mockDiscoveryHelper);
        subject.setFragmentCallback(mockFragmentCallback);
    }

    @Test
    public void itShouldSendNetworkInfoToDeviceWhenStartingConnection() throws Exception {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);

        verify(mockApplianceAccessManager).connectApplianceToHomeWiFiEvent(eq(HOME_SSID), eq(HOME_SSID_PASSWORD),
                any(ApplianceAccessManager.SetPropertiesCallback.class));
    }

    @Test
    public void itShouldKickOffTimeoutWhenStartingToConnect() throws Exception {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);

        verify(mockHandler).postDelayed(any(Runnable.class), eq(60000L));
    }

    @Test
    public void itShouldRemoveTimeoutRunnableWhenPuttingPropsFails() throws Exception {
        simulatePutPropsFailed();

        verify(mockHandler).removeCallbacks(any(Runnable.class));
    }

    @Test
    public void itShouldNavigateToWrongWifiScreenWhenPuttingPropsFails() throws Exception {
        simulatePutPropsFailed();

        verify(mockNavigator).navigateToWrongWifiNetworkScreen(any(Bundle.class));
    }

    @Test
    public void itShouldConnectToHomeWifiHotspotWhenPutPropsIsSuccess() throws Exception {
        simulatePutPropsSucceeded();

        verify(mockWiFiConnectivityManager).connectToHomeWiFiNetwork(HOME_SSID);
    }

    @Test
    public void itShouldRegisterBroadcastReceiverWhenPutPropsIsSuccess() throws Exception {
        simulatePutPropsSucceeded();

        verify(mockFragmentCallback).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
    }

    @Test
    public void itShouldNotRegisterBroadcastReceiverWhenPutPropsIsSuccessWhenCallbackIsNull() throws Exception {
        subject.setFragmentCallback(null);

        simulatePutPropsSucceeded();

        verify(mockFragmentCallback, never()).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
    }

    @Test
    public void itShouldUnregisterBroadcastReceiverWhenConnectedBackToHomeWifiNetwork() throws Exception {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.HOME_WIFI);

        verify(mockFragmentCallback).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void itShouldNotDoAnyThingWhenBroadcastOfNetworkNotConnected() throws Exception {
        mockNetworkChange(NetworkInfo.State.DISCONNECTED, WiFiUtil.HOME_WIFI);
        simulatePutPropsSucceeded();
        verify(mockFragmentCallback).registerReceiver(receiverArgumentCaptor.capture(), any(IntentFilter.class));

        reset(mockHandler, mockDiscoveryHelper, mockNavigator);

        receiverArgumentCaptor.getValue().onReceive(null, mockIntent);

        verifyZeroInteractions(mockHandler);
        verifyZeroInteractions(mockDiscoveryHelper);
        verifyZeroInteractions(mockNavigator);
    }

    @Test
    public void itShouldNotUnregisterBroadcastReceiverWhenConnectedBackToHomeWifiNetworkAndCallbackIsNull() throws Exception {
        mockNetworkChange(NetworkInfo.State.CONNECTED, WiFiUtil.HOME_WIFI);
        simulatePutPropsSucceeded();
        verify(mockFragmentCallback).registerReceiver(receiverArgumentCaptor.capture(), any(IntentFilter.class));
        subject.setFragmentCallback(null);
        receiverArgumentCaptor.getValue().onReceive(null, mockIntent);

        verify(mockFragmentCallback, never()).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void itShouldUnregisterBroadcastReceiverWhenConnectedBackToWrongWifiNetwork() throws Exception {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.WRONG_WIFI);

        verify(mockFragmentCallback).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void itShouldNotUnregisterBroadcastReceiverWhenConnectedBackToUnknownWifiNetwork() throws Exception {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.UNKNOWN_WIFI);

        verify(mockFragmentCallback, never()).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void itShouldNavigateToWIFIConnectionUnsuccessfulTroubleShootingScreenWhenConnectedBackToWrongWifiNetwork() throws Exception {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.WRONG_WIFI);

        verify(mockNavigator).navigateToWIFIConnectionUnsuccessfulTroubleShootingScreen(DEVICE_NAME, HOME_SSID);
    }

    @Test
    public void itShouldRemoveTimeoutRunnableWhenConnectedBackToWrongWifiNetwork() throws Exception {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.WRONG_WIFI);

        verify(mockHandler).removeCallbacks(any(Runnable.class));
    }

    @Test
    public void itShouldStartDiscoveryForApplianceWhenConnectedBackToHomeNetwork() throws
            Exception {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.HOME_WIFI);

        verify(mockDiscoveryHelper).startDiscovery(any(DiscoveryHelper.DiscoveryCallback.class));
    }

    @Test
    public void itShouldRemoveTimeoutRunnableWhenApplianceFoundInHomeNetwork() throws Exception {
        simulateApplianceFound();

        verify(mockHandler).removeCallbacks(any(Runnable.class));
    }

    @Test
    public void itShouldStopDiscoveryWhenApplianceFoundInHomeNetwork() throws Exception {
        simulateApplianceFound();

        verify(mockDiscoveryHelper).stopDiscovery();
    }

    @Test
    public void itShouldNavigateToSuccessScreenWhenApplianceFoundInHomeNetwork() throws Exception {
        simulateApplianceFound();

        verify(mockNavigator).navigateToEWSWiFiPairedScreen();
    }

    @Test
    public void itShouldStopDiscoveryWhenCleared() throws Exception {
        subject.clear();

        verify(mockDiscoveryHelper).stopDiscovery();
    }

    @Test
    public void itShouldUnregisterBroadcastReceiverWhenCleared() throws Exception {
        subject.clear();

        verify(mockFragmentCallback).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void itShouldRemoveTimeoutRunnableWhenCleared() throws Exception {
        subject.clear();

        verify(mockHandler).removeCallbacks(any(Runnable.class));
    }

    @Test
    public void itShouldNavigateToErrorScreenWhenTimeout() throws Exception {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);
        verify(mockHandler).postDelayed(timeoutRunnableCaptor.capture(), anyLong());
        timeoutRunnableCaptor.getValue().run();

        mockNavigator.navigateToWrongWifiNetworkScreen(any(Bundle.class));
    }

    @Test
    public void itShouldStopDiscoveryWhenTimeout() throws Exception {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);
        verify(mockHandler).postDelayed(timeoutRunnableCaptor.capture(), anyLong());
        timeoutRunnableCaptor.getValue().run();

        verify(mockDiscoveryHelper).stopDiscovery();
    }

    @Test
    public void itShouldUnregisterReceiverWhenTimeout() throws Exception {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);
        verify(mockHandler).postDelayed(timeoutRunnableCaptor.capture(), anyLong());
        timeoutRunnableCaptor.getValue().run();

        verify(mockFragmentCallback).unregisterReceiver(any(BroadcastReceiver.class));
    }

    @Test
    public void itShouldShowCancelDialogWhenCancelButtonClicked() throws Exception {
        subject.onCancelButtonClicked();

        verify(mockFragmentCallback).showCancelDialog();
    }

    @Test
    public void itShouldNotShowCancelDialogWhenCancelButtonClickedAndCallbackIsNull() throws Exception {
        subject.setFragmentCallback(null);

        subject.onCancelButtonClicked();

        verify(mockFragmentCallback, never()).showCancelDialog();
    }

    private void simulateApplianceFound() {
        simulateConnectionBackToWifi(NetworkInfo.State.CONNECTED, WiFiUtil.HOME_WIFI);
        verify(mockDiscoveryHelper).startDiscovery(discoveryCallbackArgumentCaptor.capture());
        discoveryCallbackArgumentCaptor.getValue().onApplianceFound(mockAppliance);
    }

    private void simulateConnectionBackToWifi(NetworkInfo.State networkState, @WiFiUtil.WiFiState int wifiState) {
        mockNetworkChange(networkState, wifiState);
        simulatePutPropsSucceeded();
        verify(mockFragmentCallback).registerReceiver(receiverArgumentCaptor.capture(), any(IntentFilter.class));
        receiverArgumentCaptor.getValue().onReceive(null, mockIntent);
    }

    private void mockNetworkChange(NetworkInfo.State networkState, @WiFiUtil.WiFiState int wifiState) {
        when(mockIntent.getParcelableExtra(anyString())).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.getState()).thenReturn(networkState);
        when(mockWiFiUtil.getCurrentWifiState()).thenReturn(wifiState);
    }

    private void simulatePutPropsSucceeded() {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);
        verify(mockApplianceAccessManager).connectApplianceToHomeWiFiEvent(anyString(), anyString(),
                putPropsCallbackCaptor.capture());
        putPropsCallbackCaptor.getValue().onPropertiesSet();
    }

    private void simulatePutPropsFailed() {
        subject.startConnecting(HOME_SSID, HOME_SSID_PASSWORD, DEVICE_NAME);
        verify(mockApplianceAccessManager).connectApplianceToHomeWiFiEvent(anyString(), anyString(),
                putPropsCallbackCaptor.capture());
        putPropsCallbackCaptor.getValue().onFailedToSetProperties();
    }
}