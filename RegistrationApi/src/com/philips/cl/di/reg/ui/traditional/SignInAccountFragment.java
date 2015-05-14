package com.philips.cl.di.reg.ui.traditional;

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

import com.philips.cl.di.reg.R;
import com.philips.cl.di.reg.User;
import com.philips.cl.di.reg.dao.ForgotPasswordFailureInfo;
import com.philips.cl.di.reg.dao.SignInTraditionalFailuerInfo;
import com.philips.cl.di.reg.events.EventHelper;
import com.philips.cl.di.reg.events.EventListener;
import com.philips.cl.di.reg.handlers.ForgotPasswordHandler;
import com.philips.cl.di.reg.handlers.TraditionalLoginHandler;
import com.philips.cl.di.reg.settings.RegistrationHelper;
import com.philips.cl.di.reg.ui.customviews.XEmail;
import com.philips.cl.di.reg.ui.customviews.XPassword;
import com.philips.cl.di.reg.ui.customviews.XRegError;
import com.philips.cl.di.reg.ui.customviews.onUpdateListener;
import com.philips.cl.di.reg.ui.utils.EmailValidator;
import com.philips.cl.di.reg.ui.utils.JanrainErrorMessage;
import com.philips.cl.di.reg.ui.utils.NetworkUtility;
import com.philips.cl.di.reg.ui.utils.RLog;
import com.philips.cl.di.reg.ui.utils.RegAlertDialog;
import com.philips.cl.di.reg.ui.utils.RegConstants;

public class SignInAccountFragment extends RegistrationBaseFragment implements
		OnClickListener, TraditionalLoginHandler, ForgotPasswordHandler,
		onUpdateListener, EventListener {

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

	private final int INVALID_CREDENTIAL = 10;

	private XRegError mRegError;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE,
				"UserPhilipsAccountSignInFragment : onCreateView");
		EventHelper.getInstance().registerEventNotification(
				RegConstants.IS_ONLINE, this);
		EventHelper.getInstance().registerEventNotification(
				RegConstants.JANRAIN_INIT_SUCCESS, this);
		EventHelper.getInstance().registerEventNotification(
				RegConstants.JANRAIN_INIT_FAILURE, this);
		View view = inflater.inflate(R.layout.fragment_sign_in_account, null);
		initUI(view);
		return view;
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		RLog.d(RLog.FRAGMENT_LIFECYCLE,
				"UserPhilipsAccountSignInFragment : onConfigurationChanged");
		setViewParams(config);
	}

	@Override
	public void setViewParams(Configuration config) {
		applyParams(config, mLlCreateAccountFields);
		applyParams(config, mRlSignInBtnContainer);
		applyParams(config, mRegError);
	}

	@Override
	public void onDestroy() {
		EventHelper.getInstance().unregisterEventNotification(
				RegConstants.IS_ONLINE, this);
		EventHelper.getInstance().unregisterEventNotification(
				RegConstants.JANRAIN_INIT_SUCCESS, this);
		EventHelper.getInstance().unregisterEventNotification(
				RegConstants.JANRAIN_INIT_FAILURE, this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_reg_sign_in) {
			signIn();
		} else if (id == R.id.btn_reg_forgot_password) {
			resetPassword();

		} else if (id == R.id.btn_reg_resend) {
			mEtEmail.clearFocus();
			mEtPassword.clearFocus();
			getRegistrationMainActivity().addFragment(
					new AccountActivationFragment());
		}
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
		mRlSignInBtnContainer = (RelativeLayout) view
				.findViewById(R.id.rl_reg_welcome_container);

		mEtEmail = (XEmail) view.findViewById(R.id.rl_reg_email_field);
		mEtEmail.setOnClickListener(this);
		mEtEmail.setOnUpdateListener(this);
		mEtEmail.setFocusable(true);
		mEtPassword = (XPassword) view.findViewById(R.id.rl_reg_password_field);
		mEtPassword.setOnClickListener(this);
		mEtPassword.setOnUpdateListener(this);
		mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
		setViewParams(getResources().getConfiguration());
		handleUiState();
		mUser = new User(getActivity().getApplicationContext());
		mPbSignInSpinner = (ProgressBar) view
				.findViewById(R.id.pb_reg_sign_in_spinner);
		mPbForgotPasswdSpinner = (ProgressBar) view
				.findViewById(R.id.pb_reg_forgot_spinner);

	}

	@Override
	public String getActionbarTitle() {
		return getResources().getString(R.string.SigIn_TitleTxt);
	}

	private void signIn() {
		if (mUser != null)
			showSignInSpinner();
		mEtEmail.clearFocus();
		mEtPassword.clearFocus();
		mBtnForgot.setEnabled(false);
		mBtnResend.setEnabled(false);
		mUser.loginUsingTraditional(mEtEmail.getEmailId().toString(),
				mEtPassword.getPassword().toString(), this);
	}

	private void handleUiState() {
		if (NetworkUtility.getInstance().isOnline()) {
			if (RegistrationHelper.isJanrainIntialized()) {
				mRegError.hideError();
			} else {
				mRegError.setError(getString(R.string.NoNetworkConnection));
			}
		} else {
			mRegError.setError(getString(R.string.NoNetworkConnection));
		}
	}

	@Override
	public void onLoginSuccess() {
		hideSignInSpinner();
		mBtnForgot.setEnabled(true);
		mBtnResend.setEnabled(true);
		mRegError.hideError();
		if (mUser.getEmailVerificationStatus(getActivity())) {
			getRegistrationMainActivity().addWelcomeFragmentOnVerification();
		} else {
			mRegError
					.setError(getString(R.string.Janrain_Error_Need_Email_Verification));
			mBtnResend.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoginFailedWithError(
			SignInTraditionalFailuerInfo signInTraditionalFailuerInfo) {
		mBtnForgot.setEnabled(true);
		mBtnResend.setEnabled(true);
		hideSignInSpinner();
		if (signInTraditionalFailuerInfo.getErrorCode() == INVALID_CREDENTIAL) {

			if (null != signInTraditionalFailuerInfo.getEmailErrorMessage()) {
				mEtEmail.setErrDescription(signInTraditionalFailuerInfo
						.getEmailErrorMessage());
				mEtEmail.showInvalidAlert();
			}

			if (null != signInTraditionalFailuerInfo.getPasswordErrorMessage()) {

				mEtPassword.setErrDescription(signInTraditionalFailuerInfo
						.getPasswordErrorMessage());
				mEtPassword.showInvalidAlert();
			}

			mRegError.setError(signInTraditionalFailuerInfo
					.getErrorDescription());

		} else {
			JanrainErrorMessage errorMessage = new JanrainErrorMessage(
					getActivity());
			String message = errorMessage.getError(signInTraditionalFailuerInfo
					.getErrorCode());
			updateUiStatus();
			mEtPassword.setErrDescription(message);
			mEtEmail.setErrDescription(message);
			mEtEmail.showInvalidAlert();
			mEtPassword.showJanarainError();

		}
	}

	

	@Override
	public void onSendForgotPasswordSuccess() {
		hideForgotPasswordSpinner();
		RegAlertDialog.showResetPasswordDialog(getRegistrationMainActivity());
		hideForgotPasswordSpinner();
		mBtnResend.setEnabled(true);
		mRegError.hideError();
	}

	@Override
	public void onSendForgotPasswordFailedWithError(
			ForgotPasswordFailureInfo forgotPasswordFailureInfo) {
		mBtnResend.setEnabled(true);
		hideForgotPasswordSpinner();
		
		if(forgotPasswordFailureInfo.getError().captureApiError.code == RegConstants.ONLY_SOCIAL_SIGN_IN_ERROR_CODE){
			mEtEmail.setErrDescription(forgotPasswordFailureInfo
					.getEmailErrorMessage());
			mEtEmail.showInvalidAlert();
			mRegError.setError(forgotPasswordFailureInfo
					.getEmailErrorMessage());
			return;
			
		}

		if (forgotPasswordFailureInfo.getError().captureApiError.code == RegConstants.NO_SUCH_ACCOUNT_ERROR_CODE) {

			if (null != forgotPasswordFailureInfo.getEmailErrorMessage()) {
				mEtEmail.setErrDescription(forgotPasswordFailureInfo
						.getEmailErrorMessage());
				mEtEmail.showInvalidAlert();
			}

			mRegError.setError(forgotPasswordFailureInfo.getErrorDescription());

		} else {

			JanrainErrorMessage errorMessage = new JanrainErrorMessage(
					getActivity());
			String message = errorMessage.getError(forgotPasswordFailureInfo
					.getErrorCode());
			mRegError.setError(message);
			updateUiStatus();
			mEtPassword.setErrDescription(message);
			mEtEmail.setErrDescription(message);
			mEtEmail.showInvalidAlert();
			mEtPassword.showJanarainError();

		}
	}

	private void showSignInSpinner() {
		mPbSignInSpinner.setVisibility(View.VISIBLE);
		mBtnSignInAccount.setEnabled(false);
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
		boolean validatorResult = EmailValidator.isValidEmail(mEtEmail
				.getEmailId().toString());
		if (!validatorResult) {
			mEtEmail.showInvalidAlert();
		} else {
			if (NetworkUtility.getInstance().isOnline()) {
				if (mUser != null) {
					showForgotPasswordSpinner();
					mEtEmail.clearFocus();
					mEtPassword.clearFocus();
					mBtnSignInAccount.setEnabled(false);
					mBtnResend.setEnabled(false);
					mUser.forgotPassword(mEtEmail.getEmailId().toString(), this);
				}

			} else {
				mRegError.setError(getString(R.string.NoNetworkConnection));
			}
		}
	}

	private void updateUiStatus() {
		if (mEtEmail.isValidEmail() && mEtPassword.isValidPassword()
				&& NetworkUtility.getInstance().isOnline()
				&& RegistrationHelper.isJanrainIntialized()) {
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
		if (RegConstants.IS_ONLINE.equals(event)) {
			handleUiState();
			updateUiStatus();
		} else if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
			System.out.println("reint");
			updateUiStatus();
		}
	}

}
