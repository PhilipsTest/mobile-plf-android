package com.philips.platform.pim.manager;

import androidx.lifecycle.MutableLiveData;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pim.listeners.PIMLoginListener;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.pim.BuildConfig;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.utilities.PIMInitState;
import com.philips.platform.uappframework.uappinput.UappDependencies;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class PIMSettingManagerTest extends TestCase {

    PIMSettingManager pimSettingManager;
    private static final String COMPONENT_TAGS_ID = "pim";

    @Mock
    UappDependencies mockUappDependencies;
    @Mock
    AppInfraInterface mockAppInfraInterface;
    @Mock
    LoggingInterface mockLoggingInterface;
    @Mock
    AppTaggingInterface mockAppTaggingInterface;
    @Mock
    PIMLoginListener mockUserLoginListener;
    @Mock
    MutableLiveData<PIMInitState> mockPimInitLiveData;

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        when(mockUappDependencies.getAppInfra()).thenReturn(mockAppInfraInterface);
        when(mockAppInfraInterface.getLogging()).thenReturn(mockLoggingInterface);
        when(mockLoggingInterface.createInstanceForComponent(COMPONENT_TAGS_ID, BuildConfig.VERSION_NAME)).thenReturn(mockLoggingInterface);
        when(mockAppInfraInterface.getTagging()).thenReturn(mockAppTaggingInterface);
        when(mockAppInfraInterface.getRestClient()).thenReturn(mock(RestInterface.class));
        when(mockAppTaggingInterface.createInstanceForComponent(COMPONENT_TAGS_ID, BuildConfig.VERSION_NAME)).thenReturn(mockAppTaggingInterface);

        pimSettingManager = PIMSettingManager.getInstance();
        pimSettingManager.init(mockUappDependencies);
    }

    @Test
    public void testPIMOIDCConfiguration() {
        PIMOIDCConfigration mockPimoidcConfigration = mock(PIMOIDCConfigration.class);
        pimSettingManager.setPimOidcConfigration(mockPimoidcConfigration);
        PIMOIDCConfigration pimOidcConfigration = pimSettingManager.getPimOidcConfigration();
        assertSame(mockPimoidcConfigration, pimOidcConfigration);
    }

    @Test
    public void testPIMUserManager() {
        PIMUserManager mockPimUserManager = mock(PIMUserManager.class);
        pimSettingManager.setPimUserManager(mockPimUserManager);
        PIMUserManager pimUserManager = pimSettingManager.getPimUserManager();
        assertSame(mockPimUserManager, pimUserManager);
    }

    @Test
    public void testLocale() {
        pimSettingManager.setLocale("en_US");
        String locale = pimSettingManager.getLocale();
        assertEquals("en-US", locale);
    }

    @Test
    public void testGetAppInfraInterface() {
        AppInfraInterface appInfraInterface = pimSettingManager.getAppInfraInterface();
        assertNotNull(appInfraInterface);
    }

    @Test
    public void testGetLoggingInterface() {
        LoggingInterface loggingInterface = pimSettingManager.getLoggingInterface();
        assertNotNull(loggingInterface);
    }

    @Test
    public void testGetTaggingInterface() {
        AppTaggingInterface taggingInterface = pimSettingManager.getTaggingInterface();
        assertNotNull(taggingInterface);
    }

    @Test
    public void testGetRestInterface() {
        RestInterface restClient = pimSettingManager.getRestClient();
        assertNotNull(restClient);
    }

    @Test
    public void testInitLiveData() {
        pimSettingManager.setPIMInitLiveData(mockPimInitLiveData);
        MutableLiveData<PIMInitState> pimInitLiveData = pimSettingManager.getPimInitLiveData();
        assertSame(mockPimInitLiveData, pimInitLiveData);
    }

    @Test
    public void testGetPimUserLoginListener() {
        pimSettingManager.setUserLoginInerface(mockUserLoginListener);
        PIMLoginListener pimUserLoginListener = pimSettingManager.getPimUserLoginListener();
        assertSame(mockUserLoginListener, pimUserLoginListener);
    }

    public void tearDown() throws Exception {
    }
}