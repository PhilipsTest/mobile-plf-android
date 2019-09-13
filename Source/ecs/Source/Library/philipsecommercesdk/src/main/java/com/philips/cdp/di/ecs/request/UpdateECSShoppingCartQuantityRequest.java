package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.constants.ModelConstants;
import com.philips.cdp.di.ecs.error.ECSErrorEnum;
import com.philips.cdp.di.ecs.error.ECSErrorWrapper;
import com.philips.cdp.di.ecs.error.ECSNetworkError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.cdp.di.ecs.model.cart.UpdateCartData;
import com.philips.cdp.di.ecs.model.summary.ECSProductSummary;
import com.philips.cdp.di.ecs.store.ECSURLBuilder;
import com.philips.cdp.di.ecs.util.ECSConfiguration;

import java.util.HashMap;
import java.util.Map;

public class UpdateECSShoppingCartQuantityRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String> {

    private final ECSCallback<Boolean, Exception> ecsCallback;
    private final ECSEntries entriesEntity;
    private final int quantity;

    public UpdateECSShoppingCartQuantityRequest(ECSCallback<Boolean, Exception> ecsCallback, ECSEntries entriesEntity, int quantity) {
        this.ecsCallback = ecsCallback;
        this.entriesEntity = entriesEntity;
        this.quantity = quantity;
    }

    @Override
    public void onResponse(String response) {
        try {
            UpdateCartData updateCartData = new Gson().fromJson(response,
                    UpdateCartData.class);
            ecsCallback.onResponse(true);
        }catch (Exception e){
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,null,response);
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfiguration.INSTANCE.getAccessToken());
        return header;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> payload = new HashMap<>();
        payload.put(ModelConstants.PRODUCT_CODE, entriesEntity.getProduct().getCode());
        payload.put(ModelConstants.ENTRY_CODE, entriesEntity.getEntryNumber()+"");
        payload.put(ModelConstants.PRODUCT_QUANTITY, String.valueOf(quantity));
        return payload;
    }

    @Override
    public int getMethod() {
        return Request.Method.PUT;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getUpdateProductUrl(entriesEntity.getEntryNumber()+"");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public Response.Listener<String> getStringSuccessResponseListener(){
       return this;
    }
}
