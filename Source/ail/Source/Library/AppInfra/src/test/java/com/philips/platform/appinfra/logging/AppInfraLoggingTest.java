/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.logging;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.ConfigValues;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.appinfra.logging.model.AILCloudLogMetaData;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class AppInfraLoggingTest {

    private AppInfraLogging appInfraLogging;

    @Mock
    private Context contextMock;

    @Mock
    private AppInfraInterface appInfraMock;

    @Mock
    private ApplicationInfo applicationInfoMock;

    @Mock
    private Logger loggerMock;

    @Mock
    private AppConfigurationInterface appConfigurationInterfaceMock;

    @Mock
    private
    AILCloudLogMetaData ailCloudLogMetaData;

    @Mock
    private LoggingConfiguration loggingConfigurationMock;

    private Object[] params;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        params = new Object[4];
        ailCloudLogMetaData=new AILCloudLogMetaData();
        configureMocks();
        HashMap hashMap = new HashMap();
        hashMap.put("logging.debugConfig", true);
        when(loggingConfigurationMock.getLoggingProperties()).thenReturn(hashMap);
        appInfraLogging = new AppInfraLogging(appInfraMock, null, null) {
            @Override
            protected Logger getJavaLogger(String componentId, String componentVersion) {
                return loggerMock;
            }

            @Override
            public AILCloudLogMetaData createAilCloudLogInstance() {
                return ailCloudLogMetaData;
            }

            @NonNull
            @Override
            Object[] getParamObjects() {
                return params;
            }
        };

    }

    private void configureMocks() {
        when(appInfraMock.getAppInfraContext()).thenReturn(contextMock);
        when(appInfraMock.getAppInfraContext().getApplicationInfo()).thenReturn(applicationInfoMock);
        when(loggingConfigurationMock.getAppInfra()).thenReturn(appInfraMock);
        when(appConfigurationInterfaceMock.getPropertyForKey(anyString(), anyString(), any(AppConfigurationInterface.AppConfigurationError.class))).thenReturn(ConfigValues.getMockResponse());
        when(appInfraMock.getConfigInterface()).thenReturn(appConfigurationInterfaceMock);
        AppIdentityInterface appIdentityInterface = mock(AppIdentityInterface.class);
        when(appIdentityInterface.getAppName()).thenReturn("uGrow");
        when(appIdentityInterface.getAppVersion()).thenReturn("1.0.0");
        when(appIdentityInterface.getAppState()).thenReturn(AppIdentityInterface.AppState.ACCEPTANCE);
        when(appInfraMock.getAppIdentity()).thenReturn(appIdentityInterface);
    }

    @Test
    public void testLoggingEndToEnd(){
        appInfraLogging.log(LoggingInterface.LogLevel.DEBUG, "some_event", "event_message");
        appInfraLogging.log(LoggingInterface.LogLevel.ERROR, "some_event", "event_message");
        appInfraLogging.log(LoggingInterface.LogLevel.INFO, "some_event", "event_message");
        appInfraLogging.log(LoggingInterface.LogLevel.VERBOSE, "some_event", "event_message");
        appInfraLogging.log(LoggingInterface.LogLevel.WARNING, "some_event", "event_message");
        verify(loggerMock).log(Level.WARNING, "some_event", params);
        assertEquals(params[0], "event_message");
        assertNull(params[3]);
    }

    @Test
    public void testNullCheck() {
        assertNotNull(appInfraLogging.getAilCloudLogMetaData());
        assertNotNull(appInfraLogging.getParamObjects());
        assertNotNull(appInfraLogging.getJavaLogger("componentId","componentVersion"));
    }

    @Test
    public void testUpdatingModel() {
        AppTaggingInterface appTaggingInterface = mock(AppTaggingInterface.class);
        InternationalizationInterface internationalizationInterface = mock(InternationalizationInterface.class);
        ServiceDiscoveryInterface serviceDiscoveryInterface = mock(ServiceDiscoveryInterface.class);
        when(appTaggingInterface.getTrackingIdentifier()).thenReturn("TaggingIdentifier");
        when(internationalizationInterface.getUILocaleString()).thenReturn("locale");
        when(serviceDiscoveryInterface.getHomeCountry()).thenReturn("en");
        when(appInfraMock.getTagging()).thenReturn(appTaggingInterface);
        when(appInfraMock.getInternationalization()).thenReturn(internationalizationInterface);
        when(appInfraMock.getServiceDiscovery()).thenReturn(serviceDiscoveryInterface);
        appInfraLogging.setHSDPUserUUID("uuid");
        assertEquals(ailCloudLogMetaData.getUserUUID(),"uuid");
        appInfraLogging.updateMetadata(appInfraMock);
        assertEquals(ailCloudLogMetaData.getAppName(),"uGrow");
        assertEquals(ailCloudLogMetaData.getAppVersion(),"1.0.0");
        assertEquals(ailCloudLogMetaData.getAppState(),"ACCEPTANCE");
        assertEquals(ailCloudLogMetaData.getAppId(),"TaggingIdentifier");
        assertEquals(ailCloudLogMetaData.getLocale(),"locale");
        assertEquals(ailCloudLogMetaData.getHomeCountry(),"en");
        ConsentManagerInterface consentManagerInterface = mock(ConsentManagerInterface.class);
        when(appInfraMock.getConsentManager()).thenReturn(consentManagerInterface);
        when(loggingConfigurationMock.isCloudLogEnabled()).thenReturn(true);
        appInfraLogging.onAppInfraInitialised(appInfraMock);
        assertEquals(ailCloudLogMetaData.getAppName(),"uGrow");
        assertEquals(ailCloudLogMetaData.getAppVersion(),"1.0.0");
        assertEquals(ailCloudLogMetaData.getAppState(),"ACCEPTANCE");
        assertEquals(ailCloudLogMetaData.getAppId(),"TaggingIdentifier");
        assertEquals(ailCloudLogMetaData.getLocale(),"locale");
        assertEquals(ailCloudLogMetaData.getHomeCountry(),"en");
    }


}