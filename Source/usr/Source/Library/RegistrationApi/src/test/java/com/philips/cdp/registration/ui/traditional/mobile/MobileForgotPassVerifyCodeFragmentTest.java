package com.philips.cdp.registration.ui.traditional.mobile;

import android.os.Bundle;

import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class MobileForgotPassVerifyCodeFragmentTest {
    @Spy
    private MobileForgotPassVerifyCodeFragment mobileForgotPassVerifyCodeFragment;
    @Mock
    private RegistrationBaseFragment mockRegistrationBaseFragment;
    @Mock
    private RegistrationComponent mockRegistrationComponent;
    @Mock
    private AppTaggingInterface mockAppTaggingInterface;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockRegistrationComponent.getAppTaggingInterface()).thenReturn(mockAppTaggingInterface);
        when(mockAppTaggingInterface.createInstanceForComponent(anyString(), anyString())).thenReturn(mockAppTaggingInterface);

        RegistrationConfiguration.getInstance().setComponent(mockRegistrationComponent);
        AppTagging.init();
        callRegistrationFragment();
    }

    @Test
    public void should_startFragment() throws Exception {
        Bundle args = new Bundle();
        args.putString("mobileNumber", "3535345345334");
        mobileForgotPassVerifyCodeFragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(mobileForgotPassVerifyCodeFragment);
        assertNotNull("should not be null", mobileForgotPassVerifyCodeFragment);
    }

    private void callRegistrationFragment() {
        when(mobileForgotPassVerifyCodeFragment.getRegistrationFragment()).thenReturn(new RegistrationFragment());
        RegistrationFragment registrationFragment = mobileForgotPassVerifyCodeFragment.getRegistrationFragment();
        registrationFragment.onCreate(null);
        registrationFragment.startCountDownTimer();
    }

    @Test
    public void should_AppendAtLast() {
        callRegistrationFragment();
        String normalText = "We’ve just sent an SMS with a six digit code to your phone number %s";
        String expText = "We’ve just sent an SMS with a six digit code to your phone number 342342342342";
        assertEquals(expText, mobileForgotPassVerifyCodeFragment.setDescription(normalText, "342342342342"));

    }

    @Test
    public void should_AppendInBetween() {
        callRegistrationFragment();
        String normalText = "We’ve just sent an SMS with a six digit code %s to your phone number";
        String expText = "We’ve just sent an SMS with a six digit code 342342342342 to your phone number";
        assertEquals(expText, mobileForgotPassVerifyCodeFragment.setDescription(normalText, "342342342342"));

    }

    @Test
    public void should_AppendAtStart() {
        callRegistrationFragment();
        String normalText = "%s We’ve just sent an SMS with a six digit code to your phone number";
        String expText = "342342342342 We’ve just sent an SMS with a six digit code to your phone number";
        assertEquals(expText, mobileForgotPassVerifyCodeFragment.setDescription(normalText, "342342342342"));

    }
}