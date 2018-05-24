
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
import android.support.annotation.VisibleForTesting;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingErrors;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.CounterHelper;
import com.philips.cdp.registration.events.CounterListener;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.NotificationBarHandler;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegAlertDialog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.UpdateEmail;
import com.philips.cdp.registration.update.UpdateUserProfile;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ProgressBarWithLabel;
import com.philips.platform.uid.view.widget.ValidationEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

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
    LinearLayout usrActivationresendRootLayout;

    @BindView(R2.id.usr_mobileverification_resendmailtimer_progress)
    ProgressBarWithLabel emailResendTimerProgress;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private User mUser;

    private Context mContext;

    private boolean isSocialProvider;

    private Bundle mBundle;

    View view;

    String emailUser;

    AccountActivationResendMailPresenter accountActivationResendMailPresenter;

    private PopupWindow popupWindow;

    @Inject
    User user;

    @Inject
    RegistrationHelper registrationHelper;

    private static final String BUNDLE_SAVE_EMAIL_VERIFIED_ERROR_TEXT_KEY ="saveEmailVerifiedErrorText";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onCreateView");
        mContext = getRegistrationFragment().getActivity().getApplicationContext();
        accountActivationResendMailPresenter = new AccountActivationResendMailPresenter(this, user, registrationHelper);
        RLog.d(RLog.EVENT_LISTENERS, "AccountActivationFragment register: NetworkStateListener");
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
        emailChange();
        handleOrientation(view);
        return view;
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onDestroy");
        accountActivationResendMailPresenter.unRegisterListener();
        RLog.d(RLog.EVENT_LISTENERS, "AccountActivationFragment unregister: NetworkStateListener");
        super.onDestroy();
        CounterHelper.getInstance().unregisterCounterEventNotification(RegConstants.COUNTER_TICK,
                this);
        CounterHelper.getInstance().unregisterCounterEventNotification(RegConstants.COUNTER_FINISH,
                this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
        if (mRegError.getVisibility() == View.VISIBLE) {
            boolean isEmailVerifiedError = true;
            mBundle.putBoolean("isEmailVerifiedError", isEmailVerifiedError);
            mBundle.putString(BUNDLE_SAVE_EMAIL_VERIFIED_ERROR_TEXT_KEY,
                    mContext.getResources().getString(R.string.reg_RegEmailNotVerified_AlertPopupErrorText));
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            mBundle = null;
            return;
        }else if (savedInstanceState.getString(BUNDLE_SAVE_EMAIL_VERIFIED_ERROR_TEXT_KEY) != null
                    && savedInstanceState.getBoolean("isEmailVerifiedError")) {
                mRegError.setError(savedInstanceState.getString(BUNDLE_SAVE_EMAIL_VERIFIED_ERROR_TEXT_KEY));
        }
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
        hideNotificationBar();
        getRegistrationFragment().onBackPressed();
    }

    @OnClick(R2.id.usr_activationresend_emailResend_button)
    public void resendEmail() {
        RLog.d(RLog.ONCLICK, "AccountActivationFragment : Resend");
        hideNotificationBar();
        addEmailClicked(emailUser);

    }

    private void handleResend(String email) {
        showProgressDialog();
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
        if (isSocialProvider) {
            return R.string.reg_DLS_SigIn_TitleTxt;
        } else {
            return R.string.reg_DLS_Resend_Email_Screen_title;
        }
    }

    @Override
    public void handleResendVerificationEmailSuccess() {
        RLog.d(RLog.CALLBACK, "AccountActivationFragment : onResendVerificationEmailSuccess");
        resendVerificationEmailSuccessTrackAction();
        getRegistrationFragment().startCountDownTimer();
        updateResendUIState();
        viewOrHideNotificationBar(getRegistrationFragment().getNotificationContentView(
                    mContext.getResources().getString(R.string.reg_DLS_Resend_Email_NotificationBar_Title),
                    mUser.getEmail()));
    }

    void resendVerificationEmailSuccessTrackAction() {
        HashMap<String, String> map = new HashMap<>();
        map.put(AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_RESEND_EMAIL_VERIFICATION);
        map.put(AppTagingConstants.STATUS_NOTIFICATION, AppTagingConstants.RESEND_VERIFICATION_MAIL_LINK_SENT);
        trackMultipleActionsMap(AppTagingConstants.SEND_DATA, map);
    }

    private void showAlertDialog() {
        RegAlertDialog.showDialog(mContext.getResources().getString(
                R.string.reg_DLS_Resend_Email_Wait_Error_Msg_Title),
                mContext.getResources().getString(
                        R.string.reg_DLS_Resend_Email_Wait_Error_Msg_Body_Line1),
                mContext.getResources().getString(
                        R.string.reg_DLS_Resend_Email_Wait_Error_Msg_Body_Line2),
                mContext.getResources().getString(
                        R.string.reg_DLS_Button_Title_Ok)
                , getRegistrationFragment().getParentActivity(), mContinueBtnClick);
    }

    private void updateResendUIState() {
        mResendEmail.setEnabled(true);
        mReturnButton.setEnabled(true);
        hideProgressDialog();
    }


    @Override
    public void handleResendVerificationEmailFailedWithError(
            UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.d(RLog.CALLBACK, "AccountActivationFragment : onResendVerificationEmailFailedWithError");
        updateResendUIState();
        AppTaggingErrors.trackActionResendNetworkFailure(userRegistrationFailureInfo,
                AppTagingConstants.JANRAIN);
        try {
            mRegError.setError(userRegistrationFailureInfo.getError().raw_response.getString("message"));
        } catch (Exception e) {
            mRegError.setError(mContext.getResources().getString(R.string.reg_Generic_Network_Error));
        }
        mReturnButton.setEnabled(true);
    }

    public void addEmailClicked(String emailId) {
        showProgressDialog();
        if (emailId.equals(emailEditText.getText().toString())) {
            if (proceedResend) {
                handleResend(emailId);
            } else {
                hideProgressDialog();
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
                        hideProgressDialog();
                        mRegError.setError(e.getMessage());
                    }
                }));
    }

    void refreshUser() {
        mUser.refreshUser(this);
    }

    public void storePreference(String emailOrMobileNumber) {
        RegPreferenceUtility.storePreference(getRegistrationFragment().getContext(),RegConstants.TERMS_N_CONDITIONS_ACCEPTED,
                emailOrMobileNumber);
    }

    private OnClickListener mContinueBtnClick = clickListener -> RegAlertDialog.dismissDialog();

    /**
     * @deprecated
     * @param updateUserProfile
     */
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
        RLog.d(RLog.CALLBACK, "AccountActivationFr mail" + emailUser + "  --  " + mUser.getEmail());
        hideProgressDialog();
        enableResendButton();
        emailUser = mUser.getEmail();
        viewOrHideNotificationBar(getRegistrationFragment().getNotificationContentView(
                    mContext.getResources().getString(R.string.reg_DLS_Resend_Email_NotificationBar_Title),
                    mUser.getEmail()));
        getRegistrationFragment().startCountDownTimer();
        EventBus.getDefault().post(new UpdateEmail(user.getEmail()));
        handleResend(mUser.getEmail());
    }

    @Override
    public void onRefreshUserFailed(int error) {
        mRegError.setError(mContext.getResources().getString(R.string.reg_Generic_Network_Error));
    }

    boolean proceedResend = true;

    public void updateResendTime(long timeLeft) {
        if (user.getEmail().equals(emailEditText.getText().toString())) {
            int timeRemaining = (int) (timeLeft / 1000);
            emailResendTimerProgress.setSecondaryProgress(
                    ((60 - timeRemaining) * 100) / 60);
            emailResendTimerProgress.setText(
                    String.format(mContext.getResources().getString(R.string.reg_DLS_ResendSMS_Progress_View_Progress_Text), timeRemaining));
            disableResendButton();
        }
    }

    @Override
    public void onCounterEventReceived(String event, long timeLeft) {
        int progress = 100;
        if (event.equals(RegConstants.COUNTER_FINISH)) {
            emailResendTimerProgress.setSecondaryProgress(progress);
            //Temp: Actual text is not available in localization hence kept empty for time being.
            emailResendTimerProgress.setText("");
            enableResendButton();
            proceedResend = true;
        } else {
            proceedResend = false;
            updateResendTime(timeLeft);
        }
    }

//    public void viewOrHideNotificationBar() {
//        if (popupWindow == null) {
//            View contentView = getRegistrationFragment().getNotificationContentView(
//                    mContext.getResources().getString(R.string.reg_DLS_Resend_Email_NotificationBar_Title),
//                    mUser.getEmail());
//            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            popupWindow.setContentView(contentView);
//        }
//        if (popupWindow.isShowing()) {
//            popupWindow.dismiss();
//        } else {
//            if(this.isVisible() && popupWindow != null) {
//                popupWindow.showAtLocation(getActivity().
//                        findViewById(R.id.usr_activationresend_root_layout), Gravity.TOP, 0, 0);
//            }
//        }
//    }

    @Subscribe
    public void onEvent(NotificationBarHandler event) {
        viewOrHideNotificationBar(getRegistrationFragment().getNotificationContentView(
                    mContext.getResources().getString(R.string.reg_DLS_Resend_Email_NotificationBar_Title),
                    mUser.getEmail()));
    }

    @Override
    public void onPause() {
        super.onPause();
        hideNotificationBar();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    private void emailChange() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do not do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!user.getEmail().equals(s.toString())) {
                    if (FieldsValidator.isValidEmail(s.toString())) {
                        enableUpdateButton();
                    } else {
                        disableResendButton();
                    }
                } else {
                    enableResendButton();
                }
                mRegError.hideError();
            }

            @Override
            public void afterTextChanged(Editable s) {
            // Do not do anything
            }
        });
    }


    public void enableResendButton() {
            mResendEmail.setText(getResources().getString(
                    R.string.reg_DLS_Resend_The_Email_Button_Title));
            mResendEmail.setProgressText(getResources().getString(
                    R.string.reg_DLS_Resend_The_Email_Button_Title));
            if (networkUtility.isNetworkAvailable())
                mResendEmail.setEnabled(true);
            RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : resend enab");
    }

    public void enableUpdateButton() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : resend update enable");

        mResendEmail.setText(getString(
                R.string.reg_Update_Email_Button_Text));
        mResendEmail.setProgressText(getString(
                R.string.reg_Update_Email_Button_Text));
        mResendEmail.setEnabled(true);

    }
    public void disableResendButton() {
        mResendEmail.setEnabled(false);
    }
}
