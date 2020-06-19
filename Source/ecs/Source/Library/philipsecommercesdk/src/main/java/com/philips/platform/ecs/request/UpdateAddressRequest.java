/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.util.ECSConfiguration;


import java.util.HashMap;
import java.util.Map;

public class UpdateAddressRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String>  {

    private static final long serialVersionUID = -3311124503802617373L;
    private final ECSAddress addresses;
    private final ECSCallback<Boolean,Exception> ecsCallback;

    public UpdateAddressRequest(ECSAddress addresses, ECSCallback<Boolean, Exception> ecsCallback) {
        this.addresses = addresses;
        this.ecsCallback = ecsCallback;
    }


    @Override
    public int getMethod() {
        return Request.Method.PUT;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getEditAddressUrl(addresses.getId());
    }

    @Override
    public Map<String, String> getParams() {

        return ECSRequestUtility.getAddressParams(addresses);
    }


    @Override
    public void onResponse(String response) {
        if(null!=response && response.isEmpty()) {
            ecsCallback.onResponse(true);
        }else{
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,null,response);
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public Response.Listener<String> getStringSuccessResponseListener() {
        return this;
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return header;
    }
}
