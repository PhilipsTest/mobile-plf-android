
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.apptagging.AppTaggingPages;
import com.philips.cdp.registration.apptagging.AppTagingConstants;
import com.philips.cdp.registration.dao.DIUserProfile;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;

public class WelcomeFragment extends RegistrationBaseFragment implements OnClickListener, NetworStateListener, LogoutHandler {

    private TextView mTvWelcome;

    private TextView mTvSignInEmail;

    private LinearLayout mLlContinueBtnContainer;

    private User mUser;

    private Context mContext;

    private TextView mTvEmailDetails;

    private Button mBtnSignOut;

    private Button mBtnContinue;

    private XRegError mRegError;

    private ProgressBar mPbLogout;

    private DIUserProfile userProfile;

    private ScrollView mSvRootLayout;

    @Override
    public void onAttach(Activity activity) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "UserWelcomeFragment : onCreateView");
        RegistrationHelper.getInstance().registerNetworkStateListener(this);

        View view = inflater.inflate(R.layout.fragment_welcome, null);
        mContext = getRegistrationFragment().getParentActivity().getApplicationContext();
        mUser = new User(mContext);
        mSvRootLayout = (ScrollView) view.findViewById(R.id.sv_root_layout);
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
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, " WelcomeFragment : onDetach");
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
        mPbLogout = (ProgressBar) view.findViewById(R.id.pb_reg_log_out_spinner);



        userProfile = mUser.getUserInstance(mContext);
        mTvWelcome.setText(getString(R.string.SignInSuccess_Welcome_lbltxt) + " " + userProfile.getGivenName());

        String email = getString(R.string.InitialSignedIn_SigninEmailText);
        email = String.format(email, userProfile.getEmail());
        mTvSignInEmail.setText(email);
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
            RegistrationHelper.getInstance().getUserRegistrationListener()
                    .notifyonUserRegistrationCompleteEventOccurred(getRegistrationFragment().getParentActivity());
        }
    }

    private void handleLogout() {
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SIGN_OUT);
        mUser.logout(this);
    }

    private void handleUpdate() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            mRegError.hideError();
            showProgressBar();
        } else {
            mRegError.setError(getString(R.string.NoNetworkConnection));
            scrollViewAutomatically(mRegError, mSvRootLayout);
            trackActionRegisterError(AppTagingConstants.NETWORK_ERROR_CODE);
        }
    }

    private void showProgressBar() {
        mBtnContinue.setEnabled(false);
    }

    private void hideProgressBar() {
        mBtnContinue.setEnabled(true);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        handleUiState();
    }

    @Override
    public int getTitleResourceId() {
        return R.string.SigIn_TitleTxt;
    }

    @Override
    public void onLogoutSuccess() {
        trackPage(AppTaggingPages.HOME);
        hideLogoutSpinner();
        getRegistrationFragment().replaceWithHomeFragment();
        RegistrationHelper.getInstance().getUserRegistrationListener()
                .notifyOnUserLogoutSuccess();
    }

    @Override
    public void onLogoutFailure(int responseCode, final String message) {
        mRegError.setError(message);
        hideLogoutSpinner();
        RegistrationHelper.getInstance().getUserRegistrationListener()
                .notifyOnUserLogoutFailure();
    }
    private void handleUiState() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            if (RegistrationHelper.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
            } else {
                mRegError.hideError();
            }
        } else {
            mRegError.setError(mContext.getResources().getString(R.string.NoNetworkConnection));
            trackActionLoginError(AppTagingConstants.NETWORK_ERROR_CODE);
        }
    }

    private void showLogoutSpinner() {
        mPbLogout.setVisibility(View.VISIBLE);
        mBtnSignOut.setEnabled(false);
    }

    private void hideLogoutSpinner() {
        mPbLogout.setVisibility(View.GONE);
        mBtnSignOut.setEnabled(true);
    }

}
