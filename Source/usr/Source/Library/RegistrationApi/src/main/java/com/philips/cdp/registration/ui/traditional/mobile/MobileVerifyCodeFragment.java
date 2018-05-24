
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.SMSBroadCastReceiver;
import com.philips.cdp.registration.ui.utils.UpdateMobile;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.ACTIVATION_NOT_VERIFIED;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_INAPPNATIFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_RESEND_EMAIL_VERFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_RESEND_SMS_VERFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.REGISTRATION_ACTIVATION_SMS;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SEND_DATA;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SPECIAL_EVENTS;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SUCCESS_USER_REGISTRATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.USER_ERROR;

public class MobileVerifyCodeFragment extends RegistrationBaseFragment implements
        MobileVerifyCodeContract, RefreshUserHandler, OnUpdateListener {

    public static String TAG = MobileVerifyCodeFragment.class.getSimpleName();

    @Inject
    NetworkUtility networkUtility;

    @BindView(R2.id.btn_reg_Verify)
    ProgressBarButton verifyButton;

    @BindView(R2.id.btn_reg_resend_code)
    Button smsNotReceived;

    @BindView(R2.id.reg_error_msg)
    XRegError errorMessage;

    @BindView(R2.id.reg_verify_mobile_desc1)
    Label regVerifyMobileDesc1;

    @BindView(R2.id.usr_forgotpassword_inputId_ValidationEditText)
    ValidationEditText verificationCodeValidationEditText;

    @BindView(R2.id.usr_activation_root_layout)
    LinearLayout usrAccountRootLayout;

    private Context context;

    private User user;

    private MobileVerifyCodePresenter mobileVerifyCodePresenter;

    private Handler handler;

    boolean isVerified;
    private SMSBroadCastReceiver mSMSBroadCastReceiver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        user = new User(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);

        mobileVerifyCodePresenter = new MobileVerifyCodePresenter(this);
        mSMSBroadCastReceiver = new SMSBroadCastReceiver(this);

        View view = inflater.inflate(R.layout.reg_mobile_activatiom_fragment, container, false);
        trackActionStatus(REGISTRATION_ACTIVATION_SMS,"","");
        ButterKnife.bind(this, view);
        handleOrientation(view);
        getRegistrationFragment().startCountDownTimer();
        setDescription();
        handler = new Handler();
        handleVerificationCode();
        return view;
    }

    private void setDescription() {
        String userId =  user.getMobile();
        String normalText = getString(R.string.reg_DLS_VerifySMS_Description_Text);
        SpannableString str = new SpannableString(String.format(normalText, userId));
        str.setSpan(new StyleSpan(Typeface.BOLD), normalText.length()-2, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        regVerifyMobileDesc1.setText(str);
    }

    private void handleVerificationCode() {
        RxTextView.textChangeEvents(verificationCodeValidationEditText)
                .subscribe(aBoolean -> {
                    if (verificationCodeValidationEditText.getText().length() == 6)
                        enableVerifyButton();
                    else
                        disableVerifyButton();
                });
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
        EventBus.getDefault().unregister(this);
        RegistrationHelper.getInstance().unRegisterNetworkListener(getRegistrationFragment());
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        //Do not do anything
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_DLS_URCreateAccount_NavTitle;
    }

    private void updateUiStatus() {
        if (verificationCodeValidationEditText.length() >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) {
            enableVerifyButton();
        } else {
            disableVerifyButton();
        }
    }

    public void handleUI() {
        updateUiStatus();
    }

    @Override
    public void onRefreshUserSuccess() {
        if (this.isVisible()) {
            RLog.d(RLog.EVENT_LISTENERS, "MobileActivationFragment : onRefreshUserSuccess");
            storePreference(user.getMobile());
            setDescription();
            hideProgressSpinner();
            if (isVerified)
                getRegistrationFragment().addFragment(new AddSecureEmailFragment());
        }
    }

    @Override
    public void onRefreshUserFailed(int error) {
        hideProgressSpinner();
        RLog.d(TAG, "MobileActivationFragment : onRefreshUserFailed");
    }

    @Override
    public void onUpdate() {
        handleUI();
    }

    private void trackMultipleActionsOnMobileSuccess() {
        Map<String, String> map = new HashMap<>();
        map.put(SPECIAL_EVENTS, MOBILE_RESEND_EMAIL_VERFICATION);
        map.put(MOBILE_INAPPNATIFICATION, MOBILE_RESEND_SMS_VERFICATION);
        AppTagging.trackMultipleActions(SEND_DATA, map);
    }
    @OnClick(R2.id.btn_reg_Verify)
    public void verifyClicked() {
        verifyButton.showProgressIndicator();
        smsNotReceived.setEnabled(false);
        verificationCodeValidationEditText.setEnabled(false);
        getRegistrationFragment().hideKeyBoard();
        mobileVerifyCodePresenter.verifyMobileNumber(user.getJanrainUUID(),
                verificationCodeValidationEditText.getText().toString());
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        user.refreshUser(this);
        super.onResume();
        EventBus.getDefault().register(this);
        registerSMSReceiver();
    }

    @Subscribe
    public void onEvent(UpdateMobile event){
        user.refreshUser(this);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        user.refreshUser(this);
        super.onViewStateRestored(savedInstanceState);
    }

    @OnClick(R2.id.btn_reg_resend_code)
    public void resendButtonClicked() {
        disableVerifyButton();
        verifyButton.hideProgressIndicator();
        getRegistrationFragment().addFragment( new MobileVerifyResendCodeFragment());
        errorMessage.hideError();

    }

    @Override
    public void enableVerifyButton() {
        if ((verificationCodeValidationEditText.length() >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) &&
                networkUtility.isNetworkAvailable()) {
            verifyButton.setEnabled(true);
        }
    }

    @Override
    public void disableVerifyButton() {
        verifyButton.setEnabled(false);
    }

    @Override
    public void netWorkStateOnlineUiHandle() {
        if (verificationCodeValidationEditText.length() >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) {
            verifyButton.setEnabled(true);
        }
        errorMessage.hideError();
        smsNotReceived.setEnabled(true);
    }

    @Override
    public void netWorkStateOfflineUiHandle() {
        hideProgressSpinner();
        errorMessage.setError(context.getResources().getString(R.string.reg_NoNetworkConnection));
        smsNotReceived.setEnabled(false);
        disableVerifyButton();
    }

    @Override
    public void showSmsSendFailedError() {
        errorMessage.setError(getString(R.string.reg_URX_SMS_InternalServerError));
        hideProgressSpinner();
    }

    @Override
    public void refreshUserOnSmsVerificationSuccess() {
        RLog.d(TAG, "refreshUserOnSmsVerificationSuccess");
        trackActionStatus(SEND_DATA, SPECIAL_EVENTS, SUCCESS_USER_REGISTRATION);
        isVerified = true;
        user.refreshUser(this);
    }

    @Override
    public void smsVerificationResponseError() {
        errorMessage.setError(getString(R.string.reg_Mobile_Verification_Invalid_Code));
        hideProgressSpinner();

    }

    @Override
    public void hideProgressSpinner() {
        verifyButton.hideProgressIndicator();
        smsNotReceived.setEnabled(true);
        verificationCodeValidationEditText.setEnabled(true);
        enableVerifyButton();
    }

    @Override
    public void setOtpInvalidErrorMessage() {
        trackActionStatus(SEND_DATA, USER_ERROR, ACTIVATION_NOT_VERIFIED);
        errorMessage.setError(getString(R.string.reg_Mobile_Verification_Invalid_Code));
        hideProgressSpinner();
    }

    @Override
    public void setOtpErrorMessageFromJson(String errorDescription) {
        trackActionStatus(SEND_DATA, USER_ERROR, ACTIVATION_NOT_VERIFIED);
        errorMessage.setError(errorDescription);
        hideProgressSpinner();
    }

    @Override
    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(getRegistrationFragment().getContext(), RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailOrMobileNumber);
    }

    @Override
    public void onSuccessResponse(String response) {
        RLog.d(TAG, "onSuccessResponse" + response);
        mobileVerifyCodePresenter.handleActivation(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        RLog.d(TAG, "onErrorResponse" + error);
        errorMessage.setError(getString(R.string.reg_URX_SMS_InternalServerError));
        hideProgressSpinner();
    }

    @Override
    public void showOtpInvalidError() {
        errorMessage.setError(getString(R.string.reg_Mobile_Verification_Invalid_Code));
        hideProgressSpinner();
    }

    @Override
    public void registerSMSReceiver() {
        if(mSMSBroadCastReceiver.isSmsPermissionGranted()) {
            mobileVerifyCodePresenter.registerSMSReceiver();
        }else{
            mSMSBroadCastReceiver.requestReadAndSendSmsPermission();
        }
    }

    @Override
    public void unRegisterSMSReceiver() {
        mobileVerifyCodePresenter.unRegisterSMSReceiver();
    }

    @Override
    public void onOTPReceived(String otp) {
        RLog.i(TAG,"got otp");
        verificationCodeValidationEditText.setText(otp);
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterSMSReceiver();;
    }

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    @Override
    public SMSBroadCastReceiver getSMSBroadCastReceiver() {
        return mSMSBroadCastReceiver;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMSBroadCastReceiver.SMS_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS related task you need to do.
                    registerSMSReceiver();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

}
