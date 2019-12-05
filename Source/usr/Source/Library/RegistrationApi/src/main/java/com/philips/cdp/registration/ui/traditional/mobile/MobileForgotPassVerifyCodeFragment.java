
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional.mobile;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.LocaleSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.RegistrationSettings;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.UpdateMobile;
import com.philips.cdp.registration.ui.utils.UpdateToken;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.janrain.android.Jump.getRedirectUri;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.REGISTRATION_ACTIVATION_SMS;

public class MobileForgotPassVerifyCodeFragment extends RegistrationBaseFragment implements
        MobileForgotPassVerifyCodeContract, OnUpdateListener {

    private final String TAG = "MobileForgotPassVerifyCodeFragment";
    @Inject
    NetworkUtility networkUtility;

    @BindView(R2.id.btn_reg_Verify)
    ProgressBarButton verifyButton;

    @BindView(R2.id.btn_reg_resend_code)
    Button smsNotReceived;

    @BindView(R2.id.reg_error_msg)
    XRegError errorMessage;

    @BindView(R2.id.reg_verify_mobile_desc1)
    Label verifyPasswordDesc1;

    @BindView(R2.id.usr_forgotpassword_inputId_ValidationEditText)
    ValidationEditText verificationCodeValidationEditText;

    @BindView(R2.id.usr_verification_root_layout)
    LinearLayout usrVerificationRootLayout;

    private Context context;

    private MobileForgotPassVerifyCodePresenter mobileVerifyCodePresenter;

    private String verificationSmsCodeURL;

    private String mobileNumber;

    private String responseToken;

    private String redirectUriValue;

    static final String MOBILE_NUMBER_KEY = "mobileNumber";
    static final String RESPONSE_TOKEN_KEY = "token";
    static final String RE_DIRECT_URI_KEY = "redirectUri";
    String normalText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final String verificationSmsCodeURLKey = "verificationSmsCodeURL";
        RLog.i(TAG, "Screen name is " + TAG);


        RegistrationConfiguration.getInstance().getComponent().inject(this);

        registerInlineNotificationListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mobileNumber = bundle.getString(MOBILE_NUMBER_KEY);
            responseToken = bundle.getString(RESPONSE_TOKEN_KEY);
            redirectUriValue = bundle.getString(RE_DIRECT_URI_KEY);
            verificationSmsCodeURL = bundle.getString(verificationSmsCodeURLKey);
        }

        mobileVerifyCodePresenter = new MobileForgotPassVerifyCodePresenter(this);
        View view = inflater.inflate(R.layout.reg_mobile_forgotpassword_verify_fragment, container, false);
        trackActionStatus(REGISTRATION_ACTIVATION_SMS, "", "");
        ButterKnife.bind(this, view);
        handleOrientation(view);
        getRegistrationFragment().startCountDownTimer();
        normalText = getString(R.string.USR_DLS_VerifySMS_Description_Text);

        SpannableString description = setDescription(normalText, mobileNumber);
        verifyPasswordDesc1.setText(description);
        handleVerificationCode();
        return view;
    }

    @VisibleForTesting
    protected SpannableString setDescription( String normalText, String mobileNumber) {
        String formattedStr = String.format(normalText, mobileNumber);
        StringBuilder fStringBuilder = new StringBuilder(formattedStr);
        SpannableString str = new SpannableString(formattedStr);
        str.setSpan(new StyleSpan(Typeface.BOLD), fStringBuilder.indexOf(mobileNumber), fStringBuilder.indexOf(mobileNumber) + mobileNumber.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       return str;
    }

    private void handleVerificationCode() {
        RxTextView.textChangeEvents(verificationCodeValidationEditText)
                .subscribe(aBoolean -> decideToEnableVerifyButton());
    }

    private void decideToEnableVerifyButton() {
        disableVerifyButton();
        if (verificationCodeValidationEditText.getText().length() == 0) return;
        if (!(verificationCodeValidationEditText.getText().length() < 6))
            enableVerifyButton();
    }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(UpdateToken event) {
        responseToken = event.getToken();
    }

    @Subscribe
    public void onEvent(UpdateMobile event) {
        if (this.isVisible()) {
            mobileNumber = event.getMobileNumber();
            SpannableString description = setDescription(normalText, mobileNumber);
            verifyPasswordDesc1.setText(description);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        RLog.d(TAG, " onConfigurationChanged");
        super.onConfigurationChanged(config);
        setCustomParams(config);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RegistrationHelper.getInstance().unRegisterNetworkListener(getRegistrationFragment());
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        // Do not do anything
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.USR_SigIn_TitleTxt;
    }

    private void updateUiStatus() {
        if (verificationCodeValidationEditText.getText().length()
                >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) {
            enableVerifyButton();
        } else {
            disableVerifyButton();
        }
    }

    public void handleUI() {
        updateUiStatus();
    }

    @Override
    public void onUpdate() {
        handleUI();
    }

    @OnClick(R2.id.btn_reg_Verify)
    public void verifyClicked() {
        RLog.i(TAG, TAG + ".forgotpassword verify clicked");
        verifyButton.showProgressIndicator();
        smsNotReceived.setEnabled(false);
        verificationCodeValidationEditText.setEnabled(false);
        getRegistrationFragment().hideKeyBoard();
        resetSmsPassword();
    }


    public void resetSmsPassword() {

        RLog.d(TAG, "response" + verificationCodeValidationEditText.getText()
                + " " + redirectUriValue + " " + responseToken);
        constructRedirectUri();
        final String redirectUriKey = "redirectUriValue";
        ResetPasswordWebView resetPasswordWebView = new ResetPasswordWebView();
        Bundle bundle = new Bundle();
        bundle.putString(redirectUriKey, redirectUriValue);
        resetPasswordWebView.setArguments(bundle);
        getRegistrationFragment().addFragment(resetPasswordWebView);
    }

    private void constructRedirectUri() {
        redirectUriValue = redirectUriValue + "?code=" + verificationCodeValidationEditText.getText()
                + "&token=" + responseToken;
    }

    @OnClick(R2.id.btn_reg_resend_code)
    public void resendButtonClicked() {
        RLog.i(TAG,TAG+".resendButton clicked");
        verificationCodeValidationEditText.setText("");
        final String lMobileNumberKey = "mobileNumber";
        final String tokenKey = "token";
        final String redirectUriKey = "redirectUriValue";
        final String verificationSmsCodeURLKey = "verificationSmsCodeURL";
        disableVerifyButton();
        verifyButton.hideProgressIndicator();
        errorMessage.hideError();
        addFragment(lMobileNumberKey, tokenKey, redirectUriKey, verificationSmsCodeURLKey);
    }

    private void addFragment(String mobileNumberKey, String tokenKey, String redirectUriKey,
                             String verificationSmsCodeURLKey) {
        MobileForgotPassVerifyResendCodeFragment mobileForgotPasswordVerifyCodeFragment
                = new MobileForgotPassVerifyResendCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(mobileNumberKey, mobileNumber);
        bundle.putString(tokenKey, responseToken);
        bundle.putString(redirectUriKey, getRedirectUri());
        bundle.putString(verificationSmsCodeURLKey, verificationSmsCodeURL);
        mobileForgotPasswordVerifyCodeFragment.setArguments(bundle);
        getRegistrationFragment().addFragment(mobileForgotPasswordVerifyCodeFragment);
    }

    public void enableVerifyButton() {
        if ((verificationCodeValidationEditText.getText().length()
                >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH)
                && networkUtility.isNetworkAvailable()) {
            verifyButton.setEnabled(true);
        }
    }

    public void disableVerifyButton() {
        verifyButton.setEnabled(false);
    }

    @Override
    public void netWorkStateOnlineUiHandle() {
        if (verificationCodeValidationEditText.getText().length()
                >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) {
            verifyButton.setEnabled(true);
        }
        errorMessage.hideError();
        smsNotReceived.setEnabled(true);
    }

    @Override
    public void netWorkStateOfflineUiHandle() {
        hideProgressSpinner();
        errorMessage.setError(new URError(context).getLocalizedError(ErrorType.NETWOK, ErrorCodes.NO_NETWORK));
        smsNotReceived.setEnabled(false);
        disableVerifyButton();
    }



    public void hideProgressSpinner() {
        verifyButton.hideProgressIndicator();
        smsNotReceived.setEnabled(true);
        verificationCodeValidationEditText.setEnabled(true);
        enableVerifyButton();
    }

    @Override
    public void notificationInlineMsg(String msg) {
        errorMessage.setError(msg);
    }


}
