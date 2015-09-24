
package com.philips.cdp.registration.ui.traditional;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.registration.AppTagging.AppTaggingPages;
import com.philips.cdp.registration.AppTagging.AppTagingConstants;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.handlers.ResendVerificationEmailHandler;
import com.philips.cdp.registration.handlers.TraditionalLoginHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.XEmail;
import com.philips.cdp.registration.ui.customviews.XPassword;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.customviews.onUpdateListener;
import com.philips.cdp.registration.ui.utils.EmailValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegAlertDialog;
import com.philips.cdp.registration.ui.utils.RegConstants;

public class SignInAccountFragment extends RegistrationBaseFragment implements OnClickListener,
        TraditionalLoginHandler, ForgotPasswordHandler, onUpdateListener, EventListener, ResendVerificationEmailHandler,
        NetworStateListener {

    private LinearLayout mLlCreateAccountFields;

    private RelativeLayout mRlSignInBtnContainer;

    private Button mBtnSignInAccount;

    private Button mBtnForgot;

    private Button mBtnResend;

    private XEmail mEtEmail;

    private XPassword mEtPassword;

    private User mUser;

    private ProgressBar mPbSignInSpinner;

    private ProgressBar mPbForgotPasswdSpinner;

    private XRegError mRegError;

    private Context mContext;

    private LinearLayout mLlattentionBox;

    private View mViewAttentionBoxLine;

    private TextView mTvResendDetails;

    private final int SOCIAL_SIGIN_IN_ONLY_CODE = 540;

    @Override
    public void onAttach(Activity activity) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onCreateView");
        mContext = getRegistrationFragment().getParentActivity().getApplicationContext();
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        View view = inflater.inflate(R.layout.fragment_sign_in_account, null);
        RLog.i(RLog.EVENT_LISTENERS,
                "SignInAccountFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS");
        initUI(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onDestroy");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
                this);
        RLog.i(RLog.EVENT_LISTENERS,
                "SignInAccountFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onDetach");
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "SignInAccountFragment : onConfigurationChanged");
        setViewParams(config);
    }

    @Override
    public void setViewParams(Configuration config) {
        applyParams(config, mLlCreateAccountFields);
        applyParams(config, mRlSignInBtnContainer);
        applyParams(config, mRegError);
        applyParams(config, mTvResendDetails);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_reg_sign_in) {
           /* mEtEmail.setOnUpdateListener(null);
            mEtPassword.setOnUpdateListener(null);*/
            RLog.d(RLog.ONCLICK, "SignInAccountFragment : SignIn");
            signIn();
        } else if (id == R.id.btn_reg_forgot_password) {
            RLog.d(RLog.ONCLICK, "SignInAccountFragment : Forgot Password");
            resetPassword();

        } else if (id == R.id.btn_reg_resend) {
            RLog.d(RLog.ONCLICK, "SignInAccountFragment : Resend");
            mEtEmail.clearFocus();
            mEtPassword.clearFocus();
            lauchAccountActivationFragment();
        }
    }

    private void lauchAccountActivationFragment() {
        getRegistrationFragment().addFragment(new AccountActivationFragment());
        trackPage(AppTaggingPages.ACCOUNT_ACTIVATION);
    }

    private void initUI(View view) {
        consumeTouch(view);
        mBtnSignInAccount = (Button) view.findViewById(R.id.btn_reg_sign_in);
        mBtnSignInAccount.setOnClickListener(this);
        mBtnForgot = (Button) view.findViewById(R.id.btn_reg_forgot_password);
        mBtnForgot.setOnClickListener(this);
        mBtnResend = (Button) view.findViewById(R.id.btn_reg_resend);
        mBtnResend.setOnClickListener(this);
        mLlCreateAccountFields = (LinearLayout) view
                .findViewById(R.id.ll_reg_create_account_fields);
        mRlSignInBtnContainer = (RelativeLayout) view.findViewById(R.id.rl_reg_welcome_container);

        mEtEmail = (XEmail) view.findViewById(R.id.rl_reg_email_field);
        //mEtEmail.setOnClickListener(this);
        mEtEmail.setOnUpdateListener(this);
        mEtEmail.setFocusable(true);
        mEtPassword = (XPassword) view.findViewById(R.id.rl_reg_password_field);
        //mEtPassword.setOnClickListener(this);
        mEtPassword.setOnUpdateListener(this);
        mEtPassword.isValidatePassword(false);
        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        mLlattentionBox = (LinearLayout) view.findViewById(R.id.ll_reg_attention_box);
        mViewAttentionBoxLine = view.findViewById(R.id.view_reg_attention_box_line);
        mTvResendDetails = (TextView) view.findViewById(R.id.tv_reg_resend_details);
        setViewParams(getResources().getConfiguration());
        handleUiState();

        mUser = new User(mContext);
        mPbSignInSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_sign_in_spinner);
        mPbForgotPasswdSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_forgot_spinner);

    }

    @Override
    public int getTitleResourceId() {
        return R.string.SigIn_TitleTxt;
    }

    private void signIn() {
        mEtEmail.hideValidAlertError();
        mEtPassword.hideValidAlertError();
        ((RegistrationFragment) getParentFragment()).hideKeyBoard();
        mEtEmail.clearFocus();
        mEtPassword.clearFocus();
        mBtnForgot.setEnabled(false);
        mBtnResend.setEnabled(false);
        if (mUser != null) {
            showSignInSpinner();
        }
        mUser.loginUsingTraditional(mEtEmail.getEmailId().toString(), mEtPassword.getPassword()
                .toString(), this);
    }

    private void handleUiState() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            if (RegistrationHelper.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
            } else {
                mRegError.setError(getString(R.string.NoNetworkConnection));
            }
        } else {
            trackActionLoginError(AppTagingConstants.NETWORK_ERROR_CODE);
            mRegError.setError(getString(R.string.NoNetworkConnection));
        }
    }

    @Override
    public void onLoginSuccess() {
        handleLoginSuccess();
    }

    private void launchWelcomeFragment() {
        getRegistrationFragment().addWelcomeFragmentOnVerification();
        trackPage(AppTaggingPages.WELCOME);
    }

    @Override
    public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "SignInAccountFragment : onLoginFailedWithError");
        mBtnForgot.setEnabled(true);
        mBtnResend.setEnabled(true);
        hideSignInSpinner();

        if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
            /*mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
			mEtEmail.showInvalidAlert();*/
        }

        if (null != userRegistrationFailureInfo.getPasswordErrorMessage()) {
			/*mEtPassword.setErrDescription(userRegistrationFailureInfo.getPasswordErrorMessage());
			mEtPassword.showInvalidAlert();*/
        }
        trackActionLoginError(userRegistrationFailureInfo.getError().code);
        mBtnSignInAccount.setEnabled(false);
        mRegError.setError(userRegistrationFailureInfo.getPasswordErrorMessage());
    }

    @Override
    public void onHsdpLoginFailure(int responseCode, String message) {
        hideSignInSpinner();
        mRegError.setError(message);
    }

    @Override
    public void onSendForgotPasswordSuccess() {
        RLog.i(RLog.CALLBACK, "SignInAccountFragment : onSendForgotPasswordSuccess");
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.STATUS_NOTIFICATION,
                AppTagingConstants.RESET_PASSWORD_SUCCESS);
        hideForgotPasswordSpinner();
        RegAlertDialog.showResetPasswordDialog(getRegistrationFragment().getParentActivity());
        hideForgotPasswordSpinner();
        mBtnResend.setEnabled(true);
        mRegError.hideError();
    }

    @Override
    public void onSendForgotPasswordFailedWithError(
            UserRegistrationFailureInfo userRegistrationFailureInfo) {

        RLog.i(RLog.CALLBACK, "SignInAccountFragment : onSendForgotPasswordFailedWithError");
        mBtnResend.setEnabled(true);
        hideForgotPasswordSpinner();

        if (userRegistrationFailureInfo.getError().code == SOCIAL_SIGIN_IN_ONLY_CODE) {
            mLlattentionBox.setVisibility(View.VISIBLE);
            mEtEmail.showInvalidAlert();
            mTvResendDetails.setText(getString(R.string.TraditionalSignIn_ForgotPwdSocialExplanatory_lbltxt));
            mEtEmail.setErrDescription(getString(R.string.TraditionalSignIn_ForgotPwdSocialError_lbltxt));
            mEtEmail.showErrPopUp();
        } else {
            mEtEmail.showErrPopUp();
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getSocialOnlyError());
            mEtEmail.showInvalidAlert();
            mLlattentionBox.setVisibility(View.GONE);
        }

        if (null != userRegistrationFailureInfo.getSocialOnlyError()) {
            mEtEmail.showErrPopUp();
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getSocialOnlyError());
            mEtEmail.showInvalidAlert();
            return;
        }

        if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
            mEtEmail.showInvalidAlert();
            mEtEmail.showErrPopUp();
        }

        trackActionForgotPasswordFailure(userRegistrationFailureInfo.getError().code);
    }

    private void showSignInSpinner() {
        mBtnSignInAccount.setEnabled(false);
        mPbSignInSpinner.setVisibility(View.VISIBLE);
    }

    private void hideSignInSpinner() {
        mPbSignInSpinner.setVisibility(View.INVISIBLE);
        mBtnSignInAccount.setEnabled(true);
    }

    private void showForgotPasswordSpinner() {
        mPbForgotPasswdSpinner.setVisibility(View.VISIBLE);
        mBtnForgot.setEnabled(false);
    }

    private void hideForgotPasswordSpinner() {
        mPbForgotPasswdSpinner.setVisibility(View.INVISIBLE);
        mBtnForgot.setEnabled(true);
    }

    private void resetPassword() {
        boolean validatorResult = EmailValidator.isValidEmail(mEtEmail.getEmailId().toString());
        if (!validatorResult) {
            mEtEmail.showInvalidAlert();
        } else {
            if (NetworkUtility.isNetworkAvailable(mContext)) {
                mEtEmail.hideValidAlertError();
                if (mUser != null) {
                    showForgotPasswordSpinner();
                    mEtEmail.clearFocus();
                    mEtPassword.clearFocus();
                    mBtnSignInAccount.setEnabled(false);
                    mBtnResend.setEnabled(false);
                    mUser.forgotPassword(mEtEmail.getEmailId(), this);
                }

            } else {
                mRegError.setError(getString(R.string.NoNetworkConnection));
            }
        }
    }

    private void updateUiStatus() {
        if (mEtEmail.isValidEmail() && mEtPassword.isValidPassword()
                && NetworkUtility.isNetworkAvailable(mContext)
                && RegistrationHelper.getInstance().isJanrainIntialized()) {

            mBtnSignInAccount.setEnabled(true);
            mRegError.hideError();
        } else {
            mBtnSignInAccount.setEnabled(false);
        }
    }

    @Override
    public void onUpadte() {
        updateUiStatus();
    }

    @Override
    public void onEventReceived(String event) {
        RLog.i(RLog.EVENT_LISTENERS, "SignInAccountFragment :onEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            updateUiStatus();
        }
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "SignInAccountFragment : onNetWorkStateReceived state :"
                + isOnline);
        handleUiState();
        updateUiStatus();
    }

    @Override
    public void onResendVerificationEmailSuccess() {
        trackActionStatus(AppTagingConstants.SEND_DATA,
                AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_RESEND_EMAIL_VERIFICATION);
    }

    @Override
    public void onResendVerificationEmailFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        trackActionResendVerificationFailure(userRegistrationFailureInfo.getError().code);
    }


    private void handleLoginSuccess() {
        hideSignInSpinner();
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SUCCESS_LOGIN);
        mBtnForgot.setEnabled(true);
        mBtnResend.setEnabled(true);
        mRegError.hideError();
        if (mUser.getEmailVerificationStatus(getActivity())) {
            launchWelcomeFragment();
        } else {
            mEtEmail.showEmailInvalidAlert();
            mEtEmail.showErrPopUp();
            mBtnSignInAccount.setEnabled(false);
            mEtEmail.setErrDescription(getString(R.string.Janrain_Error_Need_Email_Verification));
            mBtnResend.setVisibility(View.VISIBLE);
            mLlattentionBox.setVisibility(View.VISIBLE);
            mViewAttentionBoxLine.setVisibility(View.INVISIBLE);
        }
    }
}
