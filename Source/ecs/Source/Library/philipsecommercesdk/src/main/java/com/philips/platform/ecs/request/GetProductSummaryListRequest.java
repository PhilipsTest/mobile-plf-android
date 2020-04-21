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
import com.philips.platform.ecs.model.summary.ECSProductSummary;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.summary.ECSProductSummary;


import org.json.JSONObject;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;


public class GetProductSummaryListRequest extends AppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private final String prxSummaryListURL;
    private final ECSCallback<ECSProductSummary, Exception> ecsCallback;

    public GetProductSummaryListRequest(String prxSummaryListURL, ECSCallback<ECSProductSummary, Exception> ecsCallback) {
        this.prxSummaryListURL = prxSummaryListURL;
        this.ecsCallback = ecsCallback;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return prxSummaryListURL;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public void onResponse(JSONObject response) {
        ECSProductSummary ecsProductSummary = null;
        Exception exception = null;

        try {
            ecsProductSummary = new Gson().fromJson(response.toString(),
                    ECSProductSummary.class);
        } catch (Exception e) {
            exception = e;
        }
            if(null == exception && null!=ecsProductSummary.getData()) {
                ecsCallback.onResponse(ecsProductSummary);
            }else{
                ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
                ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
            }
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
