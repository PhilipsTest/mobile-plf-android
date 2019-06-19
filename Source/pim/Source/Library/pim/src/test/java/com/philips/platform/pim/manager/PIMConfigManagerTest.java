package com.philips.platform.pim.manager;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pim.utilities.PIMInitState;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Map;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({Uri.class, PIMSettingManager.class, PIMConfigManager.class})
@RunWith(PowerMockRunner.class)
public class PIMConfigManagerTest extends TestCase {

    private PIMConfigManager pimConfigManager;
    @Mock
    private ServiceDiscoveryInterface mockServiceDiscoveryInterface;
    @Mock
    private ServiceDiscoveryInterface.OnGetServiceUrlMapListener mockOnGetServiceUrlMapListener;
    @Captor
    private ArgumentCaptor<ServiceDiscoveryInterface.OnGetServiceUrlMapListener> captor;
    @Captor
    private ArgumentCaptor<ArrayList<String>> captorArrayList;
    @Captor
    private ArgumentCaptor<Runnable> runnables;
    @Mock
    private Thread mockThread;
    @Mock
    private LoggingInterface mockLoggingInterface;
    @Mock
    private PIMSettingManager mockPimSettingManager;
    @Mock
    private ServiceDiscoveryService mockServiceDiscoveryService;
    @Mock
    private PIMUserManager mockPimUserManager;
    @Mock
    private MutableLiveData<PIMInitState> mockPimInitViewModel;


    @Before
    public void setup() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getPimInitLiveData()).thenReturn(mockPimInitViewModel);
        runnables = ArgumentCaptor.forClass(Runnable.class);
        whenNew(Thread.class).withParameterTypes(Runnable.class).withArguments(runnables.capture()).thenReturn(mockThread);
        mockStatic(Uri.class);
        Uri uri = mock(Uri.class);
        when(Uri.class, "parse", anyString()).thenReturn(uri);
        when(mockPimUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);
        pimConfigManager = new PIMConfigManager(mockPimUserManager);
    }

    /**
     * Can't verify downloadOidcUrls() from here,as instance of PIMOidcDiscoveryManager
     * and PIMAuthManager are created locally onLoginSuccess
     */
    @Test
    public void verifyGetServicesWithCountryPreference_OnSuccess() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();
        Map<String, ServiceDiscoveryService> mockMap = mock(Map.class);
        when(mockMap.get(any())).thenReturn(mockServiceDiscoveryService);
        when(mockServiceDiscoveryService.getConfigUrls()).thenReturn(new String());
        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
        verify(mockLoggingInterface).log(DEBUG, PIMConfigManager.class.getSimpleName(), "DownloadSDServiceURLs success : getConfigUrls : " + mockServiceDiscoveryService.getConfigUrls());
    }

    @Test
    public void verifyLog_IfUserIsLoggedIn() {
        when(mockPimUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        pimConfigManager.init(mockServiceDiscoveryInterface);
        verify(mockLoggingInterface).log(DEBUG, PIMConfigManager.class.getSimpleName(), "downloadSDServiceURLs skipped as user is logged in. ");
    }


    @Test
    public void verifyGetServicesWithCountryPreference_OnSuccess_ServiceDiscoveryServiceIsNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        Map<String, ServiceDiscoveryService> mockMap = mock(Map.class);
        when(mockMap.get(any())).thenReturn(null);
        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
        verify(mockLoggingInterface).log(DEBUG, PIMConfigManager.class.getSimpleName(), "DownloadSDServiceURLs success  : serviceDiscovery response is null");
    }

    /**
     * Can't verify downloadOidcUrls() from here,as instance of PIMOidcDiscoveryManager
     * and PIMAuthManager are created locally onLoginSuccess
     */
    @Test
    public void verifyGetServicesWithCountryPreference_OnSuccess_ConfigURLIsNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        Map<String, ServiceDiscoveryService> mockMap = mock(Map.class);
        when(mockMap.get(any())).thenReturn(mockServiceDiscoveryService);
        when(mockServiceDiscoveryService.getConfigUrls()).thenReturn(null);

        mockOnGetServiceUrlMapListener.onSuccess(mockMap);
        verify(mockLoggingInterface).log(DEBUG, PIMConfigManager.class.getSimpleName(), "DownloadSDServiceURLs success : No service url found for Issuer service id");
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_NoNetwork_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.NO_NETWORK, "No Network");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_SecurityError_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.SECURITY_ERROR, "Security Error");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_ServerError_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.SERVER_ERROR, "ErrorMessage");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_CONNECTIONTIMEOUT_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.CONNECTION_TIMEOUT, "ErrorMessage");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_INVALIDRESPONSE_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "ErrorMessage");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_UNKNOWNERROR_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "ErrorMessage");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Test
    public void verifyGetServicesWithCountryPreference_OnError_NoServiceLocalEreror_NotNull() {
        pimConfigManager.init(mockServiceDiscoveryInterface);
        runnables.getValue().run();
        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(captorArrayList.capture(), captor.capture(), eq(null));
        mockOnGetServiceUrlMapListener = captor.getValue();

        ServiceDiscovery.Error sdError = new ServiceDiscovery.Error(ServiceDiscoveryInterface
                .OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR, "ErrorMessage");
        mockOnGetServiceUrlMapListener.onError(sdError.getErrorvalue(), sdError.getMessage());
        assertNotNull(sdError.getErrorvalue());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        pimConfigManager = null;
        mockServiceDiscoveryInterface = null;
        mockOnGetServiceUrlMapListener = null;
        mockPimSettingManager = null;
        captor = null;
        captorArrayList = null;
    }
}