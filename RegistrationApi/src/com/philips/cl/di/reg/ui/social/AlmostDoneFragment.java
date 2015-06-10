
package com.philips.cl.di.reg.ui.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cl.di.reg.R;
import com.philips.cl.di.reg.User;
import com.philips.cl.di.reg.adobe.analytics.AnalyticsConstants;
import com.philips.cl.di.reg.adobe.analytics.AnalyticsUtils;
import com.philips.cl.di.reg.dao.UserRegistrationFailureInfo;
import com.philips.cl.di.reg.events.EventHelper;
import com.philips.cl.di.reg.events.EventListener;
import com.philips.cl.di.reg.events.NetworStateListener;
import com.philips.cl.di.reg.handlers.SocialProviderLoginHandler;
import com.philips.cl.di.reg.settings.RegistrationHelper;
import com.philips.cl.di.reg.ui.customviews.XButton;
import com.philips.cl.di.reg.ui.customviews.XEmail;
import com.philips.cl.di.reg.ui.customviews.XRegError;
import com.philips.cl.di.reg.ui.customviews.onUpdateListener;
import com.philips.cl.di.reg.ui.traditional.AccountActivationFragment;
import com.philips.cl.di.reg.ui.traditional.RegistrationBaseFragment;
import com.philips.cl.di.reg.ui.utils.FontLoader;
import com.philips.cl.di.reg.ui.utils.NetworkUtility;
import com.philips.cl.di.reg.ui.utils.RLog;
import com.philips.cl.di.reg.ui.utils.RegConstants;

public class AlmostDoneFragment extends RegistrationBaseFragment implements EventListener,
        onUpdateListener, SocialProviderLoginHandler, NetworStateListener, OnClickListener {

	private TextView mTvSignInWith;

	private LinearLayout mLlAlmostDoneContainer;

	private LinearLayout mLlPeriodicOffersCheck;

	private RelativeLayout mRlContinueBtnContainer;

	private CheckBox mCbTerms;

	private XRegError mRegError;

	private XEmail mEtEmail;

	private XButton mBtnContinue;

	private ProgressBar mPbSpinner;

	private String mGivenName;

	private String mDisplayName;

	private String mFamilyName;

	private String mEmail;

	private String mProvider;

	private boolean isEmailExist;

	private String mRegistrationToken;

	private Context mContext;

	@Override
	public void onAttach(Activity activity) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onCreate");
		AnalyticsUtils.trackPage("FromApplication", AnalyticsConstants.PAGE_HOME);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onCreateView");
		RegistrationHelper.getInstance().registerNetworkStateListener(this);
		EventHelper.getInstance()
		        .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
		parseRegistrationInfo();
		RLog.i(RLog.EVENT_LISTENERS, "AlmostDoneFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS");
		View view = inflater.inflate(R.layout.fragment_social_almost_done, container, false);
		initUI(view);
		handleUiState();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onDestroyView");
	}

	@Override
	public void onDestroy() {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onDestroy");
		RegistrationHelper.getInstance().unRegisterNetworkListener(this);
		EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,this);
		RLog.i(RLog.EVENT_LISTENERS, "AlmostDoneFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onDetach");
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onConfigurationChanged");
		setViewParams(config);
	}

	private void parseRegistrationInfo() {

		Bundle bundle = getArguments();
		if (null != bundle) {
			try {
				JSONObject mPreRegJson = null;
				mPreRegJson = new JSONObject(bundle.getString(RegConstants.SOCIAL_TWO_STEP_ERROR));

				if (null != mPreRegJson) {
					mProvider = bundle.getString(RegConstants.SOCIAL_PROVIDER);
					mRegistrationToken = bundle.getString(RegConstants.SOCIAL_REGISTRATION_TOKEN);

					if (!mPreRegJson.isNull(RegConstants.REGISTER_GIVEN_NAME)
					        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
					                .getString(RegConstants.REGISTER_GIVEN_NAME))) {
						mGivenName = mPreRegJson.getString(RegConstants.REGISTER_GIVEN_NAME);
					}
					if (!mPreRegJson.isNull(RegConstants.REGISTER_DISPLAY_NAME)
					        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
					                .getString(RegConstants.REGISTER_DISPLAY_NAME))) {
						mDisplayName = mPreRegJson.getString(RegConstants.REGISTER_DISPLAY_NAME);
					}
					if (!mPreRegJson.isNull(RegConstants.REGISTER_FAMILY_NAME)
					        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
					                .getString(RegConstants.REGISTER_FAMILY_NAME))) {
						mFamilyName = mPreRegJson.getString(RegConstants.REGISTER_FAMILY_NAME);
					}
					if (!mPreRegJson.isNull(RegConstants.REGISTER_EMAIL)
					        && !RegConstants.SOCIAL_BLANK_CHARACTER.equals(mPreRegJson
					                .getString(RegConstants.REGISTER_EMAIL))) {
						mEmail = mPreRegJson.getString(RegConstants.REGISTER_EMAIL);
						isEmailExist = true;
					} else {
						isEmailExist = false;
					}

				}

				if (null == mGivenName) {
					mGivenName = mDisplayName;
				}

			} catch (JSONException e) {
				RLog.e(RLog.EXCEPTION, "AlmostDoneFragment Exception : "+e.getMessage());
			}
		}
	}

	private void initUI(View view) {
		consumeTouch(view);
		mBtnContinue = (XButton) view.findViewById(R.id.reg_btn_continue);
		mBtnContinue.setOnClickListener(this);
		mTvSignInWith = (TextView) view.findViewById(R.id.tv_reg_sign_in_with);
		mLlAlmostDoneContainer = (LinearLayout) view.findViewById(R.id.ll_reg_almost_done);

		mLlPeriodicOffersCheck = (LinearLayout) view
		        .findViewById(R.id.ll_reg_periodic_offers_check);

		mRlContinueBtnContainer = (RelativeLayout) view
		        .findViewById(R.id.rl_reg_btn_continue_container);

		mCbTerms = (CheckBox) view.findViewById(R.id.cb_reg_register_terms);
		FontLoader.getInstance().setTypeface(mCbTerms, "CentraleSans-Light.otf");
		mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
		mEtEmail = (XEmail) view.findViewById(R.id.rl_reg_email_field);
		mEtEmail.setOnUpdateListener(this);
		setViewParams(getResources().getConfiguration());

		mPbSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_social_almost_done_spinner);
		mPbSpinner.setClickable(false);
		mPbSpinner.setEnabled(true);

		mProvider = Character.toUpperCase(mProvider.charAt(0)) + mProvider.substring(1);
		mTvSignInWith.setText(getResources().getString(R.string.RegSignWith_Lbltxt) + " "
		        + mProvider);
		mContext = getRegistrationMainActivity().getApplicationContext();

		if (isEmailExist) {
			mEtEmail.setVisibility(View.GONE);
		} else {
			mEtEmail.setVisibility(View.VISIBLE);
		}
	}

	private void handleUiState() {
		if (NetworkUtility.isNetworkAvailable(mContext)) {
			if (RegistrationHelper.getInstance().isJanrainIntialized()) {
				mRegError.hideError();
			} else {
				mRegError.setError(getString(R.string.NoNetworkConnection));
			}
		} else {
			mRegError.setError(getString(R.string.NoNetworkConnection));
		}
	}

	private void updateUiStatus() {
		if (NetworkUtility.isNetworkAvailable(mContext)
		        && RegistrationHelper.getInstance().isJanrainIntialized()) {
			mBtnContinue.setEnabled(true);
			mRegError.hideError();
		} else {
			mBtnContinue.setEnabled(false);
		}
	}

	private void showSpinner() {
		mPbSpinner.setVisibility(View.VISIBLE);
		mBtnContinue.setEnabled(false);
	}

	private void hideSpinner() {
		mPbSpinner.setVisibility(View.INVISIBLE);
		mBtnContinue.setEnabled(true);
	}


	@Override
	public void setViewParams(Configuration config) {
		applyParams(config, mTvSignInWith);
		applyParams(config, mLlAlmostDoneContainer);
		applyParams(config, mLlPeriodicOffersCheck);
		applyParams(config, mRlContinueBtnContainer);
		applyParams(config, mRegError);
	}

	@Override
	public void onEventReceived(String event) {
		RLog.i(RLog.EVENT_LISTENERS, "AlmostDoneFragment :onEventReceived is : "+event);
		if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
			updateUiStatus();
		}

	}

	@Override
	public void onUpadte() {
		updateUiStatus();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.reg_btn_continue) {
			RLog.d(RLog.ONCLICK,"AlmostDoneFragment : Continue");
			showSpinner();
			mEtEmail.clearFocus();
			register();
		}

	}

	private void register() {
		if (NetworkUtility.isNetworkAvailable(mContext)) {
			User user = new User(mContext);
			if (isEmailExist) {
				user.registerUserInfoForSocial(mGivenName, mDisplayName, mFamilyName, mEmail, true,
				        mCbTerms.isChecked(), this, mRegistrationToken);
			} else {
				user.registerUserInfoForSocial(mGivenName, mDisplayName, mFamilyName,
				        mEtEmail.getEmailId(), true, mCbTerms.isChecked(), this, mRegistrationToken);
			}
		}
	}

	@Override
	public String getActionbarTitle() {
		return getResources().getString(R.string.SigIn_TitleTxt);
	}

	@Override
	public void onLoginSuccess() {
		RLog.i(RLog.CALLBACK,"AlmostDoneFragment : onLoginSuccess");
		hideSpinner();
	}

	@Override
	public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
		RLog.i(RLog.CALLBACK,"AlmostDoneFragment : onLoginFailedWithError");
		hideSpinner();

		if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
			mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
			mEtEmail.showInvalidAlert();
		}
		mRegError.setError(userRegistrationFailureInfo.getErrorDescription());
	}

	@Override
	public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord,
	        String socialRegistrationToken) {
		RLog.i(RLog.CALLBACK,"AlmostDoneFragment : onLoginFailedWithTwoStepError");
		hideSpinner();

	}

	@Override
	public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider,
	        String conflictingIdentityProvider, String conflictingIdpNameLocalized,
	        String existingIdpNameLocalized) {
		RLog.i(RLog.CALLBACK,"AlmostDoneFragment : onLoginFailedWithMergeFlowError");
		hideSpinner();
		getRegistrationMainActivity().addFragment(new MergeAccountFragment());

	}

	@Override
	public void onContinueSocialProviderLoginSuccess() {
		RLog.i(RLog.CALLBACK,"AlmostDoneFragment : onContinueSocialProviderLoginSuccess");
		User user = new User(mContext);
		if (user.getEmailVerificationStatus(mContext)) {
			getRegistrationMainActivity().addWelcomeFragmentOnVerification();
		} else {
			getRegistrationMainActivity().addFragment(new AccountActivationFragment());
		}
		hideSpinner();
	}

	@Override
	public void onContinueSocialProviderLoginFailure(
	        UserRegistrationFailureInfo userRegistrationFailureInfo) {
		RLog.i(RLog.CALLBACK,"AlmostDoneFragment : onContinueSocialProviderLoginFailure");
		hideSpinner();
		if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
			mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
			mEtEmail.showInvalidAlert();
		}

		if (null != userRegistrationFailureInfo.getDisplayNameErrorMessage()) {
			mEtEmail.setErrDescription(userRegistrationFailureInfo.getDisplayNameErrorMessage());
			mEtEmail.showInvalidAlert();
			mRegError.setError(userRegistrationFailureInfo.getErrorDescription() + ".\n'"
			        + mDisplayName + "' "
			        + userRegistrationFailureInfo.getDisplayNameErrorMessage());
			return;
		}

		mRegError.setError(userRegistrationFailureInfo.getErrorDescription());
	}

	@Override
	public void onNetWorkStateReceived(boolean isOnline) {
		RLog.i(RLog.NETWORK_STATE, "AlmostDone :onNetWorkStateReceived state :"+isOnline);
		handleUiState();
		updateUiStatus();
	}
}
