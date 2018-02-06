/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.ssdp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.util.ContextProvider;
import com.philips.cdp2.commlib.ssdp.SSDPControlPoint.DeviceListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.URL;

import static com.philips.cdp2.commlib.ssdp.SSDPMessage.NOTIFICATION_SUBTYPE;
import static com.philips.cdp2.commlib.ssdp.SSDPMessage.NOTIFICATION_SUBTYPE_ALIVE;
import static com.philips.cdp2.commlib.ssdp.SSDPMessage.NOTIFICATION_SUBTYPE_BYEBYE;
import static com.philips.cdp2.commlib.ssdp.SSDPMessage.NOTIFICATION_SUBTYPE_UPDATE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SSDPDevice.class})
public class SSDPControlPointTest {

    private static final String MOCK_LOCATION = "http://1.2.3.4/mock/location";

    private SSDPControlPoint ssdpControlPoint;

    @Mock
    private DeviceListener deviceListener;

    @Mock
    private SSDPMessage ssdpMessageMock;

    @Mock
    private SSDPDevice ssdpDeviceMock;

    @Mock
    private Context contextMock;

    @Mock
    private WifiManager wifiManagerMock;

    @Mock
    private WifiManager.MulticastLock lockMock;

    @Mock
    private MulticastSocket broadcastSocketMock;

    @Mock
    private MulticastSocket listenSocketMock;

    @Before
    public void setUp() {
        initMocks(this);

        DICommLog.disableLogging();

        ContextProvider.setTestingContext(contextMock);
        when(contextMock.getApplicationContext()).thenReturn(contextMock);
        when(contextMock.getSystemService(Context.WIFI_SERVICE)).thenReturn(wifiManagerMock);
        when(wifiManagerMock.createMulticastLock(anyString())).thenReturn(lockMock);
        when(ssdpMessageMock.get(SSDPMessage.LOCATION)).thenReturn(MOCK_LOCATION);

        mockStatic(SSDPDevice.class);
        when(SSDPDevice.createFromUrl(any(URL.class))).thenReturn(ssdpDeviceMock);

        ssdpControlPoint = new SSDPControlPoint() {
            @NonNull
            @Override
            MulticastSocket createBroadcastSocket() throws IOException {
                return broadcastSocketMock;
            }

            @NonNull
            @Override
            MulticastSocket createListenSocket() throws IOException {
                return listenSocketMock;
            }
        };
    }

    @Test
    public void whenAnSsdpAliveMessageIsReceived_thenAnAvailableSsdpDeviceShouldBeNotified() {
        ssdpControlPoint.addDeviceListener(deviceListener);

        when(ssdpMessageMock.get(NOTIFICATION_SUBTYPE)).thenReturn(NOTIFICATION_SUBTYPE_ALIVE);
        ssdpControlPoint.handleMessage(ssdpMessageMock);

        verify(deviceListener).onDeviceAvailable(any(SSDPDevice.class));
    }

    @Test
    public void whenAnSsdpUpdateMessageIsReceived_thenAnAvailableSsdpDeviceShouldBeNotified() {
        ssdpControlPoint.addDeviceListener(deviceListener);

        when(ssdpMessageMock.get(NOTIFICATION_SUBTYPE)).thenReturn(NOTIFICATION_SUBTYPE_UPDATE);
        ssdpControlPoint.handleMessage(ssdpMessageMock);

        verify(deviceListener).onDeviceAvailable(any(SSDPDevice.class));
    }

    @Test
    public void whenAnSsdpByeByeMessageIsReceived_thenAnUnavailableSsdpDeviceShouldBeNotified() {
        ssdpControlPoint.addDeviceListener(deviceListener);

        when(ssdpMessageMock.get(NOTIFICATION_SUBTYPE)).thenReturn(NOTIFICATION_SUBTYPE_BYEBYE);
        ssdpControlPoint.handleMessage(ssdpMessageMock);

        verify(deviceListener).onDeviceUnavailable(any(SSDPDevice.class));
    }

    @Test
    public void whenAnSsdpAliveMessageIsReceived_andAnSsdpByeByeMessageIsReceived_thenAnUnavailableSsdpDeviceShouldBeNotified() {
        ssdpControlPoint.addDeviceListener(deviceListener);

        when(ssdpMessageMock.get(NOTIFICATION_SUBTYPE)).thenReturn(NOTIFICATION_SUBTYPE_ALIVE);
        ssdpControlPoint.handleMessage(ssdpMessageMock);

        when(ssdpMessageMock.get(NOTIFICATION_SUBTYPE)).thenReturn(NOTIFICATION_SUBTYPE_BYEBYE);
        ssdpControlPoint.handleMessage(ssdpMessageMock);

        verify(deviceListener).onDeviceUnavailable(any(SSDPDevice.class));
    }

    @Test
    public void whenControlPointIsCreated_thenSocketsAreNotOpened() {

        ssdpControlPoint.start();
    }

    @Test
    public void givenControlPointIsCreated_whenStartIsInvoked_thenSocketsAreOpened() {

    }

    @Test
    public void givenControlPointIsCreated_whenStartIsInvoked_thenMulticastLockIsAcquired() {

    }

    @Test
    public void givenControlPointIsStarted_whenStopIsInvoked_thenSocketsAreClosed() {

    }

    @Test
    public void givenControlPointIsStarted_whenStopIsInvoked_thenMulticastLockIsReleased() {

    }
}
