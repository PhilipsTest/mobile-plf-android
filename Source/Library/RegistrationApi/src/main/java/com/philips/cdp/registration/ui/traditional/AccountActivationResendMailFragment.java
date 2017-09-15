
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.annotation.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.*;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.dao.*;
import com.philips.cdp.registration.events.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.customviews.*;
import com.philips.cdp.registration.ui.utils.*;
import com.philips.cdp.registration.update.*;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.*;

import org.greenrobot.eventbus.*;

import java.util.*;

import javax.inject.*;

import butterknife.*;
import io.reactivex.android.schedulers.*;
import io.reactivex.disposables.*;
import io.reactivex.observers.*;
import io.reactivex.schedulers.*;

public class AccountActivationResendMailFragment extends RegistrationBaseFragment implements
        RefreshUserHandler, AccountActivationResendMailContract, CounterListener {

    @Inject
    UpdateUserProfile updateUserProfile;

    @Inject
    NetworkUtility networkUtility;

    @BindView(R2.id.usr_activationresend_emailResend_button)
    ProgressBarButton mResendEmail;

    @BindView(R2.id.usr_activationresend_return_button)
    Button mReturnButton;

    @BindView(R2.id.usr_activationresend_activation_error)
    XRegError mRegError;

    @BindView(R2.id.usr_activationresend_emailormobile_textfield)
    ValidationEditText emailEditText;

    @BindView(R2.id.usr_activationresend_emailormobile_inputValidationLayout)
    InputValidationLayout emailEditTextInputValidation;

    @BindView(R2.id.usr_activationresend_rootLayout_scrollView)
    ScrollView mSvRootLayout;

    @BindView(R2.id.usr_activationresend_root_layout)
    LinearLayout usr_activationresend_root_layout;

    @BindView(R2.id.usr_mobileverification_resendmailtimer_progress)
    ProgressBarWithLabel emailResendTimerProgress;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private User mUser;

    private Context mContext;

    private boolean isSocialProvider;

    private boolean isEmailVerifiedError;

    private Bundle mBundle;

    View view;

    String emailUser;

    AccountActivationResendMailPresenter accountActivationResendMailPresenter;

    private PopupWindow popupWindow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onCreateView");
        mContext = getRegistrationFragment().getActivity().getApplicationContext();
        accountActivationResendMailPresenter = new AccountActivationResendMailPresenter(this);
        RLog.i(RLog.EVENT_LISTENERS, "AccountActivationFragment register: NetworkStateListener");
        accountActivationResendMailPresenter.registerListener();
        Bundle bundle = getArguments();
        if (null != bundle) {
            isSocialProvider = bundle.getBoolean(RegConstants.IS_SOCIAL_PROVIDER);
        }
        mUser = new User(mContext);
        emailUser = mUser.getEmail();
        CounterHelper.getInstance()
                .registerCounterEventNotification(RegConstants.COUNTER_TICK, this);
        CounterHelper.getInstance()
                .registerCounterEventNotification(RegConstants.COUNTER_FINISH, this);
        view = inflater.inflate(R.layout.reg_fragment_account_activation_resend, null);
        ButterKnife.bind(this, view);
        initUI(view);
        handleOrientation(view);
        return view;
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onDestroy");
        accountActivationResendMailPresenter.unRegisterListener();
        RLog.i(RLog.EVENT_LISTENERS, "AccountActivationFragment unregister: NetworkStateListener");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
        if (mRegError.getVisibility() == View.VISIBLE) {
            isEmailVerifiedError = true;
            mBundle.putBoolean("isEmailVerifiedError", isEmailVerifiedError);
            mBundle.putString("saveEmailVerifiedErrorText",
                    mContext.getResources().getString(R.string.reg_RegEmailNotVerified_AlertPopupErrorText));
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("saveEmailVerifiedErrorText") != null
                    && savedInstanceState.getBoolean("isEmailVerifiedError")) {
                mRegError.setError(savedInstanceState.getString("saveEmailVerifiedErrorText"));
            }
        }
        mBundle = null;
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onConfigurationChanged");
        setCustomParams(config);
    }

    @OnClick(R2.id.usr_activationresend_return_button)
    public void returnVerifyScreen() {
        RLog.d(RLog.ONCLICK, "AccountActivationFragment : Activate Account");
        hidePopup();
        getRegistrationFragment().onBackPressed();
    }

    @OnClick(R2.id.usr_activationresend_emailResend_button)
    public void resendEmail() {
        RLog.d(RLog.ONCLICK, "AccountActivationFragment : Resend");
        hidePopup();
        addEmailClicked(emailUser);

    }

    private void handleResend(String email) {
        showResendSpinner();
        mResendEmail.setEnabled(false);
        mReturnButton.setEnabled(false);
        accountActivationResendMailPresenter.resendMail(mUser, email);
    }

    private void initUI(View view) {
        mResendEmail.setEnabled(false);
        consumeTouch(view);
        emailEditText.setText(mUser.getEmail());
        handleUiState(networkUtility.isNetworkAvailable());
    }

    @Override
    public void handleUiState(boolean isOnline) {
        if (isOnline) {
            if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
                if (!getRegistrationFragment().getCounterState()) {
                    mResendEmail.setEnabled(true);
                }
                mReturnButton.setEnabled(true);
            } else {
                mResendEmail.setEnabled(false);
                mReturnButton.setEnabled(false);
                mRegError.setError(mContext.getResources().getString(R.string.reg_NoNetworkConnection));
            }
        } else {
            mRegError.setError(mContext.getResources().getString(R.string.reg_NoNetworkConnection));
            mResendEmail.setEnabled(false);
            mReturnButton.setEnabled(false);
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }

    private void showResendSpinner() {
        mResendEmail.showProgressIndicator();
    }

    private void hideResendSpinner() {
        mResendEmail.hideProgressIndicator();

    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, usr_activationresend_root_layout, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        if (isSocialProvider) {
            return R.string.reg_SigIn_TitleTxt;
        } else {
            return R.string.reg_verify_resend_mail_nav_title;
        }
    }

    @Override
    public void handleResendVerificationEmailSuccess() {
        RLog.i(RLog.CALLBACK, "AccountActivationFragment : onResendVerificationEmailSuccess");
        resendVerificationEmailSuccessTrackAction();
        getRegistrationFragment().startCountDownTimer();
        updateResendUIState();
        viewOrHideNotificationBar();
    }

    void resendVerificationEmailSuccessTrackAction() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_RESEND_EMAIL_VERIFICATION);
        map.put(AppTagingConstants.STATUS_NOTIFICATION, AppTagingConstants.RESEND_VERIFICATION_MAIL_LINK_SENT);
        trackMultipleActionsMap(AppTagingConstants.SEND_DATA, map);
    }

//    private void showSuccessDialog() {
//        RegAlertDialog.showDialog(mContext.getResources().getString(
//                R.string.reg_verify_resend_mail_sent),
//                mUser.getEmail(),
//                null,
//                mContext.getResources().getString(
//                        R.string.reg_verify_resend_mail_intime_button_ok)
//                , getRegistrationFragment().getParentActivity(), mContinueBtnClick);
//    }

    private void showAlertDialog() {
        RegAlertDialog.showDialog(mContext.getResources().getString(
                R.string.reg_verify_resend_mail_intime_title),
                mContext.getResources().getString(
                        R.string.reg_verify_resend_mail_intime_desc),
                mContext.getResources().getString(
                        R.string.reg_verify_resend_mail_intime_desc2),
                mContext.getResources().getString(
                        R.string.reg_verify_resend_mail_intime_button_ok)
                , getRegistrationFragment().getParentActivity(), mContinueBtnClick);
    }

    private void updateResendUIState() {
        mResendEmail.setEnabled(true);
        mReturnButton.setEnabled(true);
        hideResendSpinner();
    }


    @Override
    public void handleResendVerificationEmailFailedWithError(
            UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "AccountActivationFragment : onResendVerificationEmailFailedWithError");
        updateResendUIState();
        AppTaggingErrors.trackActionResendNetworkFailure(userRegistrationFailureInfo,
                AppTagingConstants.JANRAIN);
        try {
            mRegError.setError(userRegistrationFailureInfo.getError().raw_response.getString("message"));
        } catch (Exception e) {
            mRegError.setError(mContext.getResources().getString(R.string.reg_Generic_Network_Error));
        }
        mReturnButton.setEnabled(true);
        mRegError.setVisibility(View.GONE);
    }

    public void addEmailClicked(String emailId) {
        mResendEmail.showProgressIndicator();
        if (emailId.equals(emailEditText.getText().toString())) {
            if (proceedResend) {
                handleResend(emailId);
            } else {
                mResendEmail.hideProgressIndicator();
                showAlertDialog();
            }
        } else {
            updateUserEmail(emailEditText.getText().toString());

        }
    }

    private void updateUserEmail(String emailId) {
        disposables.add(updateUserProfile.updateUserEmail(emailId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        storePreference(emailId);
                        refreshUser();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mResendEmail.hideProgressIndicator();
                        mRegError.setError(e.getMessage());
                    }
                }));
    }

    void refreshUser() {
        mUser.refreshUser(this);
    }

    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(getRegistrationFragment().getContext(),
                emailOrMobileNumber, true);
    }

    private OnClickListener mContinueBtnClick = view -> RegAlertDialog.dismissDialog();

    @VisibleForTesting
    @Deprecated
    public void injectMocks(UpdateUserProfile updateUserProfile) {
        this.updateUserProfile = updateUserProfile;
    }

    public void cleanUp() {
        disposables.clear();
    }

    @Override
    public void onDetach() {
        cleanUp();
        super.onDetach();
    }

    @Override
    public void onRefreshUserSuccess() {
        RLog.i(RLog.CALLBACK, "AccountActivationFr mail" + emailUser + "  --  " + mUser.getEmail());
        handleResend(mUser.getEmail());
    }

    @Override
    public void onRefreshUserFailed(int error) {
        mRegError.setError(mContext.getResources().getString(R.string.reg_Generic_Network_Error));
    }

    boolean proceedResend = true;

    public void updateResendTime(long timeLeft) {
        int timeRemaining = (int) (timeLeft / 1000);
        emailResendTimerProgress.setSecondaryProgress(
                ((60 - timeRemaining) * 100) / 60);
        String timeRemainingAsString = Integer.toString(timeRemaining);
        emailResendTimerProgress.setText(
                String.format(mContext.getResources().getString(R.string.no_sms_timer), timeRemainingAsString));
        mResendEmail.setEnabled(false);
    }

    @Override
    public void onCounterEventReceived(String event, long timeLeft) {
        RLog.i(RLog.CALLBACK, "AccountActivationFragment : onRefreshUserFailed" + timeLeft);
        int progress = 100;
        if (event.equals(RegConstants.COUNTER_FINISH)) {
            emailResendTimerProgress.setSecondaryProgress(progress);
            emailResendTimerProgress.setText(mContext.getResources().getString(R.string.no_email_yet));
            mResendEmail.setEnabled(true);
            proceedResend = true;
        } else {
            proceedResend = false;
            updateResendTime(timeLeft);
        }
    }

    public void viewOrHideNotificationBar() {
        if (popupWindow == null) {
            View view = getRegistrationFragment().getNotificationContentView(
                    mContext.getResources().getString(R.string.reg_verify_resend_mail_sent),
                    mUser.getEmail());
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(view);
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAtLocation(getActivity().
                    findViewById(R.id.usr_activationresend_root_layout), Gravity.TOP, 0, 0);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }
    void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
