package com.philips.platform.pim.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pim.R;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.fragment.PIMFragment;
import com.philips.platform.pim.listeners.PIMAuthorizationServiceConfigurationListener;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.utilities.PIMConstants;

import junit.framework.TestCase;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.TokenRequest;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Serializable;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({Uri.class, AuthorizationServiceConfiguration.class, PIMSettingManager.class, PIMAuthManager.class, AuthorizationResponse.class,AuthorizationRequest.Builder.class,AuthorizationRequest.class})
@RunWith(PowerMockRunner.class)
public class PIMAuthManagerTest extends TestCase {

    private PIMAuthManager pimAuthManager;


    private AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration;

    @Mock
    private PIMAuthorizationServiceConfigurationListener mockConfigurationListener;
    @Mock
    private AuthorizationServiceDiscovery mockAuthorizationServiceDiscovery;
    @Mock
    private PIMSettingManager mockPimSettingManager;
    @Mock
    private LoggingInterface mockLoggingInterface;

    @Mock
    private AuthorizationServiceConfiguration.RetrieveConfigurationCallback mockConfigurationCallback;
    @Captor
    private ArgumentCaptor<AuthorizationServiceConfiguration.RetrieveConfigurationCallback> captorRetrieveConfigCallback;

    @Mock
    private AuthorizationService.TokenResponseCallback mockTokenResponseCallback;
    @Captor
    private ArgumentCaptor<AuthorizationService.TokenResponseCallback> captorTokenResponse;
    @Mock
    private AuthorizationService mockAuthorizationService;
    @Mock
    private AuthorizationResponse mockAuthorizationResponse;
    @Mock
    private AuthorizationException mockAuthorizationException;
    @Mock
    private Context mockContext;
    @Mock
    private TokenRequest mockTokenRequest;
    @Mock
    private PIMOIDCConfigration mockPimoidcConfigration;
    @Mock
    private Bundle mockBundle;
    @Mock
    private Uri mockUri;
    @Mock
    private Intent mockIntent;
    @Mock
    private PIMLoginListener mockPIMLoginListener;
    @Captor
    ArgumentCaptor<PIMLoginListener> captorLoginListener;


    private String baseurl = "https://stg.api.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login";

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getPimOidcConfigration()).thenReturn(mockPimoidcConfigration);
        when(mockPimoidcConfigration.getClientId()).thenReturn("9317be6b-193f-4187-9ec2-5e1802a8d8ad");

        mockStatic(AuthorizationServiceConfiguration.class);
        mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);
        when(mockPimoidcConfigration.getAuthorizationServiceConfiguration()).thenReturn(mockAuthorizationServiceConfiguration);
        mockStatic(Uri.class);
        mockUri = mock(Uri.class);
        when(Uri.class, "parse", anyString()).thenReturn(mockUri);

        whenNew(AuthorizationService.class).withArguments(mockContext).thenReturn(mockAuthorizationService);

        pimAuthManager = new PIMAuthManager();
    }

    @Test
    public void shouldFetchFromUrl_Verify_OnSuccess() throws AuthorizationServiceDiscovery.MissingArgumentException, JSONException {
        pimAuthManager.fetchAuthWellKnownConfiguration(baseurl, mockConfigurationListener);

        PowerMockito.verifyStatic(AuthorizationServiceConfiguration.class);
        AuthorizationServiceConfiguration.fetchFromUrl(ArgumentMatchers.any(Uri.class), captorRetrieveConfigCallback.capture());

        mockConfigurationCallback = captorRetrieveConfigCallback.getValue();
        mockConfigurationCallback.onFetchConfigurationCompleted(mockAuthorizationServiceConfiguration, null);

        verify(mockConfigurationListener).onSuccess(mockAuthorizationServiceConfiguration);
        verify(mockLoggingInterface).log(DEBUG, PIMAuthManager.class.getSimpleName(), "fetchAuthWellKnownConfiguration : Configuration retrieved for  proceeding : " + mockAuthorizationServiceConfiguration);
    }


    @Test
    public void shouldFetchFromUrl_Verify_OnError() throws AuthorizationServiceDiscovery.MissingArgumentException, JSONException {
        pimAuthManager.fetchAuthWellKnownConfiguration(baseurl, mockConfigurationListener);

        PowerMockito.verifyStatic(AuthorizationServiceConfiguration.class);
        AuthorizationServiceConfiguration.fetchFromUrl(ArgumentMatchers.any(Uri.class), captorRetrieveConfigCallback.capture());

        mockConfigurationCallback = captorRetrieveConfigCallback.getValue();
        AuthorizationException ex = new AuthorizationException(0, 0, null, null, null, null);
        mockConfigurationCallback.onFetchConfigurationCompleted(mockAuthorizationServiceConfiguration, ex);
        verify(mockConfigurationListener).onError(ex.getMessage());
        verify(mockLoggingInterface).log(DEBUG, PIMAuthManager.class.getSimpleName(), "fetchAuthWellKnownConfiguration : Failed to retrieve configuration for : " + ex.getMessage());
    }

    @Test
    public void shouldGetAuthorizationRequestIntent() throws Exception {
        PIMFragment mockPimFragment = mock(PIMFragment.class);

        when(mockPimFragment.getContext()).thenReturn(mockContext);
        when(mockContext.getString(R.string.redirectURL)).thenReturn("");

        mockStatic(AuthorizationRequest.Builder.class);
        AuthorizationRequest.Builder mockAuthReqBuilder = mock(AuthorizationRequest.Builder.class);
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        when(mockAuthReqBuilder.build()).thenReturn(mockAuthorizationRequest);

        Serializable mockSerializable = mock(Serializable.class);
        when(mockBundle.getSerializable(PIMConstants.PIM_KEY_CUSTOM_CLAIMS)).thenReturn(mockSerializable);

        whenNew(AuthorizationRequest.Builder.class).withArguments(eq(mockAuthorizationServiceConfiguration), anyString(),anyString(),eq(mockUri)).thenReturn(mockAuthReqBuilder);
        when(mockAuthReqBuilder.setScope(anyString())).thenReturn(mockAuthReqBuilder);
        when(mockAuthorizationService.getAuthorizationRequestIntent(mockAuthorizationRequest)).thenReturn(mockIntent);

        Intent intent = pimAuthManager.getAuthorizationRequestIntent(mockContext,mockAuthorizationServiceConfiguration,new String(),mockBundle);
        assertEquals(mockIntent, intent);
    }

    @Test
    public void getAuthorizationRequestIntent_ContextNull() {
        Intent intent = pimAuthManager.getAuthorizationRequestIntent(null,mockAuthorizationServiceConfiguration,new String(),mockBundle);
        assertEquals(null,intent);
    }

    @Test
    public void getAuthorizationRequestIntent_AuthServiceConfigurationNull() {
        Intent intent = pimAuthManager.getAuthorizationRequestIntent(mockContext,null,new String(),mockBundle);
        assertEquals(null,intent);
    }

    @Test
    public void getAuthorizationRequestIntent_ClientIdNull() {
        Intent intent = pimAuthManager.getAuthorizationRequestIntent(mockContext,mockAuthorizationServiceConfiguration,null,mockBundle);
        assertEquals(null,intent);
    }

    @Test
    public void shouldPerformTokenRequest(){
        AuthorizationException authorizationException = AuthorizationException.fromIntent(mockIntent);
        AuthorizationException mock = mock(AuthorizationException.class);

        when(AuthorizationResponse.fromIntent(mockIntent)).thenReturn(mockAuthorizationResponse);
        when(AuthorizationException.fromIntent(mockIntent)).thenReturn(mock);
        pimAuthManager.performTokenRequest(mockContext,mockIntent,mockPIMLoginListener);
    }

    public void tearDown() throws Exception {
        pimAuthManager = null;
        mockConfigurationListener = null;
        mockConfigurationCallback = null;
    }
}