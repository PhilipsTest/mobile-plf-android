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
import com.philips.platform.ecs.model.voucher.ECSVoucher;
import com.philips.platform.ecs.model.voucher.GetAppliedValue;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;
import com.philips.platform.ecs.model.voucher.GetAppliedValue;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.util.ECSConfiguration;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class GetVouchersRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private static final long serialVersionUID = 1754829078274995968L;
    private final ECSCallback<List<ECSVoucher>, Exception> ecsCallback;

    public GetVouchersRequest(ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        this.ecsCallback = ecsCallback;
    }

    @Override
    public void onResponse(JSONObject response) {

         try {
             GetAppliedValue getAppliedValue = new Gson().fromJson(response.toString(), GetAppliedValue.class);
             ecsCallback.onResponse(getAppliedValue.getVouchers());
        } catch (Exception exception) {
             String errorMessage = (response!=null)?response.toString():null;
             ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,errorMessage);
             ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
         }

    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getAppliedVoucherUrl();
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

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return header;
    }
}
