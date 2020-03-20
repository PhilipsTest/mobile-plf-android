package com.philips.cdp.registration.ui.traditional.mobile;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.janrain.android.Jump;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.configuration.ClientIDConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.restclient.URRequest;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class MobileVerifyResendCodePresenter implements NetworkStateListener {
    private String TAG = "MobileVerifyResendCodePresenter";
    private static final String VERIFICATION_SMS_CODE_SERVICE_ID = "userreg.urx.verificationsmscode";
    private static final String BASE_URL_CODE_SERVICE_ID = "userreg.janrain.api.v2";
    private static final int RESEND_OTP_REQUEST_CODE = 101;
    private static final int CHANGE_NUMBER_REQUEST_CODE = 102;
    private static final String ERROR_CODE = "errorCode";

    private static final String STAT = "stat";

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    private final MobileVerifyResendCodeContract mobileVerifyCodeContract;



    public MobileVerifyResendCodePresenter(MobileVerifyResendCodeFragment mobileVerifyCodeContract) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        this.mobileVerifyCodeContract = mobileVerifyCodeContract;
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
    }


    private void getURLFromServiceDiscoveryAndRequestVerificationCode(String mobileNumber) {

        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(VERIFICATION_SMS_CODE_SERVICE_ID);
        serviceDiscoveryInterface.getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=UTF-8");
                String url = urlMap.get(VERIFICATION_SMS_CODE_SERVICE_ID).getConfigUrls();
                if(null == url){
                    RLog.d(TAG, "getURLFromServiceDiscoveryAndRequestVerificationCode : " + "fetched url is null");
                }else {
                    RLog.i(TAG, VERIFICATION_SMS_CODE_SERVICE_ID + " URL is " + url);
                    URRequest urRequest = new URRequest(getSmsVerificationUrl(url, mobileNumber), null, header
                            , response -> mobileVerifyCodeContract.onSuccessResponse(RESEND_OTP_REQUEST_CODE, response),
                            mobileVerifyCodeContract::onErrorResponse);
                    urRequest.makeRequest(true);
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                RLog.d(TAG, "getURLFromServiceDiscoveryAndRequestVerificationCode " +
                        error.name() + "and error message is " + message);
                mobileVerifyCodeContract.enableResendButton();
            }
        },null);
    }

    void resendOTPRequest(final String mobileNumber) {
        getURLFromServiceDiscoveryAndRequestVerificationCode(mobileNumber);
    }

    @NonNull
    private String getSmsVerificationUrl(String verificationSmsCodeURL, String mobileNumber) {
        String JANRAIN_CHINA_PROVIDER = "?provider=" + "JANRAIN-CN&locale=zh_CN" + "&phonenumber=";
        RLog.d(TAG, verificationSmsCodeURL + JANRAIN_CHINA_PROVIDER + FieldsValidator.getMobileNumber(mobileNumber));
        return verificationSmsCodeURL + JANRAIN_CHINA_PROVIDER + FieldsValidator.getMobileNumber(mobileNumber);
    }


    void handleOnSuccess(int resultCode, String response) {
        if (resultCode == RESEND_OTP_REQUEST_CODE) {
            mobileVerifyCodeContract.hideProgressSpinner();
            handleResendSms(response);
        } else if (resultCode == CHANGE_NUMBER_REQUEST_CODE) {
            RLog.d(TAG, "CHANGE_NUMBER_REQUEST_CODE" + response);
            handlePhoneNumberChange(response);
        } else {
            mobileVerifyCodeContract.hideProgressSpinner();
        }
    }

    private void handlePhoneNumberChange(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            RLog.d(TAG, "CHANGE_NUMBER_REQUEST_STAT " + jsonObject.get(STAT));

            if (jsonObject.get(STAT).equals("ok")) {
                RLog.d(TAG, "CHANGE_NUMBER_REQUEST_CODE" + response);
                mobileVerifyCodeContract.refreshUser();
            } else {
                mobileVerifyCodeContract.hideProgressSpinner();
                final String errorCode = jsonObject.getString(ERROR_CODE);
                mobileVerifyCodeContract.showNumberChangeTechincalError(Integer.parseInt(errorCode));
            }
        } catch (Exception e) {
            mobileVerifyCodeContract.hideProgressSpinner();
            RLog.e(TAG, "handlePhoneNumberChange : Exception " + e.getMessage());
            // mobileVerifyCodeContract.showSmsSendFailedError();

        }
    }


    private void handleResendSms(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getInt(ERROR_CODE) == (ErrorCodes.URX_SUCCESS)) {
                mobileVerifyCodeContract.enableResendButtonAndHideSpinner();
            } else {
                final String errorCode = jsonObject.getString(ERROR_CODE);
                mobileVerifyCodeContract.showNumberChangeTechincalError(Integer.parseInt(errorCode));
            }
        } catch (Exception e) {
            RLog.e(TAG, "handleResendSms : Exception " + e.getMessage());
//            mobileVerifyCodeContract.showSmsSendFailedError();
        }
    }


    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.d(TAG, "MOBILE NUMBER network isOnline : " + isOnline);
        if (isOnline) {
            mobileVerifyCodeContract.netWorkStateOnlineUiHandle();
        } else {
            mobileVerifyCodeContract.netWorkStateOfflineUiHandle();
        }
    }


    @VisibleForTesting
    @Deprecated
    public void mockInjections(ServiceDiscoveryWrapper wrapper) {
        serviceDiscoveryWrapper = wrapper;
    }


    private void initServiceDiscoveryForUpdateMobilenumber(String mobilenumberURL) {
        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(BASE_URL_CODE_SERVICE_ID);

        serviceDiscoveryInterface.getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                String url = urlMap.get(BASE_URL_CODE_SERVICE_ID).getConfigUrls();
                if(null == url){
                    RLog.d(TAG, BASE_URL_CODE_SERVICE_ID + " :  URL is null");
                }else {
                    RLog.i(TAG, BASE_URL_CODE_SERVICE_ID + " URL is " + url);
                    URRequest urRequest = new URRequest(url + "/oauth/update_profile_native", getUpdateMobileNUmberURL(mobilenumberURL), null, response -> mobileVerifyCodeContract.onSuccessResponse(CHANGE_NUMBER_REQUEST_CODE, response), mobileVerifyCodeContract::onErrorResponse);
                    urRequest.makeRequest(false);
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                RLog.d(TAG, error.name() + "and error message is " + message);
                mobileVerifyCodeContract.enableUpdateButton();
            }
        },null);
    }

    void updatePhoneNumber(String mobilenumberURL) {
        initServiceDiscoveryForUpdateMobilenumber(mobilenumberURL);
    }

    private String getUpdateMobileNUmberURL(String mobilenumber) {

        String body = "client_id=" + getClientId() + "&locale=zh-CN&response_type=token&form=mobileNumberForm&flow=standard&" +
                "flow_version=" + Jump.getCaptureFlowVersion() + "&token=" + getAccessToken() +
                "&mobileNumberConfirm=" + mobilenumber + "&mobileNumber=" + mobilenumber;
        RLog.d(TAG, "body" + body);
        return body;
    }

    private String getClientId() {
        ClientIDConfiguration clientIDConfiguration = new ClientIDConfiguration();
        return clientIDConfiguration.getResetPasswordClientId(RegConstants.HTTPS_CONST + Jump.getCaptureDomain());
    }

    private String getAccessToken() {
        return Jump.getSignedInUser() != null ? Jump.getSignedInUser()
                .getAccessToken() : null;
    }

}
