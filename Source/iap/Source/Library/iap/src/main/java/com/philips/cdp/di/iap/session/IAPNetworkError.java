/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.session;

import android.os.Message;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.response.error.ServerError;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;

public class IAPNetworkError implements IAPNetworkErrorListener {

    private ServerError mServerError;
    private VolleyError mVolleyError;
    private int mIAPErrorCode = IAPConstant.IAP_SUCCESS;
    private String mCustomErrorMessage;
    private String mServerName;

    public IAPNetworkError(VolleyError error, int requestCode,
                           RequestListener requestListener) {
        initErrorCode(error);
        if (error instanceof com.android.volley.ServerError) {
            setServerError(error);
        } else {
            mVolleyError = error;
        }


        initMessage(requestCode, requestListener);

    }

    void initMessage(int requestCode, RequestListener requestListener) {
        Message msg = Message.obtain();
        msg.what = requestCode;
        msg.obj = this;
        if (requestListener != null) {
            requestListener.onError(msg);
        }
    }

    private void initErrorCode(final VolleyError error) {
        if (error instanceof NoConnectionError) {
            mIAPErrorCode = IAPConstant.IAP_ERROR_NO_CONNECTION;
        } else if (error instanceof AuthFailureError) {
            mIAPErrorCode = IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE;
        } else if (error instanceof TimeoutError) {
            mIAPErrorCode = IAPConstant.IAP_ERROR_CONNECTION_TIME_OUT;
        } else if (error instanceof com.android.volley.ServerError) {
            mIAPErrorCode = IAPConstant.IAP_ERROR_SERVER_ERROR;
        } else {
            mIAPErrorCode = IAPConstant.IAP_ERROR_UNKNOWN;
        }
    }

    @Override
    public String getMessage() {
        if (mCustomErrorMessage != null) {
            return mCustomErrorMessage;
        } else if (mServerError != null) {
            if (mServerError.getErrors() == null || mServerError.getErrors().get(0) == null) {
                return null;
            }
            return mServerError.getErrors().get(0).getMessage();
        } else if (mVolleyError != null) {
            if (mVolleyError.getMessage() != null) {
                IAPAnalytics.trackTechnicalErrorWithPrefix( IAPAnalyticsConstant.SERVER + mIAPErrorCode + "_" + mVolleyError.getMessage());
            }
            return mVolleyError.getMessage();
        }
        return null;
    }

    public void setCustomErrorMessage(String server, String errorMessage) {
        mServerName = server;
        mCustomErrorMessage = errorMessage;
    }

    @Override
    public int getStatusCode() {
        if (mVolleyError != null && mVolleyError.networkResponse != null) {
            return mVolleyError.networkResponse.statusCode;
        }
        return mIAPErrorCode;
    }

    public void setmIAPErrorCode(int mIAPErrorCode) {
        this.mIAPErrorCode = mIAPErrorCode;
    }

    public int getIAPErrorCode() {
        return mIAPErrorCode;
    }

    private void setServerError(VolleyError error) {
        try {
            if (error.networkResponse != null) {
                final String encodedString = Base64.encodeToString(error.networkResponse.data, Base64.DEFAULT);
                final byte[] decode = Base64.decode(encodedString, Base64.DEFAULT);
                final String errorString = new String(decode);

                mServerError = new Gson().fromJson(errorString, ServerError.class);
                if (getMessage() != null) {
                    IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                            IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.HYBRIS + getMessage());
                }
                checkInsufficientStockError(mServerError);
            }
        } catch (Exception e) {
            IAPLog.e(e.getMessage(), IAPNetworkError.class.getName());
        }
    }

    private void checkInsufficientStockError(ServerError serverError) {
        if (serverError == null || serverError.getErrors() == null
                || serverError.getErrors().get(0) == null) {
            return;
        }
        if ("InsufficientStockError".equals(serverError.getErrors().get(0).getType())) {
            mIAPErrorCode = IAPConstant.IAP_ERROR_INSUFFICIENT_STOCK_ERROR;
        }
    }

    public ServerError getServerError() {
        return mServerError;
    }
}
