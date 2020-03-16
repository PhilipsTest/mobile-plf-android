package com.philips.cdp.registration.ui.traditional;

import android.content.Context;

/**
 * Created by philips on 22/06/17.
 */

public interface CreateAccountContract {
    void handleUiState();
    void updateUiStatus();

    Context getFragmentContext();

    void launchMarketingAccountFragment();

    void launchMobileVerifyCodeFragment();

    void launchAccountActivateFragment();

    void completeRegistration();

    void emailError(String errorDescID);

    void setErrorCode(int errorDescID);

//    void setErrorCode(String errorDesc);
//
//    void genericError(String errorDesc);
//
//    void genericError(int errorDescID);

//    void serverConnectionError(int errorDesc);

    String getEmail();

    long getTrackCreateAccountTime();

    void setTrackCreateAccountTime(long trackCreateAccountTime);

    void tractCreateActionStatus(String state, String key, String value);

    void trackCheckMarketing();

    void scrollViewAutomaticallyToEmail();

    void scrollViewAutomaticallyToError();

    void hideSpinner();

    void storeTermsAndConditons();

    void userIdAlreadyUsedShowError();

    void registrtionFail();

    void storePersonalConsent();
}
