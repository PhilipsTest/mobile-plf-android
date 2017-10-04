/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.util;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class GpsUtilTest {

    @Before
    public void shouldReturnTrueAlwaysIfOSVersionIsBelowAndroidM() throws Exception {
        stubAndroidSdkVersion(Build.VERSION_CODES.LOLLIPOP);

        assertFalse(GpsUtil.isGPSRequiredForWifiScan());
    }

    @Test
    public void shouldReturnTrueIfVersionOSIsFromAndroidM() throws Exception {
        stubAndroidSdkVersion(Build.VERSION_CODES.M);

        assertTrue(GpsUtil.isGPSRequiredForWifiScan());
    }

    @Test
    public void shouldCheckIfGPSIsEnabledWhenAsked() throws Exception {
        final Context contextMock = mock(Context.class);
        final LocationManager locationMangerMock = mock(LocationManager.class);

        when(contextMock.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationMangerMock);
        when(locationMangerMock.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);

        assertTrue(GpsUtil.isGPSEnabled(contextMock));
    }

    private void stubAndroidSdkVersion(final int sdkInt) {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", sdkInt);
    }
}