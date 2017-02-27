/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.janrain.android.Jump;
import com.philips.cdp.registration.HttpClientService;
import com.philips.cdp.registration.HttpClientServiceReceiver;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.apptagging.AppTaggingErrors;
import com.philips.cdp.registration.apptagging.AppTaggingPages;
import com.philips.cdp.registration.apptagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.configuration.ClientIDConfiguration;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.customviews.XButton;
import com.philips.cdp.registration.ui.customviews.XEmail;
import com.philips.cdp.registration.ui.customviews.XRegError;
import com.philips.cdp.registration.ui.customviews.onUpdateListener;
import com.philips.cdp.registration.ui.traditional.mobile.MobileForgotPasswordVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegAlertDialog;
import com.philips.cdp.registration.ui.utils.RegChinaUtil;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 310190722 on 10/7/2015.
 */
public class ForgotPasswordFragment extends RegistrationBaseFragment implements EventListener,
        onUpdateListener, NetworStateListener, View.OnClickListener, ForgotPasswordHandler, HttpClientServiceReceiver.Listener {

    private static final int FAILURE_TO_CONNECT = -1;
    public static final String USER_REQUEST_PASSWORD_RESET_SMS_CODE = "/api/v1/user/requestPasswordResetSmsCode";
    public static final String USER_REQUEST_RESET_PASSWORD_REDIRECT_URI_SMS = "/c-w/user-registration/apps/reset-password.html";

    private LinearLayout mLlEmailField;

    private RelativeLayout mRlContinueBtnContainer;

    private TextView mTvForgotPassword;

    private XEmail mEtEmail;

    private XButton mBtnContinue;

    private XRegError mRegError;

    private Context mContext;

    private User mUser;

    private ProgressBar mPbForgotPasswdSpinner;

    private final int SOCIAL_SIGIN_IN_ONLY_CODE = 540;

    private ScrollView mSvRootLayout;

    private final int BAD_RESPONSE_CODE = 7004;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getRegistrationFragment().getActivity().getApplicationContext();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onCreateView");
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        RLog.i(RLog.EVENT_LISTENERS,
                "ResetPasswordFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS");
        View view = inflater.inflate(R.layout.reg_fragment_forgot_password, container, false);
        mUser = new User(mContext);
        mSvRootLayout = (ScrollView) view.findViewById(R.id.sv_root_layout);
        initUI(view);
        handleUiState();
        handleOrientation(view);
        return view;
    }

    private void initUI(View view) {
        consumeTouch(view);
        mEtEmail = (XEmail) view.findViewById(R.id.rl_reg_email_field);
        ((RegistrationFragment) getParentFragment()).showKeyBoard();
        mEtEmail.requestFocus();
        mEtEmail.setOnUpdateListener(this);
        mEtEmail.setOnClickListener(this);
        mEtEmail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mBtnContinue = (XButton) view.findViewById(R.id.reg_btn_continue);
        mBtnContinue.setOnClickListener(this);
        mTvForgotPassword = (TextView) view.findViewById(R.id.tv_reg_forgot_password);
        mPbForgotPasswdSpinner = (ProgressBar) view.findViewById(R.id.pb_reg_forgot_spinner);
        mRegError = (XRegError) view.findViewById(R.id.reg_error_msg);
        mLlEmailField = (LinearLayout) view.findViewById(R.id.ll_reg_email_field_container);
        mRlContinueBtnContainer = (RelativeLayout) view
                .findViewById(R.id.rl_reg_btn_continue_container);

        mEtEmail.checkingEmailorMobileSignIn();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onDestroy");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
                this);
        RLog.i(RLog.EVENT_LISTENERS,
                "ResetPasswordFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onDetach");
    }

    private Bundle mBundle;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBundle = outState;
        super.onSaveInstanceState(mBundle);
        if (mEtEmail.isEmailErrorVisible()) {
            mBundle.putString("saveEmailErrText", mEtEmail.getSavedEmailErrDescription());
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getString("saveEmailErrText") != null) {
                mEtEmail.showInvalidAlert();
                mEtEmail.showErrPopUp();
                mEtEmail.setErrDescription(savedInstanceState.getString("saveEmailErrText"));
            }
        }
        mBundle = null;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "ResetPasswordFragment : onConfigurationChanged");
        setCustomParams(config);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
        applyParams(config, mTvForgotPassword, width);
        applyParams(config, mLlEmailField, width);
        applyParams(config, mRlContinueBtnContainer, width);
        applyParams(config, mRegError, width);
    }

    private void handleUiState() {
        if (NetworkUtility.isNetworkAvailable(mContext)) {
            mRegError.hideError();
        } else {
            mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }

    private void updateUiStatus() {
        if (NetworkUtility.isNetworkAvailable(mContext) && (mEtEmail.isValidEmail() || FieldsValidator.isValidMobileNumber(mEtEmail.getEmailId()))) {
            mBtnContinue.setEnabled(true);
            mRegError.hideError();
        } else {
            mBtnContinue.setEnabled(false);
        }
    }

    @Override
    public void onEventReceived(String event) {
        RLog.i(RLog.EVENT_LISTENERS, "ResetPasswordFragment :onEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            updateUiStatus();
        }
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "AlmostDone :onNetWorkStateReceived state :" + isOnline);
        handleUiState();
        updateUiStatus();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reg_btn_continue) {
            RLog.d(RLog.ONCLICK, "SignInAccountFragment : Forgot Password");
            resetPassword();
        }
    }

    private void resetPassword() {
        boolean validatorResult;
        if (FieldsValidator.isValidEmail(mEtEmail.getEmailId())) {
            validatorResult = true;
        } else {
            validatorResult = FieldsValidator.isValidMobileNumber(mEtEmail.getEmailId());
        }

        if (!validatorResult) {
            mEtEmail.showInvalidAlert();
        } else {
            if (NetworkUtility.isNetworkAvailable(mContext)) {

                if (mUser != null) {
                    mEtEmail.clearFocus();
                    showForgotPasswordSpinner();
                    if (FieldsValidator.isValidEmail(mEtEmail.getEmailId())) {
                        mUser.forgotPassword(mEtEmail.getEmailId(), this);
                    } else {
                        serviceDiscovery();
                    }
                }

            } else {
                mRegError.setError(getString(R.string.reg_NoNetworkConnection));
            }
        }
    }

    private void showForgotPasswordSpinner() {
        mPbForgotPasswdSpinner.setVisibility(View.VISIBLE);
        mBtnContinue.setEnabled(false);
    }

    private void hideForgotPasswordSpinner() {
        mPbForgotPasswdSpinner.setVisibility(View.INVISIBLE);
        mBtnContinue.setEnabled(true);
    }

    @Override
    public void onUpadte() {
        handleOnUIThread(new Runnable() {
            @Override
            public void run() {
                updateUiStatus();
            }
        });
    }

    @Override
    public void onSendForgotPasswordSuccess() {
        handleOnUIThread(new Runnable() {
            @Override
            public void run() {
                handleSendForgotPasswordSuccess();
            }
        });
    }

    private void handleSendForgotPasswordSuccess() {
        RLog.i(RLog.CALLBACK, "ResetPasswordFragment : onSendForgotPasswordSuccess");
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.STATUS_NOTIFICATION,
                AppTagingConstants.RESET_PASSWORD_SUCCESS);
        hideForgotPasswordSpinner();
        RegAlertDialog.showResetPasswordDialog(mContext.getResources().getString(R.string.reg_ForgotPwdEmailResendMsg_Title),
                mContext.getResources().getString(R.string.reg_ForgotPwdEmailResendMsg), getRegistrationFragment().getParentActivity(), mContinueBtnClick);
        hideForgotPasswordSpinner();
        mRegError.hideError();
        getFragmentManager().popBackStack();
    }

    @Override
    public void onSendForgotPasswordFailedWithError(final UserRegistrationFailureInfo userRegistrationFailureInfo) {

        handleOnUIThread(new Runnable() {
            @Override
            public void run() {
                handleSendForgotPasswordFailedWithError(userRegistrationFailureInfo);
            }
        });

    }

    private void handleSendForgotPasswordFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "SignInAccountFragment : onSendForgotPasswordFailedWithError");
        hideForgotPasswordSpinner();
        if (userRegistrationFailureInfo.getErrorCode() == FAILURE_TO_CONNECT || userRegistrationFailureInfo.getErrorCode() == BAD_RESPONSE_CODE) {
            mRegError.setError(mContext.getResources().getString(R.string.reg_JanRain_Server_Connection_Failed));
            mEtEmail.setErrDescription(mContext.getResources().getString(R.string.reg_JanRain_Server_Connection_Failed));
            return;
        } else {
            if (userRegistrationFailureInfo.getErrorCode() == SOCIAL_SIGIN_IN_ONLY_CODE) {
                mEtEmail.showInvalidAlert();
                mEtEmail.setErrDescription(getString(R.string.reg_TraditionalSignIn_ForgotPwdSocialError_lbltxt));
                mEtEmail.showErrPopUp();
            } else {
                mEtEmail.showErrPopUp();
                mEtEmail.setErrDescription(userRegistrationFailureInfo.getSocialOnlyError());
                mEtEmail.showInvalidAlert();
            }

            if (null != userRegistrationFailureInfo.getSocialOnlyError()) {
                mEtEmail.showErrPopUp();
                mEtEmail.setErrDescription(userRegistrationFailureInfo.getSocialOnlyError());
                mEtEmail.showInvalidAlert();
                return;
            }

            if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
                mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
                mEtEmail.showInvalidAlert();
                mEtEmail.showErrPopUp();
            }
        }
        if (null != userRegistrationFailureInfo.getEmailErrorMessage()) {
            mEtEmail.setErrDescription(userRegistrationFailureInfo.getEmailErrorMessage());
            mEtEmail.showInvalidAlert();
            mEtEmail.showErrPopUp();
        }
        scrollViewAutomatically(mEtEmail, mSvRootLayout);
        AppTaggingErrors.trackActionForgotPasswordFailure(userRegistrationFailureInfo, AppTagingConstants.JANRAIN);
    }

    private View.OnClickListener mContinueBtnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            trackPage(AppTaggingPages.SIGN_IN_ACCOUNT);
            RegAlertDialog.dismissDialog();
        }
    };

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_SigIn_TitleTxt;
    }

    String verificationSmsCodeURL;
    String resetPasswordSmsRedirectUri;

    private void serviceDiscovery()  {

        AppInfraInterface appInfra = RegistrationHelper.getInstance().getAppInfraInstance();
        final ServiceDiscoveryInterface serviceDiscoveryInterface = appInfra.getServiceDiscovery();
        RLog.d(RLog.SERVICE_DISCOVERY, " Country :" + RegistrationHelper.getInstance().getCountryCode());

        //Temp: will be updated once actual URX received for reset sms
        serviceDiscoveryInterface.getServiceUrlWithCountryPreference("userreg.urx.verificationsmscode", new ServiceDiscoveryInterface.OnGetServiceUrlListener() {

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {

                RLog.d(RLog.SERVICE_DISCOVERY, " onError  : userreg.urx.verificationsmscode : " + errorvalues);
                verificationSmsCodeURL = null;
            }

            @Override
            public void onSuccess(URL url) {
                RLog.d(RLog.SERVICE_DISCOVERY, " onSuccess  : userreg.urx.verificationsmscode:" + url.toString());
                verificationSmsCodeURL = url.toString();
            }
        });

        String uriSubString = getBaseString();

        //Verification URI
        verificationSmsCodeURL = uriSubString + USER_REQUEST_PASSWORD_RESET_SMS_CODE;
        //Redirect URI
        resetPasswordSmsRedirectUri = uriSubString + USER_REQUEST_RESET_PASSWORD_REDIRECT_URI_SMS;

        getRegistrationFragment().getActivity().startService(createResendSMSIntent(verificationSmsCodeURL));
    }

    @NonNull
    private String getBaseString() {
        URL url = null;
        try {
            url = new URL(verificationSmsCodeURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return (url.getProtocol() + "://" + url.getHost());
    }

    private void handleResendVerificationEmailSuccess() {
        trackActionStatus(AppTagingConstants.SEND_DATA,
                AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_RESEND_EMAIL_VERIFICATION);
    }

    private void handleResendSMSRespone(String response) {

        final String  mobileNumberKey= "mobileNumber";
        final String tokenKey= "token";
        final String redirectUriKey= "redirectUri";
        final String verificationSmsCodeURLKey="verificationSmsCodeURL";


        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("errorCode").toString().equals("0")) {
                handleResendVerificationEmailSuccess();
                JSONObject json = null;
                String payload = null;
                String token = null;
                try {
                    json = new JSONObject(response);
                    payload = json.getString("payload");
                    json = new JSONObject(payload);
                    token = json.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RLog.i("MobileVerifyCodeFragment ", " isAccountActivate is " + token + " -- " + response);
                MobileForgotPasswordVerifyCodeFragment mobileForgotPasswordVerifyCodeFragment = new MobileForgotPasswordVerifyCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(mobileNumberKey, mEtEmail.getEmailId());
                bundle.putString(tokenKey,token);
                bundle.putString(redirectUriKey,getRedirectUri());
                bundle.putString(verificationSmsCodeURLKey, verificationSmsCodeURL);
                mobileForgotPasswordVerifyCodeFragment.setArguments(bundle);
                getRegistrationFragment().addFragment(mobileForgotPasswordVerifyCodeFragment);
            } else {
                trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.TECHNICAL_ERROR, AppTagingConstants.MOBILE_RESEND_SMS_VERFICATION_FAILURE);
                String errorMsg = RegChinaUtil.getErrorMsgDescription(jsonObject.getString("errorCode").toString(), mContext);
                mEtEmail.setErrDescription(errorMsg);
                mEtEmail.showErrPopUp();
                mEtEmail.showInvalidAlert();
                RLog.i("MobileVerifyCodeFragment ", " SMS Resend failure = " + response);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent createResendSMSIntent(String url) {

        final String receiverKey="receiver";
        final String bodyContentKey= "bodyContent";
        final String urlKey= "url";

        RLog.d(RLog.EVENT_LISTENERS, "MOBILE NUMBER *** : " + mEtEmail.getEmailId());
        RLog.d("Configration : ", " envir :" + RegistrationConfiguration.getInstance().getRegistrationEnvironment());

        Intent httpServiceIntent = new Intent(mContext, HttpClientService.class);
        HttpClientServiceReceiver receiver = new HttpClientServiceReceiver(new Handler());
        receiver.setListener(this);

        String bodyContent = "provider=JANRAIN-CN&phonenumber=" + FieldsValidator.getMobileNumber(mEtEmail.getEmailId()) +
                "&locale=zh_CN&clientId=" + getClientId() + "&code_type=short&" +
                "redirectUri=" + getRedirectUri();

        RLog.d("Configration : ", " envirr :" + getClientId() + getRedirectUri());
        httpServiceIntent.putExtra(receiverKey, receiver);
        httpServiceIntent.putExtra(bodyContentKey, bodyContent);
        httpServiceIntent.putExtra(urlKey, url);
        return httpServiceIntent;
    }

    private String getClientId() {
        ClientIDConfiguration clientIDConfiguration = new ClientIDConfiguration();
        return clientIDConfiguration.getResetPasswordClientId(RegConstants.HTTPS_CONST+Jump.getCaptureDomain());
    }

    private String getRedirectUri() {

        return resetPasswordSmsRedirectUri;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String response = resultData.getString("responseStr");
        RLog.i("MobileVerifyCodeFragment ", "onReceiveResult Response Val = " + response);
        hideForgotPasswordSpinner();
        if (response == null) {
            mEtEmail.showInvalidAlert();
            mEtEmail.setErrDescription(mContext.getResources().getString(R.string.reg_Invalid_PhoneNumber_ErrorMsg));
            mEtEmail.showErrPopUp();
            return;
        } else {
            handleResendSMSRespone(response);

        }
    }

}
