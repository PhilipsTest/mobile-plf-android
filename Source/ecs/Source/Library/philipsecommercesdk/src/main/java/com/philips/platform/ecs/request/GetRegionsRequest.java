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
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.platform.ecs.model.region.RegionsList;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.platform.ecs.model.region.RegionsList;


import org.json.JSONObject;

import java.util.List;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;


public class GetRegionsRequest extends OAuthAppInfraAbstractRequest  implements Response.Listener<JSONObject>{

    private static final long serialVersionUID = 1070708178184053005L;
    private final ECSCallback<List<ECSRegion>,Exception> ecsCallback;
    private String countryISO;

    public GetRegionsRequest(String countryISO, ECSCallback<List<ECSRegion>, Exception> ecsCallback) {
        this.ecsCallback = ecsCallback;
        this.countryISO = countryISO;
    }


    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getRegionsUrl(countryISO);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            RegionsList regionsList = new Gson().fromJson(response.toString(), RegionsList.class);
            ecsCallback.onResponse(regionsList.getRegions());
        }catch(Exception e){
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,e,response.toString());
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
