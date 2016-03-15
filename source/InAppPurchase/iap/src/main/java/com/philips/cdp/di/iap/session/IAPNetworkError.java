package com.philips.cdp.di.iap.session;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.response.error.ServerError;
import com.philips.cdp.di.iap.utils.Utility;

public class IAPNetworkError implements IAPNetworkErrorListener {

    ServerError mServerError = null;
    VolleyError mVolleyError = null;
    Context mContext;

    public IAPNetworkError(Context context, VolleyError error, int requestCode,
                           RequestListener requestListener) {
        mContext = context;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Utility.dismissProgressDialog();
            Toast.makeText(mContext, "Network timeout reached!", Toast.LENGTH_SHORT).show();
        } else {
            mVolleyError = error;
            setServerError(mVolleyError);
            Message msg = Message.obtain();
            msg.what = requestCode;
            requestListener.onError(msg);
        }
    }

    @Override
    public String getMessage() {
        if (mServerError != null) {
            return mServerError.getErrors().get(0).getMessage();
        } else {
            return mVolleyError.getMessage();
        }
    }

    @Override
    public int getStatusCode() {
        if (mVolleyError.networkResponse != null)
            return mVolleyError.networkResponse.statusCode;
        return 0;
    }

    private void setServerError(VolleyError error) {
        if (error.networkResponse != null) {
            String errorString = new String(error.networkResponse.data);
            mServerError = new Gson().fromJson(errorString, ServerError.class);
        }
    }

    public ServerError getServerError() {
        return mServerError;
    }

}
