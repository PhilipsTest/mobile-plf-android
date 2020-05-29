package com.philips.platform.pim.utilities;

import android.net.Uri;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;

import junit.framework.TestCase;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({ AuthorizationRequest.class})
@RunWith(PowerMockRunner.class)
public class PIMSecureStorageHelperTest extends TestCase {

    @Mock
    private AppInfraInterface mockAppInfraInterface;
    @Mock
    private SecureStorageInterface mockSecureStorageInterface;

    private PIMSecureStorageHelper pimSecureStorageHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockSecureStorageInterface);
        pimSecureStorageHelper = new PIMSecureStorageHelper(mockAppInfraInterface);
    }

    @Test
    public void testSaveAuthorizationRequest() {
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        boolean isStored = pimSecureStorageHelper.saveAuthorizationRequest(mockAuthorizationRequest);
        assertFalse(isStored);

        when(mockAuthorizationRequest.jsonSerializeString()).thenReturn("authRequest");
        when(mockSecureStorageInterface.storeValueForKey(eq("AUTHORIZATION_REQUEST"),anyString(),any(SecureStorageInterface.SecureStorageError.class))).thenReturn(true);
        boolean resAuth = pimSecureStorageHelper.saveAuthorizationRequest(mockAuthorizationRequest);
        assertTrue(resAuth);
    }

    @Test
    public void testGetAuthorizationRequest() throws JSONException {
        String authResponse = "com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect?code=2qcS7-xDXlkbbCEV&state=NXKhU1Ygk72QmG7SMwQznQ";
        AuthorizationRequest authorizationRequest = pimSecureStorageHelper.getAuthorizationRequest();
        assertNull(authorizationRequest);

        mockStatic(AuthorizationRequest.class);
        AuthorizationRequest mockAuthRequest = mock(AuthorizationRequest.class);
        when(AuthorizationRequest.jsonDeserialize(authResponse)).thenReturn(mockAuthRequest);
        when(mockSecureStorageInterface.fetchValueForKey(eq("AUTHORIZATION_REQUEST"),any(SecureStorageInterface.SecureStorageError.class))).thenReturn(authResponse);
        AuthorizationRequest authRequest = pimSecureStorageHelper.getAuthorizationRequest();
        assertNotNull(authRequest);
    }

    @Test
    public void testSaveAuthorizationResponse() {
        String authResponse = "com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect?code=2qcS7-xDXlkbbCEV&state=NXKhU1Ygk72QmG7SMwQznQ";
        mockStatic(AuthorizationRequest.class);
        boolean resp = pimSecureStorageHelper.saveAuthorizationResponse(authResponse);
        assertFalse(resp);

        when(mockSecureStorageInterface.storeValueForKey(eq("AUTHORIZATION_RESPONSE"),eq(authResponse),any(SecureStorageInterface.SecureStorageError.class))).thenReturn(true);
        boolean respAuth = pimSecureStorageHelper.saveAuthorizationResponse(authResponse);
        assertTrue(respAuth);
    }

    @Test
    public void testGetAuthorizationResponse() {
        String authResponse = "com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect?code=2qcS7-xDXlkbbCEV&state=NXKhU1Ygk72QmG7SMwQznQ";
        String authorizationResponse = pimSecureStorageHelper.getAuthorizationResponse();
        assertNull(authorizationResponse);

        when(mockSecureStorageInterface.fetchValueForKey(eq("AUTHORIZATION_RESPONSE"),any(SecureStorageInterface.SecureStorageError.class))).thenReturn(authResponse);
        String authRequest = pimSecureStorageHelper.getAuthorizationResponse();
        assertNotNull(authRequest);
    }

    @Test
    public void deleteAuthorizationResponse() {
        boolean isDeleted = pimSecureStorageHelper.deleteAuthorizationResponse();
        assertFalse(isDeleted);
        when(mockSecureStorageInterface.removeValueForKey("AUTHORIZATION_RESPONSE")).thenReturn(true);
        isDeleted = pimSecureStorageHelper.deleteAuthorizationResponse();
        assertTrue(isDeleted);
    }
}