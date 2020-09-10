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
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.address.GetDeliveryModes;
import com.philips.platform.ecs.store.ECSURLBuilder;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class GetDeliveryModesRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<JSONObject> {

    private static final long serialVersionUID = -4164618518447189962L;
    private final ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback;

    public GetDeliveryModesRequest(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback) {
        this.ecsCallback = ecsCallback;
    }

    @Override
    public void onResponse(JSONObject response) {
        GetDeliveryModes getDeliveryModes = null;
        Exception exception = null;

        try{
            getDeliveryModes = new Gson().fromJson(response.toString(),
                    GetDeliveryModes.class);
        } catch (Exception e) {
            exception = e;
        }

        if(null == exception && null!=getDeliveryModes && null!=getDeliveryModes.getDeliveryModes()) {
            List<ECSDeliveryMode> deliveryModes = getDeliveryModes.getDeliveryModes();
            //remove collection point delivery modes
            removePickupPoints(deliveryModes);
            ecsCallback.onResponse(deliveryModes);
        } else {
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void removePickupPoints(List<ECSDeliveryMode> deliveryModes) {
        Iterator<ECSDeliveryMode> iterator = deliveryModes.iterator();
        while (iterator.hasNext()){
            ECSDeliveryMode deliveryMode = iterator.next();
            if(deliveryMode.isPickupPoint()){
                iterator.remove();
            }
        }
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getDeliveryModesUrl();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
