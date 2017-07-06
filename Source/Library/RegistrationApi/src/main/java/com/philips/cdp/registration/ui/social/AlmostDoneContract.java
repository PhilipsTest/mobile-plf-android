package com.philips.cdp.registration.ui.social;

import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;


public interface AlmostDoneContract {

    void handleUiAcceptTerms();

    void hideAcceptTermsView();

    void updateTermsAndConditionView();

    void updateReceiveMarketingView();

    void showMarketingOptSpinner();

    void hideMarketingOptSpinner();

    void showLoginFailedError();

    void phoneNumberAlreadyInuseError();

    void emailAlreadyInuseError();


    void emailErrorMessage(UserRegistrationFailureInfo userRegistrationFailureInfo);

    void handleContinueSocialProvider();

    void addMergeAccountFragment();

    void emailFieldHide();

    void showEmailField();

    void trackMarketingOpt();

    void replaceWithHomeFragment();

    void failedToConnectToServer();

    void updateMarketingOptFailedError();

    void hideErrorMessage();

    void storePreference(String emailOrMobileNumber);

    boolean getPreferenceStoredState(String emailOrMobileNumber);

    void updateABTestingUIFlow();

    void enableContinueBtn();

    void handleOfflineMode();

    void validateEmailFieldUI();

    void handleUpdateUser();

    void marketingOptCheckDisable();

    void showMarketingOptCheck();

    void hideMarketingOptCheck();

    boolean isAcceptTermsChecked();

    void handleAcceptTermsTrue();

    void launchWelcomeFragment();

    void showTermsAndConditionError();

    boolean isAcceptTermsContainerVisible();

    boolean isMarketingOptChecked();

    String getMobileNumber();
}
