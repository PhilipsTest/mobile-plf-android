package com.philips.cdp.registration.ui.traditional.mobile;

import com.android.volley.VolleyError;

public interface MobileForgotPassVerifyResendCodeContract {

    void enableResendButton();

    void netWorkStateOnlineUiHandle();

    void hideProgressSpinner();

    void netWorkStateOfflineUiHandle();

    void showSmsSendFailedError(String localizedError);

    void enableResendButtonAndHideSpinner();

//    void showSmsResendTechincalError(String errorCodeString);

    void trackMultipleActionsOnMobileSuccess();

    void trackVerifyActionStatus(String state, String key, String value);

    void showSMSSpecifedError(int errorId);

    void updateToken(String token);

    void onSuccessResponse(String response);

    void onErrorResponse(VolleyError error);
}
