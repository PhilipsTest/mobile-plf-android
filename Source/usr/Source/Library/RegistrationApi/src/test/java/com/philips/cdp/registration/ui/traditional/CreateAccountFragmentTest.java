package com.philips.cdp.registration.ui.traditional;

import android.view.InflateException;

import com.philips.cdp.registration.BuildConfig;
import com.philips.cdp.registration.CustomRobolectricRunner;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.injection.RegistrationComponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by philips on 11/21/17.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class CreateAccountFragmentTest {

    CreateAccountFragment createAccountFragment;

    @Mock
    private RegistrationComponent mockRegistrationComponent;

    @Before
    public void setUp() throws Exception {

        initMocks(this);
        RegistrationConfiguration.getInstance().setComponent(mockRegistrationComponent);
        createAccountFragment= new CreateAccountFragment();
    }

    @Test(expected = InflateException.class)
    public void should_startFragment() throws Exception {
        SupportFragmentTestUtil.startFragment(createAccountFragment);
    }



}