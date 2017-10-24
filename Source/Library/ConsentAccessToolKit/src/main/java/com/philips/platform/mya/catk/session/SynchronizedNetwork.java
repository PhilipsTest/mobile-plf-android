/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.mya.catk.session;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.JsonArray;
import com.philips.platform.mya.catk.request.ConsentRequest;

/**
 * Created by Maqsood on 10/12/17.
 */

public class SynchronizedNetwork {

    private BasicNetwork mBasicNetwork;

    public SynchronizedNetwork(HurlStack stack) {
        HurlStack hurlStack = stack;
        if (stack == null) {
            hurlStack = new HurlStack();
        }
        mBasicNetwork = new BasicNetwork(hurlStack);
    }

    public void performRequest(ConsentRequest request, SynchronizedNetworkListener callBack) {
        try {
            NetworkResponse response = mBasicNetwork.performRequest(request);
            successResponse(request, callBack, response);

        } catch (VolleyError volleyError) {
            callBack.onSyncRequestError(volleyError);
        }
    }

    protected void successResponse(ConsentRequest request, SynchronizedNetworkListener callBack, NetworkResponse response) {
        Response<JsonArray> jsonObjectResponse = request.parseNetworkResponse(response);
        callBack.onSyncRequestSuccess(jsonObjectResponse);
    }
}
