package com.philips.cdp.registration.ui.traditional;

import com.philips.cdp.registration.*;
import com.philips.cdp.registration.dao.*;
import com.philips.cdp.registration.events.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.utils.*;

import javax.inject.*;

/**
 * Created by philips on 22/06/17.
 */

public class AccountActivationResendMailPresenter implements NetworStateListener, ResendVerificationEmailHandler {

    private final AccountActivationResendMailContract accountActivationContract;

    @Inject
    User user;

    @Inject
    RegistrationHelper registrationHelper;


    public AccountActivationResendMailPresenter(AccountActivationResendMailContract accountActivationContract) {
        URInterface.getComponent().inject(this);
        this.accountActivationContract = accountActivationContract;
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "CreateAccoutFragment :onNetWorkStateReceived : " + isOnline);
        accountActivationContract.handleUiState(isOnline);
    }

    public void registerListener() {
        registrationHelper.registerNetworkStateListener(this);
    }

    public void unRegisterListener() {
        registrationHelper.unRegisterNetworkListener(this);
    }


    @Override
    public void onResendVerificationEmailSuccess() {
        accountActivationContract.handleResendVerificationEmailSuccess();
    }

    @Override
    public void onResendVerificationEmailFailedWithError(
            final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        accountActivationContract.handleResendVerificationEmailFailedWithError(userRegistrationFailureInfo);
    }

    void resendMail(User user, String email) {
        user.resendVerificationMail(email, this);
    }

}
