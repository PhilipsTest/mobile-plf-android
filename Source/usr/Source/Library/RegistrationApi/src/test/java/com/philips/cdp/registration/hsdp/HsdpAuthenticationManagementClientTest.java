package com.philips.cdp.registration.hsdp;

import com.philips.cdp.registration.configuration.HSDPConfiguration;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.Silent.class)
public class HsdpAuthenticationManagementClientTest extends TestCase {
    private HsdpAuthenticationManagementClient hsdpAuthenticationManagementClient;
    @Mock
    HSDPConfiguration hsdpConfiguration;
    @Mock
    HsdpRequestClient hsdpRequestClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(hsdpConfiguration.getHsdpBaseUrl()).thenReturn("apiBaseUrl");
        Mockito.when(hsdpConfiguration.getHsdpAppName()).thenReturn("dhpApplicationName");
        Mockito.when(hsdpConfiguration.getHsdpSecretId()).thenReturn("signingKey");
        Mockito.when(hsdpConfiguration.getHsdpSharedId()).thenReturn("signingSecret");
        hsdpAuthenticationManagementClient = new HsdpAuthenticationManagementClient(hsdpConfiguration);

    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void shouldReturnHsdpAuthenticationResponse() {
        String apiEndpoint = "/authentication/login/social";
        String queryParams = "applicationName=" + hsdpConfiguration.getHsdpAppName();
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("accessToken", "accessToken");
        headers.put("refreshSecret", "secretKey");
        headers.put("Api-version", "2");
        Map<String, String> body = new LinkedHashMap<String, String>();
        body.put("loginId", "xyz@gmail.com");
        hsdpAuthenticationManagementClient.loginSocialProviders("xyz@gmail.com", "accessToken", "secretKey");
        Mockito.verify(hsdpRequestClient).sendSignedRequestForSocialLogin("POST", apiEndpoint, queryParams, headers, body);
    }

    @Test
    public void shouldReturnHsdpResponse_OnLogout(){
        String apiEndpoint = "/authentication/users/" + "xyz@gmail.com" + "/logout";
        String queryParams = "applicationName=" + hsdpConfiguration.getHsdpAppName();
        Map<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("accessToken", "accessToken");
        hsdpAuthenticationManagementClient.logout("xyz@gmail.com","accessToken");
    }

    @Test
    public void testSign() throws Exception {
        Method method = null;
        String s = "sample";
        Map<String, String> headers = new HashMap<>();
        Map<String, Object> rawResponse = new HashMap<>();
        HsdpResponse dhpResponse = new HsdpResponse(rawResponse);

        method = HsdpAuthenticationManagementClient.class.getDeclaredMethod("getDhpAuthenticationResponse", HsdpResponse.class);
        method.setAccessible(true);
        method.invoke(hsdpAuthenticationManagementClient, dhpResponse);
        dhpResponse = null;
        method.invoke(hsdpAuthenticationManagementClient, dhpResponse);
    }

    @Test
    public void testGetUTCdatetimeAsString() throws Exception {
        Method method = null;
        method = HsdpAuthenticationManagementClient.class.getDeclaredMethod("getUTCdatetimeAsString");
        method.setAccessible(true);
        method.invoke(hsdpAuthenticationManagementClient);
    }
}