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
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.model.payment.ECSPaymentProvider;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.model.payment.ECSPaymentProvider;
import com.philips.platform.ecs.util.ECSConfiguration;


import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class MakePaymentRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String> {

    private static final long serialVersionUID = 8785021493880410730L;
    private  ECSCallback<ECSPaymentProvider,Exception> ecsCallback;
    private ECSOrderDetail orderDetail;
    ECSAddress ecsBillingAddressRequest;

    public MakePaymentRequest(ECSOrderDetail orderDetail, ECSAddress ecsBillingAddressRequest, ECSCallback<ECSPaymentProvider, Exception> ecsCallback) {
        this.ecsCallback = ecsCallback;
        this.orderDetail = orderDetail;
        this.ecsBillingAddressRequest = ecsBillingAddressRequest;
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {

        ECSPaymentProvider makePaymentData=null;
        Exception exception = null;
        try {
            makePaymentData = new Gson().fromJson(response, ECSPaymentProvider.class);
        }catch(Exception e){
            exception=e;
        }

        if(null==exception && null!=makePaymentData){
            ecsCallback.onResponse(makePaymentData);
        }else{
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

        return new ECSURLBuilder().getMakePaymentUrl(orderDetail.getCode()); // orderID
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
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public Map<String, String> getParams() {
        return ECSRequestUtility.getAddressParams(ecsBillingAddressRequest);

    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return header;
    }

    public Response.Listener<String> getStringSuccessResponseListener(){
        return this;
    }
}
