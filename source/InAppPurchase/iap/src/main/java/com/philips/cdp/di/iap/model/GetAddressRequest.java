/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.store.Store;

import java.util.Map;

public class GetAddressRequest extends AbstractModel {

    public GetAddressRequest(final Store store, final Map<String, String> query, DataLoadListener loadListener) {
        super(store, query, loadListener);
    }

    @Override
    public String getProductionUrl() {
        return null;
    }

    @Override
    public Object parseResponse(Object response) {
        return null;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public Map<String, String> requestBody() {
        return null;
    }

    @Override
    public String getTestUrl() {
        return NetworkConstants.ADDRESS_URL;
    }
}
