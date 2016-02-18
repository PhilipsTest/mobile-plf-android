/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.response.carts.UpdateCartData;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.store.Store;

import java.util.HashMap;
import java.util.Map;

public class CartUpdateProductQuantityRequest extends AbstractModel {
    public CartUpdateProductQuantityRequest(final Store store, final Map<String, String> query, final DataLoadListener listener) {
        super(store, query, listener);
    }

    @Override
    public String getProductionUrl() {
        return null;
    }

    @Override
    public Object parseResponse(final Object response) {
        return new Gson().fromJson(response.toString(), UpdateCartData.class);
    }

    @Override
    public int getMethod() {
        return Request.Method.PUT;
    }

    @Override
    public Map<String, String> requestBody() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ModelConstants.PRODUCT_CODE, this.params.get(ModelConstants.PRODUCT_CODE));
        params.put(ModelConstants.PRODUCT_QUANTITY, this.params.get(ModelConstants.PRODUCT_QUANTITY));
        return params;
    }

    @Override
    public String getTestUrl() {
        if (params == null || !params.containsKey(ModelConstants.PRODUCT_CODE) ||
                !params.containsKey(ModelConstants.PRODUCT_QUANTITY)) {
            throw new RuntimeException("product code and quantity must be supplied");
        }
        String entrycode = params.get(ModelConstants.PRODUCT_ENTRYCODE);
        return String.format(NetworkConstants.UPDATE_QUANTITY_URL, entrycode);
    }
}