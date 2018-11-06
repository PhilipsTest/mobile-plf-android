package com.philips.cdp.di.iap.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.philips.cdp.di.iap.response.carts.AddToCartData;
import com.philips.cdp.di.iap.response.voucher.GetAppliedValue;
import com.philips.cdp.di.iap.store.StoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class GetAppliedVoucherRequest extends AbstractModel {


    public GetAppliedVoucherRequest(StoreListener store, Map<String, String> query,
                                    DataLoadListener listener) {
        super(store, query, listener);
    }
    @Override
    public Object parseResponse(Object response) {

        return   new Gson().fromJson(response.toString(), GetAppliedValue.class);
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
        return store.getAppliedVoucherUrl();
    }
}
