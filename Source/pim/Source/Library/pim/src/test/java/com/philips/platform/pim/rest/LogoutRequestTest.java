package com.philips.platform.pim.rest;

import junit.framework.TestCase;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Map;

import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
public class LogoutRequestTest extends TestCase {

    private LogoutRequest logoutRequest;

    @Mock
    private AuthState mockAuthState;

    private final String logoutEndPoint = "https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login";

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        AuthorizationResponse mockAuthorizationResponse = mock(AuthorizationResponse.class);
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);
        AuthorizationServiceDiscovery mockAuthorizationServiceDiscovery = mock(AuthorizationServiceDiscovery.class);

        Mockito.when(mockAuthState.getLastAuthorizationResponse()).thenReturn(mockAuthorizationResponse);
        Whitebox.setInternalState(mockAuthorizationResponse, "request", mockAuthorizationRequest);
        Whitebox.setInternalState(mockAuthorizationRequest, "configuration", mockAuthorizationServiceConfiguration);
        Whitebox.setInternalState(mockAuthorizationServiceConfiguration, "discoveryDoc", mockAuthorizationServiceDiscovery);
        Mockito.when(mockAuthorizationServiceDiscovery.getIssuer()).thenReturn(logoutEndPoint);

        logoutRequest = new LogoutRequest(mockAuthState, "7602c06b-c547-4aae-8f7c-f89e8c887a21");
    }

    @Test
    public void testLogoutRequest() {
        String expEndpoint = logoutEndPoint + "/token/revoke";
        String url = logoutRequest.getUrl();
        assertEquals(url, expEndpoint);
    }

    @Test
    public void testGetHeader() {
        Map<String, String> header = logoutRequest.getHeader();
        int size = header.size();
        assertEquals(size, 1);
    }

    @Test
    public void testGetBody() {
        String body = logoutRequest.getBody();
        assertNotNull(body);
    }

    @Test
    public void testGetmehtodType(){
        int methodType = logoutRequest.getMethodType();
        assertSame(methodType,PIMRequest.Method.POST);
    }

    public void tearDown() throws Exception {
    }
}