
package com.philips.cdp.registration.ui.traditional;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
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
import com.philips.cdp.registration.handlers.UpdateReceiveMarketingEmailHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.utils.FontLoader;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;

public class LogoutFragment extends RegistrationBaseFragment implements OnClickListener,
        UpdateReceiveMarketingEmailHandler, OnCheckedChangeListener, NetworStateListener, LogoutHandler {

    private TextView mTvWelcome;

    private TextView mTvSignInEmail;

    private CheckBox mCbTerms;

    private LinearLayout mLlContinueBtnContainer;

    private User mUser;

    private Context mContext;

    private TextView mTvEmailDetails;

    private Button mBtnLogOut;

    private XRegError mRegError;

    private ProgressBar mPbWelcomeCheck;

    private ProgressBar mPbLogoutFromBegin;

    private DIUserProfile userProfile;

    private ScrollView mSvRootLayout;

    private TextView mAccessAccountSettingsLink;

    private FrameLayout mFlReceivePhilipsNewsContainer;

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

        View view = inflater.inflate(R.layout.fragment_logout, null);
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
        applyParams(config, mFlReceivePhilipsNewsContainer, width);
        applyParams(config, mRegError, width);
        applyParams(config, mTvSignInEmail, width);
        applyParams(config, mAccessAccountSettingsLink, width);
    }


    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    private void init(View view) {
        consumeTouch(view);
        mTvWelcome = (TextView) view.findViewById(R.id.tv_reg_welcome);
        mLlContinueBtnContainer = (LinearLayout) view.findViewById(R.id.rl_reg_continue_id);
        mCbTerms = (CheckBox) view.findViewById(R.id.cb_reg_receive_philips_news);
        FontLoader.getInstance().setTypeface(mCbTerms, "CentraleSans-Light.otf");
        mCbTerms.setPadding(RegUtility.getCheckBoxPadding(mContext), mCbTerms.getPaddingTop(), mCbTerms.getPaddingRight(), mCbTerms.getPaddingBottom());
        mCbTerms.setVisibility(view.VISIBLE);
        mCbTerms.setChecked(mUser.getUserInstance(mContext).getReceiveMarketingEmail());
        mCbTerms.setOnCheckedChangeListener(this);
        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        mPbWelcomeCheck = (ProgressBar) view.findViewById(R.id.pb_reg_welcome_spinner);
        mPbLogoutFromBegin = (ProgressBar) view.findViewById(R.id.pb_reg_log_out_from_begin);
        mTvEmailDetails = (TextView) view.findViewById(R.id.tv_reg_email_details_container);
        mTvSignInEmail = (TextView) view.findViewById(R.id.tv_reg_sign_in_using);
        mBtnLogOut = (Button) view.findViewById(R.id.btn_reg_sign_out);
        mBtnLogOut.setOnClickListener(this);
        TextView receivePhilipsNewsView = (TextView) view.findViewById(R.id.tv_reg_philips_news);
        mAccessAccountSettingsLink = (TextView) view.findViewById(R.id.tv_reg_more_account_Setting);

        mFlReceivePhilipsNewsContainer = (FrameLayout) view.findViewById(R.id.fl_reg_receive_philips_news);
        RegUtility.linkifyPhilipsNews(receivePhilipsNewsView, getRegistrationFragment().getParentActivity(), mPhilipsNewsLinkClick);
        RegUtility.linkifyAccountSettingPhilips(mAccessAccountSettingsLink, getRegistrationFragment().getParentActivity(), mPhilipsSettingLinkClick);

        userProfile = mUser.getUserInstance(mContext);
        mTvWelcome.setText(getString(R.string.Signin_Success_Hello_lbltxt) + " " + userProfile.getGivenName());

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
        }
    }

    private void handleLogout() {
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SIGN_OUT);
        mUser.logout(this);
    }


    @Override
    public void onCheckedChanged(
            CompoundButton buttonView, boolean isChecked) {
        handleUpdate();
    }

    private void handleUpdate() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            mRegError.hideError();
            showProgressBar();
            updateUser();
        } else {
            mCbTerms.setOnCheckedChangeListener(null);
            mCbTerms.setChecked(!mCbTerms.isChecked());
            mCbTerms.setOnCheckedChangeListener(this);
            mRegError.setError(getString(R.string.NoNetworkConnection));
            scrollViewAutomatically(mRegError, mSvRootLayout);
            trackActionRegisterError(AppTagingConstants.NETWORK_ERROR_CODE);
        }
    }

    private void showProgressBar() {
        mPbWelcomeCheck.setVisibility(View.VISIBLE);
        mCbTerms.setEnabled(false);
        mBtnLogOut.setEnabled(false);
    }

    private void updateUser() {
        mUser.updateReceiveMarketingEmail(this, mCbTerms.isChecked());
    }

    @Override
    public void onUpdateReceiveMarketingEmailSuccess() {
        hideProgressBar();
        if (mCbTerms.isChecked()) {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_IN);
        } else {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_OUT);
        }
    }

    private void hideProgressBar() {
        mPbWelcomeCheck.setVisibility(View.INVISIBLE);
        mCbTerms.setEnabled(true);
        mBtnLogOut.setEnabled(true);
    }

    @Override
    public void onUpdateReceiveMarketingEmailFailedWithError(int error) {
        hideProgressBar();
        if(error== Integer.parseInt(RegConstants.INVALID_REFRESH_ACCESS_TOKEN_CODE)){
            getRegistrationFragment().replaceWithHomeFragment();
            RegistrationHelper.getInstance().getUserRegistrationListener()
                    .notifyOnLogoutSuccessWithInvalidAccessToken();
            return;
        }
        mCbTerms.setOnCheckedChangeListener(null);
        mCbTerms.setChecked(!mCbTerms.isChecked());
        mCbTerms.setOnCheckedChangeListener(this);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        handleUiState();
    }

    @Override
    public int getTitleResourceId() {
        return R.string.Account_Setting_Titletxt;
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
        if (mBtnLogOut.getVisibility() == View.VISIBLE) {
            mBtnLogOut.setEnabled(true);
            mBtnLogOut.setClickable(true);
        }
        hideLogoutSpinner();
        mRegError.setError(message);
        RegistrationHelper.getInstance().getUserRegistrationListener()
                .notifyOnUserLogoutFailure();
    }

    private void showLogoutSpinner() {
        mPbLogoutFromBegin.setVisibility(View.VISIBLE);
        mBtnLogOut.setEnabled(false);
    }

    private void hideLogoutSpinner() {
        mPbLogoutFromBegin.setVisibility(View.GONE);
        mBtnLogOut.setEnabled(true);
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

    private ClickableSpan mPhilipsSettingLinkClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            RLog.d(RLog.EVENT_LISTENERS, "RegistrationSampleActivity : onTermsAndConditionClick");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RegConstants.PHILIPS_LOGIN_URL));
            startActivity(browserIntent);
        }
    };

    private ClickableSpan mPhilipsNewsLinkClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            getRegistrationFragment().addPhilipsNewsFragment();
            trackPage(AppTaggingPages.PHILIPS_ANNOUNCEMENT);
        }
    };
}
