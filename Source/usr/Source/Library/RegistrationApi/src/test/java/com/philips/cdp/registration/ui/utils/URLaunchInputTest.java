package com.philips.cdp.registration.ui.utils;

import android.app.Activity;

import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.platform.appinfra.logging.LoggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by philips on 11/30/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class URLaunchInputTest {

    @Mock
    private RegistrationComponent mockRegistrationComponent;
    @Mock
    private LoggingInterface mockLoggingInterface;

    private URLaunchInput urLaunchInput;

    @Mock
    private UserRegistrationUIEventListener userRegListenerMock;

    @Mock
    private RegistrationContentConfiguration regConfigMock;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        RegistrationConfiguration.getInstance().setComponent(mockRegistrationComponent);
        RLog.setMockLogger(mockLoggingInterface);

        urLaunchInput=new URLaunchInput();
    }

    @Test
    public void isAddtoBackStack() {
        urLaunchInput.isAddtoBackStack();
    }

    @Test
    public void enableAddtoBackStack() {
        urLaunchInput.enableAddtoBackStack(true);
    }

    @Test
    public void getRegistrationFunction() {
        urLaunchInput.getRegistrationFunction();
    }

    @Test
    public void setUserRegistrationUIEventListener() {
        urLaunchInput.setUserRegistrationUIEventListener(userRegListenerMock);
    }

    @Test
    public void getUserRegistrationUIEventListener() {
        urLaunchInput.getUserRegistrationUIEventListener();
    }

    @Test
    public void getEndPointScreen() {
        urLaunchInput.getEndPointScreen();
    }


    @Test
    public void setRegistrationContentConfiguration() {
        urLaunchInput.setRegistrationContentConfiguration(regConfigMock);
    }

    @Test
    public void getRegistrationContentConfiguration() {
        urLaunchInput.getRegistrationContentConfiguration();
    }

    @Test
    public void getUIflow() {
        urLaunchInput.getUIflow();
    }

    @Test
    public void testURLaunch(){

        UserRegistrationUIEventListener mUserRegistrationUIEventListener = new UserRegistrationUIEventListener() {
            @Override
            public void onUserRegistrationComplete(Activity activity) {

            }

            @Override
            public void onPrivacyPolicyClick(Activity activity) {

            }

            @Override
            public void onTermsAndConditionClick(Activity activity) {

            }

            @Override
            public void onPersonalConsentClick(Activity activity) {

            }
        };
        urLaunchInput.setUserRegistrationUIEventListener(mUserRegistrationUIEventListener);
        assertNotNull(urLaunchInput.getUserRegistrationUIEventListener());
        urLaunchInput.setRegistrationFunction(RegistrationFunction.SignIn);
        assertNotNull(urLaunchInput.getRegistrationFunction());

        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        assertNotNull(urLaunchInput.getRegistrationFunction());
        urLaunchInput.enableAddtoBackStack(true);
        assertTrue(urLaunchInput.isAddtoBackStack());
        urLaunchInput.enableAddtoBackStack(false);
        assertFalse(urLaunchInput.isAddtoBackStack());
    }

}