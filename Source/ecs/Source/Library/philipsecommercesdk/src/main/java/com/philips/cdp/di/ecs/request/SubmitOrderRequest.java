/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.constants.ModelConstants;
import com.philips.cdp.di.ecs.error.ECSErrorEnum;
import com.philips.cdp.di.ecs.error.ECSErrorWrapper;
import com.philips.cdp.di.ecs.error.ECSNetworkError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.store.ECSURLBuilder;


import java.util.HashMap;
import java.util.Map;

import static com.philips.cdp.di.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class SubmitOrderRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String>  {


    ECSCallback<ECSOrderDetail,Exception> exceptionECSCallback;
    String cvv;

    public SubmitOrderRequest(String cvv,ECSCallback<ECSOrderDetail, Exception> exceptionECSCallback) {
        this.cvv=cvv;
        this.exceptionECSCallback = exceptionECSCallback;
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {

        try {
            ECSOrderDetail orderDetail = new Gson().fromJson(response, ECSOrderDetail.class);
            exceptionECSCallback.onResponse(orderDetail);
        }catch(Exception exception){
            ECSErrorWrapper ecsErrorWrapper = getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response);
            exceptionECSCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getPlaceOrderUrl();
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        if (cvv != null) {
            params.put(ModelConstants.SECURITY_CODE, cvv);
        }
             params.put(ModelConstants.CART_ID,"current");
             return  params;
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        exceptionECSCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }


    public Response.Listener<String> getStringSuccessResponseListener(){
        return this;
    }



}
