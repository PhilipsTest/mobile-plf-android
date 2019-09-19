package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philips.cdp.di.ecs.error.ECSErrorEnum;
import com.philips.cdp.di.ecs.error.ECSErrorWrapper;
import com.philips.cdp.di.ecs.error.ECSNetworkError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.summary.Data;
import com.philips.cdp.di.ecs.store.ECSURLBuilder;


import org.json.JSONObject;

import static com.philips.cdp.di.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

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
                ECSErrorWrapper ecsErrorWrapper  = getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
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
