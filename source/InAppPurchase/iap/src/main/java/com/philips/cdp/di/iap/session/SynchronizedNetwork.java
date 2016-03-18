/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.session;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;

import org.json.JSONObject;

public class SynchronizedNetwork {
    private BasicNetwork mBasicNetwork;

    public SynchronizedNetwork(HurlStack stack) {
        HurlStack hurlStack = null;
        if (stack == null) {
            hurlStack = new HurlStack();
        }
        mBasicNetwork = new BasicNetwork(stack);
    }

    public void performRequest(IAPJsonRequest request) {
        try {
            NetworkResponse response = mBasicNetwork.performRequest(request);
            Response<JSONObject> jsonObjectResponse = request.parseNetworkResponse(response);
            request.deliverResponse(jsonObjectResponse.result);
        } catch (VolleyError volleyError) {
            request.deliverError(volleyError);
        }
    }
}
