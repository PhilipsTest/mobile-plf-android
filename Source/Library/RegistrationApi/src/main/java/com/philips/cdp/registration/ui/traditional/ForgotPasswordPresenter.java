package com.philips.cdp.registration.ui.traditional;

import android.content.*;
import android.os.*;
import android.support.annotation.*;

import com.janrain.android.*;
import com.philips.cdp.registration.*;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.app.infra.*;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.configuration.*;
import com.philips.cdp.registration.dao.*;
import com.philips.cdp.registration.events.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.traditional.mobile.*;
import com.philips.cdp.registration.ui.utils.*;

import org.json.*;

import java.net.*;

import javax.inject.*;

import io.reactivex.android.schedulers.*;
import io.reactivex.disposables.*;
import io.reactivex.observers.*;
import io.reactivex.schedulers.*;

/**
 * Created by philips on 22/06/17.
 */

public class ForgotPasswordPresenter implements NetworStateListener, EventListener,
        ForgotPasswordHandler, HttpClientServiceReceiver.Listener {

    @Inject
    User user;

    @Inject
    RegistrationHelper registrationHelper;

    @Inject
    EventHelper eventHelper;

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    public static final String USER_REQUEST_PASSWORD_RESET_SMS_CODE =
            "/api/v1/user/requestPasswordResetSmsCode";

    public static final String USER_REQUEST_RESET_PASSWORD_REDIRECT_URI_SMS =
            "/c-w/user-registration/apps/reset-password.html";

    private final ForgotPasswordContract accountActivationContract;

    private final CompositeDisposable disposable = new CompositeDisposable();

    RegistrationFragment registrationFragment;

    String verificationSmsCodeURL;

    String resetPasswordSmsRedirectUri;

    String userId;

    Context context;

    public ForgotPasswordPresenter(
            ForgotPasswordContract accountActivationContract, Context context) {
        URInterface.getComponent().inject(this);
        this.accountActivationContract = accountActivationContract;
        this.context = context;
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "CreateAccoutFragment :onNetWorkStateReceived : " + isOnline);
        accountActivationContract.handleUiState(isOnline);
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
        RLog.i(RLog.EVENT_LISTENERS, "ResetPasswordFragment :onCounterEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            accountActivationContract.handleUiStatus();
        }
    }

    @Override
    public void onSendForgotPasswordSuccess() {
        accountActivationContract.handleSendForgotPasswordSuccess();
    }

    @Override
    public void onSendForgotPasswordFailedWithError(final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        accountActivationContract.handleSendForgotPasswordFailedWithError(userRegistrationFailureInfo);
    }

    void forgotPasswordRequest(String userId, User user) {
        user.forgotPassword(userId, this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String response = resultData.getString("responseStr");
        RLog.i("MobileVerifyCodeFragment ", "onReceiveResult Response Val = " + response);
        accountActivationContract.hideForgotPasswordSpinner();
        if (response == null) {
            accountActivationContract.forgotPasswordErrorMessage(
                    context.getResources().getString(R.string.reg_Invalid_PhoneNumber_ErrorMsg));
            return;
        } else {
            handleResendSMSRespone(response);
        }
    }

    private void handleResendSMSRespone(String response) {

        final String mobileNumberKey = "mobileNumber";
        final String tokenKey = "token";
        final String redirectUriKey = "redirectUri";
        final String verificationSmsCodeURLKey = "verificationSmsCodeURL";

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("errorCode").toString().equals("0")) {
                accountActivationContract.trackAction(AppTagingConstants.SEND_DATA,
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
                RLog.i("MobileVerifyCodeFragment ", " isAccountActivate is " + token + " -- " + response);
                MobileForgotPasswordVerifyCodeFragment mobileForgotPasswordVerifyCodeFragment = new MobileForgotPasswordVerifyCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(mobileNumberKey, userId);
                bundle.putString(tokenKey, token);
                bundle.putString(redirectUriKey, getRedirectUri());
                bundle.putString(verificationSmsCodeURLKey, verificationSmsCodeURL);
                mobileForgotPasswordVerifyCodeFragment.setArguments(bundle);
                registrationFragment.addFragment(mobileForgotPasswordVerifyCodeFragment);
            } else {
                accountActivationContract.trackAction(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.TECHNICAL_ERROR, AppTagingConstants.MOBILE_RESEND_SMS_VERFICATION_FAILURE);
                String errorMsg = RegChinaUtil.getErrorMsgDescription(jsonObject.getString("errorCode").toString(), context);
                accountActivationContract.forgotPasswordErrorMessage(errorMsg);
                RLog.i("MobileVerifyCodeFragment ", " SMS Resend failure = " + response);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent createResendSMSIntent(String url) {

        final String receiverKey = "receiver";
        final String bodyContentKey = "bodyContent";
        final String urlKey = "url";

        RLog.d(RLog.EVENT_LISTENERS, "MOBILE NUMBER *** : " + userId);
        RLog.d("Configration : ", " envir :" + RegistrationConfiguration.getInstance().getRegistrationEnvironment());

        Intent httpServiceIntent = new Intent(context, HttpClientService.class);
        HttpClientServiceReceiver receiver = new HttpClientServiceReceiver(new Handler());
        receiver.setListener(this);

        String bodyContent = getBodyContent();

        RLog.d("Configration : ", " envirr :" + getClientId() + getRedirectUri());
        httpServiceIntent.putExtra(receiverKey, receiver);
        httpServiceIntent.putExtra(bodyContentKey, bodyContent);
        httpServiceIntent.putExtra(urlKey, url);
        return httpServiceIntent;
    }

    String getBodyContent() {
        return "provider=JANRAIN-CN&phonenumber=" + FieldsValidator.getMobileNumber(userId) +
                "&locale=zh_CN&clientId=" + getClientId() + "&code_type=short&" +
                "redirectUri=" + getRedirectUri();
    }

    private String getClientId() {
        ClientIDConfiguration clientIDConfiguration = new ClientIDConfiguration();
        return clientIDConfiguration.getResetPasswordClientId(RegConstants.HTTPS_CONST + Jump.getCaptureDomain());
    }

    private String getRedirectUri() {
        return resetPasswordSmsRedirectUri;
    }

    void initateCreateResendSMSIntent(String userId, RegistrationFragment registrationFragment) {
        this.userId = userId;
        String smsServiceID = "userreg.urx.verificationsmscode";

        this.registrationFragment = registrationFragment;
        RLog.d(RLog.SERVICE_DISCOVERY, " Country :" + RegistrationHelper.getInstance().getCountryCode());
        disposable.add(serviceDiscoveryWrapper.getServiceUrlWithCountryPreferenceSingle(smsServiceID)
                .map(serviceUrl -> getBaseUrl(serviceUrl))
                .map(baseUrl -> {
                    resetPasswordSmsRedirectUri = baseUrl + USER_REQUEST_RESET_PASSWORD_REDIRECT_URI_SMS;
                    verificationSmsCodeURL = baseUrl + USER_REQUEST_PASSWORD_RESET_SMS_CODE;
                    return verificationSmsCodeURL;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        registrationFragment.getActivity().startService(createResendSMSIntent(verificationUrl));
                    }

                    @Override
                    public void onError(Throwable e) {
                        accountActivationContract.hideForgotPasswordSpinner();
                        accountActivationContract.forgotPasswordErrorMessage(
                                context.getString(R.string.reg_Generic_Network_Error));
                    }
                }));
    }

    @NonNull
    private String getBaseUrl(String serviceUrl) {
        String urlSeprator = "://";
        URL url = null;
        try {
            url = new URL(serviceUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return (url.getProtocol() + urlSeprator + url.getHost());
    }

    public void clearDisposal() {
        disposable.clear();
    }
}
