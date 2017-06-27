package com.philips.cdp.registration.ui.traditional;

import com.philips.cdp.registration.BuildConfig;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.ui.traditional.mobile.AddSecureEmailContract;
import com.philips.cdp.registration.ui.traditional.mobile.AddSecureEmailPresenter;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.update.UpdateUserProfile;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CreateAccountPresenterTest {

    @Mock
    RegistrationComponent registrationComponentMock;

    @Mock
    UpdateUserProfile updateUserProfileMock;

    @Mock
    RegistrationConfiguration registrationConfiguration;

    @Mock
    CreateAccountContract contractMock;

    private CreateAccountPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        URInterface.setComponent(registrationComponentMock);
        presenter = new CreateAccountPresenter(contractMock);
       // RegistrationConfigurationtion.setInstanceForTesting(registrationConfiguration);
    }

    @After
    public void tearDown() throws Exception {
        contractMock = null;
        presenter = null;
        updateUserProfileMock = null;
        registrationComponentMock = null;
        registrationConfiguration = null;

    }

    @Test
    public void testAccountCreationTime(){
        presenter.accountCreationTime();

    }

//    @Test
//    public void testOnRegisterSuccess(){
//        when(registrationConfiguration.isTermsAndConditionsAcceptanceRequired()).thenReturn(false);
//        presenter.onRegisterSuccess();
//        verify(contractMock).hideSpinner();
//        verify(contractMock).trackCheckMarketing();
//    }


}