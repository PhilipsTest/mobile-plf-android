
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.SMSBroadCastReceiver;
import com.philips.cdp.registration.ui.utils.UpdateMobile;
import com.philips.cdp.registration.ui.utils.UpdateToken;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.janrain.android.Jump.getRedirectUri;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.REGISTRATION_ACTIVATION_SMS;

public class MobileForgotPassVerifyCodeFragment extends RegistrationBaseFragment implements
        MobileForgotPassVerifyCodeContract, OnUpdateListener {

    private final String TAG = this.getClass().getSimpleName();
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

    private SMSBroadCastReceiver mSMSBroadCastReceiver;
    private boolean isUserTyping = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final String verificationSmsCodeURLKey = "verificationSmsCodeURL";

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
        mSMSBroadCastReceiver = new SMSBroadCastReceiver(this);
        View view = inflater.inflate(R.layout.reg_mobile_forgotpassword_verify_fragment, container, false);
        trackActionStatus(REGISTRATION_ACTIVATION_SMS, "", "");
        ButterKnife.bind(this, view);
        handleOrientation(view);
        getRegistrationFragment().startCountDownTimer();
        setDescription();
        Handler handler = new Handler();
        handleVerificationCode();
        return view;
    }

    private void setDescription() {
        String normalText = getString(R.string.reg_DLS_VerifySMS_Description_Text);
        SpannableString str = new SpannableString(String.format(normalText, mobileNumber));
        str.setSpan(new StyleSpan(Typeface.BOLD), normalText.length() - 2, str.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        verifyPasswordDesc1.setText(str);
    }

    private void handleVerificationCode() {
        RxTextView.textChangeEvents(verificationCodeValidationEditText)
                .subscribe(new Consumer<TextViewTextChangeEvent>() {
                    @Override
                    public void accept(TextViewTextChangeEvent aBoolean) throws Exception {
                        decideToEnableVerifyButton();
                    }
                });
    }

    private void decideToEnableVerifyButton() {
        disableVerifyButton();
        isUserTyping = false;
        if(verificationCodeValidationEditText.getText().length() == 0) return;
        if (verificationCodeValidationEditText.getText().length() < 6 ) {
            isUserTyping = true;
        } else
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
        registerSMSReceiver();
    }

    @Subscribe
    public void onEvent(UpdateToken event) {
        responseToken = event.getToken();
    }

    @Subscribe
    public void onEvent(UpdateMobile event) {
        if (this.isVisible()) {
            mobileNumber = event.getMobileNumber();
            setDescription();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MobileActivationFragment : onConfigurationChanged");
        super.onConfigurationChanged(config);
        setCustomParams(config);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RegistrationHelper.getInstance().unRegisterNetworkListener(getRegistrationFragment());
        mobileVerifyCodePresenter.cleanUp();
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
        return R.string.reg_SigIn_TitleTxt;
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
        verifyButton.showProgressIndicator();
        smsNotReceived.setEnabled(false);
        verificationCodeValidationEditText.setEnabled(false);
        getRegistrationFragment().hideKeyBoard();
        resetSmsPassword();
        removeThisFragmentFromStack();
    }

    private void removeThisFragmentFromStack() {
        getActivity().getFragmentManager().popBackStack();
    }

    public void resetSmsPassword() {

        RLog.d("MobileVerifyCodeFragment ", "response" + verificationCodeValidationEditText.getText()
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

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    @Override
    public SMSBroadCastReceiver getSMSBroadCastReceiver() {
        return mSMSBroadCastReceiver;
    }


    public void hideProgressSpinner() {
        verifyButton.hideProgressIndicator();
        smsNotReceived.setEnabled(true);
        verificationCodeValidationEditText.setEnabled(true);
        enableVerifyButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case SMSBroadCastReceiver.SMS_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerSMSReceiver();
                }
            }

        }
    }


    @Override
    public void registerSMSReceiver() {
        if (mSMSBroadCastReceiver.isSmsPermissionGranted()) {
            mobileVerifyCodePresenter.registerSMSReceiver();
        } else {
            mSMSBroadCastReceiver.requestReadAndSendSmsPermission();
        }
    }

    @Override
    public void unRegisterSMSReceiver() {
        mobileVerifyCodePresenter.unRegisterSMSReceiver();
    }

    @Override
    public void onOTPReceived(String otp) {
        RLog.i(TAG, "onOTPReceived : got otp");
        if (!isUserTyping) {
            verificationCodeValidationEditText.setText(otp);
            if(new NetworkUtility(getActivityContext()).isInternetAvailable()) {
                verifyClicked();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterSMSReceiver();
    }

    @Override
    public void notificationInlineMsg(String msg) {
        errorMessage.setError(msg);
    }
}
