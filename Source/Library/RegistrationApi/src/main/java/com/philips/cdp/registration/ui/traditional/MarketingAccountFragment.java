/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.UpdateUserDetailsHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegUtility;

public class MarketingAccountFragment extends RegistrationBaseFragment implements
        View.OnClickListener, NetworStateListener, UpdateUserDetailsHandler {

    private LinearLayout mLlCreateAccountFields;

    private LinearLayout mLlCreateAccountContainer;

    private RelativeLayout mRlCountBtnContainer;

    private RelativeLayout mRlNoThanksBtnContainer;

    private Button mBtnCountMe;

    private Button mBtnNoThanks;

    private User mUser;

    private View mViewLine;

    private Context mContext;

    private ScrollView mSvRootLayout;

    private TextView mTvJoinNow;

    private long mTrackCreateAccountTime;

    private ProgressDialog mProgressDialog;

    private XRegError mRegError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onCreateView");
        RLog.d(RLog.EVENT_LISTENERS,
                "CreateAccountFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS");
        mContext = getRegistrationFragment().getActivity().getApplicationContext();

        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        View view = inflater.inflate(R.layout.reg_fragment_marketing_opt, container, false);
        initUI(view);

        setContentConfig(view);

        mSvRootLayout = (ScrollView) view.findViewById(R.id.sv_root_layout);

        handleOrientation(view);
        mTrackCreateAccountTime = System.currentTimeMillis();
        return view;
    }

    private void setContentConfig(View view) {
        if (getRegistrationFragment().getContentConfiguration() != null) {
            updateText(view, R.id.reg_be_the_first_txt,
                    getRegistrationFragment().getContentConfiguration().getOptInTitleText());
            updateText(view, R.id.reg_what_are_you_txt,
                    getRegistrationFragment().getContentConfiguration().getOptInQuessionaryText());
            updateText(view, R.id.reg_special_officer_txt,
                    getRegistrationFragment().getContentConfiguration().getOptInDetailDescription());
            if (getRegistrationFragment().getContentConfiguration().getOptInBannerText() != null) {
                updateText(view, R.id.tv_reg_Join_now,
                        getRegistrationFragment().getContentConfiguration().getOptInBannerText());
            } else {
                defalutBannerText(view);
            }
        } else {
            defalutBannerText(view);
        }
    }
   void defalutBannerText(View view){
        String joinNow = mContext.getResources().getString(R.string.reg_Opt_In_Join_Now);
        String updateJoinNowText =  " " + "<b>" + mContext.getResources().getString(R.string.reg_Opt_In_Over_Peers) + "</b> ";
        joinNow = String.format(joinNow, updateJoinNowText);
        updateText(view, R.id.tv_reg_Join_now,joinNow);
    }

    private void updateText(View view, int textViewId, String text) {
        TextView marketBeTheFirstTextView = (TextView) view.findViewById(textViewId);
        if(text!=null && text.length()>0){
            marketBeTheFirstTextView.setText(Html.fromHtml(text));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onDestroy");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        RLog.d(RLog.EVENT_LISTENERS,
                "CreateAccountFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onDetach");
    }

    private Bundle mBundle;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mBundle = null;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "CreateAccountFragment : onConfigurationChanged");
        super.onConfigurationChanged(config);
        setCustomParams(config);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, mLlCreateAccountFields, width);
        applyParams(config, mLlCreateAccountContainer, width);
        applyParams(config, mRlCountBtnContainer, width);
        applyParams(config, mRlNoThanksBtnContainer, width);
        applyParams(config, mTvJoinNow, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reg_count_me) {
            showRefreshProgress();
            mUser.updateReceiveMarketingEmail(this, true);
        } else if (v.getId() == R.id.btn_reg_no_thanks) {
            showRefreshProgress();
            mUser.updateReceiveMarketingEmail(this, false);
        }
    }

    private void initUI(View view) {
        consumeTouch(view);
        mLlCreateAccountFields = (LinearLayout) view
                .findViewById(R.id.ll_reg_create_account_fields);
        mLlCreateAccountContainer = (LinearLayout) view
                .findViewById(R.id.ll_reg_create_account_container);
        mRlCountBtnContainer = (RelativeLayout) view.findViewById(R.id.rl_reg_count_options);
        mRlNoThanksBtnContainer = (RelativeLayout) view.findViewById(R.id.rl_reg_nothanks_options);
        mBtnCountMe = (Button) view.findViewById(R.id.btn_reg_count_me);
        mBtnNoThanks = (Button) view.findViewById(R.id.btn_reg_no_thanks);
        mTvJoinNow = (TextView) view.findViewById(R.id.tv_reg_Join_now);
        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        TextView receivePhilipsNewsView = (TextView) view.findViewById(R.id.tv_reg_philips_news);
        RegUtility.linkifyPhilipsNewsMarketing(receivePhilipsNewsView,
                getRegistrationFragment().getParentActivity(), mPhilipsNewsClick);
        mBtnCountMe.setOnClickListener(this);
        mBtnNoThanks.setOnClickListener(this);
        mViewLine = view.findViewById(R.id.reg_accept_terms_line);
;
        handleUiAcceptTerms();
        handleUiState();
        mUser = new User(mContext);
    }

    private ClickableSpan mPhilipsNewsClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            getRegistrationFragment().addPhilipsNewsFragment();
            trackPage(AppTaggingPages.PHILIPS_ANNOUNCEMENT);
        }
    };

    private void handleUiAcceptTerms() {
        if (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()) {
            mViewLine.setVisibility(View.VISIBLE);
        } else {
            mViewLine.setVisibility(View.GONE);
        }
    }

    private void handleRegistrationSuccess() {
        RLog.i(RLog.CALLBACK, "CreateAccountFragment : onRegisterSuccess");
        hideRefreshProgress();
        if (RegistrationConfiguration.getInstance().isEmailVerificationRequired() && !(mUser.isEmailVerified() || mUser.isMobileVerified())) {
            if (FieldsValidator.isValidEmail(mUser.getEmail().toString())){
                launchAccountActivateFragment();
            }else {
                launchMobileVerifyCodeFragment();
            }
        } else if (RegistrationConfiguration.getInstance().isEmailVerificationRequired() && (mUser.isEmailVerified() || mUser.isMobileVerified())) {
            this.getActivity().finish();
        } else {
            launchWelcomeFragment();
        }
        if (mTrackCreateAccountTime == 0 && RegUtility.getCreateAccountStartTime() > 0) {
            mTrackCreateAccountTime = (System.currentTimeMillis() - RegUtility.
                    getCreateAccountStartTime()) / 1000;
        } else {
            mTrackCreateAccountTime = (System.currentTimeMillis() - mTrackCreateAccountTime) / 1000;
        }

        mTrackCreateAccountTime = 0;
    }

    private void launchAccountActivateFragment() {
        getRegistrationFragment().addFragment(new AccountActivationFragment());
        trackPage(AppTaggingPages.ACCOUNT_ACTIVATION);
    }

    private void launchMobileVerifyCodeFragment() {
        getRegistrationFragment().addFragment(new MobileVerifyCodeFragment());
        trackPage(AppTaggingPages.MOBILE_VERIFY_CODE);
    }

    private void launchWelcomeFragment() {
        getRegistrationFragment().replaceWelcomeFragmentOnLogin(new WelcomeFragment());
        trackPage(AppTaggingPages.WELCOME);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_RegCreateAccount_NavTitle;
    }

    @Override
    public String getTitleResourceText() {
        String titleResourceText = null;
        if (getRegistrationFragment().getContentConfiguration() != null) {
            titleResourceText = getRegistrationFragment().getContentConfiguration().getOptInActionBarText();
        }
        return titleResourceText;
    }


    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "CreateAccoutFragment :onNetWorkStateReceived : " + isOnline);
        handleUiState();
    }

    @Override
    public void onUpdateSuccess() {
        trackRemarketing();
        RLog.i("MarketingAccountFragment", "onUpdateSuccess ");
        hideRefreshProgress();
        handleRegistrationSuccess();
    }

    private void trackRemarketing() {
        if(mUser.getReceiveMarketingEmail()){
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_IN);
        }else{
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_OUT);
        }
    }

    @Override
    public void onUpdateFailedWithError(final int error) {
        RLog.i("MarketingAccountFragment", "onUpdateFailedWithError ");
        hideRefreshProgress();
    }

    private void showRefreshProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity(), R.style.reg_Custom_loaderTheme);
            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    private void hideRefreshProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void handleUiState() {
        if (new NetworkUtility(mContext).isNetworkAvailable()) {
            if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
                mBtnCountMe.setEnabled(true);
                mBtnNoThanks.setEnabled(true);
            } else {
                mBtnCountMe.setEnabled(false);
                mBtnNoThanks.setEnabled(false);
                mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            }
        } else {
            mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            mBtnCountMe.setEnabled(false);
            mBtnNoThanks.setEnabled(false);
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }
}

