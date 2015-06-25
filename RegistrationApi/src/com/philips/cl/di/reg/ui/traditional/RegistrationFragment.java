
package com.philips.cl.di.reg.ui.traditional;

import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.adobe.mobile.Config;
import com.philips.cl.di.reg.R;
import com.philips.cl.di.reg.User;
import com.philips.cl.di.reg.adobe.analytics.AnalyticsPages;
import com.philips.cl.di.reg.adobe.analytics.AnalyticsUtils;
import com.philips.cl.di.reg.events.NetworStateListener;
import com.philips.cl.di.reg.listener.RegistrationTitleBarListener;
import com.philips.cl.di.reg.settings.RegistrationHelper;
import com.philips.cl.di.reg.settings.RegistrationHelper.Janrain;
import com.philips.cl.di.reg.ui.social.AlmostDoneFragment;
import com.philips.cl.di.reg.ui.social.MergeAccountFragment;
import com.philips.cl.di.reg.ui.utils.RLog;
import com.philips.cl.di.reg.ui.utils.RegConstants;

public class RegistrationFragment extends Fragment implements NetworStateListener, OnClickListener {

	private FragmentManager mFragmentManager;

	private final boolean VERIFICATION_SUCCESS = true;

	private Handler mSiteCatalistHandler = new Handler();

	private Activity mActivity;

	private RegistrationTitleBarListener mRegistrationUpdateTitleListener;

	private int titleResourceID = -99;

	private Runnable mPauseSiteCatalystRunnable = new Runnable() {

		@Override
		public void run() {
			Config.pauseCollectingLifecycleData();
		}
	};

	private Runnable mResumeSiteCatalystRunnable = new Runnable() {

		@Override
		public void run() {
			Config.collectLifecycleData();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		View view = inflater.inflate(R.layout.fragment_registration, container, false);
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onCreate");
		RegistrationHelper.getInstance().registerNetworkStateListener(this);
		RLog.i(RLog.EVENT_LISTENERS, "RegistrationActivity  Register: NetworStateListener");
		mFragmentManager = getChildFragmentManager();
		loadFirstFragment();
		return view;
	}

	@Override
	public void onStart() {
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		mActivity = getActivity();
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onResume");
		mSiteCatalistHandler.removeCallbacksAndMessages(null);
		mSiteCatalistHandler.post(mResumeSiteCatalystRunnable);
		super.onResume();
	}

	@Override
	public void onPause() {
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onPause");
		mSiteCatalistHandler.removeCallbacksAndMessages(null);
		mSiteCatalistHandler.post(mPauseSiteCatalystRunnable);
		super.onPause();
	}

	@Override
	public void onStop() {
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onDestroy");
		RegistrationHelper.getInstance().unregisterListener(mActivity.getApplicationContext());
		RegistrationHelper.getInstance().unRegisterNetworkListener(this);
		RLog.i(RLog.EVENT_LISTENERS, "RegistrationActivity Unregister: NetworStateListener,Context");
		super.onDestroy();
	}

	public boolean onBackPressed() {
		RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationActivity : onBackPressed");
		hideKeyBoard();
		return handleBackStack();
	}

	private boolean handleBackStack() {
		int count = mFragmentManager.getBackStackEntryCount();
		if (count == 0) {
			return true;
		}
		Fragment fragment = mFragmentManager.getFragments().get(count);
		if (fragment instanceof WelcomeFragment) {
			navigateToHome();
		} else {
			mFragmentManager.popBackStack();
		}
		return false;
	}

	public void loadFirstFragment() {
		try {
			handleUserLoginStateFragments();

		} catch (IllegalStateException e) {
			RLog.e(RLog.EXCEPTION,
			        "RegistrationActivity :FragmentTransaction Exception occured in loadFirstFragment  :"
			                + e.getMessage());
		}
	}

	private void handleUserLoginStateFragments() {
		User mUser = new User(mActivity.getApplicationContext());
		if (mUser.getEmailVerificationStatus(mActivity.getApplicationContext())) {
			replaceWithWelcomeFragment();
			return;
		}
		replaceWithHomeFragment();
	}

	private void trackPage(String prevPage, String currPage) {
		AnalyticsUtils.trackPage(prevPage, currPage);
	}

	public void replaceWithHomeFragment() {
		try {
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.fl_reg_fragment_container, new HomeFragment());
			fragmentTransaction.commitAllowingStateLoss();
			trackPage("", AnalyticsPages.HOME);
		} catch (IllegalStateException e) {
			RLog.e(RLog.EXCEPTION,
			        "RegistrationActivity :FragmentTransaction Exception occured in addFragment  :"
			                + e.getMessage());
		}
	}

	public void addFragment(Fragment fragment) {
		try {
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.fl_reg_fragment_container, fragment, fragment.getTag());
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commitAllowingStateLoss();
		} catch (IllegalStateException e) {
			RLog.e(RLog.EXCEPTION,
			        "RegistrationActivity :FragmentTransaction Exception occured in addFragment  :"
			                + e.getMessage());
		}
		hideKeyBoard();
	}

	public void navigateToHome() {
		FragmentManager fragmentManager = getChildFragmentManager();
		int fragmentCount = fragmentManager.getBackStackEntryCount();
		for (int i = fragmentCount; i >= 1; i--) {
			fragmentManager.popBackStack();
		}
	}

	public void addWelcomeFragmentOnVerification() {
		WelcomeFragment welcomeFragment = new WelcomeFragment();
		Bundle welcomeFragmentBundle = new Bundle();
		welcomeFragmentBundle.putBoolean(RegConstants.VERIFICATIN_SUCCESS, VERIFICATION_SUCCESS);
		welcomeFragment.setArguments(welcomeFragmentBundle);
		addFragment(welcomeFragment);
	}

	private void replaceWithWelcomeFragment() {
		try {
			WelcomeFragment welcomeFragment = new WelcomeFragment();
			Bundle welcomeFragmentBundle = new Bundle();
			welcomeFragmentBundle
			        .putBoolean(RegConstants.VERIFICATIN_SUCCESS, VERIFICATION_SUCCESS);
			welcomeFragmentBundle.putBoolean(RegConstants.IS_FROM_BEGINING, true);
			welcomeFragment.setArguments(welcomeFragmentBundle);
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.fl_reg_fragment_container, welcomeFragment);
			fragmentTransaction.commitAllowingStateLoss();
			trackPage("", AnalyticsPages.WELCOME);
		} catch (IllegalStateException e) {
			RLog.e(RLog.EXCEPTION,
			        "RegistrationActivity :FragmentTransaction Exception occured in addFragment  :"
			                + e.getMessage());
		}
	}

	public void addAlmostDoneFragment(JSONObject preFilledRecord, String provider,
	        String registrationToken) {
		AlmostDoneFragment socialAlmostDoneFragment = new AlmostDoneFragment();
		Bundle socialAlmostDoneFragmentBundle = new Bundle();
		socialAlmostDoneFragmentBundle.putString(RegConstants.SOCIAL_TWO_STEP_ERROR,
		        preFilledRecord.toString());
		socialAlmostDoneFragmentBundle.putString(RegConstants.SOCIAL_PROVIDER, provider);
		socialAlmostDoneFragmentBundle.putString(RegConstants.SOCIAL_REGISTRATION_TOKEN,
		        registrationToken);
		socialAlmostDoneFragment.setArguments(socialAlmostDoneFragmentBundle);
		addFragment(socialAlmostDoneFragment);
	}

	public void addMergeAccountFragment(String registrationToken, String provider) {
		MergeAccountFragment mergeAccountFragment = new MergeAccountFragment();
		Bundle mergeFragmentBundle = new Bundle();
		mergeFragmentBundle.putString(RegConstants.SOCIAL_PROVIDER, provider);
		mergeFragmentBundle.putString(RegConstants.SOCIAL_MERGE_TOKEN, registrationToken);
		mergeAccountFragment.setArguments(mergeFragmentBundle);
		addFragment(mergeAccountFragment);
	}

	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager) mActivity
		        .getSystemService(Context.INPUT_METHOD_SERVICE);
		if (mActivity.getWindow() != null && mActivity.getWindow().getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(mActivity.getWindow().getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_reg_back) {
			onBackPressed();
		}
	}

	@Override
	public void onNetWorkStateReceived(boolean isOnline) {
		if (!RegistrationHelper.getInstance().isJanrainIntialized()) {
			RLog.d(RLog.NETWORK_STATE, "RegistrationActivity :onNetWorkStateReceived");
			RegistrationHelper registrationSettings = RegistrationHelper.getInstance();
			registrationSettings.intializeRegistrationSettings(Janrain.REINITIALIZE,
			        mActivity.getApplicationContext(), Locale.getDefault());
			RLog.d(RLog.JANRAIN_INITIALIZE,
			        "RegistrationActivity : Janrain reinitialization with locale : "
			                + Locale.getDefault());
		}
	}

	public Activity getParentActivity() {
		return mActivity;
	}

	public int getFragmentCount() {
		return mFragmentManager.getFragments().size();

	}

	public RegistrationTitleBarListener getUpdateTitleListener() {
		return mRegistrationUpdateTitleListener;
	}

	public void setOnUpdateTitleListener(RegistrationTitleBarListener listener) {
		this.mRegistrationUpdateTitleListener = listener;
	}

	public void setResourceID(int titleResourceId) {
		titleResourceID = titleResourceId;
	}

	public int getResourceID() {
		return titleResourceID;
	}

}
