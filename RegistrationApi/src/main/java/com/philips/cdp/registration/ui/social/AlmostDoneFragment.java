
package com.philips.cdp.registration.ui.social;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.philips.cdp.registration.apptagging.AppTaggingPages;
import com.philips.cdp.registration.apptagging.AppTagingConstants;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.SocialProviderLoginHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.XButton;
import com.philips.cdp.registration.ui.customviews.XEmail;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.customviews.onUpdateListener;
import com.philips.cdp.registration.ui.traditional.AccountActivationFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.FontLoader;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlmostDoneFragment extends RegistrationBaseFragment implements EventListener,
        onUpdateListener, SocialProviderLoginHandler, NetworStateListener, OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView mTvSignInWith;

    private LinearLayout mLlAlmostDoneContainer;

    private LinearLayout mLlPeriodicOffersCheck;

    private LinearLayout mLlAcceptTermsContainer;

    private CheckBox mCbAcceptTerms;

    private XRegError mRegAccptTermsError;

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

    private ScrollView mSvRootLayout;

    private Bundle mBundle;

    @Override
    public void onAttach(Activity activity) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "AlmostDoneFragment : onCreateView");
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        parseRegistrationInfo();
        RLog.i(RLog.EVENT_LISTENERS,
                "AlmostDoneFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS");
        View view = inflater.inflate(R.layout.fragment_social_almost_done, container, false);
        handleOrientation(view);
        mSvRootLayout = (ScrollView) view.findViewById(R.id.sv_root_layout);
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
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
                this);
        RLog.i(RLog.EVENT_LISTENERS,
                "AlmostDoneFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS");
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
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, mTvSignInWith, width);
        applyParams(config, mLlAlmostDoneContainer, width);
        applyParams(config, mLlPeriodicOffersCheck, width);
        applyParams(config, mRlContinueBtnContainer, width);
        applyParams(config, mRegError, width);
        applyParams(config, mRegAccptTermsError, width);
        applyParams(config, mLlAcceptTermsContainer, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    private void parseRegistrationInfo() {

        mBundle = getArguments();
        if (null != mBundle) {
            try {
                JSONObject mPreRegJson = null;
                mPreRegJson = new JSONObject(mBundle.getString(RegConstants.SOCIAL_TWO_STEP_ERROR));

                if (null != mPreRegJson) {
                    mProvider = mBundle.getString(RegConstants.SOCIAL_PROVIDER);
                    mRegistrationToken = mBundle.getString(RegConstants.SOCIAL_REGISTRATION_TOKEN);

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
                RLog.e(RLog.EXCEPTION, "AlmostDoneFragment Exception : " + e.getMessage());
            }
        }
    }

    private void initUI(View view) {
        consumeTouch(view);
        mContext = getRegistrationFragment().getActivity().getApplicationContext();
        mBtnContinue = (XButton) view.findViewById(R.id.reg_btn_continue);
        mBtnContinue.setOnClickListener(this);
        mTvSignInWith = (TextView) view.findViewById(R.id.tv_reg_sign_in_with);
        mLlAlmostDoneContainer = (LinearLayout) view.findViewById(R.id.ll_reg_almost_done);

        mLlAcceptTermsContainer = (LinearLayout) view
                .findViewById(R.id.ll_reg_accept_terms);

        mLlPeriodicOffersCheck = (LinearLayout) view
                .findViewById(R.id.ll_reg_periodic_offers_check);

        mRlContinueBtnContainer = (RelativeLayout) view
                .findViewById(R.id.rl_reg_btn_continue_container);

        mCbTerms = (CheckBox) view.findViewById(R.id.cb_reg_register_terms);
        FontLoader.getInstance().setTypeface(mCbTerms, "CentraleSans-Light.otf");
        mCbTerms.setPadding(RegUtility.getCheckBoxPadding(mContext), mCbTerms.getPaddingTop(), mCbTerms.getPaddingRight(), mCbTerms.getPaddingBottom());

        TextView acceptTermsView = (TextView) view.findViewById(R.id.tv_reg_accept_terms);
        mCbAcceptTerms = (CheckBox) view.findViewById(R.id.cb_reg_accept_terms);
        RegUtility.linkifyTermsandCondition(acceptTermsView, getRegistrationFragment().getParentActivity(), mTermsAndConditionClick);

        mCbAcceptTerms.setOnCheckedChangeListener(this);

        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        mEtEmail = (XEmail) view.findViewById(R.id.rl_reg_email_field);
        mEtEmail.setOnUpdateListener(this);
        mEtEmail.setOnClickListener(this);

        mPbSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_social_almost_done_spinner);
        mPbSpinner.setClickable(false);
        mPbSpinner.setEnabled(true);

        mRegAccptTermsError = (XRegError) view.findViewById(R.id.cb_reg_accept_terms_error);

        if(null!=mProvider){
            mProvider = Character.toUpperCase(mProvider.charAt(0)) + mProvider.substring(1);
        }

        if (isEmailExist) {
            mEtEmail.setVisibility(View.GONE);
            mBtnContinue.setEnabled(true);
        } else {
            if(mBundle ==null){
                mBtnContinue.setEnabled(true);
            }else{
                View viewLine = (View)view.findViewById(R.id.reg_view_line);
                viewLine.setVisibility(View.VISIBLE);
                mEtEmail.setVisibility(View.VISIBLE);
            }
        }
        handleUiAcceptTerms(view);
    }

    private ClickableSpan mTermsAndConditionClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            RegUtility.handleTermsCondition(getRegistrationFragment().getParentActivity());
        }
    };

    private void handleUiState() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            if (RegistrationHelper.getInstance().isJanrainIntialized()) {
                mRegError.hideError();
            } else {
                mRegError.setError(getString(R.string.NoNetworkConnection));
            }
        } else {
            mRegError.setError(getString(R.string.NoNetworkConnection));
            trackActionRegisterError(AppTagingConstants.NETWORK_ERROR_CODE);
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }

    private void handleUiAcceptTerms(View view) {
        if (RegistrationConfiguration.getInstance().getFlow().isTermsAndConditionsAcceptanceRequired()) {
            mLlAcceptTermsContainer.setVisibility(View.VISIBLE);
        } else {
            View acceptTermsLine = view.findViewById(R.id.reg_view_accep_terms_line);
            acceptTermsLine.setVisibility(View.GONE);
            mLlAcceptTermsContainer.setVisibility(View.GONE);
        }
    }

    private void updateUiStatus() {
        if (isEmailExist) {
            if (NetworkUtility.isNetworkAvailable(mContext)
                    && RegistrationHelper.getInstance().isJanrainIntialized()) {
                mBtnContinue.setEnabled(true);
                mRegError.hideError();
            } else {
                mBtnContinue.setEnabled(false);
            }
        } else {
            if (NetworkUtility.isNetworkAvailable(mContext)
                    && RegistrationHelper.getInstance().isJanrainIntialized() && mEtEmail.isValidEmail()) {
                mBtnContinue.setEnabled(true);
                mRegError.hideError();
            } else {
                mBtnContinue.setEnabled(false);
            }
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
    public void onEventReceived(String event) {
        RLog.i(RLog.EVENT_LISTENERS, "AlmostDoneFragment :onEventReceived is : " + event);
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
            RLog.d(RLog.ONCLICK, "AlmostDoneFragment : Continue");
            mEtEmail.clearFocus();
            if(mBundle==null){
                launchWelcomeFragment();
                return;
            }
            if (RegistrationConfiguration.getInstance().getFlow().isTermsAndConditionsAcceptanceRequired()) {
                if (mCbAcceptTerms.isChecked()) {
                    register();
                } else {
                    mRegAccptTermsError.setError(mContext.getResources().getString(R.string.TermsAndConditionsAcceptanceText_Error));
                }
            } else {
                register();
            }
        }
    }

    private void register() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            mRegAccptTermsError.setVisibility(View.GONE);
            User user = new User(mContext);
            showSpinner();
            if (isEmailExist) {
                user.registerUserInfoForSocial(mGivenName, mDisplayName, mFamilyName, mEmail, true,
                        mCbTerms.isChecked(), this, mRegistrationToken);
            } else {
                user.registerUserInfoForSocial(mGivenName, mDisplayName, mFamilyName,
                        mEtEmail.getEmailId().toString().trim(), true, mCbTerms.isChecked(), this, mRegistrationToken);
            }
        }
    }

    private void trackMultipleActions() {
        Map<String, Object> map = new HashMap<String, Object>();
        if (mCbTerms.isChecked()) {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_IN);
        } else {
            trackActionForRemarkettingOption(AppTagingConstants.REMARKETING_OPTION_OUT);
        }
    }

    @Override
    public int getTitleResourceId() {
        return R.string.SigIn_TitleTxt;
    }

    @Override
    public void onLoginSuccess() {
        RLog.i(RLog.CALLBACK, "AlmostDoneFragment : onLoginSuccess");
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SUCCESS_LOGIN);
        hideSpinner();
    }

    @Override
    public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "AlmostDoneFragment : onLoginFailedWithError");
        hideSpinner();

        if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
            mEtEmail.showInvalidAlert();
            mEtEmail.showErrPopUp();
            scrollViewAutomatically(mEtEmail, mSvRootLayout);
        }
        trackActionRegisterError(userRegistrationFailureInfo.getError().code);
    }

    @Override
    public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord,
                                              String socialRegistrationToken) {
        RLog.i(RLog.CALLBACK, "AlmostDoneFragment : onLoginFailedWithTwoStepError");
        hideSpinner();

    }

    @Override
    public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider,
                                                String conflictingIdentityProvider, String conflictingIdpNameLocalized,
                                                String existingIdpNameLocalized, String emailId) {
        RLog.i(RLog.CALLBACK, "AlmostDoneFragment : onLoginFailedWithMergeFlowError");
        hideSpinner();
        addMergeAccountFragment();

    }

    private void addMergeAccountFragment() {
        getRegistrationFragment().addFragment(new MergeAccountFragment());
        trackPage(AppTaggingPages.MERGE_ACCOUNT);
    }

    @Override
    public void onContinueSocialProviderLoginSuccess() {
        RLog.i(RLog.CALLBACK, "AlmostDoneFragment : onContinueSocialProviderLoginSuccess");
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SUCCESS_USER_CREATION);
        trackMultipleActions();
        User user = new User(mContext);
        if (user.getEmailVerificationStatus(mContext)) {
            launchWelcomeFragment();
        } else {
            launchAccountActivateFragment();
        }
        hideSpinner();
    }

    private void launchAccountActivateFragment() {
        getRegistrationFragment().addFragment(new AccountActivationFragment());
        trackPage(AppTaggingPages.ACCOUNT_ACTIVATION);
    }

    private void launchWelcomeFragment() {
        getRegistrationFragment().addWelcomeFragmentOnVerification();
        trackPage(AppTaggingPages.WELCOME);
    }

    @Override
    public void onContinueSocialProviderLoginFailure(
            UserRegistrationFailureInfo userRegistrationFailureInfo) {

        RLog.i(RLog.CALLBACK, "AlmostDoneFragment : onContinueSocialProviderLoginFailure");
        hideSpinner();
        if (null != userRegistrationFailureInfo.getDisplayNameErrorMessage()) {
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getDisplayNameErrorMessage());
            mEtEmail.showInvalidAlert();
            mRegError.setError(userRegistrationFailureInfo.getErrorDescription() + ".\n'"
                    + mDisplayName + "' "
                    + userRegistrationFailureInfo.getDisplayNameErrorMessage());
            trackActionRegisterError(userRegistrationFailureInfo.getError().code);
            return;
        }
        if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
            mEtEmail.showInvalidAlert();
            mEtEmail.showErrPopUp();
        } else {
            mRegError.setError(userRegistrationFailureInfo.getErrorDescription());
        }
        trackActionRegisterError(userRegistrationFailureInfo.getError().code);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "AlmostDone :onNetWorkStateReceived state :" + isOnline);
        handleUiState();
        updateUiStatus();
    }

    @Override
    public void onCheckedChanged(CompoundButton viewId, boolean isChecked) {
        int id = viewId.getId();
        if (id == R.id.cb_reg_accept_terms) {
            if (isChecked) {
                mRegAccptTermsError.setVisibility(View.GONE);
            } else {
                mRegAccptTermsError.setError(mContext.getResources().getString(R.string.TermsAndConditionsAcceptanceText_Error));
            }
        }
    }
}
