package com.philips.pins.shinelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.philips.pins.shinelib.exceptions.SHNBluetoothHardwareUnavailableException;
import com.philips.pins.shinelib.helper.MockedHandler;
import com.philips.pins.shinelib.helper.Utility;
import com.philips.pins.shinelib.utility.DataMigrater;
import com.philips.pins.shinelib.utility.PersistentStorageFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doReturn;

public class SHNCentralTest extends RobolectricTest {
    private SHNCentral shnCentral;
    private MockedHandler mockedUserHandler;
    private MockedHandler mockedInternalHandler;
    private Context mockedContext;
    private PackageManager mockedPackageManager;
    private BluetoothManager mockedBluetoothManager;
    private BluetoothAdapter mockedBluetoothAdapter;
    private SharedPreferences mockedSharedPreferences;

    private DataMigrater mockedDataMigrater;

    @Captor
    ArgumentCaptor<BroadcastReceiver> broadcastReceiverArgumentCaptor;

    @Captor
    ArgumentCaptor<IntentFilter> intentFilterCaptor;

    @Before
    public void setUp() throws SHNBluetoothHardwareUnavailableException, Exception {
        initMocks(this);

        mockedContext = Utility.makeThrowingMock(Context.class);
        mockedUserHandler = new MockedHandler();
        mockedInternalHandler = new MockedHandler();
        mockedPackageManager = Utility.makeThrowingMock(PackageManager.class);
        mockedBluetoothManager = Utility.makeThrowingMock(BluetoothManager.class);
        mockedBluetoothAdapter = Utility.makeThrowingMock(BluetoothAdapter.class);
        mockedSharedPreferences = Utility.makeThrowingMock(SharedPreferences.class);
        mockedDataMigrater = Utility.makeThrowingMock(DataMigrater.class);

        doReturn(mockedContext).when(mockedContext).getApplicationContext();
        doReturn(mockedPackageManager).when(mockedContext).getPackageManager();
        doReturn(true).when(mockedPackageManager).hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        doReturn(mockedBluetoothManager).when(mockedContext).getSystemService(Context.BLUETOOTH_SERVICE);
        doReturn(mockedBluetoothAdapter).when(mockedBluetoothManager).getAdapter();
        doReturn(true).when(mockedBluetoothAdapter).isEnabled();
        doReturn(null).when(mockedContext).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
        doReturn(mockedSharedPreferences).when(mockedContext).getSharedPreferences(anyString(), anyInt());

        doNothing().when(mockedDataMigrater).execute(any(Context.class), any(PersistentStorageFactory.class));

        shnCentral = new SHNCentral(mockedUserHandler.getMock(), mockedContext) {
            @Override
            DataMigrater createDataMigrater() {
                return mockedDataMigrater;
            }

            @Override
            Handler createInternalHandler() {
                return mockedInternalHandler.getMock();
            }
        };
    }

    private BroadcastReceiver captureBroadCastReceiver(@NonNull String action) {
        verify(mockedContext, atLeastOnce()).registerReceiver(broadcastReceiverArgumentCaptor.capture(), intentFilterCaptor.capture());

        List<IntentFilter> allValues = intentFilterCaptor.getAllValues();
        for (int i = 0; i < allValues.size(); i++) {
            IntentFilter intentFilter = allValues.get(i);
            if (intentFilter.hasAction(action))
                return broadcastReceiverArgumentCaptor.getAllValues().get(i);
        }

        return null;
    }

    @Test
    public void testGetShnCentralListener() {

    }

    @Test
    public void testSetShnCentralListener() {

    }

    @Test
    public void testGetShnDeviceScanner() {

    }

    @Test
    public void testGetShnDeviceAssociation() {

    }

    @Test
    public void testGetBTDevice() {

    }
}