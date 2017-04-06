package com.philips.cdp.registration.ui.social;

import com.philips.cdp.registration.BuildConfig;
import com.philips.cdp.registration.HttpClientServiceReceiver;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeContract;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodePresenter;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.URInterface;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlmostDonePresenterTest {


    @Mock
    private RegistrationComponent mockRegistrationComponent;

    @Mock
    private AlmostDoneContract mockContract;

    @Mock
    private NetworkUtility mockNetworkUtility;

    @Mock
    RegistrationConfiguration mockRregistrationConfiguration;


    private AlmostDonePresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        URInterface.setComponent(mockRegistrationComponent);
        presenter = new AlmostDonePresenter(mockContract);
    }

    @After
    public void tearDown() throws Exception {
        mockRegistrationComponent = null;
        mockContract = null;
        presenter = null;
    }

    @Test
    public void testNetworkState_Enabled() {
        when(mockNetworkUtility.isNetworkAvailable()).thenReturn(true);
        presenter.onNetWorkStateReceived(true);
        verify(mockContract).validateEmailFieldUI();
    }

    @Test
    public void testNetworkState_Disabled() {
        when(mockNetworkUtility.isNetworkAvailable()).thenReturn(false);
        presenter.onNetWorkStateReceived(false);
        verify(mockContract).handleOfflineMode();
    }

    @Test
    public void testHandleAcceptTermsAndReceiveMarketingOpt(){
        when(mockRregistrationConfiguration.isTermsAndConditionsAcceptanceRequired()).thenReturn(true);
        presenter.handleAcceptTermsAndReceiveMarketingOpt();
        verify(mockContract).hideAcceptTermsView();
    }


}