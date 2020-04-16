package com.philips.platform.pim.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.R;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.fragment.PIMFragment;
import com.philips.platform.pim.listeners.PIMAuthServiceConfigListener;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;

import junit.framework.TestCase;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.junit.After;
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
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({Uri.class, AuthorizationServiceConfiguration.class, PIMSettingManager.class, PIMAuthManager.class, AuthorizationResponse.class, AuthorizationRequest.Builder.class,
        AuthorizationRequest.class, AuthorizationException.class, PIMErrorEnums.class})
@RunWith(PowerMockRunner.class)
public class PIMAuthManagerTest extends TestCase {

    private PIMAuthManager pimAuthManager;

    @Mock
    AuthState mockAuthState;
    @Mock
    private AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration;
    @Mock
    private PIMAuthServiceConfigListener mockConfigurationListener;
    @Mock
    private LoggingInterface mockLoggingInterface;
    @Mock
    private AuthorizationServiceConfiguration.RetrieveConfigurationCallback mockConfigurationCallback;
    @Captor
    private ArgumentCaptor<AuthorizationServiceConfiguration.RetrieveConfigurationCallback> captorRetrieveConfigCallback;
    @Captor
    private ArgumentCaptor<AuthorizationService.TokenResponseCallback> captorTokenResponse;
    @Mock
    private AuthorizationService mockAuthorizationService;
    @Mock
    private Context mockContext;
    @Mock
    private TokenRequest mockTokenRequest;
    @Mock
    private PIMOIDCConfigration mockPimoidcConfigration;
    @Mock
    private Uri mockUri;
    @Mock
    private Intent mockIntent;
    @Mock
    private PIMTokenRequestListener mockPIMTokenRequestListener;
    @Mock
    private AuthorizationResponse mockAuthResponse;
    @Mock
    private AuthorizationException mockAuthException;
    @Captor
    private ArgumentCaptor<AuthState.AuthStateAction> captorAuthStateAction;

    private String baseurl = "https://stg.api.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login";

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockStatic(PIMErrorEnums.class);
        mockStatic(PIMSettingManager.class);
        mockStatic(AuthorizationResponse.class);
        mockStatic(AuthorizationException.class);
        mockStatic(AuthorizationServiceConfiguration.class);
        mockStatic(Uri.class);

        mockUri = mock(Uri.class);
        PIMSettingManager mockPimSettingManager = mock(PIMSettingManager.class);
        mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);

        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getPimOidcConfigration()).thenReturn(mockPimoidcConfigration);
        when(mockPimoidcConfigration.getAuthorizationServiceConfiguration()).thenReturn(mockAuthorizationServiceConfiguration);
        when(Uri.class, "parse", anyString()).thenReturn(mockUri);
        whenNew(AuthorizationService.class).withArguments(mockContext).thenReturn(mockAuthorizationService);

        pimAuthManager = new PIMAuthManager(mockContext);
    }

    @Test
    public void shouldFetchFromUrl_Verify_OnSuccess() throws AuthorizationServiceDiscovery.MissingArgumentException, JSONException {
        pimAuthManager.fetchAuthWellKnownConfiguration(baseurl, mockConfigurationListener);

        PowerMockito.verifyStatic(AuthorizationServiceConfiguration.class);
        AuthorizationServiceConfiguration.fetchFromUrl(ArgumentMatchers.any(Uri.class), captorRetrieveConfigCallback.capture());

        mockConfigurationCallback = captorRetrieveConfigCallback.getValue();
        mockConfigurationCallback.onFetchConfigurationCompleted(mockAuthorizationServiceConfiguration, null);

        verify(mockConfigurationListener).onAuthServiceConfigSuccess(mockAuthorizationServiceConfiguration);
        verify(mockLoggingInterface).log(DEBUG, PIMAuthManager.class.getSimpleName(), "fetchAuthWellKnownConfiguration : Configuration retrieved for  proceeding : " + mockAuthorizationServiceConfiguration);
    }


    @Test
    public void shouldFetchFromUrl_Verify_OnError() throws Exception {
        pimAuthManager.fetchAuthWellKnownConfiguration(baseurl, mockConfigurationListener);

        PowerMockito.verifyStatic(AuthorizationServiceConfiguration.class);
        AuthorizationServiceConfiguration.fetchFromUrl(ArgumentMatchers.any(Uri.class), captorRetrieveConfigCallback.capture());

        mockConfigurationCallback = captorRetrieveConfigCallback.getValue();
        AuthorizationException ex = new AuthorizationException(0, 2100, null, null, null, null);
        mockConfigurationCallback.onFetchConfigurationCompleted(mockAuthorizationServiceConfiguration, ex);
        verify(mockConfigurationListener).onAuthServiceConfigFailed(any(Error.class));
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

        whenNew(AuthorizationRequest.Builder.class).withArguments(eq(mockAuthorizationServiceConfiguration), anyString(), anyString(), eq(mockUri)).thenReturn(mockAuthReqBuilder);
        when(mockAuthReqBuilder.setScope(anyString())).thenReturn(mockAuthReqBuilder);
        when(mockAuthReqBuilder.setAdditionalParameters(anyMap())).thenReturn(mockAuthReqBuilder);
        when(mockAuthorizationService.getAuthorizationRequestIntent(mockAuthorizationRequest)).thenReturn(mockIntent);
        pimAuthManager = new PIMAuthManager(mockContext);
        Intent intent = pimAuthManager.getAuthorizationRequestIntent(mockAuthorizationRequest);
        assertEquals(mockIntent, intent);
    }

    @Test
    public void testPerformTokenRequest() throws Exception {
        when(AuthorizationResponse.fromIntent(mockIntent)).thenReturn(mockAuthResponse);
        when(AuthorizationException.fromIntent(mockIntent)).thenReturn(mockAuthException);
        whenNew(AuthState.class).withArguments(mockAuthResponse, mockAuthException).thenReturn(mockAuthState);
        when(mockAuthResponse.createTokenExchangeRequest()).thenReturn(mockTokenRequest);
        pimAuthManager = new PIMAuthManager(mockContext);
        pimAuthManager.performTokenRequestFromLogin(mockIntent, mockPIMTokenRequestListener);

        verify(mockAuthorizationService).performTokenRequest(eq(mockTokenRequest), captorTokenResponse.capture());
        AuthorizationService.TokenResponseCallback mockTokenResponseCallback = captorTokenResponse.getValue();

        TokenResponse mockTokenResponse = mock(TokenResponse.class);
        mockTokenResponseCallback.onTokenRequestCompleted(mockTokenResponse, null);
        verify(mockPIMTokenRequestListener).onTokenRequestSuccess();

        mockTokenResponseCallback.onTokenRequestCompleted(null, mockAuthException);
        verify(mockPIMTokenRequestListener).onTokenRequestFailed(any(Error.class));
    }

    @Test
    public void testCreateAuthReqUriForMigration() throws Exception {
        String id_token_hint = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ZjkyMDk5MC0wZThkLTQ5YTAtODg4OC0xZThjZGI3MjI1NTUiLCJpc3MiOiJodHRwczovL3BoaWxpcHMuZXZhbC5qYW5yYWluY2FwdHVyZS5jb20iLCJpYXQiOjE1NjMxOTMyMzksImp0aSI6IjQyYjY0OTA1LWYyMmUtNGU2OS05YTI5LWFhOTdkYWViOGQwNyIsImV4cCI6MTU2MzE5MzUzOTAwMCwiYXVkIjpbImI5MDZmMDljLTIyYTctNDQ5Yy1hZGNiLTNmMjJhYTFiZDcxYiJdfQ.JDfOmwTihN_9zFLFz9OyE2lGmgCmFK1Lb0TZJOrJQLuGYuFt3G81rlNJMsFuDchU4PXG1q4uM0n3zwyFITuF4g";
        Map<String, String> parameter = new HashMap<>();
        parameter.put("id_token_hint", id_token_hint);
        parameter.put("claims", PIMSettingManager.getInstance().getPimOidcConfigration().getCustomClaims());
        when(mockPimoidcConfigration.getMigrationClientId()).thenReturn("7602c06b-c547-4aae-8f7c-f89e8c887a21");
        when(mockPimoidcConfigration.getMigrationRedirectUrl()).thenReturn("com.philips.apps.7602c06b-c547-4aae-8f7c-f89e8c887a21://oauthredirect");
        mockStatic(AuthorizationRequest.Builder.class);
        AuthorizationRequest.Builder mockAuthReqBuilder = mock(AuthorizationRequest.Builder.class);
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        when(mockAuthReqBuilder.build()).thenReturn(mockAuthorizationRequest);

        whenNew(AuthorizationRequest.Builder.class).withArguments(eq(mockAuthorizationServiceConfiguration), anyString(), anyString(), eq(mockUri)).thenReturn(mockAuthReqBuilder);
        when(mockAuthReqBuilder.setScope(anyString())).thenReturn(mockAuthReqBuilder);
        when(mockAuthReqBuilder.setAdditionalParameters(anyMap())).thenReturn(mockAuthReqBuilder);
        when(mockAuthReqBuilder.setPrompt("none")).thenReturn(mockAuthReqBuilder);
        pimAuthManager.createAuthRequestUriForMigration(parameter);
    }

    @Test
    public void testIsAuthorizeSuccessResponseExceptionNull() {
        boolean authorizationSuccess = pimAuthManager.isAuthorizationSuccess(mockIntent);
        assertFalse(authorizationSuccess);
    }

    @Test
    public void testIsAuthorizeSuccessExceptionNull() {
        when(AuthorizationResponse.fromIntent(mockIntent)).thenReturn(mockAuthResponse);
        boolean authorizationSuccess = pimAuthManager.isAuthorizationSuccess(mockIntent);
        assertTrue(authorizationSuccess);
    }

    @Test
    public void testIsAuthorizeSuccessResponseNull() {
        when(AuthorizationException.fromIntent(mockIntent)).thenReturn(mockAuthException);
        boolean authorizationSuccess = pimAuthManager.isAuthorizationSuccess(mockIntent);
        assertFalse(authorizationSuccess);
    }

    @Test
    public void testPerformTokenRequestForMigrationFailedWithAuthResponseNull() throws Exception {
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        AuthorizationResponse.Builder mockBuilder = mock(AuthorizationResponse.Builder.class);
        whenNew(AuthorizationResponse.Builder.class).withArguments(mockAuthorizationRequest).thenReturn(mockBuilder);
        when(mockBuilder.fromUri(mockUri)).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockAuthResponse);
        when(AuthorizationException.fromOAuthRedirect(mockUri)).thenReturn(mockAuthException);

        pimAuthManager.performTokenRequestFromLogin(mockAuthorizationRequest, anyString(), mockPIMTokenRequestListener);
        verify(mockPIMTokenRequestListener).onTokenRequestFailed(any(Error.class));
    }

    @Test
    public void testPerformTokenRequestForMigration() throws Exception {
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        AuthorizationResponse.Builder mockBuilder = mock(AuthorizationResponse.Builder.class);
        whenNew(AuthorizationResponse.Builder.class).withArguments(mockAuthorizationRequest).thenReturn(mockBuilder);
        when(mockBuilder.fromUri(mockUri)).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockAuthResponse);
        Whitebox.setInternalState(mockAuthResponse, "authorizationCode", "Jzp0FNeInMgznx-X");
        AuthorizationException authorizationException = null;
        whenNew(AuthState.class).withArguments(mockAuthResponse, authorizationException).thenReturn(mockAuthState);
        when(mockAuthResponse.createTokenExchangeRequest()).thenReturn(mockTokenRequest);

        pimAuthManager.performTokenRequestFromLogin(mockAuthorizationRequest, anyString(), mockPIMTokenRequestListener);
        verify(mockAuthorizationService).performTokenRequest(eq(mockTokenRequest), captorTokenResponse.capture());
        AuthorizationService.TokenResponseCallback responseCallback = captorTokenResponse.getValue();

        TokenResponse mockTokenResponse = mock(TokenResponse.class);
        responseCallback.onTokenRequestCompleted(mockTokenResponse, null);

        responseCallback.onTokenRequestCompleted(null, mockAuthException);
    }

    @Test
    public void testRefreshToken() {
        String accessToken = "vsu46sctqqpjwkbn";
        String idToken = "id_token";
        pimAuthManager.refreshToken(mockAuthState, mockPIMTokenRequestListener);
        verify(mockAuthState).performActionWithFreshTokens(eq(mockAuthorizationService), captorAuthStateAction.capture());
        AuthState.AuthStateAction stateAction = captorAuthStateAction.getValue();
        stateAction.execute(accessToken, idToken, null);
        verify(mockPIMTokenRequestListener).onTokenRequestSuccess();

        stateAction.execute(accessToken, idToken, mockAuthException);
        verify(mockPIMTokenRequestListener).onTokenRequestFailed(any(Error.class));
    }

    @Test
    public void testGetAuthState() {
        Whitebox.setInternalState(pimAuthManager, "mAuthState", mockAuthState);
        AuthState authState = pimAuthManager.getAuthState();
        assertSame(mockAuthState, authState);
    }

    @After
    public void tearDown() throws Exception {
        pimAuthManager = null;
        mockConfigurationListener = null;
        mockConfigurationCallback = null;
    }
}