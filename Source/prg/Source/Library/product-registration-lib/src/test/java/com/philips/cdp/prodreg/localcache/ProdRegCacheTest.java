/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.prodreg.localcache;

import androidx.annotation.NonNull;

import com.philips.platform.appinfra.securestorage.SecureStorageInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class ProdRegCacheTest {

    private ProdRegCache prodRegCache;
    private SecureStorageInterface ssInterface;
    private SecureStorageInterface.SecureStorageError ssError;

    @Before
    public void setUp() throws Exception {
        ssInterface = mock(SecureStorageInterface.class);
        ssError = mock(SecureStorageInterface.SecureStorageError.class);
        prodRegCache = new ProdRegCache() {
            @Override
            public SecureStorageInterface getAppInfraSecureStorageInterface() {
                return ssInterface;
            }

            @NonNull
            @Override
            public SecureStorageInterface.SecureStorageError getSecureStorageError() {
                return ssError;
            }
        };
    }

    @Test
    public void testStoreStringData() {
        prodRegCache.storeStringData("", "data");
        verify(ssInterface).storeValueForKey("", "data", ssError);
    }

    @Test
    public void testGetStringData() {
        final String testData = "test";
        when(ssInterface.fetchValueForKey("", ssError)).thenReturn(testData);
        final String data = prodRegCache.getStringData("");
        assertEquals(data, testData);
    }

    @Test
    public void testGetIntData() {
        final String testData = "5";
        when(ssInterface.fetchValueForKey("", ssError)).thenReturn(testData);
        final int data = prodRegCache.getIntData("");
        assertEquals(String.valueOf(data), testData);
    }
}