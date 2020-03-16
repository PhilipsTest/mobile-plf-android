
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.LoginIdValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.PasswordValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.ValidLoginId;
import com.philips.cdp.registration.ui.utils.ValidPassword;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.uid.view.widget.CheckBox;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAccountFragment extends RegistrationBaseFragment implements CreateAccountContract {

    private String TAG = "CreateAccountFragment";

    @Inject
    NetworkUtility networkUtility;

    @BindView(R2.id.usr_createScreen_passwordValidation_Layout)
    LinearLayout usrCreateScreenPasswordValidationLayout;

    @BindView(R2.id.usr_createscreen_marketingmails_checkbox)
    CheckBox usrCreatescreenMarketingmailsCheckbox;

    @BindView(R2.id.usr_createscreen_termsandconditions_checkbox)
    CheckBox usrCreatescreenTermsandconditionsCheckbox;

    @BindView(R2.id.usr_createscreen_personal_consent_checkbox)
    CheckBox usrCreatescreenPersonalConsentCheckbox;

    @BindView(R2.id.usr_createScreen_firstName_textField)
    ValidationEditText usrCreateScreenFirstNameTextField;

    @BindView(R2.id.usr_createScreen_lastName_textField)
    ValidationEditText usrCreateScreenLastNameTextField;

    @BindView(R2.id.usr_createScreen_lastName_label)
    Label lastNamelabel;

    @BindView(R2.id.usr_createScreen_firstName_inputValidation)
    InputValidationLayout usrCreateScreenFirstNameInputValidation;

    @BindView(R2.id.usr_createScreen_lastName_inputValidation)
    InputValidationLayout usrCreateScreenLastNameInputValidation;

    @BindView(R2.id.usr_createScreen_password_textField)
    ValidationEditText usrCreateScreenPasswordTextField;

    @BindView(R2.id.usr_createScreen_password_inputValidationField)
    InputValidationLayout usrCreateScreenPasswordInputValidationField;

    @BindView(R2.id.usr_createscreen_error_view)
    XRegError usrCreatescreenErrorView;

    @BindView(R2.id.usr_createscreen_termsandconditionsalert_view)
    XRegError usrCreatescreenTermsandconditionsalertView;

    @BindView(R2.id.usr_createscreen_personal_consent_alert_view)
    XRegError usrCreatescreenPersonalConsentalertView;

    @BindView(R2.id.usr_createScreen_rootLayout_scrollView)
    ScrollView usrCreateScreenRootLayoutScrollView;

    @BindView(R2.id.usr_createscreen_emailormobile_inputValidationLayout)
    InputValidationLayout usrCreatescreenEmailormobileInputValidationLayout;

    @BindView(R2.id.usr_createscreen_emailormobile_textfield)
    ValidationEditText usrCreatescreenEmailormobileTextfield;


    @BindView(R2.id.usr_createscreen_password_progressbar)
    ProgressBar usrCreatescreenPasswordProgressbar;

    @BindView(R2.id.usr_createscreen_passwordstrength_label)
    Label usrCreatescreenPasswordstrengthLabel;

    @BindView(R2.id.usr_createscreen_passwordhint_label)
    Label usrCreatescreenPasswordhintLabel;

    @BindView(R2.id.usr_createscreen_switchtologin_button)
    Button usrCreatescreenSwitchtologinButton;

    @BindView(R2.id.usr_createscreen_create_button)
    ProgressBarButton usrCreatescreenCreateButton;

    @BindView(R2.id.usr_reg_root_layout)
    LinearLayout usrCreateScreenBaseLayoutLinearLayout;

    @BindView(R2.id.usr_createscreen_emailormobile_label)
    Label usrCreatescreenEmailormobileLabel;

    private User user;

    private Context context;

    private long trackCreateAccountTime;

    private CreateAccountPresenter createAccountPresenter;

    private String emailString;

    boolean isValidEmail;

    boolean isValidPassword;

    boolean isValidFirstname;

    boolean isValidLastame = true;

    PasswordValidator passwordValidator = new PasswordValidator(new ValidPassword() {
        @Override
        public int getStrength(int strength) {
            usrCreateScreenPasswordValidationLayout.setVisibility(View.VISIBLE);
            return passwordValidation(strength);
        }
    });


    public LoginIdValidator loginIdValidator = new LoginIdValidator(new ValidLoginId() {
        @Override
        public int isValid(boolean valid) {
            isValidEmail = valid;
            enableCreateButton();
            if (!valid) {
                if (RegistrationHelper.getInstance().isMobileFlow()) {
                    usrCreatescreenEmailormobileInputValidationLayout.setErrorMessage(R.string.USR_InvalidPhoneNumber_ErrorMsg);
                } else {
                    usrCreatescreenEmailormobileInputValidationLayout.setErrorMessage(R.string.USR_InvalidOrMissingEmail_ErrorMsg);
                }
            } else
                usrCreatescreenEmailormobileInputValidationLayout.hideError();
            return 0;
        }

        @Override
        public int isEmpty(boolean emptyField) {
            if (emptyField && !RegistrationHelper.getInstance().isMobileFlow()) {
                usrCreatescreenEmailormobileInputValidationLayout.setErrorMessage(
                        R.string.USR_InvalidOrMissingEmail_ErrorMsg);
            } else if (emptyField && RegistrationHelper.getInstance().isMobileFlow()) {
                usrCreatescreenEmailormobileInputValidationLayout.setErrorMessage(
                        R.string.USR_EmptyField_ErrorMsg);
            } else {
                usrCreatescreenEmailormobileInputValidationLayout.hideError();
            }

            isValidEmail = false;
            disableCreateButton();
            return 0;
        }
    });
    private boolean consentStates;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        RLog.i(TAG, "Screen name is " + TAG);


        View view = inflater.inflate(R.layout.reg_fragment_create_account, container, false);
        registerInlineNotificationListener(this);
        createAccountPresenter = new CreateAccountPresenter(this);
        createAccountPresenter.registerListener();

        ButterKnife.bind(this, view);
        usrCreatescreenEmailormobileInputValidationLayout.setValidator(loginIdValidator);
        usrCreateScreenPasswordInputValidationField.setValidator(passwordValidator);
        usrCreateScreenPasswordInputValidationField.setErrorMessage(R.string.USR_PasswordField_ErrorMsg);
        initUI(view);

        handleABTestingFlow();
        handleUiState();
        user = new User(context);
        handleOrientation(view);
        trackCreateAccountTime = System.currentTimeMillis();
        return view;
    }

    void setContentConfig() {
        if (null != getRegistrationFragment().getContentConfiguration()) {
            if (getRegistrationFragment().getContentConfiguration().getEnableLastName()) {
                usrCreateScreenLastNameTextField.setVisibility(View.VISIBLE);
                lastNamelabel.setVisibility(View.VISIBLE);
                isValidLastame = false;
            }
        }
    }

    private int passwordValidation(int strength) {

        int strengthMedium = 2;
        int strengthWeak = 1;
        int strengthMeterNone = 0;
        int strengthMeterWeak = 33;
        int strengthMeterStrong = 100;
        int strengthMeterMedium = 66;

        RLog.d(TAG,
                " register: NetworkStateListener,strength " + strength);
        if (strength > strengthMedium) {
            passwordUiUpdate(getResources().getString(R.string.USR_Password_Strength_Strong), strengthMeterStrong, true, R.color.strong_strength_progress, R.color.strong_strength_background,
                    R.drawable.reg_password_strength_strong, 0, true);
        } else if (strength == strengthMedium) {
            passwordUiUpdate(getResources().getString(R.string.USR_Password_Strength_Medium), strengthMeterMedium, true, R.color.medium_strength_progress, R.color.medium_strength_background,
                    R.drawable.reg_password_strength_medium, 0, false);
        } else if (strength == strengthWeak) {
            passwordUiUpdate(getResources().getString(R.string.USR_Password_Strength_Weak), strengthMeterWeak, false, R.color.weak_strength_progress, R.color.weak_strength_background,
                    R.drawable.reg_password_strength_weak, R.string.USR_InValid_PwdErrorMsg, false);
        } else {
            passwordUiUpdate(getResources().getString(R.string.USR_Password_Strength_Weak), strengthMeterNone, false, R.color.weak_strength_progress, R.color.weak_strength_background,
                    R.drawable.reg_password_strength_weak, R.string.USR_PasswordField_ErrorMsg, false);
        }
        return 0;
    }

    private void passwordUiUpdate(String weak, int progress, boolean enabled, int color1, int color, int drawable, int invalidPasswordErrorId, boolean isPasswordValid) {
        usrCreatescreenPasswordstrengthLabel.setText(weak);
        usrCreatescreenPasswordstrengthLabel.setTextColor(ContextCompat.getColor(getContext(), color1));
        usrCreatescreenPasswordProgressbar.setProgress(progress);
        usrCreatescreenPasswordhintLabel.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        usrCreatescreenPasswordProgressbar.setProgressDrawable(getResources().getDrawable(drawable, null));
        isValidPassword = isPasswordValid;

        if (invalidPasswordErrorId != 0) {
            usrCreateScreenPasswordInputValidationField.setErrorMessage(getResources().getString(invalidPasswordErrorId));
        }

        if (isValidPassword) {
            usrCreatescreenPasswordhintLabel.setVisibility(View.GONE);
            enableCreateButton();
        } else {
            usrCreatescreenPasswordhintLabel.setVisibility(View.VISIBLE);
            disableCreateButton();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        RLog.d(TAG, " : onDestroy");
        if (createAccountPresenter != null)
            createAccountPresenter.unRegisterListener();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        RLog.d(TAG, " : onConfigurationChanged");
        super.onConfigurationChanged(config);
        setCustomParams(config);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        // applyParams(config, usrCreateScreenBaseLayoutLinearLayout, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    private void initUI(View view) {
        consumeTouch(view);
        setContentConfig();
        RegUtility.linkifyTermsandCondition(usrCreatescreenTermsandconditionsCheckbox, getRegistrationFragment().getParentActivity(), mTermsAndConditionClick);
        RegUtility.linkifyPhilipsNews(usrCreatescreenMarketingmailsCheckbox, getRegistrationFragment().getParentActivity(), mPhilipsNewsClick);
        if (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())
            RegUtility.linkifyPersonalConsent(usrCreatescreenPersonalConsentCheckbox, getRegistrationFragment().getParentActivity(), mPersonalConsentClick, getRegistrationFragment().getContentConfiguration());
        ((RegistrationFragment) getParentFragment()).showKeyBoard();
        usernameUihandle();
        if (RegistrationHelper.getInstance().isMobileFlow()) {
            usrCreatescreenEmailormobileLabel.setText(R.string.USR_DLS_Phonenumber_Label_Text);
            usrCreatescreenEmailormobileTextfield.setInputType(InputType.TYPE_CLASS_PHONE);
        }

        usrCreatescreenCreateButton.setEnabled(false);
        usrCreatescreenTermsandconditionsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            removeFocus();
            TextView tv = (TextView) buttonView;
            usrCreatescreenTermsandconditionsalertView.hideError();
            if (tv.getSelectionStart() == -1 && tv.getSelectionEnd() == -1) {
                // No link is clicked
                if (!isChecked) {
                    usrCreatescreenTermsandconditionsalertView.setError(context.getResources().getString(R.string.USR_TermsAndConditionsAcceptanceText_Error));
                }
            } else {
                usrCreatescreenTermsandconditionsCheckbox.setChecked(!isChecked);
                if (RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener() != null) {
                    RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener().
                            onTermsAndConditionClick(getRegistrationFragment().getParentActivity());
                } else {
                    RegUtility.showErrorMessage(getRegistrationFragment().getParentActivity());
                }
            }
        });

        if (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired()) {
            usrCreatescreenPersonalConsentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                removeFocus();
                TextView tv = (TextView) buttonView;
                usrCreatescreenPersonalConsentalertView.hideError();
                if(tv.getSelectionStart() == -1 && tv.getSelectionEnd() == -1) {
                    if (!isChecked) {
                        usrCreatescreenPersonalConsentalertView.setError(context.getResources().getString(getRegistrationFragment().getContentConfiguration().getPersonalConsentContentErrorResId()));
                    }
                }else {
                    usrCreatescreenPersonalConsentCheckbox.setChecked(!isChecked);
                    if (RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener() != null) {
                        RegistrationConfiguration.getInstance().getUserRegistrationUIEventListener().
                                onPersonalConsentClick(getRegistrationFragment().getParentActivity());
                    } else {
                        RegUtility.showErrorMessage(getRegistrationFragment().getParentActivity());
                    }
                }
            });

        } else {
            usrCreatescreenPersonalConsentalertView.setVisibility(View.GONE);
            usrCreatescreenPersonalConsentCheckbox.setVisibility(View.GONE);
        }
        usrCreatescreenMarketingmailsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                removeFocus();
                TextView tv = (TextView) compoundButton;
                if (!(tv.getSelectionStart() == -1 && tv.getSelectionEnd() == -1)) {
                    usrCreatescreenMarketingmailsCheckbox.setChecked(!isChecked);
                    getRegistrationFragment().addPhilipsNewsFragment();
                }
            }
        });
    }


    void removeFocus(){
        ((RegistrationFragment) getParentFragment()).hideKeyBoard();
        usrCreateScreenLastNameTextField.clearFocus();
        usrCreateScreenFirstNameTextField.clearFocus();
        usrCreatescreenEmailormobileTextfield.clearFocus();
        usrCreateScreenPasswordTextField.clearFocus();
    }
    private void handleABTestingFlow() {
        final UIFlow abTestingUIFlow = RegUtility.getUiFlow();
        switch (abTestingUIFlow) {

            case FLOW_A:
                RLog.d(TAG, "UI Flow Type A");
                usrCreatescreenMarketingmailsCheckbox.setVisibility(View.VISIBLE);
                trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.AB_TEST,
                        AppTagingConstants.REGISTRATION_CONTROL);
                break;
            case FLOW_B:

                RLog.d(TAG, "UI Flow Type B");
                usrCreatescreenMarketingmailsCheckbox.setVisibility(View.GONE);
                trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.AB_TEST,
                        AppTagingConstants.REGISTRATION_SPLIT_SIGN_UP);
                break;
            default:
                break;
        }
    }

    private void registerUserInfo() {
        showSpinner();
        usrCreatescreenTermsandconditionsalertView.setVisibility(View.GONE);
        usrCreatescreenPersonalConsentalertView.setVisibility(View.GONE);
        usrCreateScreenFirstNameTextField.clearFocus();
        usrCreateScreenLastNameTextField.clearFocus();
        usrCreatescreenEmailormobileTextfield.clearFocus();
        usrCreateScreenPasswordTextField.clearFocus();
        usrCreatescreenErrorView.hideError();
        if (FieldsValidator.isValidEmail(usrCreatescreenEmailormobileTextfield.getText().toString())) {
            emailString = usrCreatescreenEmailormobileTextfield.getText().toString();
        } else {
            emailString = FieldsValidator.getMobileNumber(usrCreatescreenEmailormobileTextfield.getText().toString());
        }
        RLog.d(TAG, "create : LastName " + usrCreateScreenLastNameTextField.getText().toString());

        createAccountPresenter.registerUserInfo(user, usrCreateScreenFirstNameTextField.getText().toString(), usrCreateScreenLastNameTextField.getText().toString(), emailString
                , usrCreateScreenPasswordTextField.getText().toString(), true, usrCreatescreenMarketingmailsCheckbox.isChecked());
    }


    private ClickableSpan mTermsAndConditionClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
        }
    };

    private ClickableSpan mPersonalConsentClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            trackPage(AppTaggingPages.PERSONAL_CONSENT);
        }
    };

    private ClickableSpan mPhilipsNewsClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            trackPage(AppTaggingPages.PHILIPS_ANNOUNCEMENT);
        }
    };

    @Override
    public void trackCheckMarketing() {
        trackRemarketing();
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() && RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired()) {
            if (usrCreatescreenTermsandconditionsCheckbox.isChecked()) {
                trackActionForAcceptTermsOption(AppTagingConstants.ACCEPT_TERMS_OPTION_IN);
            } else {
                trackActionForAcceptTermsOption(AppTagingConstants.ACCEPT_TERMS_OPTION_OUT);
            }

            if (usrCreatescreenPersonalConsentCheckbox.isChecked()) {
                trackActionForPersonalConsentOption(AppTagingConstants.ACCEPT_PERSONAL_CONSENT_OPTION_IN);
            } else {
                trackActionForPersonalConsentOption(AppTagingConstants.ACCEPT_PERSONAL_CONSENT_OPTION_OUT);
            }
        }
    }


    private void trackRemarketing() {
        final UIFlow abTestingUIFlow = RegUtility.getUiFlow();
        if (!abTestingUIFlow.equals(UIFlow.FLOW_B)) {
            if (usrCreatescreenMarketingmailsCheckbox.isChecked()) {
                trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_IN);
            } else {
                trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_OUT);
            }
        }
    }

    private void showSpinner() {
        usrCreatescreenCreateButton.showProgressIndicator();
        disableCreateButton();
        usrCreatescreenSwitchtologinButton.setEnabled(false);
    }

    @Override
    public void hideSpinner() {
        ThreadUtils.postInMainThread(context, () -> {
            usrCreatescreenCreateButton.hideProgressIndicator();
            usrCreatescreenSwitchtologinButton.setEnabled(true);
            enableCreateButton();
        });
    }

    @Override
    public void storeTermsAndConditons() {
        RegPreferenceUtility.storePreference(context, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailString);
    }

    @Override
    public void storePersonalConsent(){
        RegPreferenceUtility.storePreference(context, RegConstants.PERSONAL_CONSENT, emailString);
    }

    @Override
    public void userIdAlreadyUsedShowError() {
        ThreadUtils.postInMainThread(context, () -> {
            usrCreatescreenEmailormobileInputValidationLayout.showError();
        });
    }

    @Override
    public void registrtionFail() {
        usrCreatescreenCreateButton.hideProgressIndicator();
        usrCreatescreenSwitchtologinButton.setEnabled(true);
        disableCreateButton();
    }

    @Override
    public void handleUiState() {
        if (networkUtility.isNetworkAvailable()) {
            usrCreatescreenErrorView.hideError();
            hideNotificationBarView();
        }else {
            showNotificationBarOnNetworkNotAvailable();
        }
    }

    @Override
    public void launchMarketingAccountFragment() {
        if(RegistrationConfiguration.getInstance().isCustomOptoin()){
            completeRegistration();
        } else if(RegistrationConfiguration.getInstance().isSkipOptin()){
            if (FieldsValidator.isValidEmail(getEmail())) {
                launchAccountActivateFragment();
            } else {
                launchMobileVerifyCodeFragment();
            }
        } else{
            addFragment(new MarketingAccountFragment());
            trackPage(AppTaggingPages.MARKETING_OPT_IN);

        }
    }

    @Override
    public void launchMobileVerifyCodeFragment() {
        addFragment(new MobileVerifyCodeFragment());
        trackPage(AppTaggingPages.MOBILE_VERIFY_CODE);
    }

    @Override
    public void launchAccountActivateFragment() {
        addFragment(new AccountActivationFragment());
        trackPage(AppTaggingPages.ACCOUNT_ACTIVATION);
    }

    private void addFragment(Fragment fragment) {
        getRegistrationFragment().addFragment(fragment);
    }

    @Override
    public void completeRegistration() {
        getRegistrationFragment().userRegistrationComplete();
    }


    @Override
    public int getTitleResourceId() {
        return R.string.USR_DLS_URCreateAccount_NavTitle;
    }

    @Override
    public void updateUiStatus() {
        if (networkUtility.isNetworkAvailable()) {
            enableCreateButton();
            usrCreatescreenErrorView.hideError();
            hideNotificationBarView();
        } else {
            disableCreateButton();
            showNotificationBarOnNetworkNotAvailable();
        }
        if (usrCreatescreenEmailormobileInputValidationLayout.isShowingError()) {
            usrCreatescreenEmailormobileInputValidationLayout.hideError();
        }
    }

    @Override
    public Context getFragmentContext() {
        return context;
    }

    @Override
    public void setErrorCode(int errorCode) {
        updateErrorNotification(new URError(context).getLocalizedError(ErrorType.JANRAIN, errorCode), errorCode);
        enableCreateButton();
    }

    @Override
    public void emailError(String errorDescResId) {
        usrCreatescreenEmailormobileInputValidationLayout.setErrorMessage(errorDescResId);

    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public long getTrackCreateAccountTime() {
        return trackCreateAccountTime;
    }

    @Override
    public void setTrackCreateAccountTime(long trackCreateAccountTime) {
        this.trackCreateAccountTime = trackCreateAccountTime;
    }

    @Override
    public void tractCreateActionStatus(String state, String key, String value) {
        trackActionStatus(state, key, value);
    }

    @Override
    public void scrollViewAutomaticallyToEmail() {
        ThreadUtils.postInMainThread(context, () -> scrollViewAutomatically(usrCreatescreenEmailormobileTextfield, usrCreateScreenRootLayoutScrollView));
    }

    @Override
    public void scrollViewAutomaticallyToError() {

        ThreadUtils.postInMainThread(context, () -> scrollViewAutomatically(usrCreatescreenErrorView, usrCreateScreenRootLayoutScrollView));
    }

    @OnClick(R2.id.usr_createscreen_switchtologin_button)
    public void setSwitchToLogin() {
        RLog.i(TAG, TAG + ".setSwitchToLogin");
        getRegistrationFragment().addFragment(new SignInAccountFragment());
    }

    void enableCreateButton() {
        if (isValidPassword && isValidEmail && isValidFirstname && isValidLastame && networkUtility.isNetworkAvailable()) {
            usrCreatescreenCreateButton.setEnabled(true);
        }
    }

    void disableCreateButton() {
        usrCreatescreenCreateButton.setEnabled(false);
    }


    private void usernameUihandle() {

        usrCreateScreenFirstNameInputValidation.setValidator(firstName -> FieldsValidator.isValidName(firstName.toString()));
        usrCreateScreenLastNameInputValidation.setValidator(lastName -> FieldsValidator.isValidName(lastName.toString()));
        usrCreateScreenFirstNameInputValidation.setErrorMessage((R.string.USR_NameField_ErrorText));
        usrCreateScreenLastNameInputValidation.setErrorMessage((R.string.USR_LastNameField_ErrorMsg));

        usrCreateScreenFirstNameTextField.requestFocus();
        usrCreateScreenFirstNameTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    isValidFirstname = true;
                    enableCreateButton();
                    usrCreateScreenFirstNameInputValidation.setErrorMessage((R.string.USR_InvalidOrMissingName_ErrorMsg));
                    usrCreateScreenLastNameInputValidation.setErrorMessage((R.string.USR_InvalidOrMissingName_ErrorMsg));
                } else {
                    isValidFirstname = false;
                    disableCreateButton();
                    usrCreateScreenFirstNameInputValidation.setErrorMessage((R.string.USR_NameField_ErrorText));
                    usrCreateScreenLastNameInputValidation.setErrorMessage((R.string.USR_LastNameField_ErrorMsg));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        usrCreateScreenLastNameTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    isValidLastame = true;
                    enableCreateButton();
                } else {
                    isValidLastame = false;
                    disableCreateButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R2.id.usr_createscreen_create_button)
    public void createButtonWithProgressBar() {

        ((RegistrationFragment) getParentFragment()).hideKeyBoard();

        RLog.d(TAG, "createButtonWithProgressBar: Create Account");
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired() )) {
            if (usrCreatescreenTermsandconditionsCheckbox.isChecked() && usrCreatescreenPersonalConsentCheckbox.isChecked()) {
                registerUserInfo();
            } else if (!usrCreatescreenTermsandconditionsCheckbox.isChecked()) {
                usrCreatescreenTermsandconditionsalertView.setError(context.getResources().getString(R.string.USR_TermsAndConditionsAcceptanceText_Error));
            }

            if (!usrCreatescreenPersonalConsentCheckbox.isChecked()) {
                usrCreatescreenPersonalConsentalertView.setError(context.getResources().getString(getRegistrationFragment().getContentConfiguration().getPersonalConsentContentErrorResId()));
            }
        } else if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()) {
            if (usrCreatescreenTermsandconditionsCheckbox.isChecked()) {
                registerUserInfo();
            } else {
                usrCreatescreenTermsandconditionsalertView.setError(context.getResources().getString(R.string.USR_TermsAndConditionsAcceptanceText_Error));
            }
        }
    }

    @Override
    public void notificationInlineMsg(String msg) {
        usrCreatescreenErrorView.setError(msg);
    }
}