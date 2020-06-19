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
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;

import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;


import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;


public class CreateAddressRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String> {


    private static final long serialVersionUID = -170242843793515063L;
    ECSAddress ecsAddressRequest;
    private  ECSCallback<ECSAddress,Exception> ecsCallback;

    public CreateAddressRequest(ECSAddress ecsAddressRequest, ECSCallback<ECSAddress, Exception> ecsCallback) {
        this.ecsAddressRequest = ecsAddressRequest;
        this.ecsCallback = ecsCallback;
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        ECSAddress addresses=null;
        Exception exception = null;
                // created address response is not checked
        try {
            addresses = new Gson().fromJson(response, ECSAddress.class);
            ecsCallback.onResponse(addresses);
        }catch(Exception e){
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response);
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getAddressesUrl();
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
        return ECSRequestUtility.getAddressParams(ecsAddressRequest);
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = getECSError(error);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());

    }

    public ECSErrorWrapper getECSError(VolleyError error){
        return ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
    }

    public Response.Listener<String> getStringSuccessResponseListener(){
        return this;
    }



}
