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
import com.philips.platform.ecs.model.config.ECSConfig;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.config.ECSConfig;
import com.philips.platform.ecs.store.ECSURLBuilder;


import org.json.JSONObject;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class GetConfigurationRequest extends AppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private final ECSCallback<ECSConfig, Exception> eCSCallback;

    public GetConfigurationRequest(ECSCallback<ECSConfig, Exception> eCSCallback) {
        this.eCSCallback = eCSCallback;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getRawConfigUrl();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        eCSCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public void onResponse(JSONObject response) {
        ECSConfig resp = null;
        Exception exception = null;

        try {
            resp = new Gson().fromJson(response.toString(),
                    ECSConfig.class);
        } catch (Exception e) {
            exception = e;
        }
            if(null==exception && resp.getCatalogId()!=null && resp.getRootCategory()!=null && resp.getSiteId()!=null){
                eCSCallback.onResponse(resp);
            }else if(response.has("net")) {
                ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
                eCSCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
            }
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
