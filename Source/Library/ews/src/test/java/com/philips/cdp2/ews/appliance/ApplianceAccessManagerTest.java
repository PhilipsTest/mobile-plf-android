/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.appliance;

import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.port.common.DevicePort;
import com.philips.cdp.dicommclient.port.common.DevicePortProperties;
import com.philips.cdp.dicommclient.port.common.WifiPort;
import com.philips.cdp.dicommclient.port.common.WifiPortProperties;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp2.ews.annotations.ApplianceRequestType;
import com.philips.cdp2.ews.annotations.NetworkType;
import com.philips.cdp2.ews.communication.events.ApplianceConnectErrorEvent;
import com.philips.cdp2.ews.communication.events.DeviceConnectionErrorEvent;
import com.philips.cdp2.ews.communication.events.NetworkConnectEvent;
import com.philips.cdp2.ews.communication.events.ShowPasswordEntryScreenEvent;
import com.philips.cdp2.ews.logger.EWSLogger;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.philips.cdp2.ews.annotations.ApplianceRequestType.GET_WIFI_PROPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EWSLogger.class)
public class ApplianceAccessManagerTest {

    private static final String HOME_WIFI_PASSWORD = "BrightEyes123";
    private static final String HOME_WIFI_SSID = "BrightEyes2.4";

    @Mock
    private EventBus eventBusMock;

    @Mock
    private DICommPortListener<DevicePort> devicePortListenerMock;

    @Mock
    private DICommPortListener<WifiPort> wifiPortListenerMock;

    @Mock
    private DevicePort devicePortMock;

    @Mock
    private WifiPort wifiPortMock;

    @Mock
    private EWSGenericAppliance applianceMock;

    private ApplianceSessionDetailsInfo sessionInfoDetails;

    private ApplianceAccessManager accessManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        PowerMockito.mockStatic(EWSLogger.class);

        stubAppliancePorts();
        sessionInfoDetails = new ApplianceSessionDetailsInfo();
        accessManager = new ApplianceAccessManager(eventBusMock, applianceMock, sessionInfoDetails);
    }

    private void stubAppliancePorts() {
        when(applianceMock.getDevicePort()).thenReturn(devicePortMock);
        when(applianceMock.getWifiPort()).thenReturn(wifiPortMock);
    }

    //// TODO: 26/05/17  this is workaround fix to pass tests till we get an update from dicomm.
    @Test
    public void shouldFetchApplianceDevicePortPropertiesWhenAsked() throws Exception {
        accessManager.fetchDevicePortProperties(null);

        verify(wifiPortMock).reloadProperties();
        verifyRequestType(ApplianceRequestType.GET_WIFI_PROPS);
    }

    //// TODO: 26/05/17  this is workaround fix to pass tests till we get an update from dicomm.
    @Test
    public void shouldNotFetchApplianceDevicePortPropertiesIfAnotherRequestIsInProgress() throws Exception {
        accessManager.fetchDevicePortProperties(null);
        accessManager.fetchDevicePortProperties(null);
        accessManager.fetchDevicePortProperties(null);

        verify(wifiPortMock, times(1)).reloadProperties();
    }

    @Test
    public void shouldSaveApplianceDevicePortSessionDetailsWhenGetDevicePortPropertiesAreFetched() throws Exception {
        DevicePortProperties devicePortPropertiesMock = mock(DevicePortProperties.class);
        when(devicePortMock.getPortProperties()).thenReturn(devicePortPropertiesMock);

        accessManager.getDevicePortListener().onPortUpdate(devicePortMock);

        assertSame(devicePortPropertiesMock, sessionInfoDetails.getDevicePortProperties());
        verify(wifiPortMock).reloadProperties();
        verifyRequestType(ApplianceRequestType.GET_WIFI_PROPS);
    }

    @Test
    public void shouldSaveApplianceWiFIPortSessionDetailsWhenGetWiFIPortPropertiesAreFetched() throws Exception {
        WifiPortProperties wifiPortProperties = fetchWiFiProperties(GET_WIFI_PROPS);

        assertSame(wifiPortProperties, sessionInfoDetails.getWifiPortProperties());
        verifyRequestType(ApplianceRequestType.UNKNOWN);
    }

    @Test
    public void shouldSendEventToShowNextScreenWhenWiFiPortPropertiesAreReadSuccessfully() throws Exception {
        fetchWiFiProperties(GET_WIFI_PROPS);

        verify(eventBusMock).post(isA(ShowPasswordEntryScreenEvent.class));
        assertEquals(ApplianceRequestType.UNKNOWN, accessManager.getRequestType());
    }

    @Test
    public void shouldSendPairingSuccessEventWhenApplianceIsConnectedToHomeWifi() throws Exception {
        connectApplianceToHomeWiFi();

        accessManager.getWifiPortListener().onPortUpdate(wifiPortMock);

        verify(wifiPortMock).setWifiNetworkDetails(HOME_WIFI_SSID, HOME_WIFI_PASSWORD);
    }

    @Test
    public void shouldSendEventToConnectYourPhoneWithHomeWiFiOnceApplianceIsConnectedToHomeWifi() throws Exception {
        final ArgumentCaptor<NetworkConnectEvent> requestCaptor = ArgumentCaptor.forClass(NetworkConnectEvent.class);
        connectApplianceToHomeWiFi();

        accessManager.setApplianceWifiRequestType(ApplianceRequestType.PUT_WIFI_PROPS);
        accessManager.getWifiPortListener().onPortUpdate(wifiPortMock);

        verify(eventBusMock).post(requestCaptor.capture());
        NetworkConnectEvent request = requestCaptor.getValue();

        assertEquals(HOME_WIFI_SSID, request.getNetworkSSID());
        assertEquals(NetworkType.HOME_WIFI, request.getNetworkType());
    }

    private void verifyRequestType(final int requestType) {
        assertEquals(requestType, accessManager.getRequestType());
    }

    @Test
    public void shouldSendDeviceConnectionErrorEventWhenGetWifiPropsOnErrorReceived() {
        accessManager.setApplianceWifiRequestType(ApplianceRequestType.GET_WIFI_PROPS);

        accessManager.getWifiPortListener().onPortError(wifiPortMock, Error.UNKNOWN, "");

        verify(eventBusMock).post(isA(DeviceConnectionErrorEvent.class));
    }

    @Test
    public void shouldSendDeviceConnectionErrorEventWhenGetDevicePropsOnErrorReceived() {
        accessManager.setApplianceWifiRequestType(ApplianceRequestType.GET_DEVICE_PROPS);

        accessManager.getDevicePortListener().onPortError(devicePortMock, Error.UNKNOWN, "");

        verify(eventBusMock).post(isA(DeviceConnectionErrorEvent.class));
    }

    @Test
    public void shouldSendApplianceConnectErrorEventWhenPutDevicePropsOnErrorReceived() {
        accessManager.setApplianceWifiRequestType(ApplianceRequestType.PUT_WIFI_PROPS);

        accessManager.getWifiPortListener().onPortError(wifiPortMock, Error.UNKNOWN, "");

        verify(eventBusMock).post(isA(ApplianceConnectErrorEvent.class));
    }

    @Test
    public void shouldDoNothingWhenWifiPortPropertiesAreNull() {

        accessManager.setApplianceWifiRequestType(ApplianceRequestType.PUT_WIFI_PROPS);

        accessManager.getWifiPortListener().onPortUpdate(wifiPortMock);

        verifyZeroInteractions(eventBusMock);
    }

    @Test
    public void shouldDoNothingWhenDevicePortPropertiesAreNull() {
        when(devicePortMock.getPortProperties()).thenReturn(null);

        accessManager.setApplianceWifiRequestType(ApplianceRequestType.GET_DEVICE_PROPS);

        accessManager.getDevicePortListener().onPortUpdate(devicePortMock);

        verifyZeroInteractions(eventBusMock);
    }

    @Test
    public void shouldDoNothingWhenConnectingToApplianceIsCalledButStateIsNotUnknown() throws Exception {
        accessManager.setApplianceWifiRequestType(ApplianceRequestType.GET_WIFI_PROPS);

        connectApplianceToHomeWiFi();

        verifyZeroInteractions(eventBusMock, wifiPortMock, applianceMock);
    }

    @Test
    public void shouldDoNothingWhenWifiPortErrorReceivedButTypeIsUnknown() throws Exception {
        accessManager.setApplianceWifiRequestType(ApplianceRequestType.UNKNOWN);

        accessManager.getWifiPortListener().onPortError(wifiPortMock, Error.UNKNOWN, "hypothetical");

        verifyZeroInteractions(eventBusMock);

        verifyRequestType(ApplianceRequestType.UNKNOWN);
    }

    @Test
    public void shouldDoNothingWhenDevicePortErrorReceivedButTypeIsUnknown() throws Exception {
        accessManager.setApplianceWifiRequestType(ApplianceRequestType.UNKNOWN);

        accessManager.getDevicePortListener().onPortError(devicePortMock, Error.UNKNOWN, "hypothetical");

        verifyZeroInteractions(eventBusMock);

        verifyRequestType(ApplianceRequestType.UNKNOWN);
    }

    private WifiPortProperties fetchWiFiProperties(final @ApplianceRequestType int type) {
        WifiPortProperties wifiPortProperties = mock(WifiPortProperties.class);
        when(wifiPortMock.getPortProperties()).thenReturn(wifiPortProperties);
        accessManager.setApplianceWifiRequestType(type);

        accessManager.getWifiPortListener().onPortUpdate(wifiPortMock);
        return wifiPortProperties;
    }

    private void connectApplianceToHomeWiFi() {
        WifiPortProperties wifiPortProperties = mock(WifiPortProperties.class);
        when(wifiPortMock.getPortProperties()).thenReturn(wifiPortProperties);
        accessManager.connectApplianceToHomeWiFiEvent(HOME_WIFI_SSID, HOME_WIFI_PASSWORD, null);
    }
}