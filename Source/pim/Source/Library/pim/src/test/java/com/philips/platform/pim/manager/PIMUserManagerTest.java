package com.philips.platform.pim.manager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.rest.request.RequestQueue;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.errors.PIMErrorEnums;
import com.philips.platform.pim.listeners.PIMTokenRequestListener;
import com.philips.platform.pim.listeners.PIMUserProfileDownloadListener;
import com.philips.platform.pim.models.PIMOIDCUserProfile;
import com.philips.platform.pim.rest.LogoutRequest;
import com.philips.platform.pim.rest.PIMRestClient;
import com.philips.platform.pim.rest.UserProfileRequest;
import com.philips.platform.pim.utilities.PIMInitState;

import junit.framework.TestCase;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.TokenResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({PIMSettingManager.class, PIMOIDCConfigration.class, PIMUserManager.class, PIMAuthManager.class, PIMErrorEnums.class})
@RunWith(PowerMockRunner.class)
public class PIMUserManagerTest extends TestCase {

    @Mock
    Context mockContext;
    @Mock
    AppInfraInterface mockAppInfraInterface;
    @Mock
    LoggingInterface mockLoggingInterface;
    @Mock
    AppTaggingInterface mockTaggingInterface;
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;
    @Mock
    AuthState mockAuthState;
    @Mock
    TokenResponse mockLastTokenResponse;
    @Mock
    PIMRestClient mockPimRestClient;
    @Mock
    PIMAuthManager mockPimAuthManager;
    @Mock
    SecureStorageInterface mockStorageInterface;
    @Mock
    private MutableLiveData<PIMInitState> mockPimInitViewModel;


    @Captor
    ArgumentCaptor<Response.Listener> responseArgumentCaptor;
    @Captor
    ArgumentCaptor<Response.ErrorListener> errorArgumentCaptor;
    @Captor
    ArgumentCaptor<PIMTokenRequestListener> tokenRequestArgumentCaptor;

    private PIMUserManager pimUserManager;


    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMErrorEnums.class);
        mockStatic(PIMSettingManager.class);
        RestInterface mockRestInterface = mock(RestInterface.class);
        PIMSettingManager mockPimSettingManager = mock(PIMSettingManager.class);

        Mockito.when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getPimInitLiveData()).thenReturn(mockPimInitViewModel);
        Mockito.when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        Mockito.when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        Mockito.when(mockPimSettingManager.getTaggingInterface()).thenReturn(mockTaggingInterface);
        Mockito.when(mockContext.getSharedPreferences("PIM_PREF", Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        Mockito.when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        Mockito.when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockStorageInterface);
        PowerMockito.when(mockPimSettingManager.getRestClient()).thenReturn(mockRestInterface);
        PowerMockito.when(mockRestInterface.getRequestQueue()).thenReturn(mock(RequestQueue.class));

        whenNew(PIMRestClient.class).withArguments(mockRestInterface).thenReturn(mockPimRestClient);
        whenNew(PIMAuthManager.class).withArguments(mockContext).thenReturn(mockPimAuthManager);
        pimUserManager = new PIMUserManager();
        Whitebox.setInternalState(pimUserManager, "authState", mockAuthState);
        pimUserManager.init(mockContext, mockAppInfraInterface);
    }

    @Test
    public void testInitUUIDAvailable() throws Exception {
        PIMUserManager spyPimUserManager = PowerMockito.spy(pimUserManager);
        doReturn(true).when(spyPimUserManager, "isUUIDAvailable");
        spyPimUserManager.init(mockContext, mockAppInfraInterface);
    }

    @Test
    public void testRequestUserProfile() throws Exception {
        PIMUserProfileDownloadListener mockProfileDownloadListener = mock(PIMUserProfileDownloadListener.class);
        UserProfileRequest mockUserProfileRequest = mock(UserProfileRequest.class);
        whenNew(UserProfileRequest.class).withArguments(mockAuthState).thenReturn(mockUserProfileRequest);
        pimUserManager.requestUserProfile(mockAuthState, mockProfileDownloadListener);

        verify(mockPimRestClient).invokeRequest(eq(mockUserProfileRequest), responseArgumentCaptor.capture(), errorArgumentCaptor.capture());
        Response.Listener responselistener = responseArgumentCaptor.getValue();
        responselistener.onResponse(readUserProfileResponseJson());
        verify(mockProfileDownloadListener).onUserProfileDownloadSuccess();

        Response.ErrorListener errorListener = errorArgumentCaptor.getValue();
        errorListener.onErrorResponse(new VolleyError());
        verify(mockProfileDownloadListener).onUserProfileDownloadFailed(any(Error.class));
    }

    @Test
    public void testGetUserLoggedInStateWhenUserLoggedIN() {
        PIMOIDCUserProfile pimoidcUserProfile = new PIMOIDCUserProfile(readUserProfileResponseJson(), mockAuthState);
        Whitebox.setInternalState(pimUserManager, "pimoidcUserProfile", pimoidcUserProfile);
        UserLoggedInState userLoggedInState = pimUserManager.getUserLoggedInState();
        assertEquals(UserLoggedInState.USER_LOGGED_IN, userLoggedInState);
    }

    @Test
    public void testGetUserLoggedInStateWhenUserNotLoggedIN() {
        UserLoggedInState userLoggedInState = pimUserManager.getUserLoggedInState();
        assertEquals(UserLoggedInState.USER_NOT_LOGGED_IN, userLoggedInState);
    }

    @Test
    public void testGetUserProfile() {
        pimUserManager.getUserProfile();
    }

    @Test
    public void testGetUserProfileReturnsNull() {
        PIMOIDCUserProfile userProfile = pimUserManager.getUserProfile();
        assertNull(userProfile);
    }

    @Test
    public void testRefreshSession() throws Exception {
        RefreshSessionListener mockRefreshSessionListener = mock(RefreshSessionListener.class);
        AuthorizationService mockAuthorizationService = mock(AuthorizationService.class);

        whenNew(AuthorizationService.class).withArguments(mockContext).thenReturn(mockAuthorizationService);
        pimUserManager.refreshSession(mockRefreshSessionListener);
        verify(mockPimAuthManager).refreshToken(eq(mockAuthState), tokenRequestArgumentCaptor.capture());
        PIMTokenRequestListener pimTOkenReqListener = tokenRequestArgumentCaptor.getValue();
        pimTOkenReqListener.onTokenRequestSuccess();
        //verify(mockRefreshSessionListener).refreshSessionSuccess();

        Error error = mock(Error.class);
        pimTOkenReqListener.onTokenRequestFailed(error);
        verify(mockRefreshSessionListener).refreshSessionFailed(error);
    }

    @Test
    public void testLogoutSession() throws Exception {
        AppConfigurationInterface mockConfigurationInterface = mock(AppConfigurationInterface.class);
        AppConfigurationInterface.AppConfigurationError mockConfigurationError = mock(AppConfigurationInterface.AppConfigurationError.class);
        PIMOIDCConfigration mockPimoidcConfigration = mock(PIMOIDCConfigration.class);
        LogoutSessionListener mockLogoutListener = mock(LogoutSessionListener.class);
        LogoutRequest mockLogoutRequest = mock(LogoutRequest.class);

        whenNew(AppConfigurationInterface.AppConfigurationError.class).withNoArguments().thenReturn(mockConfigurationError);
        Mockito.when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockConfigurationInterface);
        Mockito.when(mockConfigurationInterface.getPropertyForKey("PIM.default", "PIM", mockConfigurationError)).thenReturn(new Object());
        Mockito.when(mockPimoidcConfigration.getClientId()).thenReturn("94e28300-565d-4110-8919-42dc4f817393");
        whenNew(PIMOIDCConfigration.class).withNoArguments().thenReturn(mockPimoidcConfigration);
        Mockito.when(mockSharedPreferences.getString("LOGIN_FLOW", PIMUserManager.LOGIN_FLOW.DEFAULT.toString())).thenReturn(PIMUserManager.LOGIN_FLOW.DEFAULT.toString());

        AuthorizationResponse mockAuthorizationResponse = mock(AuthorizationResponse.class);
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);
        AuthorizationServiceDiscovery mockAuthorizationServiceDiscovery = mock(AuthorizationServiceDiscovery.class);

        Mockito.when(mockAuthState.getLastAuthorizationResponse()).thenReturn(mockAuthorizationResponse);
        Mockito.when(mockAuthState.getLastTokenResponse()).thenReturn(mockLastTokenResponse);

        Whitebox.setInternalState(mockAuthorizationResponse, "request", mockAuthorizationRequest);
        Whitebox.setInternalState(mockAuthorizationRequest, "configuration", mockAuthorizationServiceConfiguration);
        Whitebox.setInternalState(mockAuthorizationServiceConfiguration, "discoveryDoc", mockAuthorizationServiceDiscovery);
        Mockito.when(mockAuthorizationServiceDiscovery.getIssuer()).thenReturn("https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login");

        whenNew(LogoutRequest.class).withArguments(mockAuthState, "94e28300-565d-4110-8919-42dc4f817393").thenReturn(mockLogoutRequest);
        pimUserManager.logoutSession(mockLogoutListener);
        verify(mockPimRestClient).invokeRequest(eq(mockLogoutRequest), responseArgumentCaptor.capture(), errorArgumentCaptor.capture());

        Response.Listener reponselistener = responseArgumentCaptor.getValue();
        reponselistener.onResponse("ResponseString");
        verify(mockLogoutListener).logoutSessionSuccess();

        Response.ErrorListener errorListener = errorArgumentCaptor.getValue();
        VolleyError volleyError = new VolleyError();
        errorListener.onErrorResponse(volleyError);
        verify(mockLogoutListener).logoutSessionFailed(any(Error.class));
    }

    @Test
    public void testLogoutMigratedSession() throws Exception {
        AppConfigurationInterface mockConfigurationInterface = mock(AppConfigurationInterface.class);
        AppConfigurationInterface.AppConfigurationError mockConfigurationError = mock(AppConfigurationInterface.AppConfigurationError.class);
        PIMOIDCConfigration mockPimoidcConfigration = mock(PIMOIDCConfigration.class);
        LogoutSessionListener mockLogoutListener = mock(LogoutSessionListener.class);
        LogoutRequest mockLogoutRequest = mock(LogoutRequest.class);
        SecureStorageInterface mockStorageInterface = mock(SecureStorageInterface.class);

        whenNew(AppConfigurationInterface.AppConfigurationError.class).withNoArguments().thenReturn(mockConfigurationError);
        Mockito.when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockConfigurationInterface);
        Mockito.when(mockConfigurationInterface.getPropertyForKey("PIM.default", "PIM", mockConfigurationError)).thenReturn(new Object());
        Mockito.when(mockPimoidcConfigration.getMigrationClientId()).thenReturn("7602c06b-c547-4aae-8f7c-f89e8c887a21");
        whenNew(PIMOIDCConfigration.class).withNoArguments().thenReturn(mockPimoidcConfigration);
        Mockito.when(mockSharedPreferences.getString("LOGIN_FLOW", PIMUserManager.LOGIN_FLOW.DEFAULT.toString())).thenReturn(PIMUserManager.LOGIN_FLOW.MIGRATION.toString());
        Mockito.when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockStorageInterface);

        AuthorizationResponse mockAuthorizationResponse = mock(AuthorizationResponse.class);
        AuthorizationRequest mockAuthorizationRequest = mock(AuthorizationRequest.class);
        AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);
        AuthorizationServiceDiscovery mockAuthorizationServiceDiscovery = mock(AuthorizationServiceDiscovery.class);

        Mockito.when(mockAuthState.getLastAuthorizationResponse()).thenReturn(mockAuthorizationResponse);

        Whitebox.setInternalState(pimUserManager, "authState", mockAuthState);
        Whitebox.setInternalState(mockAuthorizationResponse, "request", mockAuthorizationRequest);
        Whitebox.setInternalState(mockAuthorizationRequest, "configuration", mockAuthorizationServiceConfiguration);
        Whitebox.setInternalState(mockAuthorizationServiceConfiguration, "discoveryDoc", mockAuthorizationServiceDiscovery);
        Mockito.when(mockAuthorizationServiceDiscovery.getIssuer()).thenReturn("https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login");

        whenNew(LogoutRequest.class).withArguments(mockAuthState, "7602c06b-c547-4aae-8f7c-f89e8c887a21").thenReturn(mockLogoutRequest);
        pimUserManager.logoutSession(mockLogoutListener);
        verify(mockPimRestClient).invokeRequest(eq(mockLogoutRequest), responseArgumentCaptor.capture(), errorArgumentCaptor.capture());

        Response.Listener reponselistener = responseArgumentCaptor.getValue();
        reponselistener.onResponse(new JsonObject().toString());
        verify(mockLogoutListener).logoutSessionSuccess();

        Response.ErrorListener errorListener = errorArgumentCaptor.getValue();
        errorListener.onErrorResponse(new VolleyError());
        verify(mockLogoutListener).logoutSessionFailed(any(Error.class));
    }

    @Test
    public void testGetUserProfileFrmSSReturnsNull() throws Exception {
        String userProfile = Whitebox.invokeMethod(pimUserManager, "getUserProfileFromSecureStorage");
        assertNull(userProfile);
    }

    @Test
    public void testSaveUUIDToPrefNullUUID() throws Exception {
        Whitebox.invokeMethod(pimUserManager, "saveUUIDToPreference", (Object) null);
        verify(mockLoggingInterface).log(DEBUG, "PIMUserManager", "UUID is null");
    }

    @Test
    public void testGetAuthStorageFromSSThrowException() throws Exception {
        SecureStorageInterface.SecureStorageError secureStorageError = mock(SecureStorageInterface.SecureStorageError.class);
        whenNew(SecureStorageInterface.SecureStorageError.class).withNoArguments().thenReturn(secureStorageError);
        PIMUserManager spyPimUserManager = PowerMockito.spy(pimUserManager);
        doReturn(true).when(spyPimUserManager, "isUUIDAvailable");
        when(mockStorageInterface.fetchValueForKey(anyString(), eq(secureStorageError))).thenReturn("test");
        Whitebox.invokeMethod(spyPimUserManager, "getAuthStateFromSecureStorage");
    }

    @Test
    public void testGetUUIDFromProfileJsonReturnsNull() throws Exception {
        String uuidFromUserProfileJson = Whitebox.invokeMethod(pimUserManager, "getUUIDFromUserProfileJson", (Object) null);
        assertNull(uuidFromUserProfileJson);
    }

    @Test
    public void testGetUUIDFromProfileJsonThrowsException() throws Exception {
        JsonObject jsonObject = new JsonObject();
        String uuidFromUserProfileJson = Whitebox.invokeMethod(pimUserManager, "getUUIDFromUserProfileJson", jsonObject.toString());
        assertNull(uuidFromUserProfileJson);
    }

    @Test
    public void testSaveLoginFlow() {
        pimUserManager.saveLoginFlowType(PIMUserManager.LOGIN_FLOW.DEFAULT);
        verify(mockEditor).apply();
    }

    @Test
    public void testGetLoginFlow() {
        Mockito.when(mockSharedPreferences.getString("LOGIN_FLOW", PIMUserManager.LOGIN_FLOW.DEFAULT.toString())).thenReturn(PIMUserManager.LOGIN_FLOW.DEFAULT.toString());
        PIMUserManager.LOGIN_FLOW loginFlow = pimUserManager.getLoginFlow();
        assertSame(PIMUserManager.LOGIN_FLOW.DEFAULT, loginFlow);
    }

    @Test
    public void testFetchUserDetails() {
        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(UserDetailConstants.GIVEN_NAME);
        keyList.add(UserDetailConstants.FAMILY_NAME);
        keyList.add(UserDetailConstants.EMAIL);
        keyList.add(UserDetailConstants.MOBILE_NUMBER);
        keyList.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
        keyList.add(UserDetailConstants.UUID);
        keyList.add(UserDetailConstants.GENDER);
        keyList.add(UserDetailConstants.ACCESS_TOKEN);
        keyList.add(UserDetailConstants.TOKEN_TYPE);
        keyList.add(UserDetailConstants.EXPIRES_IN);
        keyList.add(UserDetailConstants.ID_TOKEN);

        AuthorizationResponse mockAuthorizationResponse = mock(AuthorizationResponse.class);
        Mockito.when(mockAuthState.getLastTokenResponse()).thenReturn(mockLastTokenResponse);

        Whitebox.setInternalState(mockAuthorizationResponse, "idToken", "tHFx_gB2uCyswq1f3GNvFJVQZFPRmAfziqAzPljp9P-0VweraIWEk8sec7QuvK5pR");
        Whitebox.setInternalState(mockAuthorizationResponse, "tokenType", "Bearer");
        Whitebox.setInternalState(mockAuthorizationResponse, "accessTokenExpirationTime", new Long(3600));
        Mockito.when(mockAuthState.getLastAuthorizationResponse()).thenReturn(mockAuthorizationResponse);

        PIMOIDCUserProfile pimoidcUserProfile = new PIMOIDCUserProfile(readUserProfileResponseJson(), mockAuthState);
        HashMap<String, Object> stringObjectHashMap = pimoidcUserProfile.fetchUserDetails(keyList);

        String givenName = stringObjectHashMap.get(UserDetailConstants.GIVEN_NAME).toString();
        String email = stringObjectHashMap.get(UserDetailConstants.EMAIL).toString();
        String uuid = stringObjectHashMap.get(UserDetailConstants.UUID).toString();
        String gender = stringObjectHashMap.get(UserDetailConstants.GENDER).toString();
        String tokenType = mockAuthState.getLastAuthorizationResponse().tokenType;
        String idToken =  mockAuthState.getLastAuthorizationResponse().idToken;
        Long expiresIn =  mockAuthState.getLastAuthorizationResponse().accessTokenExpirationTime;
        assertNotNull(givenName);
        assertNotNull(email);
        assertNotNull(uuid);
        assertNotNull(gender);
        assertNotNull(tokenType);
        assertNotNull(expiresIn);
        assertNotNull(idToken);
    }

    private String readUserProfileResponseJson() {
        String path = "src/test/rs/oidc_userprofile_response.json";
        File file = new File(path);
        try {
            JsonParser jsonParser = new JsonParser();
            Object obj = jsonParser.parse(new FileReader(file));
            JsonObject jsonObject = (JsonObject) obj;
            return jsonObject.toString();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public void tearDown() throws Exception {
        mockContext = null;
        mockAppInfraInterface = null;
        mockAuthState = null;
        mockEditor = null;
        mockSharedPreferences = null;
        mockLoggingInterface = null;
        mockPimAuthManager = null;
        mockStorageInterface = null;
    }
}