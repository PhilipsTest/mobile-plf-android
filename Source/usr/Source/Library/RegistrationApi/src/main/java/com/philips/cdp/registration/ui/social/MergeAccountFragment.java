
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.social;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.RegistrationSettings;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MergeAccountFragment extends RegistrationBaseFragment implements MergeAccountContract {

    private String TAG = "MergeAccountFragment";

    @Inject
    NetworkUtility networkUtility;

    @Inject
    User user;

    @BindView(R2.id.reg_error_msg)
    XRegError mRegError;

    @BindView(R2.id.usr_mergeScreen_used_email_label)
    Label mTvUsedEmail;

    @BindView(R2.id.usr_mergeScreen_rootLayout_scrollView)
    ScrollView mSvRootLayout;

    @BindView(R2.id.usr_mergeScreen_merge_button)
    ProgressBarButton mBtnMerge;

    @BindView(R2.id.usr_mergeScreen_password_inputLayout)
    InputValidationLayout mEtPassword;

    @BindView(R2.id.usr_mergeScreen_password_textField)
    ValidationEditText passwordValidationEditText;

    @BindView(R2.id.usr_mergeScreen_baseLayout_LinearLayout)
    LinearLayout usr_mergeScreen_baseLayout_LinearLayout;

    private MergeAccountPresenter mergeAccountPresenter;

    private String mEmailId;

    private String mMergeToken;

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        RLog.i(TAG, "Screen name is" + TAG);
        View view = inflater.inflate(R.layout.reg_fragment_social_merge_account, container, false);
        registerInlineNotificationListener(this);
        ButterKnife.bind(this, view);
        connectionStatus(networkUtility.isNetworkAvailable());
        mergeAccountPresenter = new MergeAccountPresenter(this, user);
        initUI(view);
        handleOrientation(view);
        return view;
    }


    private void enableMergeButton() {
        RxTextView.textChanges(passwordValidationEditText)
                .map(password -> (password.toString().length() > 0 && networkUtility.isNetworkAvailable()))
                .subscribe(enabled -> mBtnMerge.setEnabled(enabled));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mergeAccountPresenter != null) {
            mergeAccountPresenter.cleanUp();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(TAG, " onConfigurationChanged");
        setCustomParams(config);
    }

    private void initUI(View view) {
        consumeTouch(view);
        Bundle bundle = this.getArguments();

        mEmailId = bundle.getString(RegConstants.SOCIAL_MERGE_EMAIL);
        mMergeToken = bundle.getString(RegConstants.SOCIAL_MERGE_TOKEN);

        ((RegistrationFragment) getParentFragment()).showKeyBoard();
        mEtPassword.requestFocus();
        mEtPassword.setValidator(password -> password.length() > 0);
        mEtPassword.setErrorMessage(getString(R.string.USR_EmptyField_ErrorMsg));


        trackActionStatus(AppTagingConstants.SEND_DATA,
                AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.START_SOCIAL_MERGE);
        mTvUsedEmail.setText(RegUtility.fromHtml(String.format(mTvUsedEmail.getText().toString(), "<b>" + mEmailId + "</b>")));
        enableMergeButton();
    }


    @OnClick(R2.id.usr_mergeScreen_merge_button)
    public void mergeButtonClick() {
        RLog.i(TAG, TAG + ".mergeButton click");
        if (mEtPassword.hasFocus()) {
            mEtPassword.clearFocus();
        }
        getView().requestFocus();
        mergeAccount();
    }

    @OnClick(R2.id.usr_mergeScreen_forgotPassword_button)
    public void forgotButtonClick() {
        performAction();
    }

    private void mergeAccount() {
        if (networkUtility.isNetworkAvailable()) {
            mergeAccountPresenter.mergeToTraditionalAccount(mEmailId, passwordValidationEditText.getText().toString(), mMergeToken);
            mEtPassword.hideError();
            showMergeSpinner();
        }
    }

    private void performAction() {
        getRegistrationFragment().addResetPasswordFragment();
        trackPage(AppTaggingPages.FORGOT_PASSWORD);
    }

    private void showMergeSpinner() {
        mBtnMerge.showProgressIndicator();
        mBtnMerge.setEnabled(false);
    }

    private void hideMergeSpinner() {
        mBtnMerge.hideProgressIndicator();
        mBtnMerge.setEnabled(true);
    }

    @Override
    public void connectionStatus(boolean isOnline) {
        if (isOnline && UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
            mRegError.hideError();
        }
    }


    private void enableMerge() {
        mBtnMerge.setEnabled(true);
    }

    private void disableMerge() {
        mBtnMerge.setEnabled(false);
    }


    @Override
    public void setViewParams(Configuration config, int width) {
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.USR_SigIn_TitleTxt;
    }

    @Override
    public void mergeSuccess() {
        hideMergeSpinner();
        completeRegistration();
    }


    private void launchAlmostDoneForTermsAcceptanceFragment() {
        getRegistrationFragment().addAlmostDoneFragmentforTermsAcceptance();
        trackPage(AppTaggingPages.ALMOST_DONE);
    }

    private void completeRegistration() {
        String emailorMobile = mergeAccountPresenter.getLoginWithDetails();
        if (emailorMobile != null
                && (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                RegPreferenceUtility.getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailorMobile))
                && (!RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())
                && (RegistrationConfiguration.getInstance().isCustomOptoin() || RegistrationConfiguration.getInstance().isSkipOptin())
                && (RegUtility.getUiFlow() == UIFlow.FLOW_B)) {
            getRegistrationFragment().userRegistrationComplete();
            return;
        } else  if (emailorMobile != null && ((RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                !RegPreferenceUtility.getPreferenceValue(mContext, RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailorMobile) || !user.getReceiveMarketingEmail())
                || (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired() && RegistrationConfiguration.getInstance().getPersonalConsent() != null
                && RegistrationConfiguration.getInstance().getPersonalConsent() == ConsentStates.inactive))) {
            launchAlmostDoneForTermsAcceptanceFragment();
            return;
        }
         getRegistrationFragment().userRegistrationComplete();
    }


    @Override
    public void mergeFailure(String errorDescription, int errorCode) {
        if (errorCode == -1) return;
        hideMergeSpinner();
//        mEtPassword.setErrorMessage(new URError(mContext).getLocalizedError(ErrorType.JANRAIN, errorCode));
//        mEtPassword.showError();
        updateErrorNotification(errorDescription, errorCode);
    }

    @Override
    public void mergePasswordFailure() {
        hideMergeSpinner();
        mEtPassword.setErrorMessage(getString(R.string.USR_Janrain_Invalid_Credentials));
        mEtPassword.showError();
    }


    @Override
    public void mergeStatus(boolean isOnline) {
        if (isOnline && passwordValidationEditText.getText().toString().length() > 0) {
            mEtPassword.hideError();
            enableMerge();
            return;
        }
        disableMerge();
    }

    @Override
    public void notificationInlineMsg(String msg) {
        mRegError.setError(msg);
    }
}
