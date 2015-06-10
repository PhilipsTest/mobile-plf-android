package com.philips.cl.di.reg.ui.traditional;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.philips.cl.di.reg.ui.customviews.XProviderButton;
import com.philips.cl.di.reg.ui.customviews.XRegError;
import com.philips.cl.di.reg.ui.utils.NetworkUtility;
import com.philips.cl.di.reg.ui.utils.RLog;
import com.philips.cl.di.reg.ui.utils.RegConstants;

public class HomeFragment extends RegistrationBaseFragment implements OnClickListener,
        NetworStateListener, SocialProviderLoginHandler, EventListener {

	private Button mBtnCreateAccount;

	private XProviderButton mBtnMyPhilips;

	private TextView mTvWelcome;

	private TextView mTvWelcomeDesc;

	private LinearLayout mLlCreateBtnContainer;

	private LinearLayout mLlLoginBtnContainer;

	private LinearLayout mLlSocialProviderBtnContainer;

	private XRegError mRegError;

	private User mUser;

	private String mProvider;

	private ProgressBar mPbJanrainInit;

	private Context mContext;

	@Override
	public void onAttach(Activity activity) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onCreate");
		AnalyticsUtils.trackPage("FromApplication", AnalyticsConstants.PAGE_HOME);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onCreateView");

		mContext = getRegistrationMainActivity().getApplicationContext();
		EventHelper.getInstance()
		        .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
		EventHelper.getInstance()
		        .registerEventNotification(RegConstants.JANRAIN_INIT_FAILURE, this);
		EventHelper.getInstance().registerEventNotification(RegConstants.PARSING_COMPLETED, this);
		RegistrationHelper.getInstance().registerNetworkStateListener(this);
		RLog.i(RLog.EVENT_LISTENERS, "HomeFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS,JANRAIN_INIT_FAILURE,PARSING_COMPLETED");
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		initUI(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onDestroyView");
	}

	@Override
	public void onDestroy() {
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onDestroy");
		RegistrationHelper.getInstance().unRegisterNetworkListener(this);
		EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
		        this);
		EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_FAILURE,
		        this);
		EventHelper.getInstance().unregisterEventNotification(RegConstants.PARSING_COMPLETED, this);
		RLog.i(RLog.EVENT_LISTENERS, "HomeFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS,JANRAIN_INIT_FAILURE,PARSING_COMPLETED");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onDetach");
	}

	private void handleSocialProviders(final String countryCode) {
		RLog.d("HomeFragment : ", "handleSocialProviders method country code : " + countryCode);
		if (null != RegistrationHelper.getInstance().getSocialProviders()) {
			mLlSocialProviderBtnContainer.post(new Runnable() {

				@Override
				public void run() {
					mLlSocialProviderBtnContainer.removeAllViews();
					ArrayList<String> providers = new ArrayList<String>();
					providers = RegistrationHelper.getInstance().getSocialProviders()
					        .getSocialProvidersForCountry(countryCode);
					if (null != providers) {
						for (int i = 0; i < providers.size(); i++) {
							inflateEachProviderBtn(providers.get(i));
						}
						RLog.d("HomeFragment", "social providers : " + providers);
					}
				}
			});
		}
	}
	
	private void inflateEachProviderBtn(String provider) {
		if (SocialProvider.FACEBOOK.equals(provider)) {
			mLlSocialProviderBtnContainer.addView(getProviderBtn(provider,
			        R.string.Welcome_Facebook_btntxt, R.drawable.reg_facebook_ic,
			        R.drawable.reg_facebook_bg_rect, R.color.reg_btn_text_enable_color));

		} else if (SocialProvider.TWITTER.equals(provider)) {
			mLlSocialProviderBtnContainer.addView(getProviderBtn(provider,
			        R.string.Welcome_Twitter_btntxt, R.drawable.reg_twitter_ic,
			        R.drawable.reg_twitter_bg_rect, R.color.reg_btn_text_enable_color));
		} else if (SocialProvider.GOOGLE_PLUS.equals(provider)) {
			mLlSocialProviderBtnContainer.addView(getProviderBtn(provider,
			        R.string.GooglePlus_btntxt, R.drawable.reg_google_plus_ic,
			        R.drawable.reg_google_plus_bg_rect, R.color.reg_btn_text_enable_color));
		}
	}

	private XProviderButton getProviderBtn(final String providerName, int providerNameStringId,
	        int providerLogoDrawableId, int providerBgDrawableId, int providerTextColorId) {
		final XProviderButton providerBtn = new XProviderButton(mContext);
		providerBtn.setProviderName(providerNameStringId);
		providerBtn.setProviderLogoID(providerLogoDrawableId);
		providerBtn.setProviderBackgroundID(providerBgDrawableId);
		providerBtn.setProviderTextColor(providerTextColorId);
		providerBtn.setTag(providerName);
		if (NetworkUtility.isNetworkAvailable(mContext)) {
			providerBtn.setEnabled(true);
		} else {
			providerBtn.setEnabled(false);
		}

		providerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RLog.d(RLog.ONCLICK, "HomeFragment : " + providerName);
				callSocialProvider(providerName);
				providerBtn.showProgressBar();
			}
		});
		return providerBtn;
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onConfigurationChanged");
		setViewParams(config);
	}

	private void initUI(View view) {
		consumeTouch(view);
		mTvWelcome = (TextView) view.findViewById(R.id.tv_reg_welcome);
		mTvWelcomeDesc = (TextView) view.findViewById(R.id.tv_reg_welcome_desc);
		mLlCreateBtnContainer = (LinearLayout) view
		        .findViewById(R.id.ll_reg_create_account_container);
		mLlLoginBtnContainer = (LinearLayout) view.findViewById(R.id.rl_reg_singin_options);
		mBtnCreateAccount = (Button) view.findViewById(R.id.btn_reg_create_account);
		mBtnCreateAccount.setOnClickListener(this);
		mBtnMyPhilips = (XProviderButton) view.findViewById(R.id.btn_reg_my_philips);
		mBtnMyPhilips.setOnClickListener(this);

		mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);

		mPbJanrainInit = (ProgressBar) view.findViewById(R.id.pb_reg_janrain_init);
		mPbJanrainInit.setClickable(false);
		mPbJanrainInit.setEnabled(false);
		mLlSocialProviderBtnContainer = (LinearLayout) view
		        .findViewById(R.id.ll_reg_social_provider_container);

		handleSocialProviders(RegistrationHelper.getInstance().getCountryCode());
		mUser = new User(mContext);
		setViewParams(getResources().getConfiguration());
		linkifyTermAndPolicy(mTvWelcomeDesc);
		handleJanrainInitPb();
		enableControls(false);
		handleUiState();
	}

	private void handleJanrainInitPb() {
		if (NetworkUtility.isNetworkAvailable(mContext)
		        && RegistrationHelper.getInstance().isJanrainIntialized()) {
			mPbJanrainInit.setVisibility(View.GONE);
		} else if (NetworkUtility.isNetworkAvailable(mContext)
		        && !RegistrationHelper.getInstance().isJanrainIntialized()) {
			mPbJanrainInit.setVisibility(View.VISIBLE);
		} else {
			mPbJanrainInit.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		/**
		 * Library does not include resource constants after ADT 14 Link
		 * :http://tools.android.com/tips/non-constant-fields
		 */
		if (v.getId() == R.id.btn_reg_create_account) {
			RLog.d(RLog.ONCLICK, "HomeFragment : Create Account");
			AnalyticsUtils.trackAction(AnalyticsConstants.INTERACTION,
			        AnalyticsConstants.REGISTRATION_CHANNEL, AnalyticsConstants.MY_PHILIPS);
			AnalyticsUtils.trackAction(AnalyticsConstants.INTERACTION,
			        AnalyticsConstants.SPECIAL_EVENTS, AnalyticsConstants.START_USER_REGISTRATION);
			getRegistrationMainActivity().addFragment(new CreateAccountFragment());
		} else if (v.getId() == R.id.btn_reg_my_philips) {
			RLog.d(RLog.ONCLICK, "HomeFragment : My Philips");
			AnalyticsUtils.trackAction(AnalyticsConstants.INTERACTION,
			        AnalyticsConstants.LOGIN_CHANNEL, AnalyticsConstants.MY_PHILIPS);
			getRegistrationMainActivity().addFragment(new SignInAccountFragment());
		}
	}

	private void callSocialProvider(String providerName) {
		RLog.d("HomeFragment", ": callSocialProvider method provider name :" + providerName);
		mProvider = providerName;
		if (null == mUser)
			return;
		if (NetworkUtility.isNetworkAvailable(mContext)
		        && RegistrationHelper.getInstance().isJanrainIntialized()) {
			mUser.loginUserUsingSocialProvider(getActivity(), providerName, this, null);
		}
	}

	public interface SocialProvider {

		public String FACEBOOK = "facebook";

		public String TWITTER = "twitter";

		public String GOOGLE_PLUS = "googleplus";
	}

	@Override
	public void setViewParams(Configuration config) {
		applyParams(config, mTvWelcome);
		applyParams(config, mTvWelcomeDesc);
		applyParams(config, mLlCreateBtnContainer);
		applyParams(config, mLlLoginBtnContainer);
	}

	@Override
	public String getActionbarTitle() {
		return getResources().getString(R.string.SigIn_TitleTxt);
	}

	@Override
	public void onEventReceived(String event) {
		RLog.i(RLog.EVENT_LISTENERS, "HomeFragment :onEventReceived is : " + event);
		if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
			enableControls(true);
			handleJanrainInitPb();
		} else if (RegConstants.JANRAIN_INIT_FAILURE.equals(event)) {
			enableControls(false);
			handleJanrainInitPb();
		} else if (RegConstants.PARSING_COMPLETED.equals(event)) {
			handleSocialProvider();
		}
	}

	private void handleSocialProvider() {
		RegistrationHelper.getInstance().getSocialProviders();
		handleSocialProviders(RegistrationHelper.getInstance().getCountryCode());
	}

	private void handleUiState() {
		if (NetworkUtility.isNetworkAvailable(mContext)) {
			if (RegistrationHelper.getInstance().isJanrainIntialized()) {
				mRegError.hideError();
				enableControls(true);
			} else {
				mRegError.hideError();
			}
		} else {
			mRegError.setError(mContext.getResources().getString(R.string.NoNetworkConnection));
			enableControls(false);
		}
	}

	private void enableControls(boolean state) {
		if (state && NetworkUtility.isNetworkAvailable(mContext)) {
			handleBtnClickableStates(state);
			setAlphaForView(mBtnMyPhilips, 1);
			setAlphaForView(mLlSocialProviderBtnContainer, 1);
			mRegError.hideError();
		} else {
			handleBtnClickableStates(state);
			setAlphaForView(mBtnMyPhilips, 0.75f);
			setAlphaForView(mLlSocialProviderBtnContainer, 0.75f);
		}
	}

	private void handleBtnClickableStates(boolean state) {
		mBtnCreateAccount.setEnabled(state);
		mBtnMyPhilips.setEnabled(state);
		enableSocialProviders(state);
	}

	private void enableSocialProviders(boolean enableState) {
		for (int i = 0; i < mLlSocialProviderBtnContainer.getChildCount(); i++) {
			mLlSocialProviderBtnContainer.getChildAt(i).setEnabled(enableState);
		}
	}

	private void linkifyTermAndPolicy(TextView pTvPrivacyPolicy) {
		String termAndPrivacy = getResources().getString(R.string.LegalNoticeText);
		String terms = getResources().getString(R.string.TermsAndConditionsText);
		String privacy = getResources().getString(R.string.PrivacyPolicyText);
		int termStartIndex = termAndPrivacy.toLowerCase(Locale.getDefault()).indexOf(
		        terms.toLowerCase(Locale.getDefault()));
		int privacyStartIndex = termAndPrivacy.toLowerCase(Locale.getDefault()).indexOf(
		        privacy.toLowerCase(Locale.getDefault()));

		SpannableString spanableString = new SpannableString(termAndPrivacy);
		spanableString.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View widget) {
				handleTermsCondition();
			}
		}, termStartIndex, termStartIndex + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		spanableString.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View widget) {
				handlePrivacyPolicy();
			}

		}, privacyStartIndex, privacyStartIndex + privacy.length(),
		        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		removeUnderlineFromLink(spanableString);

		pTvPrivacyPolicy.setText(spanableString);
		pTvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
		pTvPrivacyPolicy.setLinkTextColor(getResources().getColor(
		        R.color.reg_hyperlink_highlight_color));
		pTvPrivacyPolicy.setHighlightColor(getResources().getColor(android.R.color.transparent));
	}

	private void removeUnderlineFromLink(SpannableString spanableString) {
		for (ClickableSpan u : spanableString.getSpans(0, spanableString.length(),
		        ClickableSpan.class)) {
			spanableString.setSpan(new UnderlineSpan() {

				public void updateDrawState(TextPaint tp) {
					tp.setUnderlineText(false);
				}
			}, spanableString.getSpanStart(u), spanableString.getSpanEnd(u), 0);
		}

		for (URLSpan u : spanableString.getSpans(0, spanableString.length(), URLSpan.class)) {
			spanableString.setSpan(new UnderlineSpan() {

				public void updateDrawState(TextPaint tp) {
					tp.setUnderlineText(false);
				}
			}, spanableString.getSpanStart(u), spanableString.getSpanEnd(u), 0);
		}
	}

	private void setAlphaForView(View view, float alpha) {
		AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
		animation.setDuration(0);
		animation.setFillAfter(true);
		view.startAnimation(animation);
	}

	private void handlePrivacyPolicy() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(
		        R.string.PrivacyPolicyURL)));
		startActivity(browserIntent);
	}

	private void handleTermsCondition() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(
		        R.string.PrivacyPolicyURL)));
		startActivity(browserIntent);
	}

	@Override
	public void onLoginSuccess() {
		RLog.i(RLog.CALLBACK,"HomeFragment : onLoginSuccess");
		hideProviderProgress();
		enableControls(true);
		User user = new User(mContext);
		if (user.getEmailVerificationStatus(mContext)) {
			getRegistrationMainActivity().addWelcomeFragmentOnVerification();
		} else {
			getRegistrationMainActivity().addFragment(new AccountActivationFragment());
		}
	}

	private void hideProviderProgress() {
		if (null != getView().findViewWithTag(mProvider)) {
			XProviderButton providerButton = (XProviderButton) getView().findViewWithTag(mProvider);
			providerButton.hideProgressBar();
		}
	}

	private void showProviderProgress() {
		if (null != getView().findViewWithTag(mProvider)) {
			XProviderButton providerButton = (XProviderButton) getView().findViewWithTag(mProvider);
			providerButton.showProgressBar();
		}
	}

	@Override
	public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
		RLog.i(RLog.CALLBACK,"HomeFragment : onLoginFailedWithError");
		hideProviderProgress();
		enableControls(true);

	}

	@Override
	public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord,
	        String socialRegistrationToken) {
		RLog.i(RLog.CALLBACK,"HomeFragment : onLoginFailedWithTwoStepError");
		hideProviderProgress();
		enableControls(true);
		RLog.i("HomeFragment", "Login failed with two step error" + "JSON OBJECT :"
		        + prefilledRecord);
		getRegistrationMainActivity().addAlmostDoneFragment(prefilledRecord, mProvider,
		        socialRegistrationToken);
	}

	@Override
	public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider,
	        String conflictingIdentityProvider, String conflictingIdpNameLocalized,
	        String existingIdpNameLocalized) {
		RLog.i(RLog.CALLBACK,"HomeFragment : onLoginFailedWithMergeFlowError");
		hideProviderProgress();
		enableControls(true);
		if (mUser.handleMergeFlowError(existingProvider)) {
			getRegistrationMainActivity().addMergeAccountFragment(mergeToken, existingProvider);
		} else {
			if (NetworkUtility.isNetworkAvailable(mContext)
			        && RegistrationHelper.getInstance().isJanrainIntialized()) {
				mProvider = existingProvider;
				showProviderProgress();
				mUser.loginUserUsingSocialProvider(getActivity(), existingProvider, this,
				        mergeToken);
			}

		}

	}

	@Override
	public void onContinueSocialProviderLoginSuccess() {
		RLog.i(RLog.CALLBACK,"HomeFragment : onContinueSocialProviderLoginSuccess");
		hideProviderProgress();
		enableControls(true);
		getRegistrationMainActivity().addFragment(new WelcomeFragment());
	}

	@Override
	public void onContinueSocialProviderLoginFailure(
	        UserRegistrationFailureInfo userRegistrationFailureInfo) {
		RLog.i(RLog.CALLBACK,"HomeFragment : onContinueSocialProviderLoginFailure");
		hideProviderProgress();
		enableControls(true);
	}

	@Override
	public void onNetWorkStateReceived(boolean isOnline) {
		RLog.i(RLog.NETWORK_STATE, "HomeFragment :onNetWorkStateReceived state :"+isOnline);
		handleUiState();
		handleJanrainInitPb();
	}

}
