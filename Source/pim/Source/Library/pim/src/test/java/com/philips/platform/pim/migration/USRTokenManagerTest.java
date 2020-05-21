package com.philips.platform.pim.migration;

import android.net.Uri;
import androidx.core.util.Pair;
import android.text.Html;
import android.text.TextUtils;

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
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.appinfra.timesync.TimeInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.listeners.RefreshUSRTokenListener;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.rest.PIMRestClient;
import com.philips.platform.pim.rest.RefreshUSRTokenRequest;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({Uri.class, PIMSettingManager.class, USRTokenManager.class, Html.class, TextUtils.class})
@PowerMockIgnore({"javax.crypto.*"})
@RunWith(PowerMockRunner.class)
public class USRTokenManagerTest extends TestCase {
    private static final String JR_CAPTURE_REFRESH_SECRET = "jr_capture_refresh_secret";
    private static final String JR_CAPTURE_SIGNED_IN_USER = "jr_capture_signed_in_user";
    private static final String JR_CAPTURE_FLOW = "jr_capture_flow";
    private static final String JR_CAPTURE_FLOW_VERSION = "jr_capture_flow_version";
    private USRTokenManager usrTokenManager;
    @Mock
    private ServiceDiscoveryInterface mockServiceDiscoveryInterface;
    @Mock
    private ServiceDiscoveryInterface.OnGetServiceUrlMapListener mockOnGetServiceUrlMapListener;
    @Captor
    private ArgumentCaptor<ServiceDiscoveryInterface.OnGetServiceUrlMapListener> captor;
    @Mock
    private AppInfraInterface mockAppInfraInterface;
    @Mock
    private LoggingInterface mockLoggingInterface;
    @Mock
    private PIMSettingManager mockPimSettingManager;
    @Mock
    private ServiceDiscoveryService mockServiceDiscoveryService;
    @Mock
    private RefreshUSRTokenListener mockRefreshUSRTokenListener;
    @Captor
    private ArgumentCaptor<ArrayList<String>> captorArrayList;
    @Mock
    private SecureStorageInterface mockSecureStorageInterface;
    @Mock
    private SecureStorageInterface.SecureStorageError mockSecureStorageError;
    @Mock
    private AppConfigurationInterface.AppConfigurationError mockAppConfigurationError;
    @Mock
    private AppConfigurationInterface mockAppConfigurationInterface;
    @Mock
    private TimeInterface mockTimeInterface;
    @Mock
    private RestInterface mockRestInterface;
    @Mock
    private PIMOIDCConfigration mockPimoidcConfigration;
    @Captor
    private ArgumentCaptor<Response.Listener<String>> captorResponseListener;
    @Captor
    private ArgumentCaptor<Response.ErrorListener> captorErrorListener;

    private USRTokenManager spyUsrTokenManager;
    private String accessToken = "vsu46sctqqpjwkbn";
    private String TAG = PIMMigrationManager.class.getSimpleName();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockStatic(PIMSettingManager.class);
        RequestQueue mockRequestQueue = mock(RequestQueue.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        when(mockPimSettingManager.getRestClient()).thenReturn(mockRestInterface);
        when(mockPimSettingManager.getPimOidcConfigration()).thenReturn(mockPimoidcConfigration);
        when(mockRestInterface.getRequestQueue()).thenReturn(mockRequestQueue);
        when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockSecureStorageInterface);
        when(mockAppInfraInterface.getServiceDiscovery()).thenReturn(mockServiceDiscoveryInterface);
        when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockAppConfigurationInterface);
        whenNew(SecureStorageInterface.SecureStorageError.class).withNoArguments().thenReturn(mockSecureStorageError);
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_SIGNED_IN_USER, mockSecureStorageError)).thenReturn(signedUserData());
        when(mockAppInfraInterface.getTime()).thenReturn(mockTimeInterface);
        when(mockTimeInterface.getUTCTime()).thenReturn(new Date());
        when(mockPimoidcConfigration.getLegacyClientID()).thenReturn("f2stykcygm7enbwfw2u9fbg6h6syb8yd");

        usrTokenManager = new USRTokenManager(mockAppInfraInterface);
        spyUsrTokenManager = spy(usrTokenManager);
    }

    @Test
    public void test_GetServicesWithCountryPreference_OnSuccess() {
        usrTokenManager.fetchRefreshedAccessToken(mockRefreshUSRTokenListener);
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();
        Map<String, ServiceDiscoveryService> mockMap = mock(Map.class);
        when(mockMap.get(any())).thenReturn(mockServiceDiscoveryService);
        when(mockServiceDiscoveryService.getConfigUrls()).thenReturn(new String());
        when(mockServiceDiscoveryService.getLocale()).thenReturn("en_US");
        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
        verify(mockLoggingInterface).log(DEBUG, PIMMigrationManager.class.getSimpleName(), "downloadUserUrlFromSD onSuccess. Refresh Url : " + "" + " Locale : " + "en_US");
        verify(mockLoggingInterface).log(DEBUG, PIMMigrationManager.class.getSimpleName(), "downloadUserUrlFromSD onSuccess");
        verify(mockLoggingInterface).log(DEBUG, PIMMigrationManager.class.getSimpleName(), "Migration Failed!! " + "Signed_in_user not found");
    }

    @Test
    public void test_GetServicesWithCountryPreference_OnError() {
        usrTokenManager.fetchRefreshedAccessToken(mockRefreshUSRTokenListener);
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();
        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.CONNECTION_TIMEOUT, "Connection Timeout");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
        verify(mockLoggingInterface).log(DEBUG, PIMMigrationManager.class.getSimpleName(), "Migration Failed!! " + " Error in downloadUserUrlFromSD : " + "Connection Timeout");
    }

    @Test
    public void test_GetServicesWithCountryPreference_OnSuccess_When_URLMap_IsNotNull() {
        Map<String, ServiceDiscoveryService> mockMap = null;
        usrTokenManager.fetchRefreshedAccessToken(mockRefreshUSRTokenListener);
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();
        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
        verify(mockLoggingInterface).log(DEBUG, PIMMigrationManager.class.getSimpleName(), "Migration Failed!! " + " Error in downloadUserUrlFromSD : " + "Not able to fetch config url");
    }

    @Test
    public void test_GetServicesWithCountryPreference_OnSuccess_When_UserIsSignedIn() throws Exception {
        Map<String, ServiceDiscoveryService> mockMap = mock(Map.class);
        AppConfigurationInterface.AppConfigurationError mockConfigurationError = mock(AppConfigurationInterface.AppConfigurationError.class);
        whenNew(AppConfigurationInterface.AppConfigurationError.class).withNoArguments().thenReturn(mockConfigurationError);
        doReturn("signature").when(spyUsrTokenManager, "getRefreshSignature", anyString(), anyString());
        doReturn("HEAD").when(spyUsrTokenManager, "getFlowVersion");
        when(mockMap.get(any())).thenReturn(mockServiceDiscoveryService);
        when(mockServiceDiscoveryService.getConfigUrls()).thenReturn("https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login");
        when(mockServiceDiscoveryService.getLocale()).thenReturn("en_US");
        Whitebox.setInternalState(spyUsrTokenManager, "signedInUser", signedUserData());
        when(mockAppConfigurationInterface.getPropertyForKey("JanRainConfiguration.RegistrationClientID", "PIM", mockConfigurationError)).thenReturn("f2stykcygm7enbwfw2u9fbg6h6syb8yd");
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        spyUsrTokenManager.fetchRefreshedAccessToken(mockRefreshUSRTokenListener);
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();
        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
    }

    @Test
    public void testRefreshUSRTokenRequest() throws Exception {
        String refreshUrl = "https://philips.eval.janraincapture.com" + "/oauth/refresh_access_token";
        Whitebox.setInternalState(spyUsrTokenManager, "signedInUser", signedUserData());
        RefreshUSRTokenRequest mockUsrTokenRequest = mock(RefreshUSRTokenRequest.class);
        whenNew(RefreshUSRTokenRequest.class).withAnyArguments().thenReturn(mockUsrTokenRequest);
        PIMRestClient mockPimRestClient = mock(PIMRestClient.class);
        PIMRestClient pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        whenNew(PIMRestClient.class).withArguments(mockRestInterface).thenReturn(mockPimRestClient);
        doReturn("signature").when(spyUsrTokenManager, "getRefreshSignature", anyString(), anyString());
        doReturn("HEAD").when(spyUsrTokenManager, "getFlowVersion");
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        Whitebox.invokeMethod(spyUsrTokenManager, "refreshUSRAccessToken", refreshUrl, "en-US", mockRefreshUSRTokenListener);
        verify(mockPimRestClient).invokeRequest(eq(mockUsrTokenRequest), captorResponseListener.capture(), captorErrorListener.capture());

        Response.Listener<String> responseListener = captorResponseListener.getValue();
        responseListener.onResponse(getRefreshTokenResponse());
        verify(mockRefreshUSRTokenListener).onRefreshTokenSuccess(anyString());

        Response.ErrorListener errorListener = captorErrorListener.getValue();
        VolleyError volleyError = new VolleyError();
        errorListener.onErrorResponse(volleyError);
        verify(mockRefreshUSRTokenListener).onRefreshTokenFailed(any(Error.class));
        responseListener.onResponse(new JsonObject().toString());
    }


    @Test
    public void testParamToStringThrowException() throws Exception {
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        doReturn("signature").when(spyUsrTokenManager, "getRefreshSignature", anyString(), anyString());
        doReturn("HEAD").when(spyUsrTokenManager, "getFlowVersion");
        HashSet<Pair<String, String>> params = Whitebox.invokeMethod(spyUsrTokenManager, "getParams", "en-US", datetime, accessToken);
        Whitebox.invokeMethod(spyUsrTokenManager, "paramsToString", params, "UTF");
    }

    @Test
    public void testGetParams() throws Exception {
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        HashSet<Pair<String, String>> params = Whitebox.invokeMethod(usrTokenManager, "getParams", "en-US", datetime, accessToken);
        assertNotNull(params);
    }

    @Test
    public void testGetRefreshSignature() throws Exception {
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        String refsignature = Whitebox.invokeMethod(spyUsrTokenManager, "getRefreshSignature", datetime, accessToken);
        assertNull(refsignature);
    }

    @Test
    public void testGetRefreshSignatureRefreshSecretNull() throws Exception {
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        String refsignature = Whitebox.invokeMethod(spyUsrTokenManager, "getRefreshSignature", datetime, accessToken);
        verify(mockLoggingInterface).log(DEBUG, TAG, "refresh secret is null");
    }

    @Test
    public void testGetRefreshSignatureThrowInvalidKeyException() throws Exception {
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        whenNew(SecretKeySpec.class).withAnyArguments().thenReturn(null);
        Whitebox.invokeMethod(spyUsrTokenManager, "getRefreshSignature", datetime, accessToken);
    }


    @Test
    public void testParseLocale() throws Exception {
        String locale = Whitebox.invokeMethod(spyUsrTokenManager, "parseLocale", "en_US");
        assertEquals("en-US", locale);
    }

    @Test
    public void testGetUTCdatetimeAsString() throws Exception {
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        assertNotNull(datetime);
    }

    @Test
    public void testGetUTCdatetimeAsStringReturnsNull() throws Exception {
        when(mockAppInfraInterface.getTime()).thenReturn(null);
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager, "getUTCdatetimeAsString");
        assertNull(datetime);
    }

    @Test
    public void testGetUSRAccessTokenSignedUserNull() throws Exception {
        String usrAccessToken = Whitebox.invokeMethod(usrTokenManager, "getUSRAccessToken");
        assertNull(usrAccessToken);
    }

    @Test
    public void testGetUSRAccessTokenSignedUser() throws Exception {
        Whitebox.setInternalState(usrTokenManager, "signedInUser", signedUserData());
        String usrAccessToken = Whitebox.invokeMethod(usrTokenManager, "getUSRAccessToken");
        assertNotNull(usrAccessToken);
    }

    @Test
    public void testGetUSRAccessTokenSignedUserWithError() throws Exception {
        Whitebox.setInternalState(usrTokenManager, "signedInUser", new JsonObject().toString());
        String usrAccessToken = Whitebox.invokeMethod(usrTokenManager, "getUSRAccessToken");
        assertNull(usrAccessToken);
    }

    @Test
    public void testFetchDataFromSecureStorage() throws Exception {
        USRTokenManager spyUsrTokenManager = PowerMockito.spy(usrTokenManager);
        String signedInUser = Whitebox.invokeMethod(spyUsrTokenManager, "fetchDataFromSecureStorage", JR_CAPTURE_SIGNED_IN_USER);
        assertEquals(signedUserData(), signedInUser);
        verify(mockSecureStorageInterface).fetchValueForKey(JR_CAPTURE_SIGNED_IN_USER, mockSecureStorageError);
    }

    @Test
    public void testIsUSRUSerAvailable() throws Exception {
        Whitebox.invokeMethod(spyUsrTokenManager, "isUSRUserAvailable");
        verifyPrivate(spyUsrTokenManager).invoke("fetchDataFromSecureStorage", JR_CAPTURE_SIGNED_IN_USER);
    }

    @Test
    public void testDeleteUSRFromSecureStorage() {
        usrTokenManager.deleteUSRFromSecureStorage();
        verify(mockSecureStorageInterface).removeValueForKey(JR_CAPTURE_SIGNED_IN_USER);
        verify(mockSecureStorageInterface).removeValueForKey(JR_CAPTURE_FLOW);
        verify(mockSecureStorageInterface).removeValueForKey(JR_CAPTURE_REFRESH_SECRET);
    }

    @Test
    public void testGetFlowVersion() throws Exception {
        mockStatic(TextUtils.class);
        when(TextUtils.isEmpty(null)).thenReturn(true);
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_FLOW_VERSION, mockSecureStorageError)).thenReturn(null);
        String flowVersion = Whitebox.invokeMethod(spyUsrTokenManager, "getFlowVersion");
        assertEquals("HEAD",flowVersion);
    }

    private String signedUserData() {
        String path = "src/test/rs/usr_signedin_user.json";
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

    private String getRefreshTokenResponse() {
        String path = "src/test/rs/usr_refreshtoken_response.json";
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

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

}