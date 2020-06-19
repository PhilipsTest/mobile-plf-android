/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class GetECSShoppingCartsRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private static final long serialVersionUID = -1024603800262177753L;
    private final ECSCallback<ECSShoppingCart, Exception> ecsCallback;


    public GetECSShoppingCartsRequest(ECSCallback<ECSShoppingCart, Exception> ecsCallback) {

        this.ecsCallback = ecsCallback;

    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public Map<String, String> getHeader() {
        HashMap<String, String> authMap = new HashMap<>();
        authMap.put("Authorization", "Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return authMap;
    }

    @Override
    public String getURL() {
       String url =  new ECSURLBuilder().getCartsUrl();
//        Log.d("Cart Url", url);
        return url;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ECSErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ECSErrorWrapper.getException(), ECSErrorWrapper.getEcsError());
    }

    @Override
    public void onResponse(JSONObject response) {
        try{
            ECSShoppingCart  resp = new Gson().fromJson(response.toString(),
                    ECSShoppingCart.class);
            ecsCallback.onResponse(resp);
        } catch (Exception exception) {
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSCartError,exception,response.toString());
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }


    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
