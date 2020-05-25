package com.philips.platform.pim.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.adobe.mobile.Analytics;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorage;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.PIMParameterToLaunchEnum;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;
import com.philips.platform.pim.listeners.PIMUserMigrationListener;
import com.philips.platform.pim.listeners.PIMUserProfileDownloadListener;
import com.philips.platform.pim.utilities.PIMSecureStorageHelper;

import junit.framework.TestCase;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({Uri.class, Analytics.class, PIMSettingManager.class, AuthorizationRequest.class, AuthorizationRequest.Builder.class, AuthorizationServiceConfiguration.class, AuthorizationResponse.class,
        AuthorizationException.class, PIMAuthManager.class, PIMErrorEnums.class,PIMLoginManager.class})
@RunWith(PowerMockRunner.class)
public class PIMLoginManagerTest extends TestCase {

    private PIMLoginManager pimLoginManager;

    @Mock
    private AuthState mockAuthState;
    @Mock
    private Intent mockIntent;
    @Mock
    private PIMAuthManager mockAuthManager;
    @Mock
    private PIMUserManager mockUserManager;
    @Mock
    AppTaggingInterface mockTaggingInterface;
    @Mock
    private PIMLoginListener mockPimLoginListener;
    @Mock
    private AppInfraInterface mockAppInfraInterface;
    @Captor
    private ArgumentCaptor<PIMTokenRequestListener> listenerArgumentCaptor;
    @Captor
    private ArgumentCaptor<PIMUserProfileDownloadListener> userProfileDwnldLstnrCaptor;
    @Mock
    private AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration;
    @Mock
    private PIMOIDCConfigration mockPimoidcConfigration;
    @Mock
    private AppConfigurationInterface.AppConfigurationError mockAppConfigurationError;

    private String redirectrURI = "com.philips.apps.94e28300-565d-4110-8919-42dc4f817393://oauthredirect";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        mockStatic(Analytics.class);
        mockStatic(PIMErrorEnums.class);
        HashMap consentParameterMap = new HashMap<PIMParameterToLaunchEnum, Object>();
        consentParameterMap.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, true);
        consentParameterMap.put(PIMParameterToLaunchEnum.PIM_ANALYTICS_CONSENT, true);
        AuthorizationService mockAuthorizationService = mock(AuthorizationService.class);
        LoggingInterface mockLoggingInterface = mock(LoggingInterface.class);
        PIMSettingManager mockPimSettingManager = mock(PIMSettingManager.class);
        Context mockContext = mock(Context.class);
        AppConfigurationInterface mockAppConfigurationInterface = mock(AppConfigurationInterface.class);

        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getPimUserManager()).thenReturn(mockUserManager);
        when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockAppConfigurationInterface);
        when(mockAppInfraInterface.getSecureStorage()).thenReturn(mock(SecureStorageInterface.class));
        whenNew(AppConfigurationInterface.AppConfigurationError.class).withNoArguments().thenReturn(mockAppConfigurationError);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        when(mockPimSettingManager.getLocale()).thenReturn("en-US");
        when(mockPimSettingManager.getTaggingInterface()).thenReturn(mockTaggingInterface);
        when(mockTaggingInterface.getPrivacyConsent()).thenReturn(AppTaggingInterface.PrivacyStatus.OPTIN);
        when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockAppConfigurationInterface);
        String customeClaims = new PIMOIDCConfigration().getCustomClaims();
        when(mockPimoidcConfigration.getCustomClaims()).thenReturn(customeClaims);
        when(mockPimoidcConfigration.getAuthorizationServiceConfiguration()).thenReturn(mockAuthorizationServiceConfiguration);
        when(mockPimoidcConfigration.getClientId()).thenReturn("94e28300-565d-4110-8919-42dc4f817393");
        when(mockPimoidcConfigration.getRedirectUrl()).thenReturn(redirectrURI);
        when(mockPimoidcConfigration.getrsID()).thenReturn("philipspimregistrationdev");
        whenNew(AuthorizationService.class).withArguments(mockContext).thenReturn(mockAuthorizationService);
        whenNew(PIMAuthManager.class).withArguments(mockContext).thenReturn(mockAuthManager);
        when(mockAuthManager.getAuthState()).thenReturn(mockAuthState);
        PIMSecureStorageHelper mockPIMSStorageHelper = mock(PIMSecureStorageHelper.class);
        whenNew(PIMSecureStorageHelper.class).withArguments(mockAppInfraInterface).thenReturn(mockPIMSStorageHelper);

        pimLoginManager = new PIMLoginManager(mockContext, mockPimoidcConfigration, consentParameterMap);
    }

    @Test
    public void testGetAuthReqIntent() throws Exception {
        mockStatic(Uri.class);
        Uri mockUri = Mockito.mock(Uri.class);

        when(Uri.class, "parse", anyString()).thenReturn(mockUri);
        when(mockTaggingInterface.getVisitorIDAppendToURL("http://")).thenReturn("adobe_mc=TS%3D1568801124%7CMCMID%3D08423335634566345415592103512568266387%7CMCORGID%3D7D976F3055DC96AB7F000101%40AdobeOrg");
        PIMLoginListener mockPimLoginListener = mock(PIMLoginListener.class);
        AuthorizationRequest mockAuthorizationRequest = Mockito.mock(AuthorizationRequest.class);
        when(mockAuthManager.createAuthorizationRequest(eq(mockPimoidcConfigration),  any(HashMap.class))).thenReturn(mockAuthorizationRequest);
        pimLoginManager.getAuthReqIntent(mockPimLoginListener);
        verify(mockAuthManager).getAuthorizationRequestIntent(mockAuthorizationRequest);
    }

    @Test
    public void testIsAuthorizationSuccess() {
        pimLoginManager.isAuthorizationSuccess(mockIntent);
        verify(mockAuthManager).isAuthorizationSuccess(mockIntent);
    }

    @Test
    public void testExchangeAuthorizeCode() throws Exception {
        pimLoginManager.exchangeAuthorizationCode(mockIntent);
        verify(mockAuthManager).performTokenRequestFromLogin(eq(mockIntent), listenerArgumentCaptor.capture());
        PIMTokenRequestListener requestListener = listenerArgumentCaptor.getValue();
        requestListener.onTokenRequestSuccess();

        Whitebox.setInternalState(pimLoginManager, "mPimLoginListener", mockPimLoginListener);
        Error mockError = mock(Error.class);
        requestListener.onTokenRequestFailed(mockError);
        verify(mockPimLoginListener).onLoginFailed(mockError);
    }

    @Test
    public void testRequestUserProfile() throws Exception {
        Whitebox.setInternalState(pimLoginManager, "mPimLoginListener", mockPimLoginListener);
        pimLoginManager.exchangeAuthorizationCode(mockIntent);
        verify(mockAuthManager).performTokenRequestFromLogin(eq(mockIntent), listenerArgumentCaptor.capture());
        PIMTokenRequestListener requestListener = listenerArgumentCaptor.getValue();
        requestListener.onTokenRequestSuccess();

        verify(mockUserManager).requestUserProfile(eq(mockAuthState), userProfileDwnldLstnrCaptor.capture());
        PIMUserProfileDownloadListener downloadListener = userProfileDwnldLstnrCaptor.getValue();
        downloadListener.onUserProfileDownloadSuccess();
        verify(mockPimLoginListener).onLoginSuccess();

        Error mockError = mock(Error.class);
        downloadListener.onUserProfileDownloadFailed(mockError);
        verify(mockPimLoginListener).onLoginFailed(mockError);
    }

    @Test
    public void testExchangeAuthorizeCodeForMigration() throws Exception {
        String authResponse = "authresponse";
        PIMUserMigrationListener mockMigrationListener = mock(PIMUserMigrationListener.class);
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        pimLoginManager.exchangeAuthorizationCodeForMigration(mockAuthorizationRequest, authResponse, mockMigrationListener);
        verify(mockAuthManager).performTokenRequestFromLogin(eq(mockAuthorizationRequest), eq(authResponse), listenerArgumentCaptor.capture());
        PIMTokenRequestListener requestListener = listenerArgumentCaptor.getValue();
        requestListener.onTokenRequestSuccess();
        verify(mockUserManager).requestUserProfile(eq(mockAuthState), userProfileDwnldLstnrCaptor.capture());
        PIMUserProfileDownloadListener downloadListener = userProfileDwnldLstnrCaptor.getValue();
        downloadListener.onUserProfileDownloadSuccess();
        verify(mockMigrationListener).onUserMigrationSuccess();

        Error mockDwonldErrorError = mock(Error.class);
        downloadListener.onUserProfileDownloadFailed(mockDwonldErrorError);
        verify(mockMigrationListener).onUserMigrationFailed(mockDwonldErrorError);

        Error mockError = mock(Error.class);
        requestListener.onTokenRequestFailed(mockError);
        verify(mockMigrationListener).onUserMigrationFailed(mockError);
    }

    @Test
    public void testCreateAdditionalParameterForLogin() throws Exception {
        when(mockTaggingInterface.getVisitorIDAppendToURL("http://")).thenReturn("adobe_mc=TS%3D1568801124%7CMCMID%3D08423335634566345415592103512568266387%7CMCORGID%3D7D976F3055DC96AB7F000101%40AdobeOrg");
        Object additionalParameterForLogin = Whitebox.invokeMethod(pimLoginManager, "createAdditionalParameterForLogin");
        assertNotNull(additionalParameterForLogin);
    }

    @Test
    public void testCreateAuthReqForMigration() {
        pimLoginManager.createAuthRequestUriForMigration(anyMap());
        verify(mockAuthManager).createAuthRequestUriForMigration(anyMap());
    }

    @Test
    public void testExchangeCodeOnEmailVerify() {
        pimLoginManager.exchangeCodeOnEmailVerify(mockPimLoginListener);
        verify(mockPimLoginListener).onLoginFailed(any(Error.class));
    }

    public void tearDown() throws Exception {
    }
}