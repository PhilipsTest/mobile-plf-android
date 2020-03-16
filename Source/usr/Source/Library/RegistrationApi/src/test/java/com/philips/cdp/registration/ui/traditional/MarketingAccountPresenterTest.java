package com.philips.cdp.registration.ui.traditional;

import android.content.Context;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.JanrainErrorEnum;
import com.philips.cdp.registration.injection.RegistrationComponent;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;

/**
 * Created by philips on 11/23/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class MarketingAccountPresenterTest {

    @Mock
    MarketingAccountContract marketingAccountContractMock;
    @Mock
    private RegistrationComponent registrationComponentMock;

    @Mock
    private LoggingInterface mockLoggingInterface;

    @Mock
    private Context mockContext;

    private MarketingAccountPresenter marketingAccountPresenter;

    @Mock
    private User userMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RegistrationConfiguration.getInstance().setComponent(registrationComponentMock);
        RLog.setMockLogger(mockLoggingInterface);

        marketingAccountPresenter = new MarketingAccountPresenter(marketingAccountContractMock);
    }

    @Test
    public void onNetWorkStateReceived() throws Exception {
        marketingAccountPresenter.onNetWorkStateReceived(true);

        Mockito.verify(marketingAccountContractMock).handleUiState();
    }

    @Test
    public void onUpdateSuccess() throws Exception {
        marketingAccountPresenter.onUpdateSuccess();

        Mockito.verify(marketingAccountContractMock).trackRemarketing();
        RLog.d("MarketingAccountFragment", "onUpdateSuccess ");
        Mockito.verify(marketingAccountContractMock).hideRefreshProgress();
        Mockito.verify(marketingAccountContractMock).handleRegistrationSuccess();
    }

    @Test
    public void onUpdateFailedWithError() throws Exception {
        marketingAccountPresenter.onUpdateFailedWithError(new Error(eq(ErrorCodes.UPDATE_USER_DETAILS_ERROR), eq(Mockito.any())));
        Mockito.verify(marketingAccountContractMock).hideRefreshProgress();
    }

    @Mock
    RegistrationHelper registrationHelperMock;

    @Test
    public void register() throws Exception {

        // Mockito.when(RegistrationHelper.getInstance()).thenReturn(registrationHelperMock);
        //marketingAccountPresenter.register();
    }

    @Test
    public void unRegister() throws Exception {

        // marketingAccountPresenter.unRegister();
    }

    @Test
    public void updateMarketingEmail() throws Exception {

        marketingAccountPresenter.updateMarketingEmail(userMock, true);

        Mockito.verify(userMock).updateReceiveMarketingEmail(marketingAccountPresenter, true);
    }

}