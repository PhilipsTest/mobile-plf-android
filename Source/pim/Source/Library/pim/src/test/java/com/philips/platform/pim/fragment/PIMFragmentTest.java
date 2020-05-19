package com.philips.platform.pim.fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonObject;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.pim.PIMActivity;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.configration.PIMOIDCConfigration;
import com.philips.platform.pim.manager.PIMLoginManager;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.utilities.PIMInitState;
import com.philips.platform.uappframework.listener.ActionBarListener;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.pim.PIMInterface.PIM_KEY_CONSENTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*", "com.sun.org.apache.xerces.internal.jaxp.*"})
@PrepareForTest({PIMSettingManager.class, PIMInitState.class, PIMLoginManager.class})
public class PIMFragmentTest extends TestCase {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<PIMActivity> activityController;
    private TestActivity pimActivity;
    private PIMFragment pimFragment;

    @Mock
    private AppInfraInterface mockAppInfraInterface;
    @Mock
    private PIMOIDCConfigration mockPimoidcConfigration;
    @Mock
    private PIMUserManager mockUserManager;
    @Mock
    private PIMSettingManager mockPimSettingManager;
    @Mock
    private LoggingInterface mockLoggingInterface;
    @Mock
    private ServiceDiscoveryInterface mockServiceDiscoveryInterface;
    @Mock
    private MutableLiveData<PIMInitState> mockMutableLiveData;
    @Captor
    private ArgumentCaptor<Observer<PIMInitState>> argumentCaptor;
    @Captor
    private ArgumentCaptor<ServiceDiscoveryInterface.OnGetServiceUrlMapListener> mapListenerArgumentCaptor;
    @Mock
    private PIMLaunchInput mockPimLaunchInput;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        mockStatic(PIMSettingManager.class);
        mockStatic(PIMInitState.class);
        when(PIMSettingManager.getInstance()).thenReturn(mockPimSettingManager);
        when(mockPimSettingManager.getLoggingInterface()).thenReturn(mockLoggingInterface);
        when(mockPimSettingManager.getPimInitLiveData()).thenReturn(mockMutableLiveData);
        when(mockPimSettingManager.getPimUserManager()).thenReturn(mockUserManager);
        when(mockPimSettingManager.getPimOidcConfigration()).thenReturn(mockPimoidcConfigration);
        when(mockPimSettingManager.getAppInfraInterface()).thenReturn(mockAppInfraInterface);
        when(mockAppInfraInterface.getServiceDiscovery()).thenReturn(mockServiceDiscoveryInterface);

        pimActivity = Robolectric.buildActivity(TestActivity.class)
                .create()
                .resume()
                .get();

        pimFragment = new PIMFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PIM_KEY_CONSENTS, mockPimLaunchInput.getParameterToLaunch());
        pimFragment.setArguments(bundle);
        pimActivity.getSupportFragmentManager().beginTransaction().add(pimFragment, "PIMFragmentTest").commit();
    }

    @Test
    public void testLoginSuccess() {
        ActionBarListener actionBarListener = mock(ActionBarListener.class);
        UserLoginListener mockUserLoginListener = mock(UserLoginListener.class);
        pimFragment.setActionbarListener(actionBarListener, mockUserLoginListener);
        pimFragment.onLoginSuccess();
        verify(mockUserLoginListener).onLoginSuccess();
    }

    @Test
    public void testLoginFailed() {
        ActionBarListener actionBarListener = mock(ActionBarListener.class);
        UserLoginListener mockUserLoginListener = mock(UserLoginListener.class);
        pimFragment.setActionbarListener(actionBarListener, mockUserLoginListener);
        Error error = mock(Error.class);
        pimFragment.onLoginFailed(error);
        verify(mockUserLoginListener).onLoginFailed(error);
    }

    @Test
    public void testInitSuccessUserLoggedIn() throws Exception {
        AppTaggingInterface mockTaggingInterface = mock(AppTaggingInterface.class);
        when(mockPimSettingManager.getTaggingInterface()).thenReturn(mockTaggingInterface);
        when(mockTaggingInterface.getVisitorIDAppendToURL("http://www.philips.com")).thenReturn("adobe_mc=TS%3D1568801124%7CMCMID%3D08423335634566345415592103512568266387%7CMCORGID%3D7D976F3055DC96AB7F000101%40AdobeOrg");

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);

        Map<String, ServiceDiscoveryService> serviceIdList = new HashMap<>();
        serviceIdList.put("userreg.janrainoidc.userprofile", new ServiceDiscoveryService());

        verify(mockMutableLiveData).observe(eq(pimFragment), argumentCaptor.capture());
        Observer<PIMInitState> value = argumentCaptor.getValue();
        value.onChanged(PIMInitState.INIT_SUCCESS);

        verify(mockServiceDiscoveryInterface).getServicesWithCountryPreference(any(ArrayList.class), mapListenerArgumentCaptor.capture(), eq(null));
        ServiceDiscoveryInterface.OnGetServiceUrlMapListener listener = mapListenerArgumentCaptor.getValue();
        listener.onSuccess(serviceIdList);

        listener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.CONNECTION_TIMEOUT, "ServiceDiscoveryError");
    }

    @Test
    public void testInitSuccessUserNotLoggedIn() throws Exception {
        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);

        verify(mockMutableLiveData).observe(eq(pimFragment), argumentCaptor.capture());
        Observer<PIMInitState> value = argumentCaptor.getValue();
        value.onChanged(PIMInitState.INIT_SUCCESS);
        //verifyPrivate(pimFragment).invoke("launchLoginPage");
    }

    @Test
    public void testInitFailed() throws Exception {
        verify(mockMutableLiveData).observe(eq(pimFragment), argumentCaptor.capture());
        Observer<PIMInitState> value = argumentCaptor.getValue();
        value.onChanged(PIMInitState.INIT_FAILED);

        value.onChanged(PIMInitState.INIT_FAILED);
        //verifyPrivate(pimFragment).invoke("enablProgressBar");
    }

    @Test
    public void testOnActivityForResultResultCanceled() {
        verify(mockMutableLiveData).observe(eq(pimFragment), argumentCaptor.capture());
        Observer<PIMInitState> value = argumentCaptor.getValue();
        value.onChanged(PIMInitState.INIT_SUCCESS);

        Intent intent = new Intent();
        JsonObject jsonObject = new JsonObject();
        JsonObject keyRequestObject = new JsonObject();
        jsonObject.add("request", keyRequestObject);
        intent.putExtra("net.openid.appauth.AuthorizationResponse", jsonObject.toString());
        pimFragment.onActivityResult(100, 0, intent);
    }

    @Test
    public void testOnActivityForResult() {
        verify(mockMutableLiveData).observe(eq(pimFragment), argumentCaptor.capture());
        Observer<PIMInitState> value = argumentCaptor.getValue();
        value.onChanged(PIMInitState.INIT_SUCCESS);

        Intent intent = new Intent();
        pimFragment.onActivityResult(100, -1, intent);
    }

    @Test
    public void onDestroy() {
        pimFragment.onDestroy();
    }

    @After
    public void tearDown() throws Exception {
        //activityController.pause().stop().destroy();
    }
}