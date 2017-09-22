/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.microapp;

import com.philips.cdp.dicommclient.discovery.DiscoveryManager;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EWSDependencyProviderTest {

    private EWSDependencyProvider dependencyProvider;

    @Mock
    private DiscoveryManager discoveryManagerMock;

    @Mock
    private AppInfraInterface appInfraInterfaceMock;

    @Mock
    private LoggingInterface loggingInterfaceMock;

    @Mock
    private AppTaggingInterface taggingInterfaceMock;

    private Map<String, String> productKeyMap;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(appInfraInterfaceMock.getTagging()).thenReturn(taggingInterfaceMock);
        when(appInfraInterfaceMock.getTagging().createInstanceForComponent(anyString(), anyString())).thenReturn(taggingInterfaceMock);

        when(appInfraInterfaceMock.getLogging()).thenReturn(loggingInterfaceMock);
        when(appInfraInterfaceMock.getLogging().createInstanceForComponent(anyString(), anyString())).thenReturn(loggingInterfaceMock);

        dependencyProvider = EWSDependencyProvider.getInstance();
        productKeyMap = new HashMap<>();
        productKeyMap.put(EWSInterface.PRODUCT_NAME, "product");
    }

    @Test
    public void shouldEnsureAllDependenciesAreInitialized() throws Exception {
        dependencyProvider.initDependencies(appInfraInterfaceMock, discoveryManagerMock, productKeyMap);

        assertTrue(dependencyProvider.areDependenciesInitialized());
        assertNotNull(dependencyProvider.getAppInfra());
        assertNotNull(dependencyProvider.getLoggerInterface());
        assertNotNull(dependencyProvider.getTaggingInterface());
        assertNotNull(dependencyProvider.getDiscoveryManager());
        assertNotNull(dependencyProvider.getProductName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMapDoesNotContainProductName() throws Exception {
        dependencyProvider.initDependencies(appInfraInterfaceMock, discoveryManagerMock, new HashMap<String, String>());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfProductNameCalledWithoutInitialization() throws Exception {
        dependencyProvider.getProductName();
    }

    @Test
    public void shouldClearAllDependenciesWhenAsked() throws Exception {
        dependencyProvider.clear();

        assertFalse(dependencyProvider.areDependenciesInitialized());
        assertNull(dependencyProvider.getAppInfra());
        assertNull(dependencyProvider.getDiscoveryManager());

    }

}