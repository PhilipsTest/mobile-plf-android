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
import com.philips.platform.ecs.model.address.GetShippingAddressData;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.GetShippingAddressData;
import com.philips.platform.ecs.store.ECSURLBuilder;


import org.json.JSONObject;

import java.util.List;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;


public class GetAddressRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<JSONObject> {

    private static final long serialVersionUID = 8012664086881344220L;
    private final ECSCallback<List<ECSAddress>, Exception> ecsCallback;

    public GetAddressRequest(ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        this.ecsCallback = ecsCallback;
    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            GetShippingAddressData getShippingAddressData = new Gson().fromJson(response.toString(),
                    GetShippingAddressData.class);
            ecsCallback.onResponse(getShippingAddressData.getAddresses());
        }catch(Exception exception){
            String responseData = response!=null?response.toString():null;
            ECSErrorWrapper ecsErrorWrapper   = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong, exception,responseData);
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getAddressesUrl();
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