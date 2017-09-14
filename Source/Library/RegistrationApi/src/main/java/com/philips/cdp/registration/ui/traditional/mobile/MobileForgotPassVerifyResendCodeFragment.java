
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional.mobile;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.Button;
import android.widget.*;

import com.philips.cdp.registration.*;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.events.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.ui.customviews.*;
import com.philips.cdp.registration.ui.traditional.*;
import com.philips.cdp.registration.ui.utils.*;
import com.philips.platform.uid.view.widget.*;

import org.greenrobot.eventbus.*;

import java.util.*;

import javax.inject.*;

import butterknife.*;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.*;

public class MobileForgotPassVerifyResendCodeFragment extends RegistrationBaseFragment implements
        MobileForgotPassVerifyResendCodeContract, RefreshUserHandler, OnUpdateListener, CounterListener {

    @BindView(R2.id.btn_reg_resend_update)
    ProgressBarButton resendSMSButton;

    @BindView(R2.id.btn_reg_code_received)
    Button smsReceivedButton;

    @BindView(R2.id.reg_error_msg)
    XRegError errorMessage;

    @BindView(R2.id.rl_reg_number_field)
    ValidationEditText phoneNumberEditText;

    @BindView(R2.id.usr_mobileverification_resendsmstimer_progress)
    ProgressBarWithLabel usr_mobileverification_resendsmstimer_progress;

    private Context context;

    private MobileForgotPassVerifyResendCodePresenter mobileVerifyResendCodePresenter;

    private Handler handler;

    @Inject
    NetworkUtility networkUtility;
    private String verificationSmsCodeURL;
    private String mobileNumber;
    private String redirectUri;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MobileActivationFragment : onCreateView");
        trackActionStatus(REGISTRATION_ACTIVATION_SMS, "", "");
        context = getRegistrationFragment().getActivity().getApplicationContext();
        mobileVerifyResendCodePresenter = new MobileForgotPassVerifyResendCodePresenter(this);

        final String mobileNumberKey = "mobileNumber";
        final String redirectUriKey = "redirectUri";
        final String verificationSmsCodeURLKey = "verificationSmsCodeURL";
        Bundle bundle = getArguments();
        mobileNumber = bundle.getString(mobileNumberKey);
        redirectUri = bundle.getString(redirectUriKey);
        verificationSmsCodeURL = bundle.getString(verificationSmsCodeURLKey);
        mobileVerifyResendCodePresenter.setRedirectUri(redirectUri);
        View view = inflater.inflate(R.layout.reg_mobile_activation_resend_fragment, container, false);
        ButterKnife.bind(this, view);
        handleOrientation(view);
        handler = new Handler();
        phoneNumberEditText.setText(mobileNumber);
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        disableResendButton();
        if (!getRegistrationFragment().getCounterState()) {
            enableResendButton();
        }
        phoneNumberChange();
        CounterHelper.getInstance()
                .registerCounterEventNotification(RegConstants.COUNTER_TICK, this);
        CounterHelper.getInstance()
                .registerCounterEventNotification(RegConstants.COUNTER_FINISH, this);
        return view;
    }

    private void phoneNumberChange() {
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (FieldsValidator.isValidMobileNumber(s.toString())) {
                    enableResendButton();
                } else {
                    disableResendButton();
                }

                errorMessage.hideError();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void showProgressDialog() {
        if (!getActivity().isFinishing()) resendSMSButton.showProgressIndicator();
    }

    private void hideProgressDialog() {
        if (resendSMSButton.isShown()) {
            resendSMSButton.hideProgressIndicator();
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
        mobileVerifyResendCodePresenter.cleanUp();
        CounterHelper.getInstance().unregisterCounterEventNotification(RegConstants.COUNTER_TICK, this);
        CounterHelper.getInstance().unregisterCounterEventNotification(RegConstants.COUNTER_FINISH, this);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, errorMessage, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_RegCreateAccount_NavTitle;
    }

    private void updateUiStatus() {
        if (FieldsValidator.isValidMobileNumber(phoneNumberEditText.getText().toString())) {
            resendSMSButton.setEnabled(true);
        } else {
            resendSMSButton.setEnabled(false);
        }
        phoneNumberEditText.setEnabled(true);
    }

    private void handleResendVerificationEmailSuccess() {
        trackActionStatus(SEND_DATA, SPECIAL_EVENTS, SUCCESS_RESEND_EMAIL_VERIFICATION);
//        RegAlertDialog.showResetPasswordDialog(context.getResources().getString(R.string.reg_Resend_SMS_title),
//                context.getResources().getString(R.string.reg_Resend_SMS_Success_Content),
//                getRegistrationFragment().getParentActivity(), mContinueVerifyBtnClick);
        viewOrHideNotificationBar();
        resendSMSButton.hideProgressIndicator();
        getRegistrationFragment().startCountDownTimer();
        if(!mobileNumber.equals(phoneNumberEditText.getText().toString())){
            EventBus.getDefault().post(new UpdateMobile(phoneNumberEditText.getText().toString()));
        }
    }

    private PopupWindow popupWindow;

    public void viewOrHideNotificationBar() {
        if (popupWindow == null) {
            View view = getRegistrationFragment().getNotificationContentView(
                    context.getResources().getString(R.string.reg_Resend_SMS_title),
                    context.getResources().getString(R.string.reg_Resend_SMS_Success_Content));
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(getActivity().
                    findViewById(R.id.ll_reg_root_container));
        }
    }

    @Subscribe
    public void onEvent(NotificationBarHandler event) {
        viewOrHideNotificationBar();
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

    @Override
    public void updateToken(String token) {
        EventBus.getDefault().post(new UpdateToken(token));

    }

    public void handleUI() {
        updateUiStatus();
    }

    @Override
    public void onRefreshUserSuccess() {
        RLog.d(RLog.EVENT_LISTENERS, "MobileActivationFragment : onRefreshUserSuccess");
    }

    @Override
    public void onRefreshUserFailed(int error) {
        hideProgressSpinner();
        RLog.d(RLog.EVENT_LISTENERS, "MobileActivationFragment : onRefreshUserFailed");
    }


    @Override
    public void onUpdate() {
        handleUI();
    }

    private View.OnClickListener mContinueVerifyBtnClick = view -> RegAlertDialog.dismissDialog();


    @Override
    public void trackVerifyActionStatus(String state, String key, String value) {
        trackActionStatus(state, key, value);
    }

    @Override
    public void showSMSSpecifedError(String id) {
        String errorMsg = RegChinaUtil.getErrorMsgDescription(id, context);
        showSmsResendTechincalError(errorMsg);
    }


    @OnClick(R2.id.btn_reg_resend_update)
    public void verifyClicked() {
        showProgressDialog();
        getRegistrationFragment().hideKeyBoard();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        errorMessage.hideError();
        mobileVerifyResendCodePresenter.resendOTPRequest(
                verificationSmsCodeURL, phoneNumberEditText.getText().toString());
        disableResendButton();
    }

    @OnClick(R2.id.btn_reg_code_received)
    public void thanksBtnClicked() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        getRegistrationFragment().onBackPressed();
    }

    @Override
    public Intent getServiceIntent() {
        return new Intent(context, HttpClientService.class);
    }

    @Override
    public HttpClientServiceReceiver getClientServiceRecevier() {
        return new HttpClientServiceReceiver(handler);
    }

    @Override
    public ComponentName startService(Intent intent) {
        return context.startService(intent);
    }

    @Override
    public void enableResendButton() {
        if (networkUtility.isNetworkAvailable())
            resendSMSButton.setEnabled(true);
            resendSMSButton.hideProgressIndicator();
    }

    public void updateResendTime(long timeLeft) {
        int timeRemaining = (int) (timeLeft / 1000);
        usr_mobileverification_resendsmstimer_progress.setSecondaryProgress(
                ((60 - timeRemaining) * 100) / 60);
        String timeRemainingAsString = Integer.toString(timeRemaining);
        usr_mobileverification_resendsmstimer_progress.setText(
                String.format(getResources().getString(R.string.no_sms_timer), timeRemainingAsString));
        disableResendButton();
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

    public void disableResendButton() {
        resendSMSButton.setEnabled(false);
    }

    @Override
    public void netWorkStateOfflineUiHandle() {
        errorMessage.setError(context.getResources().getString(R.string.reg_NoNetworkConnection));
        phoneNumberEditText.setEnabled(false);
        resendSMSButton.setEnabled(false);
    }

    @Override
    public void showSmsSendFailedError() {
        errorMessage.setError(getResources().getString(R.string.reg_URX_SMS_InternalServerError));
        phoneNumberEditText.setText(mobileNumber);
        enableResendButton();
    }

    @Override
    public void enableResendButtonAndHideSpinner() {
        trackMultipleActionsOnMobileSuccess();
        handleResendVerificationEmailSuccess();
    }

    @Override
    public void showSmsResendTechincalError(String errorCodeString) {
        trackActionStatus(SEND_DATA, TECHNICAL_ERROR, MOBILE_RESEND_SMS_VERFICATION_FAILURE);
        errorMessage.setError(errorCodeString);
        enableResendButton();
    }

    @Override
    public void trackMultipleActionsOnMobileSuccess() {
        Map<String, String> map = new HashMap<>();
        map.put(SPECIAL_EVENTS, MOBILE_RESEND_EMAIL_VERFICATION);
        map.put(MOBILE_INAPPNATIFICATION, MOBILE_RESEND_SMS_VERFICATION);
        AppTagging.trackMultipleActions(SEND_DATA, map);
    }

    @Override
    public void onCounterEventReceived(String event, long timeLeft) {
        int progress = 100;
        if (event.equals(RegConstants.COUNTER_FINISH)) {
            usr_mobileverification_resendsmstimer_progress.setSecondaryProgress(progress);
            usr_mobileverification_resendsmstimer_progress.setText(getResources().getString(R.string.no_sms_yet));
            enableResendButton();
        } else {
            updateResendTime(timeLeft);
        }
    }


}
