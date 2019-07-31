package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.network.ModelConstants;
import com.philips.cdp.di.ecs.store.ECSURLBuilder;
import com.philips.cdp.di.ecs.util.ECSConfig;

import java.util.HashMap;
import java.util.Map;

import static com.philips.cdp.di.ecs.util.ECSErrors.getDetailErrorMessage;

public class SetVoucherRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String> {

    private final String voucherCode;
    private final ECSCallback<Boolean, Exception> ecsCallback;

    public SetVoucherRequest(String voucherCode, ECSCallback<Boolean, Exception> ecsCallback) {
        this.voucherCode = voucherCode;
        this.ecsCallback = ecsCallback;
    }

    @Override
    public void onResponse(String response) {
        if(response.isEmpty()) {
            // Empty response indicate success
            ecsCallback.onResponse(true);
        }else{
            ecsCallback.onFailure(new Exception(response),response,20999);
        }
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getApplyVoucherUrl();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ecsCallback.onResponse(true);

        System.out.println("get string error :"+getDetailErrorMessage(error));
       // ecsCallback.onFailure(error, "Error Applying voucher", 9000);
    }

    @Override
    public Response.Listener<String> getStringSuccessResponseListener() {
        return this;
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfig.INSTANCE.getAccessToken());
        return header;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(ModelConstants.VOUCHER_ID, voucherCode);
        return params;
    }
}
