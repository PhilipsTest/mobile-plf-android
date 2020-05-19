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
import com.philips.platform.ecs.constants.ModelConstants;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProducts;
import com.philips.platform.ecs.store.ECSURLBuilder;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.error.ECSErrorWrapper;
import com.philips.platform.ecs.error.ECSNetworkError;
import com.philips.platform.ecs.model.products.ECSProducts;
import com.philips.platform.ecs.store.ECSURLBuilder;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;


public class GetProductListRequest extends AppInfraAbstractRequest implements Response.Listener<JSONObject>{

    private static final long serialVersionUID = 2947288703931197234L;
    private final int currentPage;
    private int pageSize = 0;
    private final ECSCallback<ECSProducts, Exception> ecsCallback;
    private ECSProducts mProducts;


    public GetProductListRequest(int currentPage, int pageSize, ECSCallback<ECSProducts, Exception> ecsCallback) {
        this.currentPage = currentPage;
        this.ecsCallback = ecsCallback;
            this.pageSize = pageSize;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getProductCatalogUrl(currentPage, pageSize);
    }

    @Override
    public Map<String, String> getParams() {
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CURRENT_PAGE, String.valueOf(currentPage));
        query.put(ModelConstants.PAGE_SIZE, String.valueOf(pageSize));
        return query;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(error,this);
        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            mProducts = new Gson().fromJson(response.toString(),
                    ECSProducts.class);
            ecsCallback.onResponse(mProducts);

        } catch (Exception exception) {
            ECSErrorWrapper ecsErrorWrapper = ECSNetworkError.getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,exception,response.toString());
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    @Override
    public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
        return this;
    }
}
