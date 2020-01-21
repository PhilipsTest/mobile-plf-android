
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.errors.ErrorType;
import com.philips.cdp.registration.errors.URError;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegAlertDialog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.UpdateEmail;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.ProgressBarButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountActivationFragment extends RegistrationBaseFragment implements
        AccountActivationContract, RefreshUserHandler {

    private String TAG = "AccountActivationFragment";

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

    @BindView(R2.id.usr_reg_root_layout)
    LinearLayout usr_activation_root_layout;

    private AccountActivationPresenter accountActivationPresenter;

    private User mUser;

    private Context mContext;

    private Bundle mBundle;

    private String mEmailId;

    private boolean isSocialProvider;

    private boolean wasAppInBackground;

    @Inject
    RegistrationHelper registrationHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        RLog.i(TAG, "Screen name is " + TAG);

        Bundle bundle = getArguments();
        if (null != bundle) {
            isSocialProvider = bundle.getBoolean(RegConstants.IS_SOCIAL_PROVIDER);
        }
        mUser = new User(mContext);
        View view = inflater.inflate(R.layout.reg_fragment_account_activation, null);
        registerInlineNotificationListener(this);
        accountActivationPresenter = new AccountActivationPresenter(this, registrationHelper);
        accountActivationPresenter.registerListener();

        ButterKnife.bind(this, view);
        getRegistrationFragment().startCountDownTimer();
        initUI(view);
        handleOrientation(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(TAG, "onStop");
        wasAppInBackground = true;
        accountActivationPresenter.unRegisterListener();
       // getRegistrationFragment().stopCountDownTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
        if (mEMailVerifiedError.getVisibility() == View.VISIBLE) {
            boolean isEmailVerifiedError = true;
            mBundle.putBoolean("isEmailVerifiedError", isEmailVerifiedError);
            mBundle.putString("saveEmailVerifiedErrorText",
                    mContext.getResources().getString(R.string.USR_Janrain_Error_Need_Email_Verification));
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("saveEmailVerifiedErrorText") != null &&
                    savedInstanceState.getBoolean("isEmailVerifiedError")) {
                updateErrorNotification(savedInstanceState.getString("saveEmailVerifiedErrorText"));
            }
        }
        mBundle = null;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(TAG, "AccountActivationFragment : onConfigurationChanged");
        setCustomParams(config);
    }

    @OnClick(R2.id.usr_activation_emailVerified_button)
    void emailVerified() {
        RLog.i(TAG, TAG + ".emailVerified clicked");

        showActivateSpinner();
        activateButtonEnable(false);
        mBtnResend.setEnabled(false);
        mUser.refreshUser(this);
    }

    @OnClick(R2.id.usr_activation_emailNotReceived_button)
    void emailResend() {
        RLog.i(TAG, TAG + ".emailResend clicked");
        getRegistrationFragment().addFragment(new AccountActivationResendMailFragment());
    }

    private void initUI(View view) {
        consumeTouch(view);
        setDiscription();
        handleUiState(networkUtility.isNetworkAvailable());
    }

    void setDiscription() {
        mEmailId = mUser.getEmail();
        String email = getString(R.string.USR_DLS_Verify_Email_Sent_Txt);
        email = String.format(email, mEmailId);
        setupSpannableText(mTvVerifyEmail, email, mEmailId);

    }

    private void setupSpannableText(TextView mTvVerifyEmailText,
                                    String moreAccountSettings, String link) {
        SpannableString spanableString = new SpannableString(moreAccountSettings);
        int termStartIndex = moreAccountSettings.toLowerCase().indexOf(
                link.toLowerCase());
        spanableString.setSpan(new StyleSpan(Typeface.BOLD), termStartIndex,
                termStartIndex + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvVerifyEmailText.setText(spanableString);
    }

    private void showActivateSpinner() {
        mBtnActivate.showProgressIndicator();
    }

    @Override
    public void setViewParams(Configuration config, int width) {
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        if (isSocialProvider) {
            return R.string.USR_DLS_SigIn_TitleTxt;
        } else {
            return R.string.USR_DLS_URCreateAccount_NavTitle;
        }
    }

    @Override
    public void handleUiState(boolean isNetworkAvailable) {
        if (isInstanceofCurrentFragment() && isVisible()) {
            if (isNetworkAvailable) {
                if (UserRegistrationInitializer.getInstance().isJanrainIntialized()||UserRegistrationInitializer.getInstance().isJumpInitializationInProgress()) {
                    mEMailVerifiedError.hideError();
                    activateButtonEnable(true);
                    mBtnResend.setEnabled(true);
                    mBtnActivate.setEnabled(true);
                } else {
                    activateButtonEnable(false);
                    mBtnResend.setEnabled(false);
                    mBtnActivate.setEnabled(false);
                    showNotificationBarOnNetworkNotAvailable();
                }
            } else {
                showNotificationBarOnNetworkNotAvailable();
                activateButtonEnable(false);
                mBtnResend.setEnabled(false);
                scrollViewAutomatically(mEMailVerifiedError, mSvRootLayout);
            }
        }
    }

    @Override
    public void updateActivationUIState() {
        if (isInstanceofCurrentFragment()) {
            hideActivateSpinner();
            if (mUser.isEmailVerified()) {
                mBtnResend.setVisibility(View.GONE);
                mEMailVerifiedError.hideError();
                trackActionStatus(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_USER_REGISTRATION);
                getRegistrationFragment().userRegistrationComplete();
            } else {
                UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo(mContext);
                userRegistrationFailureInfo.setErrorDescription(AppTagingConstants.EMAIL_VERIFICATION);
                userRegistrationFailureInfo.setErrorTagging(AppTagingConstants.EMAIL_VERIFICATION);
                showVerifyAlertDialog();
                trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.USER_ERROR,
                        AppTagingConstants.EMAIL_NOT_VERIFIED);
            }
            activateButtonEnable(true);
            mBtnResend.setEnabled(true);
        }
    }

    private void showVerifyAlertDialog() {
        if (isInstanceofCurrentFragment()) {
            RegAlertDialog.showDialog(mContext.getResources().getString(
                    R.string.USR_DLS_Email_Verify_Alert_Title),
                    mContext.getResources().getString(
                            R.string.USR_DLS_Email_Verify_Alert_Body_Line1),
                    mContext.getResources().getString(
                            R.string.USR_DLS_Email_Verify_Alert_Body_Line2),
                    mContext.getResources().getString(
                            R.string.USR_DLS_Button_Title_Ok)
                    , getRegistrationFragment().getParentActivity(), mContinueBtnClick);
        }
    }

    @Override
    public void hideActivateSpinner() {
        mBtnActivate.hideProgressIndicator();
    }

    @Override
    public void activateButtonEnable(boolean enable) {
        mBtnActivate.setClickable(enable);
        mBtnActivate.setEnabled(enable);
    }

    @Override
    public void verificationError(String errorMsg) {
        updateErrorNotification(errorMsg);
    }


    @Override
    public void onRefreshUserSuccess() {
        if (this.isVisible()) {
            RLog.d(TAG, "onRefreshUserSuccess");
            setDiscription();
            if (mEmailId.equals(mUser.getEmail())) {
                updateActivationUIState();
            } else {
                mEmailId = mUser.getEmail();
            }
        }
    }

    @Override
    public void onRefreshUserFailed(final int error) {
        handleRefreshUserFailed(error);
    }


    private void handleRefreshUserFailed(int error) {
        RLog.d(TAG, "onRefreshUserFailed");
        if (error == ErrorCodes.HSDP_ACTIVATE_ACCOUNT_FAILED) {
            verificationError(new URError(mContext).getLocalizedError(ErrorType.NETWOK, ErrorCodes.NETWORK_ERROR));
            hideActivateSpinner();
            activateButtonEnable(true);
            mBtnResend.setEnabled(true);
        } else {
            updateActivationUIState();
        }
    }

    private View.OnClickListener mContinueBtnClick = view -> RegAlertDialog.dismissDialog();


    @Subscribe
    public void onEvent(UpdateEmail event) {
        mUser.refreshUser(this);
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

        if (wasAppInBackground && isInstanceofCurrentFragment()) {
            showActivateSpinner();
            handleUiState(networkUtility.isNetworkAvailable());
            mUser.refreshUser(this);
            wasAppInBackground = false;
        }
    }

    private boolean isInstanceofCurrentFragment() {
        if (getFragmentManager() == null) return false;
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fl_reg_fragment_container);
        return (currentFragment != null && currentFragment instanceof AccountActivationFragment);
    }

    @Override
    public void notificationInlineMsg(String msg) {
        mEMailVerifiedError.setError(msg);
    }
}