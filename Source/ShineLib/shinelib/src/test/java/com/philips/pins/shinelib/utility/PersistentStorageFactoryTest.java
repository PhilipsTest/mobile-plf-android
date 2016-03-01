package com.philips.pins.shinelib.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.philips.pins.shinelib.RobolectricTest;
import com.philips.pins.shinelib.SHNDevice;
import com.philips.pins.shinelib.SharedPreferencesProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PersistentStorageFactoryTest extends RobolectricTest {

    public static final String TEST_ADDRESS = "TEST_ADDRESS";

    @Mock
    private SHNDevice shnDeviceMock;

    private PersistentStorageFactory persistentStorageFactory;

    List<String> keyList = new ArrayList<>();

    @Before
    public void setUp() {
        initMocks(this);

        when(shnDeviceMock.getAddress()).thenReturn(TEST_ADDRESS);

        persistentStorageFactory = new PersistentStorageFactory(new SharedPreferencesProvider() {

            @Override
            public SharedPreferences getSharedPreferences(String key, int mode) {
                keyList.add(key);
                return RuntimeEnvironment.application.getSharedPreferences(key, mode);
            }
        });
    }

    @Test
    public void ShouldCreateRootPreferences_WhenGetPersistentStorageIsCalled() {
        persistentStorageFactory.getPersistentStorage();

        assertThat(keyList.get(0)).isEqualTo(PersistentStorageFactory.SHINELIB_KEY);
    }

    @Test
    public void ShouldCreateUserPreferences_WhenGetPersistentStorageForUserIsCalled() {
        persistentStorageFactory.getPersistentStorageForUser();

        assertThat(keyList.get(0)).isEqualTo(PersistentStorageFactory.USER_KEY);
    }

    @Test
    public void ShouldCreateDevicePreferences_WhenGetPersistentStorageForDeviceIsCalled() {
        persistentStorageFactory.getPersistentStorageForDevice(shnDeviceMock);

        assertThat(keyList.get(0)).isEqualTo(PersistentStorageFactory.DEVICE_ADDRESS_KEY);
        assertThat(keyList.get(1)).isEqualTo(TEST_ADDRESS + PersistentStorageFactory.DEVICE_KEY);
    }

    @Test
    public void ShouldSaveAddress_WhenGetPersistentStorageForDeviceIsCalled() {
        persistentStorageFactory.getPersistentStorageForDevice(shnDeviceMock);

        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(PersistentStorageFactory.DEVICE_ADDRESS_KEY, Context.MODE_PRIVATE);
        Set<String> keySet = sharedPreferences.getStringSet(PersistentStorageFactory.DEVICE_ADDRESS_KEY, null);

        assertThat(keySet).contains(TEST_ADDRESS);
    }

    @Test
    public void ShouldNotRenewKeystore_WhenKeyIsAlreadyPresent() {
        persistentStorageFactory.getPersistentStorageForDevice(shnDeviceMock);
        persistentStorageFactory.getPersistentStorageForDevice(shnDeviceMock);

        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(PersistentStorageFactory.DEVICE_ADDRESS_KEY, Context.MODE_PRIVATE);
        Set<String> keySet = sharedPreferences.getStringSet(PersistentStorageFactory.DEVICE_ADDRESS_KEY, null);

        assertThat(keySet).hasSize(1);
    }
}