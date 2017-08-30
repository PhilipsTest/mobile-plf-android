
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
import android.view.*;
import android.widget.Button;

import com.jakewharton.rxbinding2.widget.*;
import com.philips.cdp.registration.*;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.customviews.*;
import com.philips.cdp.registration.ui.traditional.*;
import com.philips.cdp.registration.ui.utils.*;
import com.philips.platform.uid.view.widget.*;

import java.util.*;

import javax.inject.*;

import butterknife.*;
import io.reactivex.Observable;

import static com.philips.cdp.registration.app.tagging.AppTagingConstants.*;

public class MobileVerifyCodeFragment extends RegistrationBaseFragment implements
        MobileVerifyCodeContract, RefreshUserHandler, OnUpdateListener{


    @Inject
    NetworkUtility networkUtility;

    @BindView(R2.id.btn_reg_Verify)
    ProgressBarButton verifyButton;

    @BindView(R2.id.btn_reg_resend_code)
    Button smsNotReceived;

    @BindView(R2.id.reg_error_msg)
    XRegError errorMessage;

    @BindView(R2.id.usr_forgotpassword_inputId_textField6)
    ValidationEditText usr_forgotpassword_inputId_textField6;

    @BindView(R2.id.usr_forgotpassword_inputId_textField1)
    ValidationEditText usr_forgotpassword_inputId_textField1;

    @BindView(R2.id.usr_forgotpassword_inputId_textField2)
    ValidationEditText usr_forgotpassword_inputId_textField2;

    @BindView(R2.id.usr_forgotpassword_inputId_textField3)
    ValidationEditText usr_forgotpassword_inputId_textField3;

    @BindView(R2.id.usr_forgotpassword_inputId_textField4)
    ValidationEditText usr_forgotpassword_inputId_textField4;

    @BindView(R2.id.usr_forgotpassword_inputId_textField5)
    ValidationEditText usr_forgotpassword_inputId_textField5;

    @BindView(R2.id.reg_verify_mobile_desc1)
    Label reg_verify_mobile_desc1;

    private Context context;

    private User user;

    private MobileVerifyCodePresenter mobileVerifyCodePresenter;

    private Handler handler;

    boolean isVerified;

    String verifyCode;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MobileActivationFragment : onCreateView");
        trackActionStatus(REGISTRATION_ACTIVATION_SMS,"","");
        context = getRegistrationFragment().getActivity().getApplicationContext();
        mobileVerifyCodePresenter = new MobileVerifyCodePresenter(this);
        user = new User(context);
        View view = inflater.inflate(R.layout.reg_mobile_activatiom_fragment, container, false);
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
        reg_verify_mobile_desc1.setText(
                String.format(getString(R.string.reg_verify_mobile_desc1), userId));
    }

    private void handleVerificationCode() {

        Observable<String> obs1 = RxTextView.textChanges(usr_forgotpassword_inputId_textField1)
                .map(charSeq -> {
                    usr_forgotpassword_inputId_textField2.requestFocus();
                    return charSeq.toString();
                });

        Observable<String> obs2 = RxTextView.textChanges(usr_forgotpassword_inputId_textField2)
                .map(charSeq -> {
                    usr_forgotpassword_inputId_textField3.requestFocus();
                    return charSeq.toString();
                });
        Observable<String> obs3 = RxTextView.textChanges(usr_forgotpassword_inputId_textField3)
                .map(charSeq -> {
                    usr_forgotpassword_inputId_textField4.requestFocus();
                    return charSeq.toString();
                });
        Observable<String> obs4 = RxTextView.textChanges(usr_forgotpassword_inputId_textField4)
                .map(charSeq -> {
                    usr_forgotpassword_inputId_textField5.requestFocus();
                    return charSeq.toString();
                });
        Observable<String> obs5 = RxTextView.textChanges(usr_forgotpassword_inputId_textField5)
                .map(charSeq -> {
                    usr_forgotpassword_inputId_textField6.requestFocus();
                    return charSeq.toString();
                });
        Observable<String> obs6 = RxTextView.textChanges(usr_forgotpassword_inputId_textField6)
                .map(charSeq -> {
                    usr_forgotpassword_inputId_textField1.requestFocus();
                    return charSeq.toString();
                });
        getCompleteVerificationCode(obs1, obs2, obs3, obs4, obs5, obs6);
        validateVerificationCode(obs1, obs2, obs3, obs4, obs5, obs6);
    }

    private void validateVerificationCode(Observable<String> obs1, Observable<String> obs2,
                                          Observable<String> obs3, Observable<String> obs4,
                                          Observable<String> obs5, Observable<String> obs6) {
        Observable.combineLatest(obs1, obs2, obs3, obs4, obs5, obs6,
                (text1, text2, text3, text4, text5, text6) ->
                        (!text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty()
                                && !text5.isEmpty() && !text6.isEmpty()))
                .subscribe(enabled -> verifyButton.setEnabled(enabled && networkUtility.isNetworkAvailable()));
    }



    private void getCompleteVerificationCode(Observable<String> obs1, Observable<String> obs2,
                                             Observable<String> obs3, Observable<String> obs4,
                                             Observable<String> obs5, Observable<String> obs6) {
        Observable.combineLatest(obs1, obs2, obs3, obs4, obs5, obs6,
                (text1, text2, text3, text4, text5, text6) ->
                        text1 + text2 + text3 + text4 + text5 + text6)
                .subscribe(code -> verifyCode = code);
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
        if (verifyCode.length() >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) {
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
        RLog.d(RLog.EVENT_LISTENERS, "MobileActivationFragment : onRefreshUserSuccess");
        storePreference(user.getMobile());
        setDescription();
        hideProgressSpinner();
        if(isVerified)
            getRegistrationFragment().addFragment(new AddSecureEmailFragment());
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
  
    private void trackMultipleActionsOnMobileSuccess() {
        Map<String, String> map = new HashMap<>();
        map.put(SPECIAL_EVENTS, MOBILE_RESEND_EMAIL_VERFICATION);
        map.put(MOBILE_INAPPNATIFICATION, MOBILE_RESEND_SMS_VERFICATION);
        AppTagging.trackMultipleActions(SEND_DATA, map);
    }

    @OnClick(R2.id.btn_reg_Verify)
    public void verifyClicked() {
        verifyButton.showProgressIndicator();
        disableVerifyButton();
        mobileVerifyCodePresenter.verifyMobileNumber(user.getJanrainUUID(),
                verifyCode);
    }


    @Override
    public void onResume() {
        user.refreshUser(this);
        super.onResume();
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
    public void enableVerifyButton() {
        if ((verifyCode.length() >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) &&
                networkUtility.isNetworkAvailable()) {
            verifyButton.setEnabled(true);
        }
    }

    @Override
    public void netWorkStateOnlineUiHandle() {
        if (verifyCode.length() >= RegConstants.VERIFY_CODE_MINIMUM_LENGTH) {
            verifyButton.setEnabled(true);
        }
        errorMessage.hideError();
        smsNotReceived.setEnabled(true);
    }

    @Override
    public void disableVerifyButton() {
        verifyButton.setEnabled(false);
    }

    @Override
    public void netWorkStateOfflineUiHandle() {
        verifyButton.setEnabled(false);
        errorMessage.setError(context.getResources().getString(R.string.reg_NoNetworkConnection));
        smsNotReceived.setEnabled(false);
    }

    @Override
    public void showSmsSendFailedError() {
        errorMessage.setError(getString(R.string.reg_URX_SMS_InternalServerError));
    }

    @Override
    public void refreshUserOnSmsVerificationSuccess() {
        trackActionStatus(SEND_DATA, SPECIAL_EVENTS, SUCCESS_USER_REGISTRATION);
        isVerified = true;
        user.refreshUser(this);
    }

    @Override
    public void smsVerificationResponseError() {
        errorMessage.setError(getString(R.string.reg_Mobile_Verification_Invalid_Code));
    }

    @Override
    public void hideProgressSpinner() {
        verifyButton.hideProgressIndicator();
        enableVerifyButton();

    }

    @Override
    public void setOtpInvalidErrorMessage() {
        trackActionStatus(SEND_DATA, USER_ERROR, ACTIVATION_NOT_VERIFIED);
        errorMessage.setError(getString(R.string.reg_Mobile_Verification_Invalid_Code));
    }

    @Override
    public void setOtpErrorMessageFromJson(String errorDescription) {
        trackActionStatus(SEND_DATA, USER_ERROR, ACTIVATION_NOT_VERIFIED);
        errorMessage.setError(errorDescription);
    }

    @Override
    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(getRegistrationFragment().getContext(), emailOrMobileNumber, true);
    }

    @Override
    public void showOtpInvalidError() {
        errorMessage.setError(getString(R.string.reg_Mobile_Verification_Invalid_Code));
    }
}
