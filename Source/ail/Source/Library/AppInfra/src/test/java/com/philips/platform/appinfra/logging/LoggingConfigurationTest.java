/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.logging;

import android.content.Context;
import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static com.philips.platform.appinfra.logging.LoggingConfiguration.APP_INFRA_CLOUD_LOGGING_PRODUCT_KEY;
import static com.philips.platform.appinfra.logging.LoggingConfiguration.APP_INFRA_CLOUD_LOGGING_SECRET_KEY;
import static com.philips.platform.appinfra.logging.LoggingConfiguration.APP_INFRA_CLOUD_LOGGING_SHARED_KEY;
import static com.philips.platform.appinfra.logging.LoggingConfiguration.HSDP_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class LoggingConfigurationTest {

    private LoggingConfiguration loggingConfiguration;
    private AppInfraInterface mAppInfra;
    @Mock
    private AppConfigurationInterface.AppConfigurationError appConfigurationError;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Context context = mock(Context.class);
        mAppInfra = mock(AppInfra.class);
        when(mAppInfra.getAppInfraContext()).thenReturn(context);
        final HashMap hashMap = new HashMap();
        hashMap.put("logging.debugConfig", true);
        hashMap.put("componentLevelLogEnabled", true);
        hashMap.put("consoleLogEnabled", true);
        hashMap.put("cloudLogEnabled", true);
        hashMap.put("fileLogEnabled", true);
        hashMap.put("cloudBatchLimit", 5);
        hashMap.put("logLevel", "All");

        loggingConfiguration = new LoggingConfiguration(mAppInfra, "", "") {
            @Override
            HashMap<?, ?> getLoggingProperties() {
                return hashMap;
            }

            @NonNull
            @Override
            AppConfigurationInterface.AppConfigurationError getAppConfigurationError() {
                return appConfigurationError;
            }
        };
    }

    @Test
    public void testIsComponentLevelLogEnabled() {
        assertTrue(loggingConfiguration.isComponentLevelLogEnabled());
    }

    @Test
    public void testIsFileLogEnabled() {
        assertTrue(loggingConfiguration.isFileLogEnabled());
    }

    @Test
    public void testIsCloudLogEnabled() {
        assertTrue(loggingConfiguration.isCloudLogEnabled());
    }

    @Test
    public void testIsConsoleLogEnabled() {
        assertTrue(loggingConfiguration.isConsoleLogEnabled());
    }

    @Test
    public void testGetLogLevel() {
        assertEquals(loggingConfiguration.getLogLevel(), "All");
    }

    @Test
    public void testAssertingNonNull() {
        assertNotNull(loggingConfiguration.getAppInfra());
        assertNotNull(loggingConfiguration.getComponentVersion());
        assertNotNull(loggingConfiguration.getAppConfigurationError());
        assertNotNull(loggingConfiguration.getLoggingProperties());
    }

    @Test
    public void testIsLoggingEnabled() {
        boolean loggingEnabled = loggingConfiguration.isLoggingEnabled();
        assertTrue(loggingEnabled);
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("logLevel", "Off");
        loggingConfiguration = new LoggingConfiguration(mAppInfra, "", "") {
            @Override
            HashMap<?, ?> getLoggingProperties() {
                return hashMap;
            }
        };
        loggingEnabled = loggingConfiguration.isLoggingEnabled();
        assertFalse(loggingEnabled);
    }

    @Test
    public void testValidateIfComponentLogCriteriaMet() {
        assertFalse(loggingConfiguration.checkIfComponentLogCriteriaMet("componentId"));
    }

    @Test
    public void testGetBatchLimit() {
        assertEquals(loggingConfiguration.getBatchLimit(),5);
        assertFalse(loggingConfiguration.getBatchLimit() == 6);
    }

    @Test
    public void testNotNullAssertions() {
        AppConfigurationInterface appConfigurationInterface = mock(AppConfigurationInterface.class);
        when(mAppInfra.getConfigInterface()).thenReturn(appConfigurationInterface);
        when(appConfigurationInterface.getPropertyForKey(APP_INFRA_CLOUD_LOGGING_PRODUCT_KEY, HSDP_GROUP, appConfigurationError)).thenReturn("productKey");
        when(appConfigurationInterface.getPropertyForKey(APP_INFRA_CLOUD_LOGGING_SECRET_KEY, HSDP_GROUP, appConfigurationError)).thenReturn("secretKey");
        when(appConfigurationInterface.getPropertyForKey(APP_INFRA_CLOUD_LOGGING_SHARED_KEY, HSDP_GROUP, appConfigurationError)).thenReturn("sharedKey");
        assertNotNull(loggingConfiguration.getCLProductKey());
        assertEquals(loggingConfiguration.getCLProductKey(),"productKey");
        assertNotNull(loggingConfiguration.getCLSecretKey());
        assertEquals(loggingConfiguration.getCLSecretKey(),"secretKey");
        assertNotNull(loggingConfiguration.getCLSharedKey());
        assertEquals(loggingConfiguration.getCLSharedKey(),"sharedKey");
    }
}