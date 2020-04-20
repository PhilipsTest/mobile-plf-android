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
import com.philips.platform.ecs.model.asset.AssetModel;
import com.philips.platform.ecs.model.asset.Assets;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.asset.AssetModel;
import com.philips.platform.ecs.model.asset.Assets;


import org.json.JSONObject;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class GetProductAssetRequest extends AppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private final String assetUrl;
    private final ECSCallback<Assets,Exception> ecsCallback;


    public GetProductAssetRequest(String assetUrl, ECSCallback<Assets, Exception> ecsCallback) {
        this.assetUrl = assetUrl;
        this.ecsCallback = ecsCallback;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return assetUrl;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public void onResponse(JSONObject response) {
        AssetModel resp = null;
        Exception exception = null;

        try {
            resp = new Gson().fromJson(response.toString(),
                    AssetModel.class);
        } catch (Exception e) {
            exception = e;
        }

        if(null == exception && null!=resp && null!=resp.getData() && null!=resp.getData().getAssets()) {
            Assets assets = resp.getData().getAssets();
            ecsCallback.onResponse(assets);
        } else {
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
