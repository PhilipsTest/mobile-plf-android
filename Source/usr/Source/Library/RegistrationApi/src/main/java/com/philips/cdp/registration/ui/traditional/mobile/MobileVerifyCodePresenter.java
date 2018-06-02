package com.philips.cdp.registration.ui.traditional.mobile;

import android.support.annotation.VisibleForTesting;

import com.janrain.android.Jump;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errors.ErrorCodes;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.restclient.URRequest;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import static com.philips.cdp.registration.ui.utils.RegConstants.SUCCESS_STATE_RESPONSE;
import static com.philips.cdp.registration.ui.utils.RegConstants.SUCCESS_STATE_RESPONSE_OK;

public class MobileVerifyCodePresenter implements NetworkStateListener {

    private static final String TAG = MobileVerifyCodePresenter.class.getSimpleName();

    public static final String HTTPS = "https://";
    private final String USE_VERIFICATION_CODE = "/access/useVerificationCode";
    private final String VERIFICATION_CODE = "verification_code=";
    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    private final MobileVerifyCodeContract mobileVerifyCodeContract;

    public MobileVerifyCodePresenter(MobileVerifyCodeContract mobileVerifyCodeContract) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        this.mobileVerifyCodeContract = mobileVerifyCodeContract;
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
    }


    public void verifyMobileNumber(String uuid, String otp) {
        String verifiedMobileNumber = FieldsValidator.getVerifiedMobileNumber(uuid, otp);
        String url = HTTPS + Jump.getCaptureDomain() + USE_VERIFICATION_CODE;

        String bodyContent = VERIFICATION_CODE + verifiedMobileNumber;
        getRequest(url, bodyContent);
    }

    private void getRequest(String url, String bodyContent) {
        URRequest urRequest = new URRequest(url, bodyContent, null, mobileVerifyCodeContract::onSuccessResponse, mobileVerifyCodeContract::onErrorResponse);
        urRequest.makeRequest(false);
    }

    void handleActivation(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString(SUCCESS_STATE_RESPONSE).equals(SUCCESS_STATE_RESPONSE_OK)) {
                mobileVerifyCodeContract.refreshUserOnSmsVerificationSuccess();
            } else {
                mobileVerifyCodeContract.hideProgressSpinner();
                mobileVerifyCodeContract.enableVerifyButton();
                smsActivationFailed(jsonObject);
            }
        } catch (JSONException e) {
//            mobileVerifyCodeContract.smsVerificationResponseError();
            RLog.e(TAG, "handleActivation : Exception : " + e.getMessage());
        }
    }

    private void smsActivationFailed(JSONObject jsonObject) throws JSONException {
        String errorCode = jsonObject.getString("code");
        if (isResponseCodeValid(jsonObject)) {
            mobileVerifyCodeContract.setOtpInvalidErrorMessage(ErrorCodes.URX_INVALID_VERIFICATION_CODE);
        } else {
//            String errorMessage = jsonObject.getString("error_description");
            mobileVerifyCodeContract.setOtpErrorMessageFromJson(Integer.parseInt(errorCode));
        }
//        mobileVerifyCodeContract.showOtpInvalidError(errorCode);
        mobileVerifyCodeContract.enableVerifyButton();
    }

    private boolean isResponseCodeValid(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("code").equals(String.valueOf(ErrorCodes.URX_INVALID_VERIFICATION_CODE));
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
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

    public void registerSMSReceiver() {
        mobileVerifyCodeContract.getSMSBroadCastReceiver().registerReceiver();
    }

    public void unRegisterSMSReceiver() {
        mobileVerifyCodeContract.getSMSBroadCastReceiver().unRegisterReceiver();
    }
}
