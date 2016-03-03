package com.philips.pins.shinelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.philips.pins.shinelib.bluetoothwrapper.BleUtilities;
import com.philips.pins.shinelib.framework.BleUUIDCreator;
import com.philips.pins.shinelib.framework.LeScanCallbackProxy;
import com.philips.pins.shinelib.helper.MockedHandler;
import com.philips.pins.shinelib.helper.Utility;
import com.philips.pins.shinelib.utility.BleScanRecord;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

public class SHNDeviceScannerInternalTest {
    private static final long STOP_SCANNING_AFTER_10_SECONDS = 10000l;
    private SHNDeviceScannerInternal shnDeviceScannerInternal;
    private SHNDevice mockedSHNDevice;
    private SHNCentral mockedSHNCentral;
    private Context mockedContext;
    private BluetoothManager mockedBluetoothManager;
    private BluetoothAdapter mockedBluetoothAdapter;
    private MockedHandler mockedHandler;
    private List<SHNDeviceDefinitionInfo> testDeviceDefinitionInfos;
    private boolean resultForMatchesOnAdvertisedData;
    private boolean resultForUseAdvertisedDataMatcher;
    private LeScanCallbackProxy leScanCallbackProxy;

    @Before
    public void setUp() {
        initMocks(this);

        mockedContext = (Context) Utility.makeThrowingMock(Context.class);
        mockedBluetoothManager = (BluetoothManager) Utility.makeThrowingMock(BluetoothManager.class);
        mockedBluetoothAdapter = (BluetoothAdapter) Utility.makeThrowingMock(BluetoothAdapter.class);
        mockedSHNCentral = (SHNCentral) Utility.makeThrowingMock(SHNCentral.class);
        mockedSHNDevice = Utility.makeThrowingMock(SHNDevice.class);
        mockedHandler = new MockedHandler();

        doReturn(mockedHandler.getMock()).when(mockedSHNCentral).getInternalHandler();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        }).when(mockedSHNCentral).runOnUserHandlerThread(any(Runnable.class));
        doReturn(mockedSHNDevice).when(mockedSHNCentral).createSHNDeviceForAddressAndDefinition(anyString(), any(SHNDeviceDefinitionInfo.class));
        doReturn(mockedBluetoothManager).when(mockedContext).getSystemService(Context.BLUETOOTH_SERVICE);
        doReturn(mockedBluetoothAdapter).when(mockedBluetoothManager).getAdapter();
        doReturn(true).when(mockedBluetoothAdapter).startLeScan(any(BluetoothAdapter.LeScanCallback.class));
        doNothing().when(mockedBluetoothAdapter).stopLeScan(any(BluetoothAdapter.LeScanCallback.class));

        testDeviceDefinitionInfos = new ArrayList<>();
        testDeviceDefinitionInfos.add(new SHNDeviceDefinitionInfo() {
            @Override
            public String getDeviceTypeName() {
                return null;
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
        resultForMatchesOnAdvertisedData = false;
        resultForUseAdvertisedDataMatcher = false;
        BleUtilities.init(mockedContext);

        leScanCallbackProxy = new LeScanCallbackProxy();
        shnDeviceScannerInternal = new SHNDeviceScannerInternal(mockedSHNCentral, leScanCallbackProxy, testDeviceDefinitionInfos);
    }

    @Test
    public void whenScanningThenStartLeScanOnTheBluetoothAdapterIsCalled() {
        assertTrue(shnDeviceScannerInternal.startScanning(null, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS));
        verify(mockedBluetoothAdapter).startLeScan(any(BluetoothAdapter.LeScanCallback.class));
    }

    @Test
    public void whenScanningThenStartingANextScanReturnsFalse() {
        shnDeviceScannerInternal.startScanning(null, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);
        assertFalse(shnDeviceScannerInternal.startScanning(null, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS));
        verify(mockedBluetoothAdapter).startLeScan(any(BluetoothAdapter.LeScanCallback.class));
    }

    @Test
    public void whenStopScanningIsCalledThenStopLeScanOnTheBluetoothAdapterIsCalledWithTheSameParameterAsInStart() {
        // Start scanning and capture the scancallback object
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        assertTrue(shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS));
        ArgumentCaptor<BluetoothAdapter.LeScanCallback> leScanCallbackStartArgumentCaptor = ArgumentCaptor.forClass(BluetoothAdapter.LeScanCallback.class);
        verify(mockedBluetoothAdapter).startLeScan(leScanCallbackStartArgumentCaptor.capture());

        // Stop scanning and verify that the same scancallback object is used to cancel callbacks
        shnDeviceScannerInternal.stopScanning();
        ArgumentCaptor<BluetoothAdapter.LeScanCallback> leScanCallbackStopArgumentCaptor = ArgumentCaptor.forClass(BluetoothAdapter.LeScanCallback.class);
        verify(mockedBluetoothAdapter).stopLeScan(leScanCallbackStopArgumentCaptor.capture());

        assertEquals(leScanCallbackStartArgumentCaptor.getValue(), leScanCallbackStopArgumentCaptor.getValue());
    }

    @Test
    public void whenStopScanningIsCalledThenScanStoppedOnTheListenerIsCalled() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);
        shnDeviceScannerInternal.stopScanning();

        verify(mockedSHNDeviceScannerListener).scanStopped(null);
    }

    @Test
    public void whenScanningThenScanningIsStoppedAutomaticallyAfterTheMaxScanTime() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        assertTrue(shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS));
        verify(mockedBluetoothAdapter).startLeScan(any(BluetoothAdapter.LeScanCallback.class));

        // The scanner has a timer runnung to restart scanning. Some Androids don't report a device multiple times.
        mockedHandler.executeFirstScheduledExecution(); // first scan restart after 3 seconds
        mockedHandler.executeFirstScheduledExecution(); // second scan restart after 3 seconds
        mockedHandler.executeFirstScheduledExecution(); // third scan restart after 3 seconds
        mockedHandler.executeFirstScheduledExecution(); // Now it's a scanning timeout (10 secs)
        verify(mockedSHNDeviceScannerListener).scanStopped(null);
    }

    @Test
    public void whenDuringScanningADeviceIsFoundWithMatchingPrimaryServiceUUID16ThenItIsReported() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = (BluetoothDevice) Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x18}); // advertisement of the primary uuid for the device info service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());
    }

    @Test
    public void whenDuringScanningADeviceIsFoundWithMatchingPrimaryServiceUUID128ThenItIsReported() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = (BluetoothDevice) Utility.makeThrowingMock(BluetoothDevice.class);
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
    public void whenDuringScanningADeviceIsFoundMultipleTimesWithMatchingPrimaryServiceUUID16ThenItIsReportedOnlyOnce() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = (BluetoothDevice) Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x18}); // advertisement of the primary uuid for the device info service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x18}); // advertisement of the primary uuid for the device info service
        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture()); // Note that verify checks that the callback is called only once!
    }

    @Test
    public void whenDuringScanningADeviceIsFoundWithNOTMatchingPrimaryServiceUUID16ThenItIsNotReported() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = (BluetoothDevice) Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{0x03, 0x03, 0x0A, 0x17}); // advertisement of the primary uuid for an unknown service

        ArgumentCaptor<SHNDeviceFoundInfo> shnDeviceFoundInfoArgumentCaptor = ArgumentCaptor.forClass(SHNDeviceFoundInfo.class);
        verify(mockedSHNDeviceScannerListener, times(0)).deviceFound(any(SHNDeviceScanner.class), shnDeviceFoundInfoArgumentCaptor.capture());
    }

    @Test
    public void whenStopScanningIsCalledWhenNotScanningThenNothingBadHappens() {
        shnDeviceScannerInternal.stopScanning();
        shnDeviceScannerInternal.shutdown();
    }

    @Test
    public void whenThePluginScannerDataMatcherReturnsTrueThenTheScannerReportsDeviceFound() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = (BluetoothDevice) Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();
        resultForUseAdvertisedDataMatcher = true;
        resultForMatchesOnAdvertisedData = true;

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{}); // advertisement of the primary uuid for an unknown service

        verify(mockedSHNDeviceScannerListener).deviceFound(any(SHNDeviceScanner.class), isA(SHNDeviceFoundInfo.class));
    }

    @Test
    public void whenThePluginScannerDataMatcherReturnsFalseThenTheScannerDoesNotReportTheDeviceFound() {
        SHNDeviceScanner.SHNDeviceScannerListener mockedSHNDeviceScannerListener = mock(SHNDeviceScanner.SHNDeviceScannerListener.class);
        shnDeviceScannerInternal.startScanning(mockedSHNDeviceScannerListener, SHNDeviceScanner.ScannerSettingDuplicates.DuplicatesNotAllowed, STOP_SCANNING_AFTER_10_SECONDS);

        BluetoothDevice mockedBluetoothDevice = (BluetoothDevice) Utility.makeThrowingMock(BluetoothDevice.class);
        doReturn("12:34:56:78:90:AB").when(mockedBluetoothDevice).getAddress();
        doReturn("Mocked Bluetooth Device").when(mockedBluetoothDevice).getName();
        resultForUseAdvertisedDataMatcher = true;
        resultForMatchesOnAdvertisedData = false;

        leScanCallbackProxy.onLeScan(mockedBluetoothDevice, -50, new byte[]{});

        verify(mockedSHNDeviceScannerListener, never()).deviceFound(any(SHNDeviceScanner.class), isA(SHNDeviceFoundInfo.class));
    }
}
