package com.philips.platform.pim.migration;

import android.net.Uri;
import android.text.Html;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.appinfra.timesync.TimeInterface;
import com.philips.platform.pim.listeners.RefreshUSRTokenListener;
import com.philips.platform.pim.manager.PIMSettingManager;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({Uri.class, PIMSettingManager.class, USRTokenManager.class, Html.class})
@PowerMockIgnore({"javax.crypto.*" })
@RunWith(PowerMockRunner.class)
public class USRTokenManagerTest extends TestCase {
    private static final String JR_CAPTURE_REFRESH_SECRET = "jr_capture_refresh_secret";
    private static final String JR_CAPTURE_SIGNED_IN_USER = "jr_capture_signed_in_user";
    private static final String JR_CAPTURE_FLOW = "jr_capture_flow";
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

    private USRTokenManager spyUsrTokenManager;
    private String accessToken = "vsu46sctqqpjwkbn";
    private String TAG = PIMMigrationManager.class.getSimpleName();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockStatic(PIMSettingManager.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockAppInfraInterface.getSecureStorage()).thenReturn(mockSecureStorageInterface);
        when(mockAppInfraInterface.getServiceDiscovery()).thenReturn(mockServiceDiscoveryInterface);
        when(mockAppInfraInterface.getConfigInterface()).thenReturn(mockAppConfigurationInterface);
        whenNew(SecureStorageInterface.SecureStorageError.class).withNoArguments().thenReturn(mockSecureStorageError);
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_SIGNED_IN_USER, mockSecureStorageError)).thenReturn(signedUserData());
        when(mockAppInfraInterface.getTime()).thenReturn(mockTimeInterface);
        when(mockTimeInterface.getUTCTime()).thenReturn(new Date());

        usrTokenManager = new USRTokenManager(mockAppInfraInterface);
        spyUsrTokenManager = spy(usrTokenManager);

        //Whitebox.invokeMethod(spyUsrTokenManager,"getFlowVersion")


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

   /* @Test
    public void test_GetServicesWithCountryPreference_OnSuccess_When_UserIsSignedIn() throws Exception {
        Map<String, ServiceDiscoveryService> mockMap = mock(Map.class);
        AppConfigurationInterface.AppConfigurationError mockConfigurationError = mock(AppConfigurationInterface.AppConfigurationError.class);
        whenNew(AppConfigurationInterface.AppConfigurationError.class).withNoArguments().thenReturn(mockConfigurationError);
        when(mockMap.get(any())).thenReturn(mockServiceDiscoveryService);
        when(mockServiceDiscoveryService.getConfigUrls()).thenReturn("https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login");
        when(mockServiceDiscoveryService.getLocale()).thenReturn("en_US");
        Whitebox.setInternalState(usrTokenManager, "signedInUser", signedUserData());
        //when(mockAppInfraInterface.getConfigInterface().getPropertyForKey("JanRainConfiguration.RegistrationClientID", "PIM", mockAppConfigurationError)).thenReturn(usrClientIds());
        when(mockAppConfigurationInterface.getPropertyForKey("JanRainConfiguration.RegistrationClientID", "PIM", mockConfigurationError)).thenReturn("f2stykcygm7enbwfw2u9fbg6h6syb8yd");
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        usrTokenManager.fetchRefreshedAccessToken(mockRefreshUSRTokenListener);
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();
        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
    }*/

    //Updated test cases

    @Test
    public void testGetParams(){

    }

    @Test
    public void testGetRefreshSignature() throws Exception {
        when(mockSecureStorageInterface.fetchValueForKey(JR_CAPTURE_REFRESH_SECRET, mockSecureStorageError)).thenReturn("9d945b63d7a7456ee775fddd5f32f1315cda9fed");
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager,"getUTCdatetimeAsString");
        String refsignature = Whitebox.invokeMethod(spyUsrTokenManager, "getRefreshSignature", datetime,accessToken);
        assertNotNull(refsignature);
    }

    @Test
    public void testGetRefreshSignatureRefreshSecretNull() throws Exception {
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager,"getUTCdatetimeAsString");
        String refsignature = Whitebox.invokeMethod(spyUsrTokenManager, "getRefreshSignature", datetime,accessToken);
        verify(mockLoggingInterface).log(DEBUG,TAG,"refresh secret is null");
    }

    @Test
    public void testParseLocale() throws Exception {
        String locale = Whitebox.invokeMethod(spyUsrTokenManager, "parseLocale", "en_US");
        assertEquals("en-US",locale);
    }

    @Test
    public void testGetUTCdatetimeAsString() throws Exception {
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager,"getUTCdatetimeAsString");
        assertNotNull(datetime);
    }

    @Test
    public void testGetUTCdatetimeAsStringReturnsNull() throws Exception {
        when(mockAppInfraInterface.getTime()).thenReturn(null);
        String datetime = Whitebox.invokeMethod(spyUsrTokenManager,"getUTCdatetimeAsString");
        assertNull(datetime);
    }

    @Test
    public void testGetFlowVersion(){

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

    private Object usrClientIds() {
        return "\"JanRainConfiguration.RegistrationClientID\": {\n" +
                "      \"CN\": \"4rdpm7afu7bny6xnacw32etmt7htfraa\",\n" +
                "      \"default\": \"f2stykcygm7enbwfw2u9fbg6h6syb8yd\"\n" +
                "    }";
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

}