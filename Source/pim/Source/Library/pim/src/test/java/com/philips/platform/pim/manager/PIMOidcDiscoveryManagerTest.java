package com.philips.platform.pim.manager;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.listeners.PIMAuthServiceConfigListener;
import com.philips.platform.pim.models.PIMInitViewModel;
import com.philips.platform.pim.utilities.PIMInitState;

import junit.framework.TestCase;

import net.openid.appauth.AuthorizationServiceConfiguration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({Uri.class,PIMSettingManager.class,PIMOidcDiscoveryManager.class})
@RunWith(PowerMockRunner.class)
public class PIMOidcDiscoveryManagerTest extends TestCase {

    private PIMOidcDiscoveryManager pimOidcDiscoveryManager;

    @Mock
    private LoggingInterface mockLoggingInterface;
    @Mock
    private PIMAuthManager mockPimAuthManager;
    @Mock
    private PIMAuthServiceConfigListener mockServiceConfigurationListener;
    @Mock
    private PIMSettingManager mockPimSettingManager;
    @Mock
    private MutableLiveData<PIMInitState> mockPimInitViewModel;
    @Captor
    private ArgumentCaptor<PIMAuthServiceConfigListener> captorListener;
    private String baseurl = "https://stg.accounts.philips.com/c2a48310-9715-3beb-895e-000000000000/login";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getPimInitLiveData()).thenReturn(mockPimInitViewModel);
        PowerMockito.whenNew(PIMAuthManager.class).withNoArguments().thenReturn(mockPimAuthManager);

        mockStatic(Uri.class);
        Uri uri = PowerMockito.mock(Uri.class);
        when(Uri.class, "parse", anyString()).thenReturn(uri);

        pimOidcDiscoveryManager = new PIMOidcDiscoveryManager();
    }

    @Test
    public void downloadOidUrlTest_onSuccess(){
        pimOidcDiscoveryManager.downloadOidcUrls(baseurl);
        verify(mockPimAuthManager).fetchAuthWellKnownConfiguration(any(String.class),captorListener.capture());
        mockServiceConfigurationListener = captorListener.getValue();
        AuthorizationServiceConfiguration mockAuthorizationServiceConfiguration = mock(AuthorizationServiceConfiguration.class);

        mockServiceConfigurationListener.onAuthServiceConfigSuccess(mockAuthorizationServiceConfiguration);
        verify(mockLoggingInterface).log(DEBUG,PIMOidcDiscoveryManager.class.getSimpleName(),"fetchAuthWellKnownConfiguration : onAuthServiceConfigSuccess : "+mockAuthorizationServiceConfiguration);
    }

    @Test
    public void downloadOidUrlTest_onError(){
        pimOidcDiscoveryManager.downloadOidcUrls(baseurl);
        verify(mockPimAuthManager).fetchAuthWellKnownConfiguration(any(String.class),captorListener.capture());

        mockServiceConfigurationListener = captorListener.getValue();
        Error error = new Error(Error.UserDetailError.NetworkError);
        mockServiceConfigurationListener.onAuthServiceConfigFailed(error);
        verify(mockLoggingInterface).log(DEBUG,PIMOidcDiscoveryManager.class.getSimpleName(),"fetchAuthWellKnownConfiguration : onAuthServiceConfigFailed :  "+ error.getErrDesc());
    }

    @After
    public void tearDown() throws Exception {
        pimOidcDiscoveryManager = null;
        mockServiceConfigurationListener = null;
        mockPimAuthManager = null;
        captorListener = null;
    }
}