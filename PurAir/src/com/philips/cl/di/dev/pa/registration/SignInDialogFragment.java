package com.philips.cl.di.dev.pa.registration;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.philips.cl.di.dev.pa.PurAirApplication;
import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.fragment.AlertDialogFragment;
import com.philips.cl.di.dev.pa.registration.CreateAccountFragment.ErrorType;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.view.FontTextView;
import com.philips.cl.di.reg.User;
import com.philips.cl.di.reg.errormapping.Error;
import com.philips.cl.di.reg.errormapping.ErrorMessage;
import com.philips.cl.di.reg.handlers.ForgotPasswordHandler;
import com.philips.cl.di.reg.handlers.TraditionalLoginHandler;

public class SignInDialogFragment extends DialogFragment implements TraditionalLoginHandler{

	public static enum DialogType {MY_PHILIPS, FACEBOOK, TWITTER, GOOGLE_PLUS};
	
	private static final String DIALOG_SELECTED = "com.philips.cl.dev.pa.registration.sign_in_dialog";
	private FontTextView title;
	private FontTextView resetPassword;
	private Button btnClose;
	private Button btnSignIn;
	
	private User user;
	private ProgressDialog progressDialog ;
	private String mEmail ;
	private String mPassword ;
	public static SignInDialogFragment newInstance(DialogType showDialog) {
		SignInDialogFragment fragment = new SignInDialogFragment();

		Bundle args = new Bundle();
		args.putSerializable(DIALOG_SELECTED, showDialog);
		fragment.setArguments(args);		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.air_registration_sign_in_dialog, container, false);
		setCancelable(false);
		initializeView(view);
		user = new User(PurAirApplication.getAppContext());
		return view;
	}

	private void initializeView(View view) {	
		title = (FontTextView) view.findViewById(R.id.tvSingInTitle);
		resetPassword = (FontTextView) view.findViewById(R.id.tv_reset_password);
		btnClose = (Button) view.findViewById(R.id.btnClose);
		btnSignIn = (Button) view.findViewById(R.id.btnSignIn);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		final DialogType dialog = (DialogType) getArguments().getSerializable(DIALOG_SELECTED);
		
		switch (dialog) {
		case MY_PHILIPS:
			title.setText(R.string.sign_in_to_my_philips);
			break;
		case FACEBOOK:
			title.setText(R.string.sign_in_to_facebook);
			break;
		case GOOGLE_PLUS:
			title.setText(R.string.sign_in_to_google_plus);
			break;
		case TWITTER:
			title.setText(R.string.sign_in_to_twitter);
			break;
		}

		final EditText etEmail = (EditText) view.findViewById(R.id.etEmailAddress);
		final EditText etPassword = (EditText) view.findViewById(R.id.etPassword);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (v.getId() == R.id.btnSignIn) {
					mEmail = etEmail.getText().toString();
					mPassword = etPassword.getText().toString();
					
					switch (dialog) {
					case MY_PHILIPS:
						Log.i("TEMP", "My Philips: E-mail: " + mEmail + " Password: " + mPassword);
						signOnWithMyPhilips() ;
						break;
					case FACEBOOK:
						Log.i("TEMP", "Facebook: E-mail: " + mEmail + " Password: " + mPassword);
						break;
					case TWITTER:
						Log.i("TEMP", "Twitter: E-mail: " + mEmail + " Password: " + mPassword);
						break;
					case GOOGLE_PLUS:
						Log.i("TEMP", "Google+: E-mail: " + mEmail + " Password: " + mPassword);
						break;
					default:
						break;
					}
				}
				else if(v.getId() == R.id.btnClose) {
					dismiss() ;
				} else if(v.getId() == R.id.tv_reset_password) {
					mEmail = etEmail.getText().toString();
					if(! EmailValidator.getInstance().validate(mEmail)){
						AlertDialogFragment resetPasswordSuccess = AlertDialogFragment.newInstance(R.string.invalid_email, R.string.ok);
						resetPasswordSuccess.show(getChildFragmentManager(), getTag());
					} else {
						showProgressDialog();
						User user = new User(PurAirApplication.getAppContext());
						ALog.i(ALog.USER_REGISTRATION, "Forgot passwordEmail " + mEmail);
						user.forgotPassword(mEmail, new ForgotPasswordHandler() {
							
							@Override
							public void onSendForgotPasswordSuccess() {
								cancelProgressDialog();
								AlertDialogFragment resetPasswordSuccess = AlertDialogFragment.newInstance(R.string.reset_password_success, R.string.ok);
								resetPasswordSuccess.show(getChildFragmentManager(), getTag());
							}
							
							@Override
							public void onSendForgotPasswordFailedWithError(int error) {
								cancelProgressDialog();
								ALog.i(ALog.USER_REGISTRATION, "onSendForgotPasswordFailedWithError error " + error);
								AlertDialogFragment resetPasswordSuccess = AlertDialogFragment.newInstance(R.string.reset_password_failed, R.string.ok);
								resetPasswordSuccess.show(getChildFragmentManager(), getTag());
//								showErrorDialog(UserRegistrationController.getInstance().getErrorEnum(error));
							}
						});
					}
				}
			}
		};
		
		btnClose.setOnClickListener(clickListener);
		btnSignIn.setOnClickListener(clickListener);
		resetPassword.setOnClickListener(clickListener);
	}
	
	private void signOnWithMyPhilips() {
		switch(isInputValidated()) {
		case NONE:	
			try {
				user.loginUsingTraditional(mEmail, mPassword, SignInDialogFragment.this, PurAirApplication.getAppContext());
				showProgressDialog() ;
			} catch (Exception e) {
				e.printStackTrace();
				showErrorDialog(Error.GENERIC_ERROR);
			}
			break;
		case EMAIL:
			showErrorDialog(Error.INVALID_EMAILID) ;
			break ;
		case PASSWORD:
			showErrorDialog(Error.INVALID_PASSWORD) ;
			break ;
		default:
			break;
		}
	}

	public ErrorType isInputValidated() {
		ALog.i(ALog.USER_REGISTRATION, "isInputValidated: password: " + mPassword + " + emailId: " +mEmail) ;
		if(! EmailValidator.getInstance().validate(mEmail)) return ErrorType.EMAIL;
		if(mPassword == null || mPassword.length() < 6) return ErrorType.PASSWORD;
		return ErrorType.NONE ;
	}
	
	private void showProgressDialog() {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	private void cancelProgressDialog() {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel() ;
		}
	}
	private void showErrorDialog(Error type) {
		try {
			RegistrationErrorDialogFragment dialog = RegistrationErrorDialogFragment.newInstance(type);
			FragmentManager fragMan = getFragmentManager();
			dialog.show(fragMan, null);
		} catch (IllegalStateException e) {
			ALog.e(ALog.USER_REGISTRATION, e.getMessage());
		}
	}

	//TODO : Move to UserRegistrationActivity
	@Override
	public void onLoginSuccess() {
		ALog.i(ALog.USER_REGISTRATION, "onLoginSuccess");
		cancelProgressDialog() ;
		if(getActivity() != null && getActivity() instanceof UserRegistrationActivity) {
			((UserRegistrationActivity) getActivity()).showSuccessFragment();
		}
		dismiss() ;
	}

	@Override
	public void onLoginFailedWithError(int error) {
		ALog.i(ALog.USER_REGISTRATION, "onLoginError errorCode: "+error+" errormessage: "+new ErrorMessage().getError(error));
		cancelProgressDialog() ;
		showErrorDialog(UserRegistrationController.getInstance().getErrorEnum(error)) ;
	}
}
