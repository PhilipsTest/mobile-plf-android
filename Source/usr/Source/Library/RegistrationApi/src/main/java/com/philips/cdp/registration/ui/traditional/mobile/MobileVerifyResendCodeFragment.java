
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
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.volley.VolleyError;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.URNotification;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.CountDownEvent;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.NotificationBarHandler;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.UpdateMobile;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ProgressBarWithLabel;
import com.philips.platform.uid.view.widget.ValidationEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_INAPPNATIFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_RESEND_EMAIL_VERFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_RESEND_SMS_VERFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.MOBILE_RESEND_SMS_VERFICATION_FAILURE;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.REGISTRATION_ACTIVATION_SMS;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SEND_DATA;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SPECIAL_EVENTS;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.SUCCESS_RESEND_SMS_VERIFICATION;
import static com.philips.cdp.registration.app.tagging.AppTagingConstants.TECHNICAL_ERROR;

public class MobileVerifyResendCodeFragment extends RegistrationBaseFragment implements
        MobileVerifyResendCodeContract, RefreshUserHandler, OnUpdateListener {

    private String TAG = "MobileVerifyResendCodeFragment";

    @BindView(R2.id.btn_reg_resend_update)
    ProgressBarButton resendSMSButton;

    @BindView(R2.id.btn_reg_code_received)
    Button smsReceivedButton;

    @BindView(R2.id.reg_error_msg)
    XRegError errorMessage;

    @BindView(R2.id.usr_reg_root_layout)
    LinearLayout rootLayout;

    @BindView(R2.id.rl_reg_number_field)
    ValidationEditText phoneNumberEditText;

    @BindView(R2.id.usr_mobileverification_resend_inputValidation)
    InputValidationLayout usrMobileverificationResendInputValidation;

    @BindView(R2.id.usr_mobileverification_resendsmstimer_progress)
    ProgressBarWithLabel usrMobileverificationResendsmstimerProgress;

    private Context context;

    private User user;

    private MobileVerifyResendCodePresenter mobileVerifyResendCodePresenter;

    @Inject
    NetworkUtility networkUtility;

    private PopupWindow popupWindow;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        user = new User(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        RLog.i(TAG, "Screen name is " + TAG);
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        registerInlineNotificationListener(this);
        mobileVerifyResendCodePresenter = new MobileVerifyResendCodePresenter(this);
        View view = inflater.inflate(R.layout.reg_mobile_activation_resend_fragment, container,
                false);
        ButterKnife.bind(this, view);

        trackActionStatus(REGISTRATION_ACTIVATION_SMS, "", "");
        handleOrientation(view);
        phoneNumberEditText.setText(user.getMobile());
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        disableResendButton();
        if (!getRegistrationFragment().getCounterState()) {
            enableResendButton();
        }
        phoneNumberChange();
        return view;
    }

    private void phoneNumberChange() {
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do not do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!user.getMobile().equals(s.toString())) {
                    resendSMSButton.setText(getActivity().getResources().getString(
                            R.string.USR_Update_MobileNumber_Button_Text));
                    resendSMSButton.setProgressText(getActivity().getResources().getString(
                            R.string.USR_Update_MobileNumber_Button_Text));
                    if (FieldsValidator.isValidMobileNumber(s.toString())) {
                        enableUpdateButton();
                        usrMobileverificationResendInputValidation.hideError();
                    } else {
                        usrMobileverificationResendInputValidation.showError();
                        disableResendButton();
                    }
                } else {
                    resendSMSButton.setText(getActivity().getResources().getString(
                            R.string.USR_Resend_SMS_title));
                    resendSMSButton.setProgressText(getActivity().getResources().getString(
                            R.string.USR_Resend_SMS_title));
                    usrMobileverificationResendInputValidation.hideError();
                    enableResendButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do not do anything
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        RLog.d(TAG, " : onConfigurationChanged");
        super.onConfigurationChanged(config);
        setCustomParams(config);
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
        return R.string.USR_Resend_SMS_title;
    }

    private void updateUiStatus() {
        if (FieldsValidator.isValidMobileNumber(phoneNumberEditText.getText().toString())) {
            resendSMSButton.setEnabled(true);
        } else {
            resendSMSButton.setEnabled(false);
        }
        phoneNumberEditText.setEnabled(true);
        smsReceivedButton.setEnabled(true);
    }

    private void handleResendVerificationSMSSuccess() {
        trackActionStatus(SEND_DATA, SPECIAL_EVENTS, SUCCESS_RESEND_SMS_VERIFICATION);
        viewOrHideNotificationBar();
        getRegistrationFragment().startCountDownTimer();
    }

    public void handleUI() {
        updateUiStatus();
    }

    @Override
    public void onRefreshUserSuccess() {
        RLog.d(TAG, " : onRefreshUserSuccess");
        EventBus.getDefault().post(new UpdateMobile(user.getMobile()));
        RLog.d(TAG, " : onRefreshUserSuccess mobile" + user.getMobile());
        mobileVerifyResendCodePresenter.resendOTPRequest(user.getMobile());

    }

    @Override
    public void onRefreshUserFailed(int error) {
        hideProgressSpinner();
        RLog.d(TAG, " : onRefreshUserFailed");
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

    @OnClick(R2.id.btn_reg_resend_update)
    public void verifyClicked() {
        showProgressDialog();
        getRegistrationFragment().hideKeyBoard();
        errorMessage.hideError();
        hidePopup();
        RLog.i(TAG, TAG + ".verifyClicked");
        if (phoneNumberEditText.getText().toString().equals(user.getMobile())) {
            mobileVerifyResendCodePresenter.resendOTPRequest(user.getMobile());
            disableResendButton();
        } else {
            if (FieldsValidator.isValidMobileNumber(phoneNumberEditText.getText().toString())) {
                disableResendButton();
                mobileVerifyResendCodePresenter.updatePhoneNumber(
                        FieldsValidator.getMobileNumber(phoneNumberEditText.getText().toString()));
            } else {
                errorMessage.setError(getActivity().getResources().getString(
                        R.string.USR_InvalidPhoneNumber_ErrorMsg));
            }
        }
    }


    @OnClick(R2.id.btn_reg_code_received)
    public void thanksBtnClicked() {
        RLog.i(TAG, TAG + ".thanksButton clicked");

        hidePopup();
        getRegistrationFragment().onBackPressed();
    }

    void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void enableResendButton() {
        resendSMSButton.setText(getActivity().getResources().getString(
                R.string.USR_Resend_SMS_title));
        resendSMSButton.setProgressText(getActivity().getResources().getString(
                R.string.USR_Resend_SMS_title));
        if (networkUtility.isNetworkAvailable())
            resendSMSButton.setEnabled(true);
    }

    @Override
    public void enableUpdateButton() {
        resendSMSButton.setText(getActivity().getResources().getString(
                R.string.USR_Update_MobileNumber_Button_Text));
        resendSMSButton.setProgressText(getActivity().getResources().getString(
                R.string.USR_Update_MobileNumber_Button_Text));
        resendSMSButton.setEnabled(true);

    }

    public void updateResendTime(long timeLeft) {
        if (user.getMobile().equals(phoneNumberEditText.getText().toString())) {
            int timeRemaining = (int) (timeLeft / 1000);
            usrMobileverificationResendsmstimerProgress.setSecondaryProgress(
                    ((60 - timeRemaining) * 100) / 60);
            usrMobileverificationResendsmstimerProgress.setText(
                    String.format(getString(R.string.USR_DLS_ResendSMS_Progress_View_Progress_Text), timeRemaining));
            disableResendButton();
        }
    }

    @Override
    public void netWorkStateOnlineUiHandle() {
        errorMessage.hideError();
        updateUiStatus();
    }

    @Override
    public void hideProgressSpinner() {
        enableResendButton();
        hideProgressDialog();
    }

    @Override
    public void disableResendButton() {
        resendSMSButton.setEnabled(false);
    }

    @Override
    public void netWorkStateOfflineUiHandle() {
        phoneNumberEditText.setEnabled(false);
        resendSMSButton.setEnabled(false);
        smsReceivedButton.setEnabled(false);
        hideProgressDialog();
    }

    @Override
    public void enableResendButtonAndHideSpinner() {
        trackMultipleActionsOnMobileSuccess();
        handleResendVerificationSMSSuccess();
    }

    @Override
    public void showSmsResendTechincalError(String errorCodeString) {
        trackActionStatus(SEND_DATA, TECHNICAL_ERROR, MOBILE_RESEND_SMS_VERFICATION_FAILURE);
        errorMessage.setError(errorCodeString);
        enableResendButton();
    }

    @Override
    public void showNumberChangeTechincalError(int errorCode) {
        trackActionStatus(SEND_DATA, TECHNICAL_ERROR, MOBILE_RESEND_SMS_VERFICATION_FAILURE);
        updateErrorNotification(new URError(context).getLocalizedError(ErrorType.URX, errorCode), errorCode);
        enableUpdateButton();
    }

    @Override
    public void refreshUser() {
        user.refreshUser(this);
        getRegistrationFragment().stopCountDownTimer();
        disableResendButton();
    }

    @Override
    public void onSuccessResponse(int requestCode, String response) {
        mobileVerifyResendCodePresenter.handleOnSuccess(requestCode, response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        RLog.e(TAG, "onErrorResponse : VolleyError = " + error.getMessage());
        onErrorOfResendSMSIntent(error);
        hideProgressSpinner();
    }

    private void onErrorOfResendSMSIntent(VolleyError error) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(error.getMessage());
            final String errorCode = jsonObject.getString("errorCode");
//            errorMessage.setError(errorMsg);
            phoneNumberEditText.setText(user.getMobile());
            enableResendButton();
            RLog.e(TAG, "onErrorOfResendSMSIntent : Error from Request " + error.getMessage());
            final Integer code = Integer.parseInt(errorCode);
            if (URNotification.INLINE_ERROR_CODE.contains(code)) {
                errorMessage.setError(new URError(context).getLocalizedError(ErrorType.URX, code));
            } else {
                updateErrorNotification(new URError(context).getLocalizedError(ErrorType.URX, code));
            }
        } catch (JSONException e) {
            RLog.e(TAG, "onErrorOfResendSMSIntent : Exception Occurred" + e.getMessage());
        }
    }

    @Subscribe
    public void onCountDownEvent(CountDownEvent event) {
        int progress = 100;
        if (event.getEvent().equals(RegConstants.COUNTER_FINISH)) {
            usrMobileverificationResendsmstimerProgress.setSecondaryProgress(progress);
            usrMobileverificationResendsmstimerProgress.setText(getResources().getString(R.string.USR_DLS_ResendSMS_Progress_View_Title_Text));
            enableResendButton();
        } else {
            updateResendTime(event.getTimeleft());
        }
    }

    public void viewOrHideNotificationBar() {
        if (popupWindow == null) {
            View view = getRegistrationFragment().getNotificationContentView(
                    context.getResources().getString(R.string.USR_DLS_ResendSMS_NotificationBar_Title),
                    user.getMobile());
            RLog.d(TAG, "MobileActivationFragment : onRefreshUserSuccess mobile" + user.getMobile());
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(view);
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        } else {
            if (this.isVisible() && popupWindow != null) {
                popupWindow.showAtLocation(getActivity().
                        findViewById(R.id.usr_reg_root_layout), Gravity.TOP, 0, 0);
            }
        }
    }

    @Subscribe
    public void onEvent(NotificationBarHandler event) {
        viewOrHideNotificationBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        hidePopup();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this) ){
            EventBus.getDefault().register(this);
        }
    }


    @Override
    public void notificationInlineMsg(String msg) {
        errorMessage.setError(msg);
    }
}
