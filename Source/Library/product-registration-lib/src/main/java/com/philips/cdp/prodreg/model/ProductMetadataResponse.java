package com.philips.cdp.prodreg.model;

import com.google.gson.Gson;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProductMetadataResponse extends ResponseData {

    private ProductMetadataResponseData data;

    private String success;

    public ProductMetadataResponseData getData() {
        return data;
    }

    public void setData(ProductMetadataResponseData data) {
        this.data = data;
    }

    public String isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "ClassPojo [data = " + data + ", success = " + success + "]";
    }

    public ResponseData parseJsonResponseData(JSONObject response) {
        ProductMetadataResponse productMetaData;
        productMetaData = new Gson().fromJson(response.toString(), ProductMetadataResponse.class);
        return productMetaData;
    }
}
