package com.philips.cdp.di.iap.session;

import android.content.Context;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.store.Store;
import com.philips.cdp.di.iap.utils.IAPLog;

import org.json.JSONObject;

public class NetworkController {
    IAPHurlStack mIAPHurlStack;
    RequestQueue hybrisVolleyQueue;
    Context context;
    private Store store;
    private OAuthHandler oAuthHandler;

    public NetworkController(Context context) {
        this.context = context;
        initStore();
        oAuthHandler = new TestEnvOAuthHandler();
        mIAPHurlStack = new IAPHurlStack(oAuthHandler);
        hybrisVolleyCreateConnection(context);
    }

    private void initStore() {
        store = new Store(context);
    }

    public void hybrisVolleyCreateConnection(Context context) {
        hybrisVolleyQueue = VolleyWrapper.newRequestQueue(context, mIAPHurlStack.getHurlStack());
    }

    void refreshOAuthToken(RequestListener listener) {
            oAuthHandler.refreshToken(listener);
    }

    public void sendHybrisRequest(final int requestCode, final AbstractModel model, final
    RequestListener requestListener) {
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                IAPLog.d(IAPLog.LOG, "Response from sendHybrisRequest onError =" + error
                        .getLocalizedMessage() + " requestCode=" + requestCode + "in " +
                        requestListener.getClass().getSimpleName());
                if (requestListener != null) {
                    new IAPNetworkError(error, requestCode, requestListener);
                }
            }
        };

        Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {
                if (requestListener != null) {
                    Message msg = Message.obtain();
                    msg.what = requestCode;

                    if(response != null && response.length() == 0){
                        msg.obj = NetworkConstants.EMPTY_RESPONSE;
                    }else{
                        msg.obj = model.parseResponse(response);
                    }

                    requestListener.onSuccess(msg);
                    IAPLog.d(IAPLog.LOG, "Response from sendHybrisRequest onSuccess =" + msg +  " requestCode=" + requestCode + "in " +
                            requestListener.getClass().getSimpleName());
                }
            }
        };

        IAPJsonRequest jsObjRequest = new IAPJsonRequest(model.getMethod(), model.getUrl(),
                model.requestBody(), response, error);
        addToVolleyQueue(jsObjRequest);
    }

    public void addToVolleyQueue(final IAPJsonRequest jsObjRequest) {
        hybrisVolleyQueue.add(jsObjRequest);
    }

    public Store getStore() {
        return store;
    }

}