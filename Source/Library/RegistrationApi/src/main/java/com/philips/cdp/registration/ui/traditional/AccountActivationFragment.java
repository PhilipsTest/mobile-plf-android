
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
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
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
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.*;

import javax.inject.*;

import butterknife.*;

public class AccountActivationFragment extends RegistrationBaseFragment implements
        AccountActivationContract,RefreshUserHandler,CounterListener {

    @Inject
    NetworkUtility networkUtility;

    @BindView(R2.id.usr_activation_emailVerified_button)
    ProgressBarButton mBtnActivate;

    @BindView(R2.id.usr_activation_emailNotReceived_button)
    Button mBtnResend;

    @BindView(R2.id.usr_activation_email_label)
    TextView mTvVerifyEmail;

    @BindView(R2.id.usr_activation_rootLayout_scrollView)
    ScrollView mSvRootLayout;

    @BindView(R2.id.usr_activation_activation_error)
    XRegError mEMailVerifiedError;

    AccountActivationPresenter accountActivationPresenter;

    private User mUser;

    private Context mContext;

    private Bundle mBundle;

    private String mEmailId;

    private boolean isSocialProvider;

    private boolean isEmailVerifiedError;

    private boolean proceedResend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onCreateView");
        mContext = getRegistrationFragment().getActivity().getApplicationContext();
        accountActivationPresenter = new AccountActivationPresenter(this);
        accountActivationPresenter.registerListener();
        RLog.i(RLog.EVENT_LISTENERS, "AccountActivationFragment register: NetworStateListener");

        Bundle bundle = getArguments();
        if (null != bundle) {
            isSocialProvider = bundle.getBoolean(RegConstants.IS_SOCIAL_PROVIDER);
        }
        mUser = new User(mContext);
        View view = inflater.inflate(R.layout.reg_fragment_account_activation, null);
        ButterKnife.bind(this, view);
        CounterHelper.getInstance()
                .registerCounterEventNotification(RegConstants.COUNTER_TICK, this);
        CounterHelper.getInstance()
                .registerCounterEventNotification(RegConstants.COUNTER_FINISH, this);
        getRegistrationFragment().startCountDownTimer();
        initUI(view);
        handleOrientation(view);
        return view;
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AccountActivationFragment : onDestroy");
        RLog.i(RLog.EVENT_LISTENERS, "AccountActivationFragment unregister: NetworStateListener");
        accountActivationPresenter.unRegisterListener();
        getRegistrationFragment().stopCountDownTimer();
        CounterHelper.getInstance()
                .unregisterCounterEventNotification(RegConstants.COUNTER_TICK, this);
        CounterHelper.getInstance()
                .unregisterCounterEventNotification(RegConstants.COUNTER_FINISH, this);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
        if (mEMailVerifiedError.getVisibility() == View.VISIBLE) {
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
            if (savedInstanceState.getString("saveEmailVerifiedErrorText") != null &&
                    savedInstanceState.getBoolean("isEmailVerifiedError")) {
                mEMailVerifiedError.setError(
                        savedInstanceState.getString("saveEmailVerifiedErrorText"));
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

    @OnClick(R2.id.usr_activation_emailVerified_button)
    void emailVerified() {
        RLog.d(RLog.ONCLICK, "AccountActivationFragment : Activate Account");
        showActivateSpinner();
        mBtnActivate.setEnabled(false);
        mBtnResend.setEnabled(false);
        mUser.refreshUser(this);
    }

    @OnClick(R2.id.usr_activation_emailNotReceived_button)
    void emailResend() {
        RLog.d(RLog.ONCLICK, "AccountActivationFragment : Resend email");

        if(proceedResend){
            getRegistrationFragment().addFragment(new AccountActivationResendMailFragment());
        }
        else{
            showResendAlertDialog();
        }
    }

    private void initUI(View view) {
        consumeTouch(view);
        mEmailId = mUser.getEmail();
        String email = getString(R.string.reg_VerifyEmail_EmailSentto_lbltxt);
        email = String.format(email, mEmailId);
        handleUiState(networkUtility.isNetworkAvailable());
        email = String.format(email, mEmailId);
        mTvVerifyEmail.setText(email);
        setupLinkify(mTvVerifyEmail,  email, mEmailId);
    }
    private static void setupLinkify(TextView accountSettingPhilipsNews,
                                     String moreAccountSettings, String link) {
        SpannableString spanableString = new SpannableString(moreAccountSettings);
        int termStartIndex = moreAccountSettings.toLowerCase().indexOf(
                link.toLowerCase());
        spanableString.setSpan(new StyleSpan(Typeface.BOLD), termStartIndex,
                termStartIndex + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        accountSettingPhilipsNews.setText(spanableString);
    }

    private void showActivateSpinner() {
        mBtnActivate.showProgressIndicator();
    }

    private void launchWelcomeFragment() {
        getRegistrationFragment().addWelcomeFragmentOnVerification();
        trackPage(AppTaggingPages.WELCOME);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, mTvVerifyEmail, width);
        applyParams(config, mEMailVerifiedError, width);
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
            return R.string.reg_RegCreateAccount_NavTitle;
        }
    }

    @Override
    public void handleUiState(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                mEMailVerifiedError.hideError();
                mBtnActivate.setEnabled(true);
                mBtnResend.setEnabled(true);
            } else {
                mBtnActivate.setEnabled(false);
                mBtnResend.setEnabled(false);
                mEMailVerifiedError.setError(getString(R.string.reg_NoNetworkConnection));
            }
        } else {
            mEMailVerifiedError.setError(getString(R.string.reg_NoNetworkConnection));
            mBtnActivate.setEnabled(false);
            mBtnResend.setEnabled(false);
            scrollViewAutomatically(mEMailVerifiedError, mSvRootLayout);
        }

    }

    @Override
    public void updateActivationUIState() {
        hideActivateSpinner();
        mBtnActivate.setEnabled(true);
        mBtnResend.setEnabled(true);
        if (mUser.isEmailVerified()) {
            mBtnResend.setVisibility(View.GONE);
            mEMailVerifiedError.hideError();
            trackActionStatus(AppTagingConstants.SEND_DATA,
                    AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_USER_REGISTRATION);
            launchWelcomeFragment();

        } else {
            UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
            userRegistrationFailureInfo.setErrorDescription(AppTagingConstants.EMAIL_VERIFICATION);
            showVerifyAlertDialog();
            trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.USER_ERROR,
                    AppTagingConstants.EMAIL_NOT_VERIFIED);
        }
    }

    private void showVerifyAlertDialog() {
        RegAlertDialog.showDialog(mContext.getResources().getString(
                R.string.reg_almost_verify_alert_title),
                mContext.getResources().getString(
                        R.string.reg_almost_verify_alert_content),
                mContext.getResources().getString(
                        R.string.reg_almost_verify_alert_content2),
                mContext.getResources().getString(
                        R.string.reg_almost_verify_alert_button)
                , getRegistrationFragment().getParentActivity(), mContinueBtnClick);
    }

    private void showResendAlertDialog(){
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

    @Override
    public void hideActivateSpinner() {
        mBtnActivate.hideProgressIndicator();
    }

    @Override
    public void activateButtonEnable(boolean enable) {
        mBtnActivate.setEnabled(enable);

    }

    @Override
    public void verificationError(String errorMsg) {
        mEMailVerifiedError.setError(errorMsg);
    }


    @Override
    public void onRefreshUserSuccess() {
        RLog.i(RLog.CALLBACK, "AccountActivationFragment : onRefreshUserSuccess");
        updateActivationUIState();
    }

    @Override
    public void onRefreshUserFailed(final int error) {
        handleRefreshUserFailed(error);
    }


    private void handleRefreshUserFailed(int error) {
        RLog.i(RLog.CALLBACK, "AccountActivationFragment : onRefreshUserFailed");
        if (error == RegConstants.HSDP_ACTIVATE_ACCOUNT_FAILED) {
            verificationError(mContext.getString(R.string.reg_JanRain_Server_Connection_Failed));
            hideActivateSpinner();
            activateButtonEnable(true);
            mBtnResend.setEnabled(true);
        } else {
            updateActivationUIState();
        }
    }
    private View.OnClickListener mContinueBtnClick = view -> RegAlertDialog.dismissDialog();

    @Override
    public void onCounterEventReceived(String event, long timeLeft) {
        RLog.i(RLog.CALLBACK, "AccountActivationFragment : onRefreshUserFailed"+timeLeft);

        if (event.equals(RegConstants.COUNTER_FINISH)) {
            proceedResend = true;
        } else {
            proceedResend = false;
        }
    }
}
