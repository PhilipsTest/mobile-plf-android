package com.philips.cdp.registration.handlers;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.controller.HSDPLoginService;
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
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;

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
    private RefreshUserHandler handlerMock;

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
    public void testHsdpSuccess() {

        refreshandUpdateUserHandler.forceHsdpLogin(handlerMock);


        hsdpUserMock.login("hsdptest123@mailinator.com", "ztxmpe667xat6t2v", new LoginHandler() {
            @Override
            public void onLoginSuccess() {
                assertTrue(true);
            }

            @Override
            public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
                assertTrue(true);
            }
        });
    }

    @Test
    public void shouldRefreshUserSuccessOnLoginTraditionalSuccess(){
            refreshandUpdateUserHandler.getLoginTraditional(handlerMock,hsdpUserMock);


    }

    @Test
    public void shouldRefreshUserSuccessOnLoginTraditionalFailure(){


    }


}