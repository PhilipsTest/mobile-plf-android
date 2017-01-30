package com.philips.cdp.registration.coppa.utils;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;

import org.junit.Before;
import org.mockito.Mock;

/**
 * Created by 310230979  on 8/31/2016.
 */
public class CoppaLaunchInputTest extends InstrumentationTestCase{

    CoppaLaunchInput coppaLaunchInput;

    @Mock
    RegistrationFunction registrationFunction;

//    @Mock
//    UserRegistrationUIEventListener userRegistrationListener;

    @Mock
    UserRegistrationUIEventListener mUserRegistrationCoppaUIEventListener;

    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        coppaLaunchInput = new CoppaLaunchInput();
    }

    public void testinit(){
        assertNotNull(coppaLaunchInput);
    }

    public void testIsAccountSettingsTrue(){
        coppaLaunchInput.setRegistrationLaunchMode(RegistrationLaunchMode.AccountSettings);
        assertEquals(RegistrationLaunchMode.AccountSettings,coppaLaunchInput.getRegistrationLaunchMode());
    }
    public void testIsParentalFragmentTrue(){
        coppaLaunchInput.setParentalFragment(true);
        assertEquals(true,coppaLaunchInput.isParentalFragment());
    }

    public void testIsParentalFragmentFalse(){
        coppaLaunchInput.setParentalFragment(false);
        assertEquals(false,coppaLaunchInput.isParentalFragment());
    }
    public void testRegistrationFunction(){
        coppaLaunchInput.setRegistrationFunction(registrationFunction);
        assertEquals(registrationFunction,coppaLaunchInput.getRegistrationFunction());
    }
    public void testUserRegistrationListener(){
        coppaLaunchInput.setUserRegistrationUIEventListener(mUserRegistrationCoppaUIEventListener);
        assertEquals(mUserRegistrationCoppaUIEventListener,coppaLaunchInput.getUserRegistrationUIEventListener());
    }

}