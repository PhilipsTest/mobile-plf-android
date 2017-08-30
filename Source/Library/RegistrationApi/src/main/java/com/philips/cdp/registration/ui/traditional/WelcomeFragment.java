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
import android.support.annotation.Nullable;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.philips.cdp.registration.*;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.utils.*;

import javax.inject.Inject;

public class WelcomeFragment extends RegistrationBaseFragment implements OnClickListener, NetworkStateListener, LogoutHandler {

    @Inject
    NetworkUtility networkUtility;

    private TextView mTvWelcome;

    private TextView mTvSignInEmail;

    private LinearLayout mLlContinueBtnContainer;

    private User mUser;

    private Context mContext;

    private TextView mTvEmailDetails;

    private Button mBtnSignOut;

    private Button mBtnContinue;

    private XRegError mRegError;

    private ProgressDialog mProgressDialog;

    private String mUserDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "UserWelcomeFragment : onCreateView");
        RegistrationHelper.getInstance().registerNetworkStateListener(this);

        View view = inflater.inflate(R.layout.reg_fragment_welcome, null);
        mContext = getRegistrationFragment().getParentActivity().getApplicationContext();
        mUser = new User(mContext);
        init(view);
        handleUiState();
        handleOrientation(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onDestroy");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        hideLogoutSpinner();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onDetach");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "UserWelcomeFragment : onConfigurationChanged");
        setCustomParams(config);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, mTvWelcome, width);
        applyParams(config, mTvEmailDetails, width);
        applyParams(config, mLlContinueBtnContainer, width);
        applyParams(config, mRegError, width);
        applyParams(config, mTvSignInEmail, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    private void init(View view) {
        consumeTouch(view);
        mTvWelcome = (TextView) view.findViewById(R.id.tv_reg_welcome);
        mLlContinueBtnContainer = (LinearLayout) view.findViewById(R.id.rl_reg_continue_id);
        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        mTvEmailDetails = (TextView) view.findViewById(R.id.tv_reg_email_details_container);
        mTvSignInEmail = (TextView) view.findViewById(R.id.tv_reg_sign_in_using);
        mBtnSignOut = (Button) view.findViewById(R.id.btn_reg_sign_out);
        mBtnSignOut.setOnClickListener(this);
        mBtnContinue = (Button) view.findViewById(R.id.btn_reg_continue);
        mBtnContinue.setOnClickListener(this);

        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getActivity(), R.style.reg_Custom_loaderTheme);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        mProgressDialog.setCancelable(false);

        String userName = mUser.getGivenName();
        if (userName != null && !userName.equalsIgnoreCase("null")) {
            String welcomeUser = getString(R.string.reg_InitialSignedIn_Welcome_User_lbltxt);
            welcomeUser = String.format(welcomeUser, userName);
            mTvWelcome.setText(welcomeUser);
        }

        if (FieldsValidator.isValidMobileNumber(mUser.getMobile())){
            mUserDetails = getString(R.string.reg_InitialSignedIn_SigninMobileNumberText);
            mUserDetails = String.format(mUserDetails, mUser.getMobile());
            mTvSignInEmail.setText(mUserDetails);
        }else if (FieldsValidator.isValidEmail(mUser.getEmail())) {
            String email = getString(R.string.reg_InitialSignedIn_SigninEmailText);
            email = String.format(email, mUser.getEmail());
            mTvSignInEmail.setText(email);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_reg_sign_out) {
            RLog.d(RLog.ONCLICK, "WelcomeFragment : Sign Out");
            showLogoutSpinner();
            handleLogout();
        } else if (id == R.id.btn_reg_continue) {
            RLog.d(RLog.ONCLICK, " WelcomeFragment : Continue");
            if (getRegistrationFragment().getUserRegistrationUIEventListener() != null) {
                getRegistrationFragment().getUserRegistrationUIEventListener().
                        onUserRegistrationComplete(getRegistrationFragment().getParentActivity());
            }
        }
    }

    private void handleLogout() {
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.LOGOUT_BTN_SELECTED);
        mUser.logout(this);
    }


    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        handleUiState();
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_SigIn_TitleTxt;
    }

    @Override
    public void onLogoutSuccess() {
                trackPage(AppTaggingPages.HOME);
                hideLogoutSpinner();
                if (null != getRegistrationFragment()) {
                    getRegistrationFragment().replaceWithHomeFragment();
                }
    }

    @Override
    public void onLogoutFailure(int responseCode, final String message) {
                mRegError.setError(message);
                hideLogoutSpinner();
    }

    private void handleUiState() {
        if (networkUtility.isNetworkAvailable()) {
            if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
            } else {
                mRegError.hideError();
            }
        } else {
            mRegError.setError(mContext.getResources().getString(R.string.reg_NoNetworkConnection));
        }
    }

    private void showLogoutSpinner() {
        if (!(getActivity().isFinishing()) && (mProgressDialog != null)) mProgressDialog.show();
        mBtnSignOut.setEnabled(false);
    }

    private void hideLogoutSpinner() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
        if (mBtnSignOut != null) {
            mBtnSignOut.setEnabled(true);
        }
    }
}
