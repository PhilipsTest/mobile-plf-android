/*
 * Copyright (c) Koninklijke Philips N.V., 2015, 2016, 2017.
 * All rights reserved.
 */

package com.philips.pins.shinelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.philips.pins.shinelib.bluetoothwrapper.BTDevice;
import com.philips.pins.shinelib.bluetoothwrapper.BTGatt;
import com.philips.pins.shinelib.capabilities.SHNCapabilityNotifications;
import com.philips.pins.shinelib.framework.Timer;
import com.philips.pins.shinelib.helper.MockedHandler;
import com.philips.pins.shinelib.helper.Utility;
import com.philips.pins.shinelib.wrappers.SHNCapabilityNotificationsWrapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class SHNDeviceImplTest {
    public static final String TEST_DEVICE_TYPE = "TEST_DEVICE_TYPE";
    public static final byte[] MOCK_BYTES = new byte[]{0x42};
    public static final UUID MOCK_UUID = UUID.randomUUID();
    private SHNDeviceImpl shnDevice;

    @Mock
    private BTDevice mockedBTDevice;

    @Mock
    private SHNCentral mockedSHNCentral;

    @Mock
    private Context mockedContext;

    @Mock
    private Timer timerMock;

    @Mock
    private BTGatt mockedBTGatt;

    @Mock
    private SHNService mockedSHNService;

    @Mock
    private SHNCharacteristic mockedSHNCharacteristic;

    @Mock
    private BluetoothGattService mockedBluetoothGattService;

    @Mock
    private BluetoothGattCharacteristic mockedBluetoothGattCharacteristic;

    @Mock
    private BluetoothGattDescriptor mockedBluetoothGattDescriptor;

    @Mock
    private SHNDeviceImpl.SHNDeviceListener mockedSHNDeviceListener;

    @Mock
    private SHNDevice.DiscoveryListener mockedDiscoveryListener;

    @Mock
    private BluetoothDevice mockedBluetoothDevice;

    private MockedHandler mockedInternalHandler;
    private MockedHandler mockedUserHandler;
    private BTGatt.BTGattCallback btGattCallback;
    private SHNCentral.SHNBondStatusListener bondStatusListener;
    private List<BluetoothGattService> discoveredServices;
    private List<BluetoothGattCharacteristic> discoveredCharacteristics;
    private SHNService.State mockedServiceState;
    public static final String ADDRESS_STRING = "DE:AD:CO:DE:12:34";
    public static final String NAME_STRING = "TestDevice";
    private boolean useTimeoutConnect = true;

    @Before
    public void setUp() {
        initMocks(this);

        mockedInternalHandler = new MockedHandler();
        mockedUserHandler = new MockedHandler();

        Timer.setHandler(mockedInternalHandler.getMock());

        doReturn(mockedInternalHandler.getMock()).when(mockedSHNCentral).getInternalHandler();
        doReturn(mockedUserHandler.getMock()).when(mockedSHNCentral).getUserHandler();
        doReturn(true).when(mockedSHNCentral).isBluetoothAdapterEnabled();

        doAnswer(new Answer<BTGatt>() {
            @Override
            public BTGatt answer(InvocationOnMock invocation) throws Throwable {
                btGattCallback = (BTGatt.BTGattCallback) invocation.getArguments()[3];
                return mockedBTGatt;
            }
        }).when(mockedBTDevice).connectGatt(isA(Context.class), anyBoolean(), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));

        when(mockedBTDevice.createBond()).thenReturn(true);

        doReturn(ADDRESS_STRING).when(mockedBTDevice).getAddress();

        discoveredServices = new ArrayList<>();
        discoveredServices.add(mockedBluetoothGattService);
        doReturn(discoveredServices).when(mockedBTGatt).getServices();

        discoveredCharacteristics = new ArrayList<>();
        discoveredCharacteristics.add(mockedBluetoothGattCharacteristic);
        doReturn(discoveredCharacteristics).when(mockedBluetoothGattService).getCharacteristics();

        mockedServiceState = SHNService.State.Ready;
        doAnswer(new Answer<SHNService.State>() {
            @Override
            public SHNService.State answer(InvocationOnMock invocation) throws Throwable {
                return mockedServiceState;
            }
        }).when(mockedSHNService).getState();
        doReturn(mockedContext).when(mockedSHNCentral).getApplicationContext();

        doReturn(mockedBluetoothGattService).when(mockedBluetoothGattCharacteristic).getService();
        doReturn(mockedBluetoothGattCharacteristic).when(mockedBluetoothGattDescriptor).getCharacteristic();

        // Mock Characteristic for DiscoveryListener
        doReturn(MOCK_UUID).when(mockedBluetoothGattCharacteristic).getUuid();
        doReturn(MOCK_BYTES).when(mockedBluetoothGattCharacteristic).getValue();

        doReturn(NAME_STRING).when(mockedBTDevice).getName();
        doReturn(ADDRESS_STRING).when(mockedBTDevice).getAddress();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                bondStatusListener = (SHNCentral.SHNBondStatusListener) invocation.getArguments()[0];
                return null;
            }
        }).when(mockedSHNCentral).registerBondStatusListenerForAddress(isA(SHNCentral.SHNBondStatusListener.class), anyString());


        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, false);
        shnDevice.registerSHNDeviceListener(mockedSHNDeviceListener);
        shnDevice.registerDiscoveryListener(mockedDiscoveryListener);
        shnDevice.registerService(mockedSHNService);

        when(mockedBluetoothDevice.getAddress()).thenReturn(ADDRESS_STRING);
    }

    private void connectTillGATTConnected() {
        if (useTimeoutConnect) {
            shnDevice.connect();
        } else {
            shnDevice.connect(false, -1L);
        }
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);
    }

    private void connectTillGATTServicesDiscovered() {
        connectTillGATTConnected();
        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);
    }

    private void getDeviceInConnectedState() {
        connectTillGATTServicesDiscovered();
        mockedServiceState = SHNService.State.Ready;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);
    }

    // State Disconnected
    @Test
    public void whenASHNDeviceIsCreatedThenItsStateIsDisconnected() {
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
    }

    @Test
    public void whenInStateDisconnectedTheDisconnectMethodIsCalledThenTheStateIsDisconnected() {
        shnDevice.disconnect();

        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
    }

    @Test
    public void whenInStateDisconnectedTheDisconnectMethodIsCalledThenListenerIsNotified() {
        shnDevice.disconnect();

        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
    }

    @Test
    public void whenInStateDisconnectedTheConnectMethodIsCalledThenTheStateChangesToConnecting() {
        shnDevice.connect();
        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
    }

    @Test
    public void whenInStateDisconnectedWhenTheStateChangesToConnectingThenTheConnectGattIsCalled() {
        shnDevice.connect();
        verify(mockedBTDevice).connectGatt(mockedContext, false, mockedSHNCentral, btGattCallback);
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    // State GattConnecting
    @Test
    public void whenInStateConnectingGATTCallbackIndicatedDisconnectedThenTheOnFailedToConnectGetsCalled() {
        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(SHNDevice.State.Disconnected, shnDevice.getState());
    }

    @Test
    public void whenInStateConnectingGATTCallbackIndicatedDisconnectedThenConnectTimerIsStopped() {
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateConnectingGATTCallbackIndicatedDisconnectedThenBtGattCloseIsCalled() {
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);

        verify(mockedBTGatt).close();
    }

    @Test
    public void whenInStateConnectingThenThereIsNoTimerRunning() {
        shnDevice.connect();
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateConnectingTheGattCallbackIndicatesConnectedThenDiscoverServicesIsCalled() {
        connectTillGATTConnected();
        reset(mockedSHNDeviceListener);
        verify(mockedBTGatt).discoverServices();

        assertEquals(SHNDevice.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateConnectingTheGattCallbackIndicatesConnectedWithStatusFailureThenDisconnectIsCalledAndTimerStarted() {
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_FAILURE, BluetoothGatt.STATE_CONNECTED);
        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateConnectingTheGattCallbackIndicatesConnectedWithStatusFailureThenCloseIsCalled() {
        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_FAILURE, BluetoothGatt.STATE_CONNECTED);

        assertEquals(SHNDevice.State.Disconnecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        verify(mockedBTGatt).disconnect();
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        mockedInternalHandler.executeFirstScheduledExecution();
        verify(mockedSHNDeviceListener).onFailedToConnect(eq(shnDevice), any(SHNResult.class));
    }

    @Test
    public void whenInStateConnectingTheGattCallbackIndicatesConnectedWithStatusFailureThenDisconnectFromBLELayerIsNotCalled() {
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_FAILURE, BluetoothGatt.STATE_CONNECTED);
        verify(mockedSHNService, never()).disconnectFromBLELayer();
    }

    @Test
    public void whenInStateConnectingTheGattIndicatesDisconnectedThenTheOnFailedToConnectIsCalled() {
        shnDevice.connect();
        reset(mockedSHNDeviceListener);

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        assertEquals(SHNDevice.State.Disconnected, shnDevice.getState());
        verify(mockedSHNDeviceListener, times(2)).onStateUpdated(shnDevice);
    }

    @Test
    public void whenInStateConnectingDisconnectIsCalledThenStateIsDisconnecting() {
        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        shnDevice.disconnect();

        assertEquals(SHNDevice.State.Disconnecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void disconnectIsDeferredTillConnectCallbackIsReceived() {
        whenInStateConnectingDisconnectIsCalledThenStateIsDisconnecting();
        reset(mockedBTGatt);

        btGattCallback.onConnectionStateChange(mockedBTGatt, 0, BluetoothGatt.STATE_CONNECTED);

        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        PowerMockito.verifyNoMoreInteractions(mockedSHNDeviceListener);
    }

    @Test
    public void whenDisconnectedBleCallbackIsReceivedThenCloseIsCalled() throws Exception {
        disconnectIsDeferredTillConnectCallbackIsReceived();
        reset(mockedBTGatt, mockedSHNDeviceListener);

        btGattCallback.onConnectionStateChange(mockedBTGatt, 0, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt, never()).disconnect();
        verify(mockedBTGatt).close();
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        PowerMockito.verifyNoMoreInteractions(mockedSHNDeviceListener);
    }

    @Test
    public void whenInStateConnectingAndDisconnectIsCalledThenTheStateBecomesDisconnecting() {
        shnDevice.connect();
        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        shnDevice.disconnect();
        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
    }

    @Test
    public void whenInStateConnectingDisconnectIsCalledAndThenDisconnectOnBTGattIsCalled() {
        whenInStateConnectingAndDisconnectIsCalledThenTheStateBecomesDisconnecting();

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_DISCONNECTED);
        verify(mockedSHNDeviceListener, never()).onFailedToConnect(any(SHNDevice.class), any(SHNResult.class));
    }

    @Test
    public void whenInStateConnectingConnectIsCalledAndThenCallIsIgnored() {
        shnDevice.connect();

        reset(mockedBTDevice, mockedSHNDeviceListener);
        shnDevice.connect();

        verifyNoMoreInteractions(mockedBTDevice, mockedSHNDeviceListener);
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    //State Bonding

    @Test
    public void whenInStateWaitingUntilBondedThenRegisterBondStatusListenerForAddressIsCalled() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.APP);
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);

        verify(mockedSHNCentral).registerBondStatusListenerForAddress(any(SHNCentral.SHNBondStatusListener.class), anyString());
    }

    @Test
    public void whenInStateWaitingUntilBondedAndBondingCompletedThenBondStatusListenerIsUnregistered() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.APP);
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);

        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_BONDED, BluetoothDevice.BOND_BONDING);
        mockedInternalHandler.executeFirstScheduledExecution();

        verify(mockedSHNCentral).unregisterBondStatusListenerForAddress(any(SHNCentral.SHNBondStatusListener.class), anyString());
    }

    @Test
    public void whenInStateWaitingUntilBondedAndBondingFailedThenBondStatusListenerIsUnregistered() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.APP);
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED);

        getDeviceInConnectedState();
        shnDevice.disconnect();

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNCentral).unregisterBondStatusListenerForAddress(any(SHNCentral.SHNBondStatusListener.class), anyString());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, true);
        shnDevice.registerSHNDeviceListener(mockedSHNDeviceListener);

        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);

        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingNoneSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.NONE);
        shnDevice.registerSHNDeviceListener(mockedSHNDeviceListener);

        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);

        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedBTGatt).discoverServices();
        verify(mockedBTDevice, never()).createBond();
    }

    @Test
    public void whenBondingPeripheralSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.PERIPHERAL);
        shnDevice.registerSHNDeviceListener(mockedSHNDeviceListener);

        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);

        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedBTGatt, never()).discoverServices();
        verify(mockedBTDevice, never()).createBond();
    }

    @Test
    public void whenBondingAppSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.APP);
        shnDevice.registerSHNDeviceListener(mockedSHNDeviceListener);

        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);

        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedBTGatt, never()).discoverServices();
        verify(mockedBTDevice).createBond();
    }

    @Test
    public void whenBondingAppSHNDeviceInStateConnectingCreataeBondReturnsFalseGATTCallbackIndicatedConnectedThenStateIsConnecting() {
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, SHNDeviceImpl.SHNBondInitiator.APP);
        shnDevice.registerSHNDeviceListener(mockedSHNDeviceListener);

        when(mockedBTDevice.createBond()).thenReturn(false);

        shnDevice.connect();
        reset(mockedSHNDeviceListener);
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);

        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedBTGatt).discoverServices();
        verify(mockedBTDevice).createBond();
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndStateIsBondingThenWaitingUntilBondingStartedTimerIsStopped() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);

        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_BONDING, BluetoothDevice.BOND_NONE);

        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndBondIsCreatedThenServicesAreDiscovered() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);

        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_BONDING, BluetoothDevice.BOND_NONE);
        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_BONDED, BluetoothDevice.BOND_BONDING);

        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        mockedInternalHandler.executeFirstScheduledExecution();

        verify(mockedBTGatt).discoverServices();
        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndBondIsNotCreatedThenDeviceIsDisonnected() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);

        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_BONDING, BluetoothDevice.BOND_NONE);
        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_NONE, BluetoothDevice.BOND_BONDING);

        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndBondTimerExpiresThenServicesAreDiscovered() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);

        mockedInternalHandler.executeFirstScheduledExecution();

        verify(mockedBTGatt).discoverServices();
        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndGATTIndicatesDisconnectedThenStateIsDisconnected() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt, never()).disconnect();
        verify(mockedBTGatt).close();
        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndBondIsNotCreatedAndDeviceIsDisdconnectenThenFailedErrorIsGiven() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);

        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_BONDING, BluetoothDevice.BOND_NONE);
        bondStatusListener.onBondStatusChanged(mockedBluetoothDevice, BluetoothDevice.BOND_NONE, BluetoothDevice.BOND_BONDING);

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt).disconnect();
        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorBondLost);
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndDisconnectIsCalledThenStateIsDisconnecting() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedSHNDeviceListener);

        shnDevice.disconnect();

        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenBondingSHNDeviceInStateConnectingAndConnectIsCalledThenCallIsIgnored() {
        whenBondingSHNDeviceInStateConnectingGATTCallbackIndicatedConnectedThenStateIsConnecting();
        reset(mockedBTDevice, mockedSHNDeviceListener);

        shnDevice.connect();

        verifyNoMoreInteractions(mockedBTDevice, mockedSHNDeviceListener);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    // State DiscoveringServices
    @Test
    public void whenInStateDiscoveringServicesTheGattCallbackIndicatesServicesDiscoveredThenTheSHNServiceIsConnectedToTheBleService() {
        connectTillGATTConnected();
        reset(mockedSHNDeviceListener);

        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);

        verify(mockedSHNService).connectToBLELayer(mockedBTGatt, mockedBluetoothGattService);
        verify(mockedSHNDeviceListener, never()).onStateUpdated(any(SHNDevice.class));
        verify(mockedSHNDeviceListener, never()).onFailedToConnect(any(SHNDevice.class), any(SHNResult.class));
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateDiscoveringServicesDisconnectIsCalledThenStateIsDisconnecting() {
        connectTillGATTConnected();
        reset(mockedSHNDeviceListener);

        shnDevice.disconnect();

        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenDiscoveringServicesConnectTimeoutOccursThenTheStateIsChangedToDisconnecting() {
        connectTillGATTConnected();
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        reset(mockedSHNDeviceListener);

        mockedInternalHandler.executeFirstScheduledExecution();

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
    }

    @Test
    public void whenInStateDiscoveringServicesGATIndicatedDisconnectedThenStateIsDisconnected() {
        connectTillGATTConnected();
        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt, never()).disconnect();
        verify(mockedBTGatt).close();
        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateDiscoveringConnectIsCalledAndThenCallIsIgnored() {
        connectTillGATTConnected();

        reset(mockedBTDevice, mockedSHNDeviceListener);
        shnDevice.connect();

        verifyNoMoreInteractions(mockedBTDevice, mockedSHNDeviceListener);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    // State InitializingServices
    @Test
    public void whenServicesAreDiscoveredServicesTheGattCallbackIndicatesServicesDiscoveredThenGetServicesIsCalled() {
        connectTillGATTServicesDiscovered();
        verify(mockedBTGatt, times(2)).getServices();
    }

    @Test
    public void whenServicesAreDiscoveredAndNoServiceAreFoundThenReconnectWithTheDevice() {
        connectTillGATTConnected();
        reset(mockedBTDevice);

        List emptyServices = new ArrayList<>();
        doReturn(emptyServices).when(mockedBTGatt).getServices();

        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);

        verify(mockedBTGatt).disconnect();

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED);

        verify(mockedBTDevice).connectGatt(isA(Context.class), isA(Boolean.class), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenServicesAreDiscoveredAndDirectlyBecomeReadyThenTheDeviceBecomesConnected() {
        connectTillGATTConnected();
        reset(mockedSHNDeviceListener);

        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);
        mockedServiceState = SHNService.State.Ready;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDevice.State.Connected, shnDevice.getState());
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        verify(mockedSHNDeviceListener, never()).onFailedToConnect(any(SHNDevice.class), any(SHNResult.class));
    }

    @Test
    public void whenServicesAreDiscoveredAndTheServiceIndicatesAvailableThenTheStateRemainsConnecting() {
        connectTillGATTServicesDiscovered();
        reset(mockedSHNDeviceListener);
        mockedServiceState = SHNService.State.Available;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDeviceImpl.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
    }

    @Test
    public void whenServicesAreDiscoveredAndGotoErrorStateThenTheDeviceBecomesDisconnecting() {
        connectTillGATTConnected();
        reset(mockedSHNDeviceListener);

        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);
        mockedServiceState = SHNService.State.Error;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDevice.State.Disconnecting, shnDevice.getState());
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        verify(mockedSHNDeviceListener, never()).onFailedToConnect(any(SHNDevice.class), any(SHNResult.class));
        verify(mockedBTGatt).disconnect();
    }

    @Test
    public void whenServicesAreDiscoveredAndBTGATTIndicatesDisconnectedThenStateIsDisconecting() {
        connectTillGATTServicesDiscovered();
        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt, never()).disconnect();
        verify(mockedBTGatt).close();
        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenServicesAreDiscoveredAndDisconnectIsCalledThenStateIsDisconnecting() {
        connectTillGATTServicesDiscovered();
        reset(mockedSHNDeviceListener);

        shnDevice.disconnect();

        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenServicesAreDiscoveredConnectIsCalledAndThenCallIsIgnored() {
        connectTillGATTServicesDiscovered();

        reset(mockedBTDevice, mockedSHNDeviceListener);
        shnDevice.connect();

        verifyNoMoreInteractions(mockedBTDevice, mockedSHNDeviceListener);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateInitializingServicesATimeoutOccursThenTheStateIsChangedToDisconnecting() {
        shnDevice.connect();
        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_CONNECTED);
        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);
        reset(mockedSHNDeviceListener);

        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        mockedInternalHandler.executeFirstScheduledExecution();

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
    }

    // State Ready
    @Test
    public void whenInStateConnectedThenThereIsNoTimerRunning() {
        getDeviceInConnectedState();
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateConnectedDisconnectIsCalledThenTheStateIsChangedToDisconnecting() {
        getDeviceInConnectedState();

        shnDevice.disconnect();

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
    }

    @Test
    public void whenInStateConnectedDisconnectIsCalledThenDisconnectOnBTGattIsCalled() {
        getDeviceInConnectedState();

        shnDevice.disconnect();

        verify(mockedBTGatt).disconnect();
        verify(mockedBTGatt, never()).close();
    }

    @Test
    public void whenInStateConnectedAndBTGATTIndicatesDisconnectedThenStateIsDisconnected() {
        getDeviceInConnectedState();
        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt, never()).disconnect();
        verify(mockedBTGatt).close();
        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenInStateConnectedConnectIsCalledAndThenListenerIsNotified() {
        getDeviceInConnectedState();
        reset(mockedBTDevice, mockedSHNDeviceListener);

        shnDevice.connect();

        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        verifyNoMoreInteractions(mockedBTDevice, mockedSHNDeviceListener);
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    // State Disconnecting
    @Test
    public void whenInStateDisconnectingTheCallbackIndicatesDisconnectedThenTheStateIsChangedToDisconnected() {
        getDeviceInConnectedState();
        shnDevice.disconnect();

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);

        assertEquals(SHNDeviceImpl.State.Disconnected, shnDevice.getState());
        verify(mockedSHNService).disconnectFromBLELayer();
        verify(mockedBTGatt).close();
    }

    @Test
    public void whenInStateDisconnectingTheServiceIndicatesUnavailableThenTheStateIsDisconnecting() {
        getDeviceInConnectedState();
        shnDevice.disconnect();

        mockedServiceState = SHNService.State.Unavailable;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
    }

    @Test
    public void whenInStateDisconnectingTheServiceIndicatesAvailableThenTheStateIsDisconnecting() {
        getDeviceInConnectedState();
        shnDevice.disconnect();
        reset(mockedSHNDeviceListener, mockedBTGatt);

        mockedServiceState = SHNService.State.Available;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verifyZeroInteractions(mockedSHNDeviceListener, mockedBTGatt);
    }

    @Test
    public void whenInStateDisconnectingTheServiceIndicatesReadyThenTheStateIsDisconnecting() {
        getDeviceInConnectedState();
        shnDevice.disconnect();
        reset(mockedSHNDeviceListener, mockedBTGatt);

        mockedServiceState = SHNService.State.Ready;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verifyZeroInteractions(mockedSHNDeviceListener, mockedBTGatt);
    }

    @Test
    public void whenInStateConnectedAServicesGoesToErrorStateThenTheDeviceBecomesDisconnecting() {
        connectTillGATTConnected();
        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_SUCCESS);
        mockedServiceState = SHNService.State.Ready;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        reset(mockedSHNDeviceListener);
        mockedServiceState = SHNService.State.Error;
        shnDevice.onServiceStateChanged(mockedSHNService, mockedServiceState);

        assertEquals(SHNDevice.State.Disconnecting, shnDevice.getState());
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        verify(mockedSHNDeviceListener, never()).onFailedToConnect(any(SHNDevice.class), any(SHNResult.class));
        verify(mockedBTGatt).disconnect();
    }

    @Test
    public void whenInStateDisconnectingTheDisconnectMethodIsCalledThenThenStateIsDisconnecting() {
        getDeviceInConnectedState();
        shnDevice.disconnect();
        reset(mockedSHNDeviceListener, mockedBTGatt);
        shnDevice.disconnect();

        assertEquals(SHNDeviceImpl.State.Disconnecting, shnDevice.getState());
        verifyZeroInteractions(mockedSHNDeviceListener, mockedBTGatt);
    }

    @Test
    public void whenInStateDisconnectingConnectMethodIsCalledThenListenerIsNotifiedWithFailedToConnect() {
        getDeviceInConnectedState();
        shnDevice.disconnect();
        reset(mockedBTDevice, mockedSHNDeviceListener);

        shnDevice.connect();

        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        verifyNoMoreInteractions(mockedBTDevice, mockedSHNDeviceListener);
        assertEquals(1, mockedInternalHandler.getScheduledExecutionCount());
    }

    // Receiving responses tot requests
    @Test
    public void whenInStateConnectedOnCharacteristicReadWithDataThenTheServiceIsCalled() {
        getDeviceInConnectedState();

        btGattCallback.onCharacteristicReadWithData(mockedBTGatt, mockedBluetoothGattCharacteristic,
                BluetoothGatt.GATT_SUCCESS, new byte[]{'d', 'a', 't', 'a'});
        verify(mockedSHNService).onCharacteristicReadWithData(isA(BTGatt.class), isA(BluetoothGattCharacteristic.class),
                anyInt(), isA(byte[].class));
    }

    @Test
    public void whenInStateConnectedOnCharacteristicWriteThenTheServiceIsCalled() {
        getDeviceInConnectedState();

        btGattCallback.onCharacteristicWrite(mockedBTGatt, mockedBluetoothGattCharacteristic,
                BluetoothGatt.GATT_SUCCESS);
        verify(mockedSHNService).onCharacteristicWrite(isA(BTGatt.class), isA(BluetoothGattCharacteristic.class),
                anyInt());
    }

    @Test
    public void whenInStateConnectedOnCharacteristicChangedWithDataThenTheServiceIsCalled() {
        getDeviceInConnectedState();

        btGattCallback.onCharacteristicChangedWithData(mockedBTGatt, mockedBluetoothGattCharacteristic,
                new byte[]{'d', 'a', 't', 'a'});
        verify(mockedSHNService).onCharacteristicChangedWithData(isA(BTGatt.class),
                isA(BluetoothGattCharacteristic.class), isA(byte[].class));
    }

    @Test
    public void whenInStateConnectedOnDescriptorReadWithDataThenTheServiceIsCalled() {
        getDeviceInConnectedState();

        btGattCallback.onDescriptorReadWithData(mockedBTGatt, mockedBluetoothGattDescriptor, BluetoothGatt.GATT_SUCCESS,
                new byte[]{'d', 'a', 't', 'a'});
        verify(mockedSHNService).onDescriptorReadWithData(isA(BTGatt.class), isA(BluetoothGattDescriptor.class),
                anyInt(), isA(byte[].class));
    }

    @Test
    public void whenInStateConnectedOnDescriptorWriteThenTheServiceIsCalled() {
        getDeviceInConnectedState();

        btGattCallback.onDescriptorWrite(mockedBTGatt, mockedBluetoothGattDescriptor, BluetoothGatt.GATT_SUCCESS);
        verify(mockedSHNService).onDescriptorWrite(isA(BTGatt.class), isA(BluetoothGattDescriptor.class), anyInt());
    }

    // Test toString()
    @Test
    public void whenToStringIsCalledThenAStringWithReadableInfoAboutTheDeviceIsReturned() {
        assertEquals("SHNDevice - " + NAME_STRING + " [" + ADDRESS_STRING + "]", shnDevice.toString());
    }

    // Test Capability functions
    @Test
    public void whenNoCapabilitiesAreRegisteredThenGetSupportedCapabilityTypesIsEmpty() {
        assertTrue(shnDevice.getSupportedCapabilityTypes().isEmpty());
        assertNull(shnDevice.getCapabilityForType(SHNCapabilityType.NOTIFICATIONS));
    }

    @Test
    public void whenRegisteringACapabilityThenGetSupportedCapabilityTypesReturnsThatTypeAndTheDeprecatedType() {
        SHNCapabilityNotifications mockedSHNCapabilityNotifications =
                Utility.makeThrowingMock(SHNCapabilityNotifications.class);
        shnDevice.registerCapability(mockedSHNCapabilityNotifications, SHNCapabilityType.NOTIFICATIONS);
        assertEquals(2, shnDevice.getSupportedCapabilityTypes().size());

        assertTrue(shnDevice.getSupportedCapabilityTypes().contains(SHNCapabilityType.NOTIFICATIONS));
        assertNotNull(shnDevice.getCapabilityForType(SHNCapabilityType.NOTIFICATIONS));
        assertTrue(shnDevice.getCapabilityForType(
                SHNCapabilityType.NOTIFICATIONS) instanceof SHNCapabilityNotificationsWrapper);

        assertTrue(shnDevice.getSupportedCapabilityTypes().contains(SHNCapabilityType.Notifications));
        assertNotNull(shnDevice.getCapabilityForType(SHNCapabilityType.Notifications));
        assertTrue(shnDevice.getCapabilityForType(
                SHNCapabilityType.Notifications) instanceof SHNCapabilityNotificationsWrapper);
    }

    @Test
    public void whenRegisteringADeprecatedCapabilityThenGetSupportedCapabilityTypesReturnsThatTypeAndTheDeprecatedType() {
        SHNCapabilityNotifications mockedSHNCapabilityNotifications =
                Utility.makeThrowingMock(SHNCapabilityNotifications.class);
        shnDevice.registerCapability(mockedSHNCapabilityNotifications, SHNCapabilityType.Notifications);
        assertEquals(2, shnDevice.getSupportedCapabilityTypes().size());

        assertTrue(shnDevice.getSupportedCapabilityTypes().contains(SHNCapabilityType.NOTIFICATIONS));
        assertNotNull(shnDevice.getCapabilityForType(SHNCapabilityType.NOTIFICATIONS));
        assertTrue(shnDevice.getCapabilityForType(
                SHNCapabilityType.NOTIFICATIONS) instanceof SHNCapabilityNotificationsWrapper);

        assertTrue(shnDevice.getSupportedCapabilityTypes().contains(SHNCapabilityType.Notifications));
        assertNotNull(shnDevice.getCapabilityForType(SHNCapabilityType.Notifications));
        assertTrue(shnDevice.getCapabilityForType(
                SHNCapabilityType.Notifications) instanceof SHNCapabilityNotificationsWrapper);
    }

    @Test
    public void whenRegisteringACapabilityMoreThanOnceThenAnExceptionIsThrown() {
        SHNCapabilityNotifications mockedSHNCapabilityNotifications =
                Utility.makeThrowingMock(SHNCapabilityNotifications.class);
        shnDevice.registerCapability(mockedSHNCapabilityNotifications, SHNCapabilityType.NOTIFICATIONS);
        boolean exceptionCaught = false;
        try {
            shnDevice.registerCapability(mockedSHNCapabilityNotifications, SHNCapabilityType.NOTIFICATIONS);
        } catch (Exception e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    // Error conditions
    @Test
    public void whenInStateConnectingTheGattCallbackIndicatesServicesDiscoveredErrorThenADisconnectIsInitiated() {
        connectTillGATTConnected();
        btGattCallback.onServicesDiscovered(mockedBTGatt, BluetoothGatt.GATT_FAILURE);
        verify(mockedBTGatt).disconnect();
        assertEquals(SHNDevice.State.Disconnecting, shnDevice.getState());
    }

    // Tests for the connect without timeout
    @Test
    public void whenConnectWithoutTimeoutThenNoTimeoutIsSet() {
        shnDevice.connect(false, -1L);
        assertEquals(0, mockedInternalHandler.getScheduledExecutionCount());
    }

    @Test
    public void whenConnectWithoutTimeoutThenConnectGattIsCalledWithAutoConnect() {
        shnDevice.connect(false, -1L);

        verify(mockedBTDevice).connectGatt(isA(Context.class), eq(true), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenConnectWithoutTimeoutAndRemoteDisconnectsThenDisconnectGattIsCalledWithAutoConnect() {
        useTimeoutConnect = false;
        getDeviceInConnectedState();
        assertEquals(SHNDevice.State.Connected, shnDevice.getState());

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS,
                BluetoothGatt.STATE_DISCONNECTED);
        verify(mockedBTGatt).close();
    }

    //
    @Test
    public void whenConnectWithTimeOutCalledThenConnectGattIsCalled() throws Exception {
        shnDevice.connect(1L);

        verify(mockedBTDevice).connectGatt(isA(Context.class), eq(false), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenConnectWithTimeOutCalledThenStateIsConnecting() throws Exception {
        shnDevice.connect(1L);

        assertEquals(SHNDevice.State.Connecting, shnDevice.getState());
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
    }

    @Test
    public void whenConnectWithTimeOutCalledInStateConnectingThenGattConnectIsNotCalledAgain() throws Exception {
        shnDevice.connect(1L);
        shnDevice.connect(1L);

        verify(mockedBTDevice, times(1)).connectGatt(isA(Context.class), eq(false), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenConnectWithTimeOutCalledThenRegisterSHNCentralStatusListenerForAddressIsCalled() {
        shnDevice.connect(1L);

        verify(mockedSHNCentral).registerSHNCentralStatusListenerForAddress(shnDevice, ADDRESS_STRING);
    }

    @Test
    public void whenGattErrorIsReceivedAndTimeOutNotElapsedThenRetryIsIssuedSilently() {
        shnDevice.connect(1000L);

        reset(mockedSHNDeviceListener);

        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener, never()).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(SHNDevice.State.Connecting, shnDevice.getState());
        verify(mockedBTDevice, times(2)).connectGatt(isA(Context.class), eq(false), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenGattErrorIsReceivedAndTimeOutNotElapsedThenThePreviousConnectionIsClosed() {
        shnDevice.connect(1000L);

        reset(mockedSHNDeviceListener);

        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedBTGatt).close();
    }

    @Test
    public void whenGattErrorIsReceivedAndTimeOutIsElapsedThenErrorIsReported() {
        shnDevice.connect(100L);

        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(SHNDevice.State.Disconnected, shnDevice.getState());
    }

    @Test
    public void whenGattErrorIsReceivedAndTimeOutNotElapsedThenRetryIsIssuedSilentlyMultipleTimes() {
        shnDevice.connect(100L);
        reset(mockedSHNDeviceListener);

        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);
        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);
        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener, never()).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        verify(mockedSHNDeviceListener, never()).onStateUpdated(shnDevice);
        assertEquals(SHNDevice.State.Connecting, shnDevice.getState());
        verify(mockedBTDevice, times(4)).connectGatt(isA(Context.class), eq(false), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenDisconnectIsIssuedThenRetryIsNotPerformed() {
        shnDevice.connect(100L);
        shnDevice.disconnect();
        reset(mockedSHNDeviceListener);

        btGattCallback.onConnectionStateChange(mockedBTGatt, BluetoothGatt.GATT_SUCCESS, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener, never()).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        verify(mockedSHNDeviceListener).onStateUpdated(shnDevice);
        assertEquals(SHNDevice.State.Disconnected, shnDevice.getState());
        verify(mockedBTDevice, times(1)).connectGatt(isA(Context.class), eq(false), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test(expected = InvalidParameterException.class)
    public void whenNegativeTimeOutItProvidedThenExceptionIsGenerated() {
        shnDevice.connect(-100);
    }

    @Test
    public void when0TimeOutItProvidedThenConnectionIsPerformedOnce() {
        shnDevice.connect(0);
        reset(mockedSHNDeviceListener);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        btGattCallback.onConnectionStateChange(mockedBTGatt, SHNDeviceImpl.GATT_ERROR, BluetoothGatt.STATE_DISCONNECTED);

        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(SHNDevice.State.Disconnected, shnDevice.getState());
        verify(mockedBTDevice, times(1)).connectGatt(isA(Context.class), eq(false), isA(SHNCentral.class), isA(BTGatt.BTGattCallback.class));
    }

    @Test
    public void whenBluetoothIsSwitchedOffDuringReconnectCycleThenFailedToConnectIsReported() {
        shnDevice.connect(100L);
        reset(mockedSHNDeviceListener);
        when(mockedSHNCentral.getBluetoothAdapterState()).thenReturn(BluetoothAdapter.STATE_OFF);
        StateRecorder recorder = new StateRecorder();
        doAnswer(recorder).when(mockedSHNDeviceListener).onStateUpdated(isA(SHNDevice.class));

        shnDevice.onStateUpdated(mockedSHNCentral);

        verify(mockedSHNDeviceListener).onFailedToConnect(shnDevice, SHNResult.SHNErrorInvalidState);
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnecting));
        assertEquals(Integer.valueOf(1), recorder.statesReported.get(SHNDevice.State.Disconnected));
        assertEquals(SHNDevice.State.Disconnected, shnDevice.getState());
    }

    @Test
    public void whenReadRSSIOnConnectedDeviceThenGattReadRSSIIsCalled() {
        getDeviceInConnectedState();

        shnDevice.readRSSI();

        verify(mockedBTGatt).readRSSI();
    }

    @Test
    public void whenOnReadRSSIIsCalledThenTheListsnerIsNotified() {
        getDeviceInConnectedState();

        btGattCallback.onReadRemoteRssi(mockedBTGatt, 10, BluetoothGatt.GATT_SUCCESS);

        verify(mockedSHNDeviceListener).onReadRSSI(10);
    }

    /**
     * DiscoveryListener Tests
     */

    @Test
    public void whenDeviceHasDiscoveryListenerItCalls_onServiceDiscovered() throws Exception {
        connectTillGATTServicesDiscovered();
        verify(mockedDiscoveryListener, times(discoveredServices.size())).onServiceDiscovered(
                mockedBluetoothGattService.getUuid(), mockedSHNService);
    }

    @Test
    public void whenDeviceHasNoDiscoveryListenerItNeverCalls_onServiceDiscovered() throws Exception {
        // Creating new SHNDeviceImpl => will have no DiscoveryListener set
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, true);
        connectTillGATTServicesDiscovered();
        verify(mockedDiscoveryListener, never()).onServiceDiscovered(any(UUID.class), any(SHNService.class));
    }

    @Test
    public void whenDeviceHasDiscoveryListenerItCalls_onCharacteristicDiscovered() throws Exception {
        shnDevice.onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
        verify(mockedDiscoveryListener).onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
    }

    @Test
    public void whenDeviceHasNoDiscoveryListenerItNeverCalls_onCharacteristicDiscovered() throws Exception {
        // Creating new SHNDeviceImpl => will have no DiscoveryListener set
        shnDevice = new SHNDeviceImpl(mockedBTDevice, mockedSHNCentral, TEST_DEVICE_TYPE, true);

        shnDevice.onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
        verify(mockedDiscoveryListener, never()).onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
    }

    @Test
    public void whenMultipleDiscoveryListenersAreRegisteredOnlyTheLastOneIsCalled() throws Exception {
        SHNDevice.DiscoveryListener mock2 = mock(SHNDevice.DiscoveryListener.class);
        shnDevice.registerDiscoveryListener(mock2);

        shnDevice.onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
        verify(mock2, times(1)).onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
        verify(mockedDiscoveryListener, never()).onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
    }

    @Test
    public void whenDiscoveryListenerIsUnregisteredNoCallsWillBeForwarded() throws Exception {
        shnDevice.unregisterDiscoveryListener(mockedDiscoveryListener);
        shnDevice.onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
        verify(mockedDiscoveryListener, never()).onCharacteristicDiscovered(MOCK_UUID, MOCK_BYTES, mockedSHNCharacteristic);
    }

    @Test
    public void shouldBeRobustAgainstServiceStateChangingToErrorWhileGattStateIsDisconnected() throws Exception {
        connectTillGATTServicesDiscovered();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                shnDevice.onServiceStateChanged(mockedSHNService, SHNService.State.Error);
                return null;
            }
        }).when(mockedSHNService).disconnectFromBLELayer();
        btGattCallback.onConnectionStateChange(mockedBTGatt, 0 /* don't care */, BluetoothProfile.STATE_DISCONNECTED);
    }

    private class StateRecorder implements Answer<Void> {

        Map<SHNDevice.State, Integer> statesReported = new HashMap<>();

        @Override
        public Void answer(final InvocationOnMock invocation) throws Throwable {
            Integer oldCount = statesReported.get(shnDevice.getState());
            if (oldCount == null) oldCount = 0;
            statesReported.put(shnDevice.getState(), oldCount + 1);
            return null;
        }
    }
}