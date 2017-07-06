
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.social;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingErrors;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.handlers.TraditionalLoginHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.customviews.OnUpdateListener;
import com.philips.cdp.registration.ui.customviews.PasswordView;
import com.philips.cdp.registration.ui.customviews.XButton;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegAlertDialog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.URInterface;

import javax.inject.Inject;

public class MergeAccountFragment extends RegistrationBaseFragment implements EventListener,
        OnUpdateListener, TraditionalLoginHandler, ForgotPasswordHandler, NetworStateListener,
        OnClickListener {

    @Inject
    NetworkUtility networkUtility;

    private TextView mTvAccountMergeSignIn;

    private LinearLayout mLlUsedEMailAddressContainer;

    private LinearLayout mLlCreateAccountFields;

    private RelativeLayout mRlSingInOptions;

    private XRegError mRegError;

    private XButton mBtnMerge;

    private XButton mBtnForgotPassword;

    private String mEmailId;

    private PasswordView mEtPassword;

    private ProgressBar mPbMergeSpinner;

    private ProgressBar mPbForgotPaswwordSpinner;

    private String mMergeToken;

    private User mUser;

    private Context mContext;

    private TextView mTvUsedEmail;

    private ScrollView mSvRootLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onCreateView");
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        mContext = getRegistrationFragment().getParentActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.reg_fragment_social_merge_account, container, false);
        RLog.i(RLog.EVENT_LISTENERS,
                "MergeAccountFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS");
        mUser = new User(mContext);
        mSvRootLayout = (ScrollView) view.findViewById(R.id.sv_root_layout);
        initUI(view);
        handleUiErrorState();
        handleOrientation(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onDestroy");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
                this);
        RLog.i(RLog.EVENT_LISTENERS,
                "MergeAccountFragment unregister: JANRAIN_INIT_SUCCESS,NetworStateListener");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onDetach");
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "MergeAccountFragment : onConfigurationChanged");
        setCustomParams(config);
    }

    private void initUI(View view) {
        consumeTouch(view);
        Bundle bundle = this.getArguments();
        mBtnMerge = (XButton) view.findViewById(R.id.btn_reg_merg);
        mBtnMerge.setOnClickListener(this);

        mEmailId = bundle.getString(RegConstants.SOCIAL_MERGE_EMAIL);

        mBtnForgotPassword = (XButton) view.findViewById(R.id.btn_reg_forgot_password);
        mBtnForgotPassword.setOnClickListener(this);
        mTvAccountMergeSignIn = (TextView) view.findViewById(R.id.tv_reg_account_merge_sign_in);
        mLlUsedEMailAddressContainer = (LinearLayout) view
                .findViewById(R.id.ll_reg_account_merge_container);

        mLlCreateAccountFields = (LinearLayout) view
                .findViewById(R.id.ll_reg_create_account_fields);

        mRlSingInOptions = (RelativeLayout) view.findViewById(R.id.rl_reg_btn_container);
        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        mEtPassword = (PasswordView) view.findViewById(R.id.rl_reg_password_field);
        ((RegistrationFragment) getParentFragment()).showKeyBoard();
        mEtPassword.requestFocus();
        mEtPassword.setOnUpdateListener(this);
        mEtPassword.isValidatePassword(false);

        mPbMergeSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_merge_sign_in_spinner);
        mPbMergeSpinner.setClickable(false);
        mPbMergeSpinner.setEnabled(true);

        mPbForgotPaswwordSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_forgot_spinner);
        mPbForgotPaswwordSpinner.setClickable(false);
        mPbForgotPaswwordSpinner.setEnabled(true);

        mTvUsedEmail = (TextView) view.findViewById(R.id.tv_reg_used_email);

        mMergeToken = bundle.getString(RegConstants.SOCIAL_MERGE_TOKEN);

        mEtPassword.setHint(mContext.getResources().getString(R.string.reg_Account_Merge_EnterPassword_Placeholder_txtFiled));

        trackActionStatus(AppTagingConstants.SEND_DATA,
                AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.START_SOCIAL_MERGE);

        String usedEmail = getString(R.string.reg_Account_Merge_UsedEmail_Error_lbltxt);
        usedEmail = String.format(usedEmail, mEmailId);
        mTvUsedEmail.setText(usedEmail);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reg_merg) {
            RLog.d(RLog.ONCLICK, "MergeAccountFragment : Merge");
            if (mEtPassword.hasFocus()) {
                mEtPassword.clearFocus();
            }
            getView().requestFocus();
            mergeAccount();

        } else if (v.getId() == R.id.btn_reg_forgot_password) {
            RLog.d(RLog.ONCLICK, "MergeAccountFragment : Forgot Password");
            resetPassword();
        }
    }

    private void mergeAccount() {
        if (networkUtility.isNetworkAvailable()) {
            mUser.mergeToTraditionalAccount(mEmailId, mEtPassword.getPassword(), mMergeToken, this);
            showMergeSpinner();
        } else {
            mRegError.setError(getString(R.string.reg_JanRain_Error_Check_Internet));
        }
    }

    private void resetPassword() {
        boolean validatorResult = FieldsValidator.isValidEmail(mEmailId);
        if (!validatorResult) {
        } else {
            if (networkUtility.isNetworkAvailable()) {
                if (mUser != null) {
                    showForgotPasswordSpinner();
                    mEtPassword.clearFocus();
                    mBtnMerge.setEnabled(false);
                    mUser.forgotPassword(mEmailId.toString(), this);
                }

            } else {
                mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            }
        }
    }

    private void showMergeSpinner() {
        mPbMergeSpinner.setVisibility(View.VISIBLE);
        mBtnMerge.setEnabled(false);
    }

    private void hideMergeSpinner() {
        mPbMergeSpinner.setVisibility(View.INVISIBLE);
        mBtnMerge.setEnabled(true);
    }

    private void showForgotPasswordSpinner() {
        mPbForgotPaswwordSpinner.setVisibility(View.VISIBLE);
        mBtnForgotPassword.setEnabled(false);
    }

    private void hideForgotPasswordSpinner() {
        mPbForgotPaswwordSpinner.setVisibility(View.INVISIBLE);
        mBtnForgotPassword.setEnabled(true);
    }

    private void handleUiErrorState() {
        if (networkUtility.isNetworkAvailable()) {
            if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
            } else {
                mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            }
        } else {
            mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }

    private void updateUiStatus() {
        RLog.i("MergeAccountFragment", "updateUiStatus");
        if (mEtPassword.isValidPassword()
                && networkUtility.isNetworkAvailable()
                && UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
            mBtnMerge.setEnabled(true);
            mBtnForgotPassword.setEnabled(true);
            mRegError.hideError();
        } else {
            mBtnMerge.setEnabled(false);
        }
    }

    @Override
    public void onEventReceived(String event) {
        RLog.i(RLog.EVENT_LISTENERS, "MergeAccountFragment :onCounterEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            updateUiStatus();
        }
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, mTvAccountMergeSignIn, width);
        applyParams(config, mLlUsedEMailAddressContainer, width);
        applyParams(config, mLlCreateAccountFields, width);
        applyParams(config, mRlSingInOptions, width);
        applyParams(config, mRegError, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public void onUpdate() {
        updateUiStatus();
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_SigIn_TitleTxt;
    }

    @Override
    public void onLoginSuccess() {
        RLog.i(RLog.CALLBACK, "MergeAccountFragment : onLoginSuccess");
        trackActionStatus(AppTagingConstants.SEND_DATA,
                AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_SOCIAL_MERGE);
        hideMergeSpinner();
        launchWelcomeFragment();
    }

    private void launchAlmostDoneScreen() {
        getRegistrationFragment().addAlmostDoneFragmentforTermsAcceptance();
        trackPage(AppTaggingPages.ALMOST_DONE);
    }

    private void launchWelcomeFragment() {
        if (!mUser.getReceiveMarketingEmail()) {
            launchAlmostDoneScreen();
            return;
        }
        getRegistrationFragment().addWelcomeFragmentOnVerification();
        trackPage(AppTaggingPages.WELCOME);
    }

    @Override
    public void onLoginFailedWithError(final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        handleLoginFailed(userRegistrationFailureInfo);
    }

    private void handleLoginFailed(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "MergeAccountFragment : onLoginFailedWithError");
        hideMergeSpinner();
        mRegError.setError(userRegistrationFailureInfo.getErrorDescription());
        scrollViewAutomatically(mRegError, mSvRootLayout);
    }


    @Override
    public void onSendForgotPasswordSuccess() {
        handleSendForgetPasswordSuccess();
    }

    private void handleSendForgetPasswordSuccess() {

        RLog.i(RLog.CALLBACK, "MergeAccountFragment : onSendForgotPasswordSuccess");
        RegAlertDialog.showResetPasswordDialog(mContext.getResources().getString(R.string.reg_ForgotPwdEmailResendMsg_Title),
                mContext.getResources().getString(R.string.reg_ForgotPwdEmailResendMsg), getRegistrationFragment().getParentActivity(), mContinueBtnClick);
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.STATUS_NOTIFICATION,
                AppTagingConstants.RESET_PASSWORD_SUCCESS);
        hideForgotPasswordSpinner();
        mRegError.hideError();
    }

    @Override
    public void onSendForgotPasswordFailedWithError(final
                                                    UserRegistrationFailureInfo userRegistrationFailureInfo) {

        RLog.i(RLog.CALLBACK, "MergeAccountFragment : onSendForgotPasswordFailedWithError");
        hideForgotPasswordSpinner();
        mRegError.setError(userRegistrationFailureInfo.getErrorDescription());
        scrollViewAutomatically(mRegError, mSvRootLayout);
        AppTaggingErrors.trackActionForgotPasswordFailure(userRegistrationFailureInfo, AppTagingConstants.JANRAIN);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "MergeAccountFragment :onNetWorkStateReceived state :"
                + isOnline);
        handleUiErrorState();
        updateUiStatus();
    }

    private OnClickListener mContinueBtnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            RegAlertDialog.dismissDialog();
        }
    };
}
