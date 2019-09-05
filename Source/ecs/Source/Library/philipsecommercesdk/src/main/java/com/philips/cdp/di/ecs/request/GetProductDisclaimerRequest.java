package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.error.ECSErrorEnum;
import com.philips.cdp.di.ecs.error.ECSNetworkError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.disclaimer.DisclaimerModel;
import com.philips.cdp.di.ecs.model.disclaimer.Disclaimers;

import org.json.JSONObject;

import static com.philips.cdp.di.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class GetProductDisclaimerRequest extends AppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private final String assetUrl;
    private final ECSCallback<Disclaimers,Exception> ecsCallback;


    public GetProductDisclaimerRequest(String assetUrl, ECSCallback<Disclaimers, Exception> ecsCallback) {
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
        ECSError ecsError = ECSNetworkError.getErrorLocalizedErrorMessage(error);
        ecsCallback.onFailure(ecsError.getException(), ecsError.getErrorcode());
    }

    @Override
    public void onResponse(JSONObject response) {
        Disclaimers disclaimers=null;
        DisclaimerModel resp = null;
        Exception exception = null;

        try {
            resp = new Gson().fromJson(response.toString(),
                    DisclaimerModel.class);
        } catch (Exception e) {
            exception = e;
        }

        if(null == exception && null!=resp && null!=resp.getData() && null!=resp.getData().getDisclaimers()) {
            disclaimers = resp.getData().getDisclaimers();
            ecsCallback.onResponse(disclaimers);
        } else {
            ECSError ecsError = getErrorLocalizedErrorMessage(ECSErrorEnum.somethingWentWrong,exception,response.toString());
            ecsCallback.onFailure(ecsError.getException(), ecsError.getErrorcode());
        }
    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
