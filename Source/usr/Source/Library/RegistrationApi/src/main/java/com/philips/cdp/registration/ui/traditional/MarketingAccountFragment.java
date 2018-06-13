/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MarketingAccountFragment extends RegistrationBaseFragment implements
        View.OnClickListener, MarketingAccountContract {

    private static final String TAG = MarketingAccountFragment.class.getSimpleName();
    @BindView(R2.id.usr_marketingScreen_countMe_button)
    ProgressBarButton countMeButton;

    @BindView(R2.id.usr_marketingScreen_maybeLater_button)
    Button mayBeLaterButton;

    @BindView(R2.id.usr_marketingScreen_rootLayout_scrollView)
    ScrollView rootLayoutScrollView;

    @BindView(R2.id.usr_marketingScreen_error_regerror)
    XRegError errorRegError;

    @BindView(R2.id.usr_marketingScreen_philipsNews_label)
    Label receivePhilipsNewsLabel;

    @BindView(R2.id.usr_marketingScreen_rootContainer_linearLayout)
    LinearLayout usrMarketingScreenRootContainerLinearLayout;

    private User mUser;

    private Context mContext;

    private Bundle mBundle;

    private long mTrackCreateAccountTime;

    MarketingAccountPresenter marketingAccountPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        RLog.i(TAG, "onAttach : is called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.i(TAG, "onCreateView : is called");
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        registerInlineNotificationListener(this);
        View view = inflater.inflate(R.layout.reg_fragment_marketing_opt, container, false);

        marketingAccountPresenter = new MarketingAccountPresenter(this);
        marketingAccountPresenter.register();

        ButterKnife.bind(this, view);
        initUI(view);
        setContentConfig(view);
        handleOrientation(view);
        mTrackCreateAccountTime = System.currentTimeMillis();
        return view;
    }

    private void setContentConfig(View view) {
        RLog.i(TAG, "setContentConfig : is called");
        if (getRegistrationFragment().getContentConfiguration() != null) {
            updateText(view, R.id.usr_marketingScreen_headTitle_Lable,
                    getRegistrationFragment().getContentConfiguration().getOptInQuessionaryText());
            updateText(view, R.id.usr_marketingScreen_specialOffer_label,
                    getRegistrationFragment().getContentConfiguration().getOptInDetailDescription());
            if (getRegistrationFragment().getContentConfiguration().getOptInBannerText() != null) {
                updateText(view, R.id.usr_marketingScreen_joinNow_Label,
                        getRegistrationFragment().getContentConfiguration().getOptInBannerText());
            } else {
                defalutBannerText(view);
            }
        } else {
            defalutBannerText(view);
            RLog.i(TAG, "setContentConfig : getContentConfiguration : is null");
        }
    }

    @SuppressLint("StringFormatInvalid")
    void defalutBannerText(View view) {
        RLog.i(TAG, "defalutBannerText : is called");
        String joinNow = mContext.getResources().getString(R.string.DLS_Optin_Body_Line2);
        String updateJoinNowText = mContext.getResources().getString(R.string.DLS_Optin_Body_Line2);
        joinNow = String.format(joinNow, updateJoinNowText);
        updateText(view, R.id.usr_marketingScreen_joinNow_Label, joinNow);
    }

    private void updateText(View view, int textViewId, String text) {
        TextView marketBeTheFirstTextView = (TextView) view.findViewById(textViewId);
        if (text != null && text.length() > 0) {
            marketBeTheFirstTextView.setText(text);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.i(TAG, "onStop : is called");
        if (marketingAccountPresenter != null) {
            marketingAccountPresenter.unRegister();
            RLog.i(TAG, "onStop : unregister NetworStateListener,JANRAIN_INIT_SUCCESS");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        RLog.i(TAG, "onSaveInstanceState : is called");
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        RLog.i(TAG, "onViewStateRestored : is called");
        super.onViewStateRestored(savedInstanceState);
        mBundle = null;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        RLog.i(TAG, "onConfigurationChanged : is called");
        super.onConfigurationChanged(config);
        setCustomParams(config);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        // no value
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.usr_marketingScreen_countMe_button) {
            showProgressDialog();
            marketingAccountPresenter.updateMarketingEmail(mUser, true);
            RLog.i(TAG, "updateMarketingEmail : is called with update true");
        } else if (v.getId() == R.id.usr_marketingScreen_maybeLater_button) {
            showProgressDialog();
            marketingAccountPresenter.updateMarketingEmail(mUser, false);
            RLog.i(TAG, "updateMarketingEmail : is called with update false");
        }
    }

    private void initUI(View view) {
        consumeTouch(view);
        RegUtility.linkifyPhilipsNewsMarketing(receivePhilipsNewsLabel,
                getRegistrationFragment().getParentActivity(), mPhilipsNewsClick);
        countMeButton.setOnClickListener(this);
        mayBeLaterButton.setOnClickListener(this);
        handleUiState();
        mUser = new User(mContext);
    }

    private ClickableSpan mPhilipsNewsClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            getRegistrationFragment().addPhilipsNewsFragment();
            trackPage(AppTaggingPages.PHILIPS_ANNOUNCEMENT);
            RLog.i(TAG, "PHILIPS_ANNOUNCEMENT : Fragment is loaded");
        }
    };

    @Override
    public void handleRegistrationSuccess() {
        RLog.i(TAG, "handleRegistrationSuccess : is called");
        hideRefreshProgress();
        if (RegistrationConfiguration.getInstance().isEmailVerificationRequired() && !(mUser.isEmailVerified() || mUser.isMobileVerified())) {
            if (FieldsValidator.isValidEmail(mUser.getEmail())) {
                launchAccountActivateFragment();
                RLog.i(TAG, "handleRegistrationSuccess : launchAccountActivateFragment is called");
            } else {
                launchMobileVerifyCodeFragment();
                RLog.i(TAG, "handleRegistrationSuccess : launchMobileVerifyCodeFragment is called");
            }
        } else if (RegistrationConfiguration.getInstance().isEmailVerificationRequired() && (mUser.isEmailVerified() || mUser.isMobileVerified())) {
            getRegistrationFragment().userRegistrationComplete();
            RLog.i(TAG, "handleRegistrationSuccess : userRegistrationComplete is called");
        } else {
            getRegistrationFragment().userRegistrationComplete();
            RLog.i(TAG, "handleRegistrationSuccess : else : userRegistrationComplete is called");
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

    @Override
    public int getTitleResourceId() {
        return R.string.DLS_OptIn_Navigation_Bar_Title;
    }

    @Override
    public void trackRemarketing() {
        if (mUser.getReceiveMarketingEmail()) {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_IN);
        } else {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_OUT);
        }
    }

    @Override
    public void hideRefreshProgress() {
        hideProgressDialog();
    }

    @Override
    public void handleUiState() {
        if (new NetworkUtility(mContext).isNetworkAvailable()) {
            if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                errorRegError.hideError();
                countMeButton.setEnabled(true);
                mayBeLaterButton.setEnabled(true);
            } else {
                countMeButton.setEnabled(false);
                mayBeLaterButton.setEnabled(false);
//                errorRegError.setError(getString(R.string.reg_NoNetworkConnection));
                showNotificationBarOnNetworkNotAvailable();
            }
        } else {
//            errorRegError.setError(getString(R.string.reg_NoNetworkConnection));
            showNotificationBarOnNetworkNotAvailable();
            countMeButton.setEnabled(false);
            mayBeLaterButton.setEnabled(false);
            scrollViewAutomatically(errorRegError, rootLayoutScrollView);
        }
    }

    @Override
    public void notificationInlineMsg(String msg) {
        errorRegError.setError(msg);
    }
}

