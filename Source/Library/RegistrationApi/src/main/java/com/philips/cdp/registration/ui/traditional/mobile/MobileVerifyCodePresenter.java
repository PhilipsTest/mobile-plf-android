package com.philips.cdp.registration.ui.traditional.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.*;

import com.janrain.android.Jump;
import com.philips.cdp.registration.HttpClientServiceReceiver;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.ui.utils.*;

import org.json.*;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.philips.cdp.registration.HttpClientService.*;
import static com.philips.cdp.registration.ui.utils.RegConstants.*;

public class MobileVerifyCodePresenter implements HttpClientServiceReceiver.Listener, NetworStateListener {

    private static final int SMS_ACTIVATION_REQUEST_CODE = 100;

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    private final MobileVerifyCodeContract mobileVerifyCodeContract;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MobileVerifyCodePresenter(MobileVerifyCodeContract mobileVerifyCodeContract) {
        URInterface.getComponent().inject(this);
        this.mobileVerifyCodeContract = mobileVerifyCodeContract;
    }


    public void verifyMobileNumber(String uuid, String otp) {
        Intent smsActivationIntent = createSMSActivationIntent(uuid, otp);
        mobileVerifyCodeContract.startService(smsActivationIntent);
    }

    private Intent createSMSActivationIntent(String uuid, String otp) {
        String verifiedMobileNumber = FieldsValidator.getVerifiedMobileNumber(uuid, otp);
        String url = "https://"+ Jump.getCaptureDomain()+"/access/useVerificationCode";

        String bodyContent = "verification_code=" + verifiedMobileNumber;
        RLog.i("MobileVerifyCodeFragment ", "verification_code" + verifiedMobileNumber);
        Intent httpServiceIntent = getHttpServiceIntent(url, bodyContent, SMS_ACTIVATION_REQUEST_CODE);
        return httpServiceIntent;
    }



    @NonNull
    private Intent getHttpServiceIntent(String url, String value, int resendOtpRequestCode) {
        HttpClientServiceReceiver receiver = mobileVerifyCodeContract.getClientServiceRecevier();
        receiver.setListener(this);
        Intent httpServiceIntent = mobileVerifyCodeContract.getServiceIntent();
        httpServiceIntent.putExtra(HTTP_RECEIVER, receiver);
        httpServiceIntent.putExtra(HTTP_BODY_CONTENT, value);
        httpServiceIntent.putExtra(HTTP_URL_TO_BE_CALLED, url);
        httpServiceIntent.putExtra(HTTP_SERVICE_REQUEST_CODE, resendOtpRequestCode);
        return httpServiceIntent;
    }

    public void cleanUp() {
        compositeDisposable.clear();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String response = resultData.getString(HTTP_SERVICE_RESPONSE);
        if (response == null || response.isEmpty()) {
            mobileVerifyCodeContract.showSmsSendFailedError();
            return;
        }

        if(resultCode == SMS_ACTIVATION_REQUEST_CODE) {
            handleActivation(response);
        }

    }


    private void handleActivation(String response) {
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
            mobileVerifyCodeContract.smsVerificationResponseError();
        }
    }

    private void smsActivationFailed(JSONObject jsonObject) throws JSONException {
        if (isResponseCodeValid(jsonObject)) {
            mobileVerifyCodeContract.setOtpInvalidErrorMessage();
        } else {
            String errorMessage = jsonObject.getString("error_description");
            mobileVerifyCodeContract.setOtpErrorMessageFromJson(errorMessage);
        }
        mobileVerifyCodeContract.showOtpInvalidError();
        mobileVerifyCodeContract.enableVerifyButton();
    }

    private boolean isResponseCodeValid(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("code").toString().equals(String.valueOf(RegChinaConstants.URXInvalidVerificationCode));
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        if(isOnline) {
            mobileVerifyCodeContract.enableVerifyButton();
            mobileVerifyCodeContract.hideErrorMessage();
        } else {
            mobileVerifyCodeContract.disableVerifyButton();
            mobileVerifyCodeContract.showNoNetworkErrorMessage();
        }
    }


    @VisibleForTesting
    @Deprecated
    public void mockInjections(ServiceDiscoveryWrapper wrapper) {
        serviceDiscoveryWrapper = wrapper;
    }


}
