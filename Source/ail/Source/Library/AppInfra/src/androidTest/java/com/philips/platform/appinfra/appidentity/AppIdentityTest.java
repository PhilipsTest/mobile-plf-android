/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.appidentity;

import android.content.Context;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.ConfigValues;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationManager;
import com.philips.platform.appinfra.logging.LoggingInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AppIdentityTest {

    private AppIdentityInterface mAppIdentityManager = null;

    private AppInfra mAppInfra;

    private AppConfigurationInterface.AppConfigurationError configError;
    private AppConfigurationManager mConfigInterface;
    private AppIdentityInterface appIdentityInterface;
    private JSONObject result = null;
    private Context context;

    @Before
    public void setUp() throws Exception {
        context = getInstrumentation().getContext();
        assertNotNull(context);

        mAppInfra = new AppInfra.Builder().build(context);
        assertNotNull(mAppInfra);

        mConfigInterface = new AppConfigurationManager(mAppInfra) {
            @Override
            protected JSONObject getMasterConfigFromApp() {
                try {
                    String testJson = ConfigValues.testJson();
                    result = new JSONObject(testJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        };
        mAppInfra = new AppInfra.Builder().setConfig(mConfigInterface).build(context);

        mAppIdentityManager = new AppIdentityManager(mAppInfra);
        assertNotNull(mAppIdentityManager);
        configError = new AppConfigurationInterface
                .AppConfigurationError();

        assertNotNull(configError);
        appIdentityInterface = mAppInfra.getAppIdentity();
    }

    @Test
    public void testGetLocalizedAppName() {
        assertNotNull(mAppIdentityManager.getLocalizedAppName());
    }

    @Test
    public void testGetAppName() {
        try {
            assertNotNull(mAppIdentityManager.getAppName());
        } catch (IllegalArgumentException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppIdentity", e.getMessage());
        }
    }

    @Test
    public void testGetMicrositeId() {
        assertNotNull(mAppIdentityManager.getMicrositeId());
        assertEquals("77000", mAppIdentityManager.getMicrositeId());
        assertNotSame("3200", mAppIdentityManager.getMicrositeId());
    }

    @Test
    public void testGetSector() {
        assertNotNull(mAppIdentityManager.getSector());
        assertEquals("B2C", mAppIdentityManager.getSector());
        assertNotSame("test", mAppIdentityManager.getSector());
    }

    @Test
    public void testGetServiceDiscoveryEnvironment() {
        assertNotNull(mAppIdentityManager.getServiceDiscoveryEnvironment());
        assertEquals("STAGING", mAppIdentityManager.getServiceDiscoveryEnvironment());
        assertNotSame("development", mAppIdentityManager.getServiceDiscoveryEnvironment());
    }

    @Test
    public void testGetAppState() {
        assertNotNull(mAppIdentityManager.getAppState());
        assertEquals("STAGING", mAppIdentityManager.getAppState().toString());
        assertNotSame("NewState", mAppIdentityManager.getAppState().toString());
    }

    @Test
    public void testGetAppVersionRegularExpression() {
        try {
            assertNotSame("Appversion is not in proper format", mAppIdentityManager.getAppVersion(), "!!2.0");
        } catch (IllegalArgumentException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppIdentity", e.getMessage());
        }
    }

    @Test
    public void testAppState() {
        mAppInfra.getConfigInterface().setPropertyForKey("appidentity.appState", "appinfra",
                "Staging", configError);
        String defAppState = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
                ("appidentity.appState", "appinfra", configError);
        assertNotNull(defAppState);
        assertEquals("Appstate is staging", defAppState, "Staging");
        assertNotSame("AppState is Staging", "Testing", "Staging");
    }

    @Test
    public void testValidateAppState() {
        JSONObject appinfra;
        try {
            appinfra = result.getJSONObject("APPINFRA");
            appinfra.put("APPIDENTITY.APPSTATE", "DEVELOPMENT");
            overrideAppConfigValues(result);
            assertSame("DEVELOPMENT", mAppIdentityManager.getAppState().toString());
            assertEquals("DEVELOPMENT", mAppIdentityManager.getAppState().toString());
            appinfra.put("APPIDENTITY.APPSTATE", "TEST");
            assertEquals("TEST", mAppIdentityManager.getAppState().toString());
            appinfra.put("APPIDENTITY.APPSTATE", "ACCEPTANCE");
            assertEquals("ACCEPTANCE", mAppIdentityManager.getAppState().toString());
            appinfra.put("APPIDENTITY.APPSTATE", "PRODUCTION");
            assertEquals("PRODUCTION", mAppIdentityManager.getAppState().toString());
            appinfra.put("APPIDENTITY.APPSTATE", "XYZ");
            assertNull(mAppIdentityManager.getAppState().toString());
            appinfra.put("APPIDENTITY.APPSTATE", "");
            assertNull(mAppIdentityManager.getAppState().toString());
        } catch (JSONException | IllegalArgumentException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppIdentity", e.getMessage());
        }
    }

    @Test
    public void testValidateServiceDiscoveryEnv() {
        JSONObject appinfra;
        try {
            appinfra = result.getJSONObject("APPINFRA");
            appinfra.put("APPIDENTITY.SERVICEDISCOVERYENVIRONMENT", "PRODUCTION");
            overrideAppConfigValues(result);
            assertSame("PRODUCTION", mAppIdentityManager.getServiceDiscoveryEnvironment());
            appinfra.put("APPIDENTITY.SERVICEDISCOVERYENVIRONMENT", "TEST");
            assertNull(mAppIdentityManager.getServiceDiscoveryEnvironment());
            appinfra.put("APPIDENTITY.SERVICEDISCOVERYENVIRONMENT", "");
            assertNull(mAppIdentityManager.getAppState().toString());
        } catch (JSONException | IllegalArgumentException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppIdentity", e.getMessage());
        }
    }

    @Test
    public void testValidServiceDiscoveryEnv() {
        assertTrue(mAppIdentityManager.getServiceDiscoveryEnvironment().equalsIgnoreCase("PRODUCTION") ||
                mAppIdentityManager.getServiceDiscoveryEnvironment().equalsIgnoreCase("STAGING"));
    }

    @Test
    public void testInvalidServiceDiscoveryEnv() {
        assertFalse(mAppIdentityManager.getServiceDiscoveryEnvironment().equalsIgnoreCase("xyz") ||
                mAppIdentityManager.getServiceDiscoveryEnvironment().equalsIgnoreCase("fdf"));
    }

    @Test
    public void testValidateMicroSiteId() {
        JSONObject appinfra;
        try {
            appinfra = result.getJSONObject("APPINFRA");
            appinfra.put("APPIDENTITY.MICROSITEID", "77001");
            overrideAppConfigValues(result);
            assertSame("77001", mAppIdentityManager.getMicrositeId());
            appinfra.put("APPIDENTITY.MICROSITEID", "dsa$");
            assertNull(mAppIdentityManager.getMicrositeId());
            appinfra.put("APPIDENTITY.MICROSITEID", "");
            assertNull(mAppIdentityManager.getMicrositeId());
        } catch (JSONException | IllegalArgumentException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppIdentity", e.getMessage());
        }
    }

    @Test
    public void testValidateSector() {
        JSONObject appinfra;
        try {
            appinfra = result.getJSONObject("APPINFRA");
            appinfra.put("APPIDENTITY.SECTOR", "b2b");
            overrideAppConfigValues(result);
            assertSame("b2b", mAppIdentityManager.getSector());
            appinfra.put("APPIDENTITY.SECTOR", "b2b_Li");
            assertSame("b2b_Li", mAppIdentityManager.getSector());
            appinfra.put("APPIDENTITY.SECTOR", "b2b_HC");
            assertSame("b2b_HC", mAppIdentityManager.getSector());
            appinfra.put("APPIDENTITY.SECTOR", "xyz");
            assertNull(mAppIdentityManager.getSector());
            appinfra.put("APPIDENTITY.SECTOR", "");
            assertNull(mAppIdentityManager.getSector());
        } catch (JSONException | IllegalArgumentException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppIdentity", e.getMessage());
        }
    }

    @Test
    public void testValidAppVersion() {
        AppIdentityManagerHelper appIdentityManagerHelper = new AppIdentityManagerHelper(mAppInfra);
        assertFalse(appIdentityManagerHelper.isValidAppVersion("1.3"));
        assertFalse(appIdentityManagerHelper.isValidAppVersion("1"));
        assertFalse(appIdentityManagerHelper.isValidAppVersion("1.3ds"));
        assertFalse(appIdentityManagerHelper.isValidAppVersion("1.3.6$"));
        assertTrue(appIdentityManagerHelper.isValidAppVersion("1.3.5"));
        assertTrue(appIdentityManagerHelper.isValidAppVersion("1.2.3_rc1"));
        assertTrue(appIdentityManagerHelper.isValidAppVersion("1.2.3-rc1"));
    }

    private void overrideAppConfigValues(final JSONObject appConfig) {
        mConfigInterface = new AppConfigurationManager(mAppInfra) {
            @Override
            protected JSONObject getMasterConfigFromApp() {
                return appConfig;
            }
        };
        mAppInfra = new AppInfra.Builder().setConfig(mConfigInterface).build(context);
        mConfigInterface.resetConfig();
        mAppIdentityManager = mAppInfra.getAppIdentity();
    }
}
