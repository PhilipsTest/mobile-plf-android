package com.philips.cdp.registration.ui.traditional;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.janrain.android.Jump;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.ClientIDConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.handlers.ForgotPasswordHandler;
import com.philips.cdp.registration.restclient.URRequest;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.traditional.mobile.MobileForgotPassVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by philips on 22/06/17.
 */

public class ForgotPasswordPresenter implements NetworkStateListener, EventListener,
        ForgotPasswordHandler {

    private static String TAG = "ForgotPasswordPresenter";


    private final RegistrationHelper registrationHelper;


    private final EventHelper eventHelper;

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    private static final String USER_REQUEST_RESET_SMS_CODE =
            "/api/v1/user/requestPasswordResetSmsCode";

    private static final String USER_REQUEST_RESET_REDIRECT_URI_SMS =
            "/c-w/user-registration/apps/reset-password.html";

    private final ForgotPasswordContract forgotPasswordContract;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private String verificationSmsCodeURL;

    private String resetPasswordSmsRedirectUri;

    private String mobileNumber;

    private Context context;

    public ForgotPasswordPresenter(
            RegistrationHelper registrationHelper, EventHelper eventHelper, ForgotPasswordContract forgotPasswordContract, Context context) {
        this.registrationHelper = registrationHelper;
        this.eventHelper = eventHelper;
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        this.forgotPasswordContract = forgotPasswordContract;
        this.context = context;
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.d(TAG, "CreateAccoutFragment :onNetWorkStateReceived : " + isOnline);
        forgotPasswordContract.handleUiState(isOnline);
    }

    public void registerListener() {
        registrationHelper.registerNetworkStateListener(this);
        eventHelper
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
    }

    public void unRegisterListener() {
        registrationHelper.unRegisterNetworkListener(this);
        eventHelper.unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
    }

    @Override
    public void onEventReceived(String event) {
        RLog.d(TAG, "ResetPasswordFragment :onCounterEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            forgotPasswordContract.handleUiStatus();
        }
    }

    @Override
    public void onSendForgotPasswordSuccess() {
        forgotPasswordContract.handleSendForgotPasswordSuccess();
    }

    @Override
    public void onSendForgotPasswordFailedWithError(final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        forgotPasswordContract.handleSendForgotPasswordFailedWithError(userRegistrationFailureInfo);
    }

    void forgotPasswordRequest(String emailId, User user) {
        user.forgotPassword(emailId, this);
    }

    void handleResendSMSRespone(String response) {
        forgotPasswordContract.hideForgotPasswordSpinner();
        final String mobileNumberKey = "mobileNumber";
        final String tokenKey = "token";
        final String redirectUriKey = "redirectUri";
        final String verificationSmsCodeURLKey = "verificationSmsCodeURL";
        try {
            JSONObject jsonObject = new JSONObject(response);
            final String errorCode = jsonObject.getString("errorCode");
            RLog.e(TAG, " handleResendSMSRespone: Response Error code = " + errorCode);
            if ("0".equals(errorCode)) {
                forgotPasswordContract.trackAction(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.SUCCESS_RESEND_EMAIL_VERIFICATION);
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
                RLog.d(TAG, " isAccountActivate is " + token + " -- " + response);
                constructMobileVerifyCodeFragment(mobileNumberKey, tokenKey, redirectUriKey, verificationSmsCodeURLKey, token);
            } else {
                forgotPasswordContract.trackAction(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.TECHNICAL_ERROR, AppTagingConstants.MOBILE_RESEND_SMS_VERFICATION_FAILURE);
                forgotPasswordContract.forgotPasswordErrorMessage(errorCode);
                RLog.d(TAG, "handleResendSMSRespone: SMS Resend failure = " + response);
            }
        } catch (Exception e) {
            RLog.e(TAG,"handleResendSMSRespone: Exception : "+e.getMessage());
        }
    }

    private void constructMobileVerifyCodeFragment(String mobileNumberKey, String tokenKey, String redirectUriKey, String verificationSmsCodeURLKey, String token) {
        MobileForgotPassVerifyCodeFragment mobileForgotPasswordVerifyCodeFragment = new MobileForgotPassVerifyCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(mobileNumberKey, mobileNumber);
        bundle.putString(tokenKey, token);
        bundle.putString(redirectUriKey, getRedirectUri());
        bundle.putString(verificationSmsCodeURLKey, verificationSmsCodeURL);
        mobileForgotPasswordVerifyCodeFragment.setArguments(bundle);
        forgotPasswordContract.addFragment(mobileForgotPasswordVerifyCodeFragment);
    }

    @NonNull
    private String getBodyContent() {
        String body  = "provider=JANRAIN-CN&phonenumber=" + FieldsValidator.getMobileNumber(mobileNumber) +
                "&locale=zh_CN&clientId=" + getClientId() + "&code_type=short&" +
                "redirectUri=" + getRedirectUri();
        RLog.d(TAG, "body :  "+ body);
        return body;
    }

    private String getClientId() {
        ClientIDConfiguration clientIDConfiguration = new ClientIDConfiguration();
        return clientIDConfiguration.getResetPasswordClientId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+RegistrationHelper.getInstance().getCountryCode());
    }

    private String getRedirectUri() {
        return resetPasswordSmsRedirectUri;
    }

    void initateCreateResendSMSIntent(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        String smsServiceID = "userreg.urx.verificationsmscode";

        RLog.d(TAG, " Country :" + RegistrationHelper.getInstance().getCountryCode());
        disposable.add(serviceDiscoveryWrapper.getServiceUrlWithCountryPreferenceSingle(smsServiceID)
                .map(serviceUrl -> getBaseUrl(serviceUrl))
                .map(baseUrl -> {
                    resetPasswordSmsRedirectUri = baseUrl + USER_REQUEST_RESET_REDIRECT_URI_SMS;
                    verificationSmsCodeURL = baseUrl + USER_REQUEST_RESET_SMS_CODE;
                    return verificationSmsCodeURL;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        RLog.i(TAG, "CreateResendSMSIntent url :  "+ verificationUrl);

                        URRequest urRequest = new URRequest(verificationUrl, getBodyContent(), null, forgotPasswordContract::onSuccessResponse, forgotPasswordContract::onErrorResponse);
                        urRequest.makeRequest(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        RLog.e(TAG, "initateCreateResendSMSIntent : Error = " + e.getMessage());
                        forgotPasswordContract.hideForgotPasswordSpinner();
                        forgotPasswordContract.forgotPasswordErrorMessage(
                                context.getString(R.string.USR_Janrain_HSDP_ServerErrorMsg));

                    }
                }));
    }

    @NonNull
    private String getBaseUrl(String serviceUrl) {
        String urlSeparator = "://";
        URL url;
        try {
            url = new URL(serviceUrl);
            return (url.getProtocol() + urlSeparator + url.getHost());
        } catch (MalformedURLException e) {
            RLog.d(TAG, "MalformedURLException = " + e.getMessage());
        }
        return "";
    }

    void clearDisposable() {
        disposable.clear();
    }
}
