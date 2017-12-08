
package com.philips.dhpclient;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import com.philips.dhpclient.DhpApiClientConfiguration;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by 310243576 on 8/19/2016.
 */
public class DhpApiClientConfigurationTest extends InstrumentationTestCase {

    DhpApiClientConfiguration mDhpApiClientConfiguration;

    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        // Necessary to get Mockito framework working
//        MockitoAnnotations.initMocks(this);
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        mDhpApiClientConfiguration = new DhpApiClientConfiguration("apiBaseUrl", "dhpApplicationName", "signingKey","signingSecret");
        assertNotNull(mDhpApiClientConfiguration);
    }

    @Test
    public void testGetApiBaseUrl() throws Exception {
        assertEquals("apiBaseUrl",mDhpApiClientConfiguration.getApiBaseUrl());

    }

    @Test
    public void testGetDhpApplicationName() throws Exception {
        assertEquals("dhpApplicationName",mDhpApiClientConfiguration.getDhpApplicationName());

    }

    @Test
    public void testGetSigningKey() throws Exception {
        assertEquals("signingKey",mDhpApiClientConfiguration.getSigningKey());

    }

    @Test
    public void testGetSigningSecret() throws Exception {
        assertEquals("signingSecret",mDhpApiClientConfiguration.getSigningSecret());

    }
}
