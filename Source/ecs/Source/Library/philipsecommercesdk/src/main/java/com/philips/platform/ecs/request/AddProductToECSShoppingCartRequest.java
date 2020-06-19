/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;

import java.util.HashMap;
import java.util.Map;



public class AddProductToECSShoppingCartRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String> {

    private static final long serialVersionUID = 3738686474755375554L;
    private final ECSCallback<Boolean,Exception> ecsCallback;

    private final String ctn;


    public AddProductToECSShoppingCartRequest(String ctn, ECSCallback<Boolean, Exception> ecsCallback) {
        this.ecsCallback = ecsCallback;
        this.ctn = ctn;
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getAddToCartUrl();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return header;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> jsonParams = new HashMap<>();
        jsonParams.put("code", ctn);
        return jsonParams;
    }

    @Override
    public void onResponse(String response) {

        if(null!=response && !response.isEmpty()){
            ecsCallback.onResponse(true);
        }else{
            ecsCallback.onResponse(false);
        }
    }

    public Response.Listener<String> getStringSuccessResponseListener(){
        return this;
    }

}
