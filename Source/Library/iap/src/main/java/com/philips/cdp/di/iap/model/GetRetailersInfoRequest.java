package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.store.StoreListener;
import com.philips.cdp.di.iap.response.retailers.WebResults;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.ModelConstants;

import java.util.Map;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class GetRetailersInfoRequest extends AbstractModel {
    private static final String PREFIX_RETAILERS = "www.philips.com/api/wtb/v1";
    private static final String RETAILERS_ALTER = "online-retailers?product=%s&lang=en";
    private final String mRetailerUrl;

    public GetRetailersInfoRequest(final StoreListener store, final Map<String, String> query, DataLoadListener loadListener) {
        super(store, query, loadListener);
        mRetailerUrl = createRetailersURL(store);
    }

    private String createRetailersURL(final StoreListener store) {
        StringBuilder builder = new StringBuilder("https://");
        builder.append(PREFIX_RETAILERS).append("/");
        builder.append(NetworkConstants.PRX_SECTOR_CODE).append("/");
        builder.append(store.getLocale()).append("/");
        builder.append(RETAILERS_ALTER);
        return builder.toString();
    }

    @Override
    public Object parseResponse(final Object response) {
        return new Gson().fromJson(response.toString(), WebResults.class);
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
    public String getUrl() {
        if (params == null || !params.containsKey(ModelConstants.PRODUCT_CODE)) {
            throw new RuntimeException("CTN must be specified");
        }
        String ctn = params.get(ModelConstants.PRODUCT_CODE);
        return String.format(mRetailerUrl, ctn);
    }
}

