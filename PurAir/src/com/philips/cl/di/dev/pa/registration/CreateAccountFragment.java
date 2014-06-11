package com.philips.cl.di.dev.pa.registration;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.fragment.BaseFragment;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.networkutils.NetworkReceiver;
import com.philips.cl.di.dev.pa.view.FontTextView;
import com.philips.cl.di.reg.dao.DIUserProfile;
import com.philips.cl.di.reg.errormapping.Error;

public class CreateAccountFragment extends BaseFragment implements OnClickListener {
	
	public enum ErrorType { NAME, PASSWORD, EMAIL, NONE}

	private EditText mEditTextName;
	private EditText mEditTextEmail;
	private EditText mEditTextPassword;
	
	private CheckBox mCheckBoxReceivInfo;
	
	private Button mButtonCreateAccount;
	
	private LinearLayout mLayoutPhilips;
	private LinearLayout mLayoutMyPhilips;
	
	private String mName;
	private String mEmail;
	private String mPassword;
	private boolean mReceiveInfo;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.air_registration_create_account_fragment, container, false);
		initViews(view);
		
		return view;
	}
	
	private void initViews(View view) {
		mEditTextName = (EditText) view.findViewById(R.id.etName);
		mEditTextEmail = (EditText) view.findViewById(R.id.etEmailAddress);
		mEditTextPassword = (EditText) view.findViewById(R.id.etPassword);
		
		mCheckBoxReceivInfo = (CheckBox) view.findViewById(R.id.rbReceiveInformation);
		mCheckBoxReceivInfo.setOnClickListener(this);
		
		mButtonCreateAccount = (Button) view.findViewById(R.id.btnCreateAccount);
		mButtonCreateAccount.setOnClickListener(this);
		
		mLayoutPhilips = (LinearLayout) view.findViewById(R.id.llFirstRow); 
		mLayoutMyPhilips = (LinearLayout) view.findViewById(R.id.llMyPhilips);
		
		((ImageView) mLayoutMyPhilips.findViewById(R.id.logo)).setImageResource(R.drawable.indoor_pollutants);
		((FontTextView) mLayoutMyPhilips.findViewById(R.id.title)).setText(R.string.my_philips);
		mLayoutPhilips.setOnClickListener(this);
	}
	
	private void storeUserProfile() {
		DIUserProfile profile = new DIUserProfile();
		profile.setEmail(mEmail);
		profile.setGivenName(mName);
		profile.setPassword(mPassword);
		profile.setOlderThanAgeLimit(true);
		profile.setReceiveMarketingEmail(mReceiveInfo);
		
		((UserRegistrationActivity)getActivity()).setDIUserProfile(profile);
	}
	
	private void showSignInDialog(SignInDialogFragment.DialogType type) {
		SignInDialogFragment dialog = SignInDialogFragment.newInstance(type);
		FragmentManager fragMan = getFragmentManager();
		dialog.show(fragMan, null);
	}
	
	private void showErrorDialog(Error type) {
		RegistrationErrorDialogFragment dialog = RegistrationErrorDialogFragment.newInstance(type);
		FragmentManager fragMan = getFragmentManager();
		dialog.show(fragMan, null);
	}

	//TODO : Refactor
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rbReceiveInformation:
			break;
		case R.id.btnCreateAccount:
			ALog.i(ALog.CONNECTIVITY, "onClick$btnCreateAccount " + NetworkReceiver.getInstance().getLastKnownNetworkState());
			
			// Get input from form
			getInput();
			
			switch (isInputValidated()) {
			case NONE:		
				// Store user profile in UserRegistrationActivity so it can be used when usage agreement is accepted
				storeUserProfile();
				
				((UserRegistrationActivity)getActivity()).showUsageAgreementFragment();
				
				break;
			case PASSWORD:
				showErrorDialog(Error.INVALID_PASSWORD) ;
				break;
			case EMAIL:
				showErrorDialog(Error.INVALID_EMAILID) ;
				break;			
			case NAME:
				showErrorDialog(Error.INVALID_USERNAME_OR_PASSWORD) ;
				break;
			default:
				break;
			}
			break;

		case R.id.llFirstRow:
			showSignInDialog(SignInDialogFragment.DialogType.MY_PHILIPS);
			break;
		}
	}

	private void getInput() {
		mName = mEditTextName.getText().toString();
		mEmail = mEditTextEmail.getText().toString();
		mPassword = mEditTextPassword.getText().toString();
		mReceiveInfo = mCheckBoxReceivInfo.isChecked();
	}

	public ErrorType isInputValidated() {
		ALog.i(ALog.USER_REGISTRATION, "isInputValidated name " + mName + " pass " + mPassword + " email " + mEmail);
		if(mName == null || mName.length() < 1) return ErrorType.NAME;
		if(! EmailValidator.getInstance().validate(mEmail)) return ErrorType.EMAIL;
		if(mPassword == null || mPassword.length() < 6) return ErrorType.PASSWORD;
		return ErrorType.NONE ;
	}
}
