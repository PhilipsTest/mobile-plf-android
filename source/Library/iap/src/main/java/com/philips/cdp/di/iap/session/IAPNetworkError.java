package com.philips.cdp.di.iap.session;

import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.response.error.ServerError;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.tagging.Tagging;

public class IAPNetworkError implements IAPNetworkErrorListener {

    private ServerError mServerError;
    private VolleyError mVolleyError;
    private int mIAPErrorCode = IAPConstant.IAP_SUCCESS;

    public IAPNetworkError(VolleyError error, int requestCode,
                           RequestListener requestListener) {
        initErrorCode(error);
        if (error instanceof com.android.volley.ServerError) {
            setServerError(error);
        } else {
            mVolleyError = error;
        }

        Tagging.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.ERROR, getMessage());

        Message msg = Message.obtain();
        msg.what = requestCode;
        msg.obj = this;
        requestListener.onError(msg);
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
        if (mServerError != null) {
            return mServerError.getErrors().get(0).getMessage();
        } else if (mVolleyError != null) {
            return mVolleyError.getMessage();
        }
        return null;
    }

    @Override
    public int getStatusCode() {
        if (mVolleyError.networkResponse != null)
            return mVolleyError.networkResponse.statusCode;
        return mIAPErrorCode;
    }

    public int getIAPErrorCode() {
        return mIAPErrorCode;
    }

    private void setServerError(VolleyError error) {
        try {
            if (error.networkResponse != null) {
                String errorString = new String(error.networkResponse.data);
                mServerError = new Gson().fromJson(errorString, ServerError.class);
                checkInsufficientStockError(mServerError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkInsufficientStockError(ServerError serverError) {
        if ("InsufficientStockError".equals(serverError.getErrors().get(0).getType())) {
            Tagging.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.INSUFFICIENT_STOCK_ERROR);
            mIAPErrorCode = IAPConstant.IAP_ERROR_INSUFFICIENT_STOCK_ERROR;
        }
    }

    public ServerError getServerError() {
        return mServerError;
    }
}
