/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.philips.cdp.di.iap.store.Store;

import java.util.HashMap;
import java.util.Map;

public class CartDeleteProductRequest extends AbstractModel {
    public CartDeleteProductRequest(final Store store, final Map<String, String> query, final DataLoadListener listener) {
        super(store, query, listener);
    }

    @Override
    public String getProductionUrl() {
        return null;
    }

    @Override
    public Object parseResponse(final Object response) {
        return null;
    }

    @Override
    public int getMethod() {
        return Request.Method.DELETE;
    }

    @Override
    public Map<String, String> requestBody() {
        Map<String, String> payload = new HashMap<>();
        payload.put(ModelConstants.PRODUCT_CODE, params.get(ModelConstants.PRODUCT_CODE));
        payload.put(ModelConstants.ENTRY_CODE, params.get(ModelConstants.ENTRY_CODE));
        return payload;
    }

    @Override
    public String getTestUrl() {
        if (params == null) {
            throw new RuntimeException("Cart ID and Entry Number has to be supplied");
        }
        int entryNumber = Integer.parseInt(params.get(ModelConstants.ENTRY_CODE));
        return store.getModifyProductUrl(String.valueOf(entryNumber));
    }
}