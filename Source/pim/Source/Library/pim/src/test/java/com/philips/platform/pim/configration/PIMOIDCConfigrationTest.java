package com.philips.platform.pim.configration;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pim.manager.PIMSettingManager;

import junit.framework.TestCase;

import net.openid.appauth.AuthorizationServiceConfiguration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({PIMOIDCConfigration.class, PIMSettingManager.class})
@RunWith(PowerMockRunner.class)
public class PIMOIDCConfigrationTest extends TestCase {


    @Mock
    private AppConfigurationInterface mockAppConfigurationInterface;
    @Mock
    private AppConfigurationInterface.AppConfigurationError mockAppConfigurationError;
    @Mock
    private PIMSettingManager mockPimSettingManager;

    private PIMOIDCConfigration pimoidcConfigration;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        AppInfraInterface mockAppInfraInterface = mock(AppInfraInterface.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockAppConfigurationInterface);
        whenNew(AppConfigurationInterface.AppConfigurationError.class).withNoArguments().thenReturn(mockAppConfigurationError);
        pimoidcConfigration = new PIMOIDCConfigration();
    }

    @Test
    public void testGetAuthorizationServiceConfiguration() {
        AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);
        PIMOIDCConfigration pimoidcConfigration = new PIMOIDCConfigration(mockAuthorizationServiceConfiguration);
        AuthorizationServiceConfiguration authServiceConfiguration = pimoidcConfigration.getAuthorizationServiceConfiguration();
        assertNotNull(authServiceConfiguration);
        assertSame(mockAuthorizationServiceConfiguration, authServiceConfiguration);
    }

    @Test
    public void testGetRSID() {
        when(mockAppConfigurationInterface.getPropertyForKey("rsids", "PIM", mockAppConfigurationError)).thenReturn("philipspimregistrationdev");
        String rsid = pimoidcConfigration.getrsID();
        assertNotNull(rsid);
        assertEquals("philipspimregistrationdev", rsid);
    }

    @Test
    public void testGetRSIDReturnsNull() {
        String rsid = pimoidcConfigration.getrsID();
        assertNull(rsid);
    }

    @Test
    public void testGetClientID() {
        when(mockAppConfigurationInterface.getPropertyForKey("clientId", "PIM", mockAppConfigurationError)).thenReturn("94e28300-565d-4110-8919-42dc4f817393");
        String clientId = pimoidcConfigration.getClientId();
        assertNotNull(clientId);
        assertEquals("94e28300-565d-4110-8919-42dc4f817393", clientId);
    }

    @Test
    public void testGetDefaultClientIDReturnsNull() {
        String clientId = pimoidcConfigration.getClientId();
        assertNull(clientId);
    }

    @Test
    public void testGetDefaultRedirectUrl() {
        when(mockAppConfigurationInterface.getPropertyForKey("redirectUri", "PIM", mockAppConfigurationError)).thenReturn("com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect");
        String redirectUrl = pimoidcConfigration.getRedirectUrl();
        assertNotNull(redirectUrl);
        assertEquals("com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect", redirectUrl);
    }

    @Test
    public void testGetRedirectUrlreturnsNull() {
        String redirectUrl = pimoidcConfigration.getRedirectUrl();
        assertNull(redirectUrl);
    }

    @Test
    public void testGetURClientID() {
        when(mockAppConfigurationInterface.getPropertyForKey("legacyClientId", "PIM", mockAppConfigurationError)).thenReturn("f2stykcygm7enbwfw2u9fbg6h6syb8yd");
        String urClientId = pimoidcConfigration.getLegacyClientID();
        assertNotNull(urClientId);
        assertEquals("f2stykcygm7enbwfw2u9fbg6h6syb8yd", urClientId);
    }

    public void testURClientIDReturnsNull() {
        String urClientId = pimoidcConfigration.getLegacyClientID();
        assertNull(urClientId);
    }

    @Test
    public void testGetMigrationClientID() {
        when(mockAppConfigurationInterface.getPropertyForKey("migrationClientId", "PIM", mockAppConfigurationError)).thenReturn("7602c06b-c547-4aae-8f7c-f89e8c887a21");
        String migrationClientId = pimoidcConfigration.getMigrationClientId();
        assertNotNull(migrationClientId);
        assertEquals("7602c06b-c547-4aae-8f7c-f89e8c887a21", migrationClientId);
    }

    @Test
    public void testGetAPIKey() {
        when(mockAppConfigurationInterface.getPropertyForKey("apiKey", "PIM", mockAppConfigurationError)).thenReturn("nYO1gXoy5J7AaHT8KPu2D9JxN2cZo77M8zdBD2iJ");
        String apiKey = pimoidcConfigration.getAPIKey();
        assertNotNull(apiKey);
        assertEquals("nYO1gXoy5J7AaHT8KPu2D9JxN2cZo77M8zdBD2iJ", apiKey);
    }

    @Test
    public void testGetAPIKeyReturnsNull() {
        String apiKey = pimoidcConfigration.getAPIKey();
        assertNull(apiKey);
    }

    @Test
    public void testGetMigrationClientIdNull() {
        String migrationClientId = pimoidcConfigration.getMigrationClientId();
        assertNull(migrationClientId);
    }

    @Test
    public void testGetMigrationRedirectUrl() {
        when(mockAppConfigurationInterface.getPropertyForKey("clientId", "PIM", mockAppConfigurationError)).thenReturn("94e28300-565d-4110-8919-42dc4f817393");
        when(mockAppConfigurationInterface.getPropertyForKey("redirectUri", "PIM", mockAppConfigurationError)).thenReturn("com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect");
        when(mockAppConfigurationInterface.getPropertyForKey("migrationClientId", "PIM", mockAppConfigurationError)).thenReturn("7602c06b-c547-4aae-8f7c-f89e8c887a21");
        String redirectUrl = pimoidcConfigration.getMigrationRedirectUrl();
        assertNotNull(redirectUrl);
        assertEquals("com.philips.apps.7602c06b-c547-4aae-8f7c-f89e8c887a21://oauthredirect", redirectUrl);
    }

    @Test
    public void testGetCustomClaims() {
        LoggingInterface mockLoggingInterface = mock(LoggingInterface.class);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        String jsonString = pimoidcConfigration.getCustomClaims();
        assertNotNull(jsonString);
        verify(mockLoggingInterface).log(DEBUG, "PIMOIDCConfigration", "PIM_KEY_CUSTOM_CLAIMS: " + jsonString);
    }

    @Test
    public void testGetMarketingOptinKey(){
        when(mockAppConfigurationInterface.getPropertyForKey("marketingOptinApiKey", "PIM", mockAppConfigurationError)).thenReturn("kwSyJKK3gg5NHSmjeq1OD5BBdyOCKtyK7XVIuq5I");
        String apiKey = pimoidcConfigration.getMarketingOptinAPIKey();
        assertNotNull(apiKey);
        assertEquals("kwSyJKK3gg5NHSmjeq1OD5BBdyOCKtyK7XVIuq5I", apiKey);
    }

    @After
    public void tearDown() throws Exception {
        mockAppConfigurationInterface = null;
    }
}