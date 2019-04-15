
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.social;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.AccountActivationFragment;
import com.philips.cdp.registration.ui.traditional.MarketingAccountFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.LoginIdValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.ValidLoginId;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.uid.view.widget.CheckBox;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.FIREBASE_SUCCESSFUL_REGISTRATION_DONE;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SUCCESS_USER_CREATION;

public class AlmostDoneFragment extends RegistrationBaseFragment implements AlmostDoneContract,
        OnUpdateListener {

    private static String TAG = "AlmostDoneFragment";

    @BindView(R2.id.usr_almostDoneScreen_termsAndConditions_checkBox)
    CheckBox acceptTermsCheck;

    @BindView(R2.id.usr_almostDoneScreen_acceptTerms_error)
    XRegError acceptTermserrorMessage;

    @BindView(R2.id.usr_almostDoneScreen_marketingMails_checkBox)
    CheckBox marketingOptCheck;

    @BindView(R2.id.usr_almostDoneScreen_error_regError)
    XRegError errorMessage;

    @BindView(R2.id.usr_almostDoneScreen_email_inputValidationLayout)
    InputValidationLayout loginIdEditText;

    @BindView(R2.id.usr_almostDoneScreen_email_EditText)
    ValidationEditText emailEditText;

    @BindView(R2.id.usr_almostDoneScreen_email_label)
    Label emailTitleLabel;

    @BindView(R2.id.usr_almostDoneScreen_continue_button)
    ProgressBarButton continueButton;

    @BindView(R2.id.usr_almostDoneScreen_rootLayout_scrollView)
    ScrollView rootLayout;

    @BindView(R2.id.usr_almostDoneScreen_description_label)
    Label almostDoneDescriptionLabel;

    @BindView(R2.id.usr_almostDoneScreen_rootContainer_linearLayout)
    LinearLayout usr_almostDoneScreen_rootContainer_linearLayout;

    @Inject
    User mUser;

    private AlmostDonePresenter almostDonePresenter;

    private Context mContext;

    private Bundle mBundle;

    boolean isValidEmail;


    public LoginIdValidator loginIdValidator = new LoginIdValidator(new ValidLoginId() {
        @Override
        public int isValid(boolean valid) {
            isValidEmail = valid;
            continueButton.setEnabled(isValidEmail);
            return 0;
        }


        @Override
        public int isEmpty(boolean emptyField) {
            if (emptyField) {
                loginIdEditText.setErrorMessage(
                        R.string.USR_EmptyField_ErrorMsg);
            } else {
                if (RegistrationHelper.getInstance().isMobileFlow()) {
                    loginIdEditText.setErrorMessage(
                            R.string.USR_InvalidEmailOrPhoneNumber_ErrorMsg);
                } else {
                    loginIdEditText.setErrorMessage(
                            R.string.USR_InvalidEmailAdddress_ErrorMsg);
                }
            }
            isValidEmail = false;
            continueButton.setEnabled(isValidEmail);
            return 0;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        mBundle = getArguments();
        if (null != mBundle) {
            trackAbtesting();
        }
        registerInlineNotificationListener(this);
        View view = inflater.inflate(R.layout.reg_fragment_social_almost_done, container, false);
        initializeUI(view);
        RLog.i(TAG, "Screen name is" + TAG);
        return view;
    }

    private void initializeUI(View view) {
        RLog.d(TAG, "initializeUI : is called");
        ButterKnife.bind(this, view);
        loginIdEditText.setValidator(loginIdValidator);
        almostDoneDescriptionLabel.setText("");
        almostDoneDescriptionLabel.setVisibility(View.GONE);
        almostDonePresenter = new AlmostDonePresenter(this, mUser);
        initUI(view);
        handleUiAcceptTerms();
        almostDonePresenter.parseRegistrationInfo(mBundle);
        almostDonePresenter.updateUIControls();
        handleOrientation(view);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        RLog.d(TAG, "onAttach : is called");
    }

    @Override
    public void onDestroy() {
        RLog.d(TAG, "onDestroy : is called");
        if (almostDonePresenter != null) {
            almostDonePresenter.cleanUp();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(TAG, "onConfigurationChanged : is called");
        setCustomParams(config);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        //Do nothing
    }

    @Override
    protected void handleOrientation(View view) {
        RLog.d(TAG, "handleOrientation : is called");
        handleOrientationOnView(view);
    }

    private void initUI(View view) {
        consumeTouch(view);

        acceptTermsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                {
                    TextView tv = (TextView) buttonView;
                    acceptTermserrorMessage.hideError();
                    if (tv.getSelectionStart() == -1 && tv.getSelectionEnd() == -1) {
                        // No link is clicked
                        if (!isChecked) {
                            acceptTermserrorMessage.setError(mContext.getResources().getString(R.string.USR_TermsAndConditionsAcceptanceText_Error));
                        }
                    } else {
                        acceptTermsCheck.setChecked(!isChecked);
                        if (RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener() != null) {
                            RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener()
                                    .onTermsAndConditionClick(getRegistrationFragment().getParentActivity());
                        } else {
                            RegUtility.showErrorMessage(getRegistrationFragment().getParentActivity());
                        }
                    }
                }
            }
        });


        marketingOptCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                {
                    TextView tv = (TextView) compoundButton;
                    if (!(tv.getSelectionStart() == -1 && tv.getSelectionEnd() == -1)) {
                        marketingOptCheck.setChecked(!b);
                        getRegistrationFragment().addPhilipsNewsFragment();

                    }
                }
            }
        });


        if (RegistrationHelper.getInstance().isMobileFlow()) {
            RLog.d(TAG, "initUI : isMobileFlow true");
            emailTitleLabel.setText(R.string.USR_DLS_Phonenumber_Label_Text);
            emailEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        }

        marketingOptCheck.setPadding(RegUtility.getCheckBoxPadding(mContext), marketingOptCheck.getPaddingTop(),
                marketingOptCheck.getPaddingRight(), marketingOptCheck.getPaddingBottom());
        RegUtility.linkifyTermsandCondition(acceptTermsCheck, getRegistrationFragment().getParentActivity(), mTermsAndConditionClick);
        updateReceiveMarketingViewStyle();
    }

    private void updateReceiveMarketingViewStyle() {
        RLog.d(TAG, "updateReceiveMarketingViewStyle : is  called");
        RegUtility.linkifyPhilipsNews(marketingOptCheck, getRegistrationFragment().getParentActivity(), mPhilipsNewsClick);
    }

    @Override
    public void emailFieldHide() {
        RLog.d(TAG, "emailFieldHide : is  called");
        emailEditText.setVisibility(View.GONE);
        emailTitleLabel.setVisibility(View.GONE);
        continueButton.setEnabled(true);
    }

    @Override
    public void showEmailField() {
        RLog.d(TAG, "showEmailField : is  called");
        emailEditText.setVisibility(View.VISIBLE);
        emailTitleLabel.setVisibility(View.VISIBLE);
        almostDoneDescriptionLabel.setVisibility(View.VISIBLE);
        String baseString = mContext.getResources().getString(R.string.USR_DLS_Almost_Done_TextField_Base_Text);
        almostDoneDescriptionLabel.setText(String.format(baseString, mContext.getResources().getString(R.string.USR_Email_address_TitleTxt)));
        if (RegistrationHelper.getInstance().isMobileFlow()) {
            almostDoneDescriptionLabel.setText(String.format(baseString, mContext.getResources().getString(R.string.USR_DLS_Almost_Done_TextField_Mobile_Text)));
        }
        continueButton.setEnabled(false);

    }

    private ClickableSpan mTermsAndConditionClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            RLog.d(TAG, "TermsAndCondition button is  called");

        }
    };

    private ClickableSpan mPhilipsNewsClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            RLog.d(TAG, "PhilipsNewsClick : onClick : Philips ANNOUNCEMENT text is clicked");
            trackPage(AppTaggingPages.PHILIPS_ANNOUNCEMENT);
        }
    };

    @Override
    public void handleUiAcceptTerms() {
        RLog.d(TAG, "handleUiAcceptTerms : is called");
        almostDonePresenter.handleAcceptTermsAndReceiveMarketingOpt();
    }

    @Override
    public void hideAcceptTermsView() {
        acceptTermsCheck.setVisibility(View.GONE);
    }

    @Override
    public void updateTermsAndConditionView() {
        hideAcceptTermsAndConditionContainer();
    }

    @Override
    public void showMarketingOptCheck() {
        marketingOptCheck.setVisibility(View.VISIBLE);
        almostDoneDescriptionLabel.setVisibility(View.VISIBLE);
        almostDoneDescriptionLabel.setText(mContext.getResources().getString(R.string.USR_DLS_Almost_Done_Marketing_OptIn_Text));
    }

    @Override
    public void hideMarketingOptCheck() {
        marketingOptCheck.setVisibility(View.GONE);
        if (emailEditText.getVisibility() != View.VISIBLE) {
            almostDoneDescriptionLabel.setVisibility(View.GONE);
        }
    }

    private void hideAcceptTermsAndConditionContainer() {
        acceptTermsCheck.setChecked(true);
        acceptTermsCheck.setVisibility(View.GONE);
    }

    @Override
    public void updateReceiveMarketingView() {
        marketingOptCheck.setVisibility(View.GONE);
    }

    @Override
    public void updateABTestingUIFlow() {
        final UIFlow abTestingUIFlow = RegUtility.getUiFlow();

        switch (abTestingUIFlow) {
            case FLOW_A:
                RLog.d(TAG, "UI Flow Type A");
                acceptTermsCheck.setVisibility(View.VISIBLE);
                break;

            case FLOW_B:
                RLog.d(TAG, "UI Flow Type B");
                acceptTermsCheck.setVisibility(View.VISIBLE);
                marketingOptCheck.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void validateEmailFieldUI() {
        if ((emailEditText.isShown() && isValidEmail) ||
                (emailEditText.getVisibility() != View.VISIBLE)) {
            continueButton.setEnabled(true);
        }
        errorMessage.hideError();
    }

    @Override
    public void enableContinueBtn() {
        continueButton.setEnabled(true);
        errorMessage.hideError();
    }

    @Override
    public void handleOfflineMode() {
//        errorMessage.setError(new URError(mContext).getLocalizedError(ErrorType.NETWOK, ErrorCodes.NO_NETWORK));
        continueButton.setEnabled(false);
//        scrollViewAutomatically(errorMessage, rootLayout);
    }

    @Override
    public void showMarketingOptSpinner() {
        marketingOptCheck.setEnabled(false);
        continueButton.setEnabled(false);
        continueButton.showProgressIndicator();
    }

    @Override
    public void hideMarketingOptSpinner() {
        marketingOptCheck.setEnabled(true);
        continueButton.setEnabled(true);
        continueButton.hideProgressIndicator();

    }

    @OnClick(R2.id.usr_almostDoneScreen_continue_button)
    public void continueButtonClicked() {
        RLog.i(TAG, TAG + ".continueButton Clicked ");

        loginIdEditText.clearFocus();

        if (marketingOptCheck.getVisibility() == View.VISIBLE && isMarketingOptChecked()) {
            almostDonePresenter.handleUpdateMarketingOpt();
        }
        if (mBundle == null) {
            almostDonePresenter.handleTraditionalTermsAndCondition();
        } else {
            almostDonePresenter.handleSocialTermsAndCondition();
        }
    }

    @Override
    public String getEmailOrMobileNumber() {
        if (FieldsValidator.isValidEmail(emailEditText.getText().toString())) {
            return emailEditText.getText().toString();
        } else {
            return FieldsValidator.getMobileNumber(emailEditText.getText().toString());
        }
    }

    @Override
    public void showTryAgainError() {
        errorMessage.setError(mContext.getString(R.string.USR_Janrain_HSDP_ServerErrorMsg));
        scrollViewAutomatically(errorMessage, rootLayout);
    }

    @Override
    public void showAnyOtherErrors(String errorDescription) {
        errorMessage.setError(errorDescription);
        scrollViewAutomatically(errorMessage, rootLayout);
    }

    @Override
    public boolean isAcceptTermsContainerVisible() {
        return acceptTermsCheck.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean isAcceptTermsChecked() {
        return acceptTermsCheck.isChecked();
    }

    @Override
    public void showTermsAndConditionError() {
        acceptTermserrorMessage.setError(mContext.getResources().getString(R.string.USR_TermsAndConditionsAcceptanceText_Error));
    }

    @Override
    public void handleAcceptTermsTrue() {
        almostDonePresenter.storeEmailOrMobileInPreference();
        trackActionForAcceptTermsOption(AppTagingConstants.ACCEPT_TERMS_OPTION_IN);
        //completeRegistration();;
        handleABTestingFlow();
    }

    @Override
    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailOrMobileNumber);
    }

    private void trackMultipleActions() {
        trackABTestingUIFlow();
        trackTermsAndConditionAccepted();
    }

    private void trackTermsAndConditionAccepted() {
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()) {
            if (acceptTermsCheck.isChecked()) {
                trackActionForAcceptTermsOption(AppTagingConstants.ACCEPT_TERMS_OPTION_IN);
            } else {
                trackActionForAcceptTermsOption(AppTagingConstants.ACCEPT_TERMS_OPTION_OUT);
            }
        }
    }

    private void trackABTestingUIFlow() {
        final UIFlow abTestingUIFlow = RegUtility.getUiFlow();
        if (!abTestingUIFlow.equals(UIFlow.FLOW_B)) {
            trackMarketingOpt();
        }
    }

    @Override
    public void trackMarketingOpt() {
        if (marketingOptCheck.isChecked()) {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_IN);
        } else {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_OUT);
        }
    }

    @Override
    public int getTitleResourceId() {
        return R.string.USR_SigIn_TitleTxt;
    }

    @Override
    public void phoneNumberAlreadyInuseError() {
        final String string = getPhoneOrEmailString(mContext.getResources().getString(R.string.USR_DLS_Phonenumber_Label_Text));
        loginIdEditText.setErrorMessage(string);
        loginIdEditText.showError();
    }

    private String getPhoneOrEmailString(String string) {
        return String.format(mContext.getResources().getString(R.string.USR_Janrain_EntityAlreadyExists_ErrorMsg), string);
    }

    @Override
    public void emailAlreadyInuseError() {
        final String string = getPhoneOrEmailString(mContext.getResources().getString(R.string.USR_DLS_Email_Label_Text));
        loginIdEditText.setErrorMessage(string);
        loginIdEditText.showError();
    }

    @Override
    public void showLoginFailedError() {
        loginIdEditText.showError();
        scrollViewAutomatically(loginIdEditText, rootLayout);
    }

    @Override
    public void addMergeAccountFragment() {
        getRegistrationFragment().addFragment(new MergeAccountFragment());
        trackPage(AppTaggingPages.MERGE_ACCOUNT);
    }

    @Override
    public void handleContinueSocialProvider() {
        RLog.d(TAG, "AlmostDoneFragment : onContinueSocialProviderLoginSuccess");
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                SUCCESS_USER_CREATION);
        ABTestClientInterface abTestClientInterface = RegistrationConfiguration.getInstance().getComponent().getAbTestClientInterface();
        abTestClientInterface.tagEvent(FIREBASE_SUCCESSFUL_REGISTRATION_DONE, null);
        trackMultipleActions();
        handleABTestingFlow();
        hideMarketingOptSpinner();
    }

    private void handleABTestingFlow() {
        final UIFlow abTestingUIFlow = RegUtility.getUiFlow();
        switch (abTestingUIFlow) {
            case FLOW_A:
                RLog.d(TAG, "UI Flow Type A");
                if (almostDonePresenter.isEmailVerificationStatus()) {
                    completeRegistration();
                    trackActionStatus(AppTagingConstants.SEND_DATA,
                            AppTagingConstants.SPECIAL_EVENTS,
                            AppTagingConstants.SUCCESS_USER_REGISTRATION);
                } else {
                    launchAccountActivateFragment();
                }
                break;
            case FLOW_B:
                RLog.d(TAG, "UI Flow Type B");
                if (!marketingOptCheck.isShown() && !mUser.getReceiveMarketingEmail()) {
                    launchMarketingAccountFragment();
                    trackActionStatus(AppTagingConstants.SEND_DATA,
                            AppTagingConstants.SPECIAL_EVENTS,
                            AppTagingConstants.SUCCESS_USER_REGISTRATION);
                } else {
                    completeRegistration();
                }
                break;
            default:
                break;
        }
    }

    public void clearUserData() {
        if (null != acceptTermsCheck && !acceptTermsCheck.isChecked() &&
                RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()) {
            almostDonePresenter.handleClearUserData();
        }
    }

    private void launchMarketingAccountFragment() {
        getRegistrationFragment().addFragment(new MarketingAccountFragment());
        trackPage(AppTaggingPages.MARKETING_OPT_IN);
    }

    private void launchAccountActivateFragment() {
        trackPage(AppTaggingPages.ACCOUNT_ACTIVATION);
        if (almostDonePresenter.isValidEmail()) {
            getRegistrationFragment().addFragment(new AccountActivationFragment());
        } else {
            getRegistrationFragment().addFragment(new MobileVerifyCodeFragment());
        }
    }

    @Override
    public void completeRegistration() {
        getRegistrationFragment().userRegistrationComplete();
    }

    @Override
    public void emailErrorMessage(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        if (userRegistrationFailureInfo.getErrorCode() == ErrorCodes.JANRAIN_INVALID_DATA_FOR_VALIDATION) {
            if (RegistrationHelper.getInstance().isMobileFlow()) {
                phoneNumberAlreadyInuseError();
            } else {
                emailAlreadyInuseError();
            }
        } else {
            loginIdEditText.setErrorMessage(userRegistrationFailureInfo.getErrorDescription());
            loginIdEditText.showError();
        }
    }

    @Override
    public void marketingOptCheckDisable() {
        marketingOptCheck.setOnCheckedChangeListener(null);
        marketingOptCheck.setChecked(!marketingOptCheck.isChecked());
        showNotificationBarOnNetworkNotAvailable();
    }

    @Override
    public void handleUpdateUser() {
        errorMessage.hideError();
        almostDonePresenter.updateUser(marketingOptCheck.isChecked());
    }


    @Override
    public void failedToConnectToServer() {
        errorMessage.setError(mContext.getResources().getString(R.string.USR_Generic_Network_Error));
    }

    @Override
    public void replaceWithHomeFragment() {
        if (getRegistrationFragment() != null) {
            getRegistrationFragment().replaceWithHomeFragment(getRegistrationFragment().getFragmentManager());
        }
    }

    @Override
    public void onUpdate() {
        almostDonePresenter.updateUIControls();
    }

    @Override
    public void updateMarketingOptFailedError() {
        marketingOptCheck.setOnCheckedChangeListener(null);
        marketingOptCheck.setChecked(!marketingOptCheck.isChecked());
    }

    @Override
    public void hideErrorMessage() {
        acceptTermserrorMessage.setVisibility(View.GONE);
    }

    public boolean getPreferenceStoredState(String emailOrMobileNumber) {
        return RegPreferenceUtility.getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailOrMobileNumber);
    }

    @Override
    public boolean isMarketingOptChecked() {
        return marketingOptCheck.isChecked();
    }

    private void trackAbtesting() {
        RLog.d(TAG, "trackAbtesting : is called");
        final UIFlow abTestingFlow = RegUtility.getUiFlow();

        switch (abTestingFlow) {
            case FLOW_A:
                RLog.d(TAG, "UI Flow Type A");
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.AB_TEST,
                        AppTagingConstants.REGISTRATION_CONTROL);
                break;

            case FLOW_B:
                RLog.d(TAG, "UI Flow Type B");
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.AB_TEST,
                        AppTagingConstants.REGISTRATION_SPLIT_SIGN_UP);
                break;
            default:
                break;
        }
    }

    @Override
    public void notificationInlineMsg(String msg) {
        errorMessage.setError(msg);
    }
}