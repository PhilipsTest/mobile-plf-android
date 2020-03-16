package com.philips.cdp.registration.ui.social;


import android.os.Bundle;

import com.janrain.android.Jump;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.handlers.SocialLoginProviderHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.LoginFailureNotification;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pif.chi.datamodel.ConsentStates;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.FIREBASE_SUCCESSFUL_REGISTRATION_DONE;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SUCCESS_LOGIN;
import static com.philips.cdp.registration.ui.utils.UIFlow.FLOW_A;

public class AlmostDonePresenter implements NetworkStateListener, SocialLoginProviderHandler, UpdateUserDetailsHandler {


    @Inject
    User mUser;

    private final AlmostDoneContract almostDoneContract;

    private String mGivenName;

    private String mDisplayName;

    private String mFamilyName;

    private String mEmail;

    private boolean isEmailExist;

    private String mProvider;

    private String mRegistrationToken;

    private Bundle mBundle;

    private boolean isOnline = true;
    private String TAG = "AlmostDoneFragment";

    public AlmostDonePresenter(AlmostDoneContract almostDoneContract, User user) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        this.mUser = user;
        this.almostDoneContract = almostDoneContract;
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
    }

    public void cleanUp() {
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        setOnline(isOnline);
        updateUIControls();
    }

    public void updateUIControls() {
        if (isEmailExist) {
            if (isOnline()) {
                almostDoneContract.enableContinueBtn();
            } else {
                almostDoneContract.handleOfflineMode();
            }
        } else {
            if (isOnline()) {
                almostDoneContract.validateEmailFieldUI();
            } else {
                almostDoneContract.handleOfflineMode();
            }
        }
    }

    public void handleAcceptTermsAndReceiveMarketingOpt() {
        if (RegistrationConfiguration.getInstance().isEmailVerificationRequired()) {
            if (isEmailExist && almostDoneContract.getPreferenceStoredState((mEmail))) {
                almostDoneContract.hideAcceptTermsView();

                updateTermsAndReceiveMarketingOpt(false);
            }
            if (mBundle != null && mBundle.getString(RegConstants.SOCIAL_TWO_STEP_ERROR) != null) {
                almostDoneContract.updateABTestingUIFlow();
                almostDoneContract.showAcceptTermsView();
            }
        } else {
            almostDoneContract.hideAcceptTermsView();
        }
        if(RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())
            almostDoneContract.showPersonalConsentOptCheck();
        else
            almostDoneContract.hidePersonalConsentOptCheck();

        updateTermsAndReceiveMarketingOpt(setMarketingOptinVisible());
    }

    private boolean setMarketingOptinVisible() {
        return (RegUtility.getUiFlow() != UIFlow.FLOW_B) ? true : mUser.isTermsAndConditionAccepted();
    }

    public void updateTermsAndReceiveMarketingOpt(boolean optinState) {
        if (mUser.isTermsAndConditionAccepted()) {
            almostDoneContract.updateTermsAndConditionView();
        }
        if (mUser.isPersonalConsentAccepted()) {
            almostDoneContract.updatePersonalConsentView();
        }




        if (!mUser.getReceiveMarketingEmail() && optinState && !RegistrationConfiguration.getInstance().isCustomOptoin() && !RegistrationConfiguration.getInstance().isSkipOptin()) {
            almostDoneContract.showMarketingOptCheck();
        } else if ( !mUser.getReceiveMarketingEmail() && RegUtility.getUiFlow() == (FLOW_A)) {
            almostDoneContract.showMarketingOptCheck();
        } else if (!mUser.getReceiveMarketingEmail() && !RegistrationConfiguration.getInstance().isCustomOptoin() && !RegistrationConfiguration.getInstance().isSkipOptin()) {
            almostDoneContract.showMarketingOptCheck();
            if (mBundle != null && mBundle.getString(RegConstants.SOCIAL_TWO_STEP_ERROR) != null) {
                almostDoneContract.hideMarketingOptCheck();
            }
        } else {
            almostDoneContract.hideMarketingOptCheck();
        }
    }

    @Override
    public void onLoginSuccess() {
        AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                SUCCESS_LOGIN);
        AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.KEY_COUNTRY_SELECTED,
                RegistrationHelper.getInstance().getCountryCode());
        ABTestClientInterface abTestClientInterface = RegistrationConfiguration.getInstance().getComponent().getAbTestClientInterface();
        abTestClientInterface.tagEvent(FIREBASE_SUCCESSFUL_REGISTRATION_DONE, null);
//        AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.KEY_COUNTRY_SELECTED,
//                RegistrationHelper.getInstance().getCountryCode());
    }

    @Override
    public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        handleLoginFailed(userRegistrationFailureInfo);
    }

    private void handleLoginFailed(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        almostDoneContract.hideMarketingOptSpinner();
        EventBus.getDefault().post(new LoginFailureNotification());
        if (userRegistrationFailureInfo.getErrorCode() == ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION) {
            if (RegistrationHelper.getInstance().isMobileFlow()) {
                almostDoneContract.phoneNumberAlreadyInuseError();
            } else {
                almostDoneContract.emailAlreadyInuseError();
            }
        } else if (userRegistrationFailureInfo.getErrorCode() == ErrorCodes.HSDP_INPUT_ERROR_3160) {
            almostDoneContract.showTryAgainError();
        } else {
            almostDoneContract.showAnyOtherErrors(userRegistrationFailureInfo.getErrorDescription());
        }
    }

    @Override
    public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord,
                                              String socialRegistrationToken) {
        almostDoneContract.hideMarketingOptSpinner();
    }

    @Override
    public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider,
                                                String conflictingIdentityProvider, String conflictingIdpNameLocalized,
                                                String existingIdpNameLocalized, String emailId) {
        almostDoneContract.hideMarketingOptSpinner();
        almostDoneContract.addMergeAccountFragment();
    }


    @Override
    public void onContinueSocialProviderLoginFailure(final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        handleContinueSocialProviderFailed(userRegistrationFailureInfo);
    }

    private void handleContinueSocialProviderFailed(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        almostDoneContract.hideMarketingOptSpinner();
        if (null != userRegistrationFailureInfo.getErrorDescription()) {
            almostDoneContract.emailErrorMessage(userRegistrationFailureInfo);
        } else {
            emailAlreadyInUse(userRegistrationFailureInfo);
        }
    }

    private void emailAlreadyInUse(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        if (userRegistrationFailureInfo.getErrorCode() == ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION) {
            if (RegistrationHelper.getInstance().isMobileFlow()) {
                almostDoneContract.phoneNumberAlreadyInuseError();
            } else {
                almostDoneContract.emailAlreadyInuseError();
            }
        }
    }

    @Override
    public void onContinueSocialProviderLoginSuccess() {
        storeEmailOrMobileInPreference();
        almostDoneContract.handleContinueSocialProvider();
    }

    public void parseRegistrationInfo(Bundle bundle) {
        mBundle = bundle;
        if (null != bundle) {
            handleSocialTwoStepError(bundle);
        }
        if (null != mProvider) {
            mProvider = Character.toUpperCase(mProvider.charAt(0)) + mProvider.substring(1);
        }
        if (isEmailExist) {
            almostDoneContract.emailFieldHide();
        } else {
            if (bundle == null) {
                almostDoneContract.enableContinueBtn();
            } else {
                almostDoneContract.showEmailField();
            }
        }
    }

    private void handleSocialTwoStepError(Bundle bundle) {
        try {
            if (bundle.getString(RegConstants.SOCIAL_TWO_STEP_ERROR) != null) {
                JSONObject mPreRegJson = new JSONObject(bundle.getString(RegConstants.SOCIAL_TWO_STEP_ERROR));
                performSocialTwoStepError(mPreRegJson, bundle);
            }
            if (null == mGivenName) {
                mGivenName = mDisplayName;
            }

        } catch (JSONException e) {
            RLog.e(TAG, "handleSocialTwoStepError JSONException : " + e.getMessage());
        }
    }

    private void performSocialTwoStepError(JSONObject mPreRegJson, Bundle mBundle) {
        try {
            if (null != mPreRegJson) {
                mProvider = mBundle.getString(RegConstants.SOCIAL_PROVIDER);
                mRegistrationToken = mBundle.getString(RegConstants.SOCIAL_REGISTRATION_TOKEN);

                if (!mPreRegJson.isNull(RegConstants.REGISTER_GIVEN_NAME)
                        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
                        .getString(RegConstants.REGISTER_GIVEN_NAME))) {
                    setGivenName(mPreRegJson.getString(RegConstants.REGISTER_GIVEN_NAME));
                }
                if (!mPreRegJson.isNull(RegConstants.REGISTER_DISPLAY_NAME)
                        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
                        .getString(RegConstants.REGISTER_DISPLAY_NAME))) {
                    setDisplayName(mPreRegJson.getString(RegConstants.REGISTER_DISPLAY_NAME));
                }
                if (!mPreRegJson.isNull(RegConstants.REGISTER_FAMILY_NAME)
                        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
                        .getString(RegConstants.REGISTER_FAMILY_NAME))) {
                    setFamilyName(mPreRegJson.getString(RegConstants.REGISTER_FAMILY_NAME));
                }
                if (!mPreRegJson.isNull(RegConstants.REGISTER_EMAIL)
                        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
                        .getString(RegConstants.REGISTER_EMAIL))) {
                    setEmail(mPreRegJson.getString(RegConstants.REGISTER_EMAIL));
                    setEmailExist(true);
                } else {
                    setEmailExist(false);
                }
            }
        } catch (JSONException e) {
            RLog.e(TAG, "AlmostDoneFragment Exception : " + e.getMessage());
        }
    }

    @Override
    public void onUpdateSuccess() {
        almostDoneContract.hideMarketingOptSpinner();
        almostDoneContract.trackMarketingOpt();
    }

    @Override
    public void onUpdateFailedWithError(final Error error) {
        handleUpdateReceiveMarket(error.getErrCode());
    }

    private void handleUpdateReceiveMarket(int error) {
        almostDoneContract.hideMarketingOptSpinner();
        if (error == ErrorCodes.HSDP_INPUT_ERROR_1151) {
            almostDoneContract.replaceWithHomeFragment();
            return;
        }
        if (error == ErrorCodes.UNKNOWN_ERROR || error == ErrorCodes.BAD_RESPONSE_ERROR_CODE) {
            almostDoneContract.failedToConnectToServer();
            return;
        }
        almostDoneContract.updateMarketingOptFailedError();
    }

    public void updateUser(boolean isReMarketingOptCheck) {
        if (Jump.getSignedInUser() != null) {
            almostDoneContract.showMarketingOptSpinner();
            mUser.updateReceiveMarketingEmail(this, isReMarketingOptCheck);
        }
    }

    public void register(boolean isReMarketingOptCheck, String email) {
        if (isOnline()) {
            almostDoneContract.hideErrorMessage();
            almostDoneContract.showMarketingOptSpinner();
            mUser.registerUserInfoForSocial(mGivenName, mDisplayName, mFamilyName, isEmailExist ? mEmail : email, true,
                    isReMarketingOptCheck, this, mRegistrationToken);
            setEmail(email);
        }
    }

    public void storeEmailOrMobileInPreference() {
        if (FieldsValidator.isValidEmail(mEmail)) {
            almostDoneContract.storePreference(mEmail);
            return;
        }
        String mobileNo = mUser.getMobile();
        String email = mUser.getEmail();

        if (FieldsValidator.isValidMobileNumber(mobileNo)) {
            almostDoneContract.storePreference(mobileNo);
        }
        if (FieldsValidator.isValidEmail(email)) {
            almostDoneContract.storePreference(email);
        }
    }

    public boolean isValidEmail() {
        return FieldsValidator.isValidEmail(mEmail);
    }

    public boolean isEmailVerificationStatus() {
        return (mUser.isEmailVerified() || mUser.isMobileVerified());
    }

    public void handleClearUserData() {
        mUser.logout(null);
    }

    public void handleUpdateMarketingOpt() {
        if (isOnline()) {
            almostDoneContract.handleUpdateUser();
        } else {
            almostDoneContract.marketingOptCheckDisable();
        }
    }


    public void handleTraditionalTermsAndCondition() {
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()
                && (!RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())) {
            if (almostDoneContract.isAcceptTermsChecked()) {
                almostDoneContract.handleAcceptTermsTrue();
            } else {
                almostDoneContract.showTermsAndConditionError();
            }
        } else if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()
                && (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())) {
            if (almostDoneContract.isAcceptTermsChecked() && almostDoneContract.isAcceptPersonalConsentChecked()) {
                almostDoneContract.handleAcceptTermsTrue();
                almostDoneContract.handleAcceptPersonalConsentTrue();
            } else if (almostDoneContract.isAcceptTermsChecked() && !almostDoneContract.isAcceptPersonalConsentChecked()) {
                almostDoneContract.hideTermsAndConditionError();
                almostDoneContract.showPersonalConsentError();
            } else if (almostDoneContract.isAcceptPersonalConsentChecked() && !almostDoneContract.isAcceptTermsChecked()) {
                almostDoneContract.showTermsAndConditionError();
                almostDoneContract.hideAcceptPersonalConsentChecked();
            } else {
                almostDoneContract.showTermsAndConditionError();
                almostDoneContract.showPersonalConsentError();
            }
        } else if (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired()) {
            if (almostDoneContract.isAcceptPersonalConsentChecked()) {
                almostDoneContract.handleAcceptPersonalConsentTrue();
            } else {
                almostDoneContract.showPersonalConsentError();
            }
        } else {
            almostDoneContract.completeRegistration();
        }
    }

    void handleSocialTermsAndCondition() {
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                (!RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired() )) {
            if (almostDoneContract.isAcceptTermsChecked()) {
                register(almostDoneContract.isMarketingOptChecked(), almostDoneContract.getEmailOrMobileNumber());
            } else {
                almostDoneContract.showTermsAndConditionError();
            }
        } else if (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired()
                && RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() && almostDoneContract.isAcceptTermsContainerVisible()) {
            if (almostDoneContract.isAcceptTermsChecked() && almostDoneContract.isAcceptPersonalConsentChecked()) {
                register(almostDoneContract.isMarketingOptChecked(), almostDoneContract.getEmailOrMobileNumber());
            } else if (almostDoneContract.isAcceptTermsChecked() && !almostDoneContract.isAcceptPersonalConsentChecked()) {
                almostDoneContract.hideTermsAndConditionError();
                almostDoneContract.showPersonalConsentError();
            } else if (almostDoneContract.isAcceptPersonalConsentChecked() && !almostDoneContract.isAcceptTermsChecked()) {
                almostDoneContract.showTermsAndConditionError();
                almostDoneContract.hideAcceptPersonalConsentChecked();
            } else {
                almostDoneContract.showTermsAndConditionError();
                almostDoneContract.showPersonalConsentError();
            }

        } else if (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired()) {
            if (almostDoneContract.isAcceptPersonalConsentChecked()) {
                almostDoneContract.handleAcceptPersonalConsentTrue();
            } else {
                almostDoneContract.showPersonalConsentError();
            }
        } else {
            register(almostDoneContract.isMarketingOptChecked(), almostDoneContract.getEmailOrMobileNumber());
        }
    }

    public String getGivenName() {
        return mGivenName;
    }

    public void setGivenName(String mGivenName) {
        this.mGivenName = mGivenName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getFamilyName() {
        return mFamilyName;
    }

    public void setFamilyName(String mFamilyName) {
        this.mFamilyName = mFamilyName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public boolean isEmailExist() {
        return isEmailExist;
    }

    public void setEmailExist(boolean emailExist) {
        isEmailExist = emailExist;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}


