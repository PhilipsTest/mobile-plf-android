package com.philips.cdp.registration.handlers;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.AppConfiguration;
import com.philips.cdp.registration.configuration.HSDPConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.controller.LoginTraditional;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.JumpFlowDownloadStatusListener;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
public class RefreshandUpdateUserHandlerTest extends TestCase {

    private RefreshandUpdateUserHandler refreshandUpdateUserHandler;
    @Mock
    private Context context;
    @Mock
    private RefreshUserHandler refreshUserHandler;
    @Mock
    private User user;
    @Mock
    private RegistrationComponent mockRegistrationComponent;
    @Mock
    private JumpFlowDownloadStatusListener jumpFlowDownloadStatusListener;
    @Mock
    private HsdpUser hsdpUserMock;
    @Mock
    private LoginTraditional loginTraditionalMock;
    @Mock
    private AppConfiguration appConfigurationMock;
    @Mock
    private HSDPConfiguration hsdpConfigurationMock;
    @Mock
    private RefreshUserHandler handlerMock;

    @Mock
    private UserRegistrationFailureInfo userRegistrationFailureInfoMock;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        RegistrationConfiguration.getInstance().setComponent(mockRegistrationComponent);
        refreshandUpdateUserHandler = new RefreshandUpdateUserHandler(context);
    }

    @Test
    public void testRefreshUpdateUserFailed() {
        UserRegistrationInitializer.getInstance().setJanrainIntialized(false);
        UserRegistrationInitializer.getInstance().setJumpInitializationInProgress(true);
        refreshandUpdateUserHandler.refreshAndUpdateUser(refreshUserHandler, user);
//        Mockito.verify(UserRegistrationInitializer.getInstance(), Mockito.times(0)).registerJumpFlowDownloadListener(jumpFlowDownloadStatusListener);
    }

    @Test
    public void testRefreshUpdateUserSuccess() {
        UserRegistrationInitializer.getInstance().setJanrainIntialized(false);
        UserRegistrationInitializer.getInstance().setJumpInitializationInProgress(true);
        refreshandUpdateUserHandler.refreshAndUpdateUser(refreshUserHandler, user);
        //  Mockito.verify(refreshUserHandler).onRefreshUserSuccess();
    }

    @Test
    public void testRefreshUpdateUserFailedWithoutInit() {
        UserRegistrationInitializer.getInstance().setJanrainIntialized(false);
        UserRegistrationInitializer.getInstance().setJumpInitializationInProgress(false);
        refreshandUpdateUserHandler.refreshAndUpdateUser(refreshUserHandler, user);
        //  Mockito.verify(refreshUserHandler).onRefreshUserSuccess();
    }

    @Test
    public void testOnFlowDownloadSuccess() {
       refreshandUpdateUserHandler.onFlowDownloadSuccess();
        //  Mockito.verify(refreshUserHandler).onRefreshUserSuccess();
    }

    @Test
    public void testOnFlowDownloadFailed() {
        refreshandUpdateUserHandler.onFlowDownloadFailure();
        //  Mockito.verify(refreshUserHandler).onRefreshUserSuccess();
    }

    @Test
    public void getTraditionalMethodShouldNotReturnNull() {
        assertNotNull(refreshandUpdateUserHandler.getLoginTraditional(handlerMock));
    }

    @Test
    public void shouldRefreshUserSuccessOnLoginTraditionalSuccess(){
        LoginHandler loginHandler = refreshandUpdateUserHandler.getLoginHandler(handlerMock);
        loginHandler.onLoginSuccess();
        Mockito.verify(handlerMock).onRefreshUserSuccess();
    }

    @Test
    public void shouldRefreshUserFailureOnLoginTraditionalFailure(){
        LoginHandler loginHandler = refreshandUpdateUserHandler.getLoginHandler(handlerMock);
        loginHandler.onLoginFailedWithError(userRegistrationFailureInfoMock);
        Mockito.verify(handlerMock).onRefreshUserFailed(anyInt());
    }

    @Test
    public void shouldCallLoginHSDPIfHSDPFlowIsEnabled() {
        Mockito.when(hsdpConfigurationMock.getHsdpSecretId()).thenReturn("secret");
        Mockito.when(hsdpConfigurationMock.getHsdpSharedId()).thenReturn("shared");
        Mockito.when(appConfigurationMock.getRegistrationEnvironment()).thenReturn("ACCEPTANCE");
        RegistrationConfiguration.getInstance().appConfiguration = appConfigurationMock;
        RegistrationConfiguration.getInstance().hsdpConfiguration = hsdpConfigurationMock;

        refreshandUpdateUserHandler.forceHsdpLogin(handlerMock,loginTraditionalMock);
        Mockito.verify(loginTraditionalMock).loginIntoHsdp();
    }
}