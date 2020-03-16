package com.philips.cdp.registration.ui.traditional;

import android.content.Context;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingErrors;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.handlers.TraditionalRegistrationHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;

import javax.inject.Inject;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.FIREBASE_SUCCESSFUL_REGISTRATION_DONE;

public class CreateAccountPresenter implements NetworkStateListener, EventListener, TraditionalRegistrationHandler {

    private static final String TAG = "CreateAccountPresenter";

    private final static int EMAIL_ADDRESS_ALREADY_USE_CODE = 390;
    private final static int TOO_MANY_REGISTARTION_ATTEMPTS = 510;

    private final CreateAccountContract createAccountContract;

    @Inject
    User user;

    @Inject
    RegistrationHelper registrationHelper;

    @Inject
    EventHelper eventHelper;

    public CreateAccountPresenter(CreateAccountContract createAccountContract) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        this.createAccountContract = createAccountContract;
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.d(TAG, " onNetWorkStateReceived : " + isOnline);
        createAccountContract.handleUiState();
        createAccountContract.updateUiStatus();
    }

    public void registerListener() {
        registrationHelper.registerNetworkStateListener(this);
        eventHelper.registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
    }

    public void unRegisterListener() {
        registrationHelper.unRegisterNetworkListener(this);
        eventHelper.unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        eventHelper.unregisterEventNotification(RegConstants.JANRAIN_INIT_FAILURE, this);
    }


    public void registerUserInfo(User user, String firstName, String lastName, String email,
                                 String password, boolean olderThanAgeLimit, boolean isReceiveMarketingEmail) {
        user.registerUserInfoForTraditional(firstName, lastName, email
                , password, olderThanAgeLimit, isReceiveMarketingEmail, this);
    }

    @Override
    public void onEventReceived(String event) {
        RLog.d(TAG, "CreateAccoutFragment :onCounterEventReceived : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            createAccountContract.updateUiStatus();
        }
    }

    @Override
    public void onRegisterSuccess() {

        handleRegistrationSuccess();

    }

    @Override
    public void onRegisterFailedWithFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        handleRegisterFailedWithFailure(userRegistrationFailureInfo);
    }

    private void handleRegistrationSuccess() {
        RLog.d(TAG, "onRegisterSuccess");
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()) {
            createAccountContract.storeTermsAndConditons();
        }

        if (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired()) {
            createAccountContract.storePersonalConsent();
        }
        createAccountContract.hideSpinner();
        createAccountContract.trackCheckMarketing();
        selectABTestingFlow();
        accountCreationTime();
    }

    public void accountCreationTime() {
        if (createAccountContract.getTrackCreateAccountTime() == 0 && RegUtility.getCreateAccountStartTime() > 0) {
            createAccountContract.setTrackCreateAccountTime((System.currentTimeMillis() - RegUtility.getCreateAccountStartTime()) / 1000);
        } else {
            createAccountContract.setTrackCreateAccountTime((System.currentTimeMillis() - createAccountContract.getTrackCreateAccountTime()) / 1000);
        }
//        createAccountContract.tractCreateActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.TOTAL_TIME_CREATE_ACCOUNT, String.valueOf(createAccountContract.getTrackCreateAccountTime()));

        createAccountContract.setTrackCreateAccountTime(0);
    }

    private void selectABTestingFlow() {
        final UIFlow abTestingUIFlow = RegUtility.getUiFlow();
        createAccountContract.tractCreateActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SUCCESS_USER_CREATION);
        ABTestClientInterface abTestClientInterface = RegistrationConfiguration.getInstance().getComponent().getAbTestClientInterface();
        abTestClientInterface.tagEvent(FIREBASE_SUCCESSFUL_REGISTRATION_DONE, null);
        switch (abTestingUIFlow) {
            case FLOW_A:
                RLog.d(TAG, "UI Flow Type A ");
                setABTestingFlow();
                break;
            case FLOW_B:
                RLog.d(TAG, "UI Flow Type B");
                createAccountContract.launchMarketingAccountFragment();
                break;
        }
    }

    private void setABTestingFlow() {
        if (RegistrationConfiguration.getInstance().isEmailVerificationRequired()) {
            if (FieldsValidator.isValidEmail(createAccountContract.getEmail())) {
                createAccountContract.launchAccountActivateFragment();
            } else {
                createAccountContract.launchMobileVerifyCodeFragment();
            }
        } else {
            createAccountContract.completeRegistration();
        }
    }


    private void handleRegisterFailedWithFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.e(TAG, "handleRegisterFailedWithFailure : onRegisterFailedWithFailure" + userRegistrationFailureInfo.getErrorCode());
        createAccountContract.registrtionFail();
        if (userRegistrationFailureInfo.getErrorCode() == ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION) {
            final Context fragmentContext = createAccountContract.getFragmentContext();
            String alreadyTxt;
            if (RegistrationHelper.getInstance().isMobileFlow()) {
                alreadyTxt = String.format(fragmentContext.getString(R.string.USR_Janrain_EntityAlreadyExists_ErrorMsg), fragmentContext.getString(R.string.USR_DLS_Phonenumber_Label_Text));
                createAccountContract.emailError(alreadyTxt);
            } else {
                alreadyTxt = String.format(fragmentContext.getString(R.string.USR_Janrain_EntityAlreadyExists_ErrorMsg), fragmentContext.getString(R.string.USR_DLS_Email_Label_Text));
                createAccountContract.emailError(alreadyTxt);
            }

            createAccountContract.scrollViewAutomaticallyToEmail();
            createAccountContract.userIdAlreadyUsedShowError();
        } else {
            createAccountContract.setErrorCode(userRegistrationFailureInfo.getErrorCode());
        }
//        else if (userRegistrationFailureInfo.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
//            createAccountContract.serverConnectionError(R.string.reg_JanRain_Server_Connection_Failed);
//        } else if (userRegistrationFailureInfo.getErrorCode() == TOO_MANY_REGISTARTION_ATTEMPTS) {
//            createAccountContract.genericError(R.string.reg_Generic_Network_Error);
//        } else {
//            createAccountContract.genericError(userRegistrationFailureInfo.getErrorDescription());
//        }

        AppTaggingErrors.trackActionRegisterError(userRegistrationFailureInfo, AppTagingConstants.JANRAIN);
    }

}
