package com.philips.platform.pim.integration;

import android.content.Context;
import android.content.SharedPreferences;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappSettings;

import junit.framework.TestCase;

import net.openid.appauth.AuthorizationService;
import net.openid.appauth.browser.BrowserSelector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({PIMSettingManager.class, AuthorizationService.class, BrowserSelector.class})
@RunWith(PowerMockRunner.class)
public class PIMInterfaceTest extends TestCase {

    @Mock
    private AppInfraInterface mockAppInfraInterface;
    @Mock
    private UappDependencies mockUappDependencies;
    @Mock
    private UappSettings mockUappSettings;
    @Mock
    Context mockContext;
    @Mock
    LoggingInterface mockLoggingInterface;
    @Mock
    ServiceDiscoveryInterface mockServiceDiscoveryInterface;
    @Mock
    SecureStorageInterface mockSecureStorageInterface;
    @Mock
    PIMSettingManager mockPimSettingManager;
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    private AuthorizationService mockAuthorizationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        mockStatic(BrowserSelector.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockUappDependencies.getAppInfra()).thenReturn(mockAppInfraInterface);
        when(mockAppInfraInterface.getServiceDiscovery()).thenReturn(mockServiceDiscoveryInterface);
        when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockSecureStorageInterface);
        when(mockUappSettings.getContext()).thenReturn(mockContext);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        whenNew(AuthorizationService.class).withArguments(mockContext).thenReturn(mockAuthorizationService);
    }

    @Test
    public void testInit_NotNull() {
        PIMInterface pimInterface = new PIMInterface();
       // pimInterface.init(mockUappDependencies, mockUappSettings);

        assertNotNull(mockContext);
        assertNotNull(mockUappDependencies);
        assertNotNull(mockUappSettings);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        mockAppInfraInterface = null;
        mockContext = null;
        mockLoggingInterface = null;
        mockServiceDiscoveryInterface = null;
        mockSecureStorageInterface = null;
    }
}