package com.philips.cdp.registration.ui.traditional.mobile;

import android.text.SpannableString;

import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class MobileVerifyCodeFragmentTest {
    @Spy
    private MobileVerifyCodeFragment mobileVerifyCodeFragment;
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

    private void callRegistrationFragment() {
        when(mobileVerifyCodeFragment.getRegistrationFragment()).thenReturn(new RegistrationFragment());
        RegistrationFragment registrationFragment = mobileVerifyCodeFragment.getRegistrationFragment();
        registrationFragment.onCreate(null);
        registrationFragment.startCountDownTimer();
    }

    @Test
    public void should_AppendAtLast() {
        callRegistrationFragment();
        String normalText = "We’ve just sent an SMS with a six digit code to your phone number %s";
        SpannableString expText = new SpannableString("We’ve just sent an SMS with a six digit code to your phone number 342342342342");
        SpannableString actual = mobileVerifyCodeFragment.setDescription(normalText, "342342342342");
        assertEquals(expText.toString(), actual.toString());
    }

    @Test
    public void should_AppendInBetween() {
        callRegistrationFragment();
        String normalText = "We’ve just sent an SMS with a six digit code %s to your phone number";
        SpannableString expText = new SpannableString("We’ve just sent an SMS with a six digit code 342342342342 to your phone number");
        SpannableString actual = mobileVerifyCodeFragment.setDescription(normalText, "342342342342");
        assertEquals(expText.toString(), actual.toString());

    }

    @Test
    public void should_AppendAtStart() {
        callRegistrationFragment();
        String normalText = "%s We’ve just sent an SMS with a six digit code to your phone number";
        SpannableString expText = new SpannableString("342342342342 We’ve just sent an SMS with a six digit code to your phone number");
        SpannableString actual = mobileVerifyCodeFragment.setDescription(normalText, "342342342342");
        assertEquals(expText.toString(), actual.toString());

    }
}