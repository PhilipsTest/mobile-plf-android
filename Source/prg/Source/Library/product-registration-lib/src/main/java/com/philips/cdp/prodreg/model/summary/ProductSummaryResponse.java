/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.model.summary;

import com.google.gson.Gson;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductSummaryResponse extends ResponseData {

    private boolean success;
    private Data data;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public ResponseData parseJsonResponseData(final JSONObject response) {
        ProductSummaryResponse productSummaryResponse = null;
        if(response!=null)
         productSummaryResponse = new Gson().fromJson(response.toString(), ProductSummaryResponse.class);
        return productSummaryResponse;
    }
}
