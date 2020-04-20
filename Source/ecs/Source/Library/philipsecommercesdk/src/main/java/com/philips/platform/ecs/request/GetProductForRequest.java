/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.summary.Data;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.summary.Data;
import com.philips.platform.ecs.store.ECSURLBuilder;


import org.json.JSONObject;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class GetProductForRequest extends AppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private String ctn;
    private final ECSCallback<ECSProduct, Exception> ecsCallback;

    public GetProductForRequest(String ctn, ECSCallback<ECSProduct, Exception> ecsCallback) {
        this.ctn = ctn;
        this.ecsCallback = ecsCallback;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getProduct(ctn);
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

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(JSONObject response) {
            ECSProduct product = null;
            Exception exception = null;

            try {
                product = getJson().fromJson(response.toString(),
                        ECSProduct.class);
            } catch (Exception e) {
                exception = e;
            }

            if (null == exception && null != product && null != product.getCode()) {
                ecsCallback.onResponse(product);
            } else {
                ECSErrorWrapper ecsErrorWrapper  = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
                ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
            }
    }

    public Gson getJson(){

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equalsIgnoreCase("summary");
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return clazz == Data.class;
                    }
                })
                .create();
        return gson;
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
