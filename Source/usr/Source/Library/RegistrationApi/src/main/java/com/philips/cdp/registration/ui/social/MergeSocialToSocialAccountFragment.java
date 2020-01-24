
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.facebook.CallbackManager;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.URFaceBookUtility;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MergeSocialToSocialAccountFragment extends RegistrationBaseFragment implements
        MergeSocialToSocialAccountContract {

    private String TAG = "MergeSocialToSocialAccountFragment";

    @Inject
    NetworkUtility networkUtility;

    @Inject
    User user;

    @BindView(R2.id.reg_error_msg)
    XRegError mRegError;

    @BindView(R2.id.usr_mergeScreen_login_button)
    ProgressBarButton usr_mergeScreen_login_button;

    @BindView(R2.id.usr_mergeScreen_logout_button)
    Button usr_mergeScreen_logout_button;

    @BindView(R2.id.usr_mergeScreen_used_social_label)
    Label usr_mergeScreen_used_social_label;

    @BindView(R2.id.usr_mergeScreen_used_social_again_label)
    Label usr_mergeScreen_used_social_again_label;

    @BindView(R2.id.usr_mergeScreen_rootLayout_scrollView)
    ScrollView usr_mergeScreen_rootLayout_scrollView;

    @BindView(R2.id.ll_root_layout)
    LinearLayout ll_root_layout;

    @BindView(R2.id.usr_mergeScreen_baseLayout_LinearLayout)
    LinearLayout usr_mergeScreen_baseLayout_LinearLayout;

    private String mConflictProvider;

    private String mMergeToken;

    private Context mContext;

    private MergeSocialToSocialAccountPresenter mergeSocialToSocialAccountPresenter;
    private URFaceBookUtility mURFaceBookUtility;
    private CallbackManager mCallbackManager;
    private String mEmail;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        RLog.i(TAG, "Screen name is" + TAG);
        View view = inflater.inflate(R.layout.reg_fragment_social_to_social_merge_account, container, false);
        registerInlineNotificationListener(this);
        ButterKnife.bind(this, view);
        initUI(view);
        networkChangeStatus(networkUtility.isNetworkAvailable());
        handleOrientation(view);
        mergeSocialToSocialAccountPresenter = new MergeSocialToSocialAccountPresenter(this, user);
        if (RegistrationConfiguration.getInstance().isFacebookSDKSupport()) {
            mURFaceBookUtility = new URFaceBookUtility(this);
            mCallbackManager = mURFaceBookUtility.getCallBackManager();
            initFacebookLogIn();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        mergeSocialToSocialAccountPresenter.cleanUp();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        setCustomParams(config);
    }

    private void initUI(View view) {
        consumeTouch(view);
        Bundle bundle = this.getArguments();
        mMergeToken = bundle.getString(RegConstants.SOCIAL_MERGE_TOKEN);
        trackActionStatus(AppTagingConstants.SEND_DATA,
                AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.START_SOCIAL_MERGE);
        mConflictProvider = bundle.getString(RegConstants.CONFLICTING_SOCIAL_PROVIDER);
        mEmail = bundle.getString(RegConstants.SOCIAL_MERGE_EMAIL);
        RLog.e(TAG, "Social Provider : " + mConflictProvider);
        String conflictingProvider = "USR_" + mConflictProvider;
        int conflictSocialProviderId = getRegistrationFragment().getParentActivity().getResources().getIdentifier(conflictingProvider, "string",
                getRegistrationFragment().getParentActivity().getPackageName());
        String conflictSocialProvider = mContext.getResources().getString(conflictSocialProviderId);

        usr_mergeScreen_used_social_label.setText(RegUtility.fromHtml(String.format(usr_mergeScreen_used_social_label.getText().toString(), "<b>" + conflictSocialProvider + "</b>")));
        usr_mergeScreen_used_social_again_label.setText(RegUtility.fromHtml(String.format(usr_mergeScreen_used_social_again_label.getText().toString(), "<b>" + mEmail + "</b>")));
        usr_mergeScreen_login_button.setText(String.format(usr_mergeScreen_login_button.getText(), conflictSocialProvider));
    }


    private AlertDialogFragment alertDialogFragment;

    @OnClick(R2.id.usr_mergeScreen_logout_button)
    void showLogoutAlert() {
        RLog.i(TAG, TAG + ".logoutAlert clicked");
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.LOGOUT_BUTTON_SELECTED);
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext())
                .setDialogType(DialogConstants.TYPE_DIALOG)
                .setDialogLayout(R.layout.social_merge_dialog)
                .setNegativeButton(mContext.getString(R.string.USR_Social_Merge_Cancel_btntxt), v -> alertDialogFragment.dismiss())
                .setPositiveButton(mContext.getString(R.string.USR_DLS_Merge_Accounts_Logout_Dialog__Button_Title), v -> {
                    alertDialogFragment.dismiss();
                    performAction();
                })
                .setDimLayer(DialogConstants.DIM_STRONG)
                .setCancelable(false);
        builder.setTitle(mContext.getString(R.string.USR_DLS_Merge_Accounts_Logout_Dialog_Title));
        alertDialogFragment = builder.create();
        alertDialogFragment.show(getFragmentManager(), null);

    }

    private void performAction() {
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SIGN_OUT);
        trackPage(AppTaggingPages.HOME);
        mergeSocialToSocialAccountPresenter.logout();
        getFragmentManager().popBackStack();
        getRegistrationFragment().handleBackEvent();
    }

    @OnClick(R2.id.usr_mergeScreen_login_button)
    void mergeAccount() {
        RLog.i(TAG, TAG + ".mergeAccount clicked");
        if (networkUtility.isNetworkAvailable()) {
            if (RegistrationConfiguration.getInstance().isFacebookSDKSupport() && mConflictProvider.equalsIgnoreCase(RegConstants.SOCIAL_PROVIDER_FACEBOOK)) {
                startFaceBookLogin();
            } else {
                mergeSocialToSocialAccountPresenter.loginUserUsingSocialProvider(mConflictProvider, mMergeToken);
            }
            showMergeSpinner();
        }
    }

    private void showMergeSpinner() {
        usr_mergeScreen_rootLayout_scrollView.setVisibility(View.INVISIBLE);
        ll_root_layout.setVisibility(View.VISIBLE);
        usr_mergeScreen_login_button.showProgressIndicator();
        usr_mergeScreen_login_button.setEnabled(false);
    }

    private void hideMergeSpinner() {
        usr_mergeScreen_rootLayout_scrollView.setVisibility(View.VISIBLE);
        ll_root_layout.setVisibility(View.INVISIBLE);
        usr_mergeScreen_login_button.hideProgressIndicator();
        usr_mergeScreen_login_button.setEnabled(true);
    }

    private void networkChangeStatus(boolean isOnline) {
        if (isOnline) {
            mRegError.hideError();
        }
    }

    private void updateUiOnNetworkChange(boolean isOnline) {
        if (isOnline) {
            usr_mergeScreen_login_button.setEnabled(true);
            usr_mergeScreen_logout_button.setEnabled(true);
            mRegError.hideError();
        } else {
            usr_mergeScreen_login_button.setEnabled(false);
        }
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        //NOP
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }


    @Override
    public int getTitleResourceId() {
        return R.string.USR_SigIn_TitleTxt;
    }

    private void completeRegistration() {
        String emailorMobile = mergeSocialToSocialAccountPresenter.getLoginWithDetails();

        if (emailorMobile != null
                && (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                RegPreferenceUtility.getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailorMobile))
                && (!RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())
                && (RegistrationConfiguration.getInstance().isCustomOptoin() || RegistrationConfiguration.getInstance().isSkipOptin())
                && (RegUtility.getUiFlow() == UIFlow.FLOW_B)) {
            getRegistrationFragment().userRegistrationComplete();
            return;
        } else  if (emailorMobile != null && ((RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()
                && !RegPreferenceUtility.getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailorMobile) || !mergeSocialToSocialAccountPresenter.getReceiveMarketingEmail())
                || (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired() && RegistrationConfiguration.getInstance().getPersonalConsent() != null
                && RegistrationConfiguration.getInstance().getPersonalConsent() == ConsentStates.inactive )))
        {

            launchAlmostDoneForTermsAcceptanceFragment();
            return;
        }
        getRegistrationFragment().userRegistrationComplete();
    }

    private void launchAlmostDoneForTermsAcceptanceFragment() {
        trackPage(AppTaggingPages.ALMOST_DONE);
        getRegistrationFragment().addAlmostDoneFragmentforTermsAcceptance();
    }


    @Override
    public void connectionStatus(boolean isOnline) {
        networkChangeStatus(isOnline);
    }

    @Override
    public void mergeStatus(boolean isOnline) {
        updateUiOnNetworkChange(isOnline);
    }

    @Override
    public void mergeSuccess() {
        hideMergeSpinner();
        completeRegistration();
    }

    @Override
    public void mergeFailure(UserRegistrationFailureInfo failureInfo) {
        hideMergeSpinner();
        if (failureInfo.getErrorCode() != ErrorCodes.AUTHENTICATION_CANCELLED_BY_USER) {
            updateErrorNotification(failureInfo.getErrorDescription(), failureInfo.getErrorCode());
        }
    }

    @Override
    public void mergeFailureIgnored() {
        hideMergeSpinner();
    }

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    @Override
    public MergeSocialToSocialAccountFragment getHomeFragment() {
        return this;
    }

    @Override
    public URFaceBookUtility getURFaceBookUtility() {
        return mURFaceBookUtility;
    }

    @Override
    public void initFacebookLogIn() {
        mergeSocialToSocialAccountPresenter.registerFaceBookCallBack();
    }

    @Override
    public void onFaceBookCancel() {
        getFragmentManager().popBackStack();
        getRegistrationFragment().handleBackEvent();
    }

    @Override
    public void onFaceBookEmailReceived(String email) {
        startAccessTokenAuthForFacebook();
    }

    @Override
    public void startFaceBookLogin() {
        mURFaceBookUtility.startFaceBookLogIn();
    }

    @Override
    public void doHideProgressDialog() {
        hideProgressDialog();
    }

    @Override
    public void startAccessTokenAuthForFacebook() {
        mergeSocialToSocialAccountPresenter.startAccessTokenAuthForFacebook(mMergeToken);
    }

    @Override
    public CallbackManager getCallBackManager() {
        return mCallbackManager;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void notificationInlineMsg(String msg) {
        mRegError.setError(msg);
    }
}