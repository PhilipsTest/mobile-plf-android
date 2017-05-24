/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.pins.shinelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.philips.pins.shinelib.bluetoothwrapper.BleUtilities;
import com.philips.pins.shinelib.framework.BleUUIDCreator;
import com.philips.pins.shinelib.framework.LeScanCallbackProxy;
import com.philips.pins.shinelib.helper.MockedHandler;
import com.philips.pins.shinelib.helper.Utility;
import com.philips.pins.shinelib.utility.BleScanRecord;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

public class SHNDeviceScannerInternalTest {
    private static final long STOP_SCANNING_AFTER_10_SECONDS = 10000L;
    public static final String MOCKED_BLUETOOTH_DEVICE_NAME = "Mocked Bluetooth Device";

    @Mock
    private Context contextMock;

    @Mock
    private SHNDevice deviceMock;

    @Mock
    private SHNCentral shnCentralMock;

    @Mock
    private BleUtilities bleUtilitiesMock;

    @Mock
    private SHNInternalScanRequest internalScanRequestMock1;

    @Mock
    private SHNInternalScanRequest internalScanRequestMock2;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    private MockedHandler mockedHandler;
    private List<SHNDeviceDefinitionInfo> testDeviceDefinitionInfos;
    private boolean resultForMatchesOnAdvertisedData;
    private boolean resultForUseAdvertisedDataMatcher;
    private LeScanCallbackProxy leScanCallbackProxy;
    private SHNDeviceScannerInternal shnDeviceScannerInternal;

    @Before
    public void setUp() {
        initMocks(this);
        
        mockedHandler = new MockedHandler();

        when(shnCentralMock.getInternalHandler()).thenReturn(mockedHandler.getMock());

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        }).when(shnCentralMock).runOnUserHandlerThread(any(Runnable.class));
        doReturn(deviceMock).when(shnCentralMock).createSHNDeviceForAddressAndDefinition(anyString(), any(SHNDeviceDefinitionInfo.class));

        doReturn(bleUtilitiesMock).when(shnCentralMock).getBleUtilities();

        doReturn(true).when(bleUtilitiesMock).startLeScan(any(BluetoothAdapter.LeScanCallback.class));
        doReturn(true).when(bleUtilitiesMock).startLeScan(any(UUID[].class), any(BluetoothAdapter.LeScanCallback.class));
        doNothing().when(bleUtilitiesMock).stopLeScan(any(BluetoothAdapter.LeScanCallback.class));

        when(deviceMock.getDeviceTypeName()).thenReturn(MOCKED_BLUETOOTH_DEVICE_NAME);

        testDeviceDefinitionInfos = new ArrayList<>();
        testDeviceDefinitionInfos.add(new SHNDeviceDefinitionInfo() {
            @Override
            public String getDeviceTypeName() {
                return MOCKED_BLUETOOTH_DEVICE_NAME;
            }

            @Override
            public Set<UUID> getPrimaryServiceUUIDs() {
                Set<UUID> primaryServiceUUIDs = new HashSet<>();
                primaryServiceUUIDs.add(UUID.fromString(BleUUIDCreator.create128bitBleUUIDFrom16BitBleUUID(0x180A)));
                return primaryServiceUUIDs;
            }

            @Override
            public SHNAssociationProcedurePlugin createSHNAssociationProcedure(SHNCentral central, SHNAssociationProcedurePlugin.SHNAssociationProcedureListener shnAssociationProcedureListener) {
                return null;
            }

            @Override
            public SHNDeviceDefinition getSHNDeviceDefinition() {
                return null;
            }

            @Override
            public boolean useAdvertisedDataMatcher() {
                return resultForUseAdvertisedDataMatcher;
            }

            @Override
            public boolean matchesOnAdvertisedData(BluetoothDevice bluetoothDevice, BleScanRecord bleScanRecord, int rssi) {
                return resultForMatchesOnAdvertisedData;
            }
        });

        leScanCallbackProxy = new LeScanCallbackProxy(bleUtilitiesMock);
        shnDeviceScannerInternal = new TestSHNDeviceScannerInternal(shnCentralMock, testDeviceDefinitionInfos);
    }

    @Test
    public void whenScanning_ThenStartLeScanOnTheBluetoothAdapterIsCalled() {
        boolean startScanning = shnDeviceScannerInternal.startScanning(null, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        assertThat(startScanning).isTrue();
        verify(bleUtilitiesMock).startLeScan(any(UUID[].class), any(BluetoothAdapter.LeScanCallback.class));
    }

    @Test
    public void whenScanning_ThenStartingANextScanReturnsAlsoTrueButStartLeScanIsNotCalledTwice() {
        shnDeviceScannerInternal.startScanning(null, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);
        boolean startScanning = shnDeviceScannerInternal.startScanning(null, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        assertThat(startScanning).isTrue();
        verify(bleUtilitiesMock, times(1)).startLeScan(any(UUID[].class), any(BluetoothAdapter.LeScanCallback.class));
    }

    @Test
    public void whenStopScanningIsCalled_ThenStopLeScanOnTheBluetoothAdapterIsCalledWithTheSameParameterAsInStart() {
        // Start scanning and capture the scancallback object
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        assertTrue(shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS));
        ArgumentCaptor<BluetoothAdapter.LeScanCallback> leScanCallbackStartArgumentCaptor = ArgumentCaptor.forClass(BluetoothAdapter.LeScanCallback.class);
        verify(bleUtilitiesMock).startLeScan(any(UUID[].class), leScanCallbackStartArgumentCaptor.capture());

        // Stop scanning and verify that the same scancallback object is used to cancel callbacks
        shnDeviceScannerInternal.stopScanning();
        ArgumentCaptor<BluetoothAdapter.LeScanCallback> leScanCallbackStopArgumentCaptor = ArgumentCaptor.forClass(BluetoothAdapter.LeScanCallback.class);
        verify(bleUtilitiesMock).stopLeScan(leScanCallbackStopArgumentCaptor.capture());

        assertEquals(leScanCallbackStartArgumentCaptor.getValue(), leScanCallbackStopArgumentCaptor.getValue());
    }

    @Test
    public void whenStopScanningIsCalled_ThenScanStoppedOnTheListenerIsCalled() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);
        shnDeviceScannerInternal.stopScanning();

        verify(mockedSHNDeviceScannerListener).scanStopped(null);
    }

    @Test
    public void whenScanning_ThenScanningIsStoppedAutomaticallyAfterTheMaxScanTime() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        assertTrue(shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS));
        verify(bleUtilitiesMock).startLeScan(any(UUID[].class), any(BluetoothAdapter.LeScanCallback.class));

        // The scanner has a timer running to restart scanning. Some Androids don't report a device multiple times.
        mockedHandler.executeFirstScheduledExecution(); // first scan restart after 3 seconds
        mockedHandler.executeFirstScheduledExecution(); // second scan restart after 3 seconds
        mockedHandler.executeFirstScheduledExecution(); // third scan restart after 3 seconds
        mockedHandler.executeFirstScheduledExecution(); // Now it's a scanning timeout (10 secs)
        verify(mockedSHNDeviceScannerListener).scanStopped(null);
    }

    @Test
    public void whenDuringScanningADeviceIsFoundWithMatchingPrimaryServiceUUID16_ThenItIsReported() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x18}); // advertisement of the primary uuid for the device info service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());
    }

    @Test
    public void whenDuringScanningADeviceIsFoundWithMatchingPrimaryServiceUUID128_ThenItIsReported() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{
                (byte) 0x10, // len
                (byte) 0x07, // type
                (byte) 0xFB, (byte) 0x34, (byte) 0x9B, (byte) 0x5F,
                (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x80,
                (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                (byte) 0x0A, (byte) 0x18, (byte) 0x00, (byte) 0x00,
                (byte) 0x03, (byte) 0xFF, (byte) 0x0A, (byte) 0x18, // unhandled type with data length 3!
                (byte) 0x00, (byte) 0x00, (byte) 0x00}); // len = 0 // advertisement of the primary uuid for the device info service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());
    }

    @Test
    public void whenDuringScanningADeviceIsFoundMultipleTimesWithMatchingPrimaryServiceUUID16_ThenItIsReportedOnlyOnce() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x18}); // advertisement of the primary uuid for the device info service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x18}); // advertisement of the primary uuid for the device info service
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture()); // Note that verify checks that the callback is called only once!
    }

    @Test
    public void whenDuringScanningADeviceIsFoundWithNOTMatchingPrimaryServiceUUID16_ThenItIsNotReported() {
        resultForUseAdvertisedDataMatcher = true;

        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x17}); // advertisement of the primary uuid for an unknown service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener, never()).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());
    }

    @Test
    public void whenStopScanningIsCalledWhenNotScanning_ThenNothingBadHappens() {
        shnDeviceScannerInternal.stopScanning();
    }

    @Test
    public void whenThePluginScannerDataMatcherReturnsTrue_ThenTheScannerReportsDeviceFound() {
        resultForUseAdvertisedDataMatcher = true;
        resultForMatchesOnAdvertisedData = true;

        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{}); // advertisement of the primary uuid for an unknown service

        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), isA(SHNDeviceFoundInfo.class));
    }

    @Test
    public void whenThePluginScannerDataMatcherReturnsFalse_ThenTheScannerDoesNotReportTheDeviceFound() {
        resultForUseAdvertisedDataMatcher = true;
        resultForMatchesOnAdvertisedData = false;

        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{});

        verify(mockedSHNDeviceScannerListener, never()).deviceFound(any(SHNDeviceScanner.class), isA(SHNDeviceFoundInfo.class));
    }

    @Test
    public void whenScanningTwice_ThenBothScanRequestGetStartedMessage() {
        shnDeviceScannerInternal.startScanning(internalScanRequestMock1);
        shnDeviceScannerInternal.startScanning(internalScanRequestMock2);

        verify(internalScanRequestMock1).scanningStarted(shnDeviceScannerInternal, mockedHandler.getMock());
        verify(internalScanRequestMock2).scanningStarted(shnDeviceScannerInternal, mockedHandler.getMock());
    }

    @Test
    public void whenScanningTwice_ThenWhenOneIsStoppedScanningItShouldBeInformed() {
        shnDeviceScannerInternal.startScanning(internalScanRequestMock1);
        shnDeviceScannerInternal.startScanning(internalScanRequestMock2);

        Handler handlerMock = mockedHandler.getMock();
        verify(handlerMock).postDelayed(runnableCaptor.capture(), eq(SHNDeviceScannerInternal.SCANNING_RESTART_INTERVAL_MS));

        shnDeviceScannerInternal.stopScanning(internalScanRequestMock1);
        verify(internalScanRequestMock1).scanningStopped();
    }

    @Test
    public void whenScanningTwice_ThenWhenOneIsStoppedScanningShouldContinueForTheOther() {
        shnDeviceScannerInternal.startScanning(internalScanRequestMock1);
        shnDeviceScannerInternal.startScanning(internalScanRequestMock2);

        Handler handlerMock = mockedHandler.getMock();
        verify(handlerMock).postDelayed(runnableCaptor.capture(), eq(SHNDeviceScannerInternal.SCANNING_RESTART_INTERVAL_MS));

        shnDeviceScannerInternal.stopScanning(internalScanRequestMock1);
        verify(internalScanRequestMock1).scanningStopped();
    }

    @Test
    public void whenScanningTwice_ThenWhenBothAreStoppedScanningShouldStop() {
        shnDeviceScannerInternal.startScanning(internalScanRequestMock1);
        shnDeviceScannerInternal.startScanning(internalScanRequestMock2);

        Handler handlerMock = mockedHandler.getMock();
        verify(handlerMock).postDelayed(runnableCaptor.capture(), eq(SHNDeviceScannerInternal.SCANNING_RESTART_INTERVAL_MS));

        shnDeviceScannerInternal.stopScanning(internalScanRequestMock1);
        shnDeviceScannerInternal.stopScanning(internalScanRequestMock2);

        verify(handlerMock).removeCallbacks(runnableCaptor.getValue());
    }

    private class TestSHNDeviceScannerInternal extends SHNDeviceScannerInternal {

        TestSHNDeviceScannerInternal(@NonNull final SHNCentral shnCentral, @NonNull final List<SHNDeviceDefinitionInfo> registeredDeviceDefinitions) {
            super(shnCentral, registeredDeviceDefinitions);
        }

        @Override
        LeScanCallbackProxy createLeScanCallbackProxy() {
            return leScanCallbackProxy;
        }
    }
}
