/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.cdp.prxclient.datamodels.cdls;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.io.Serializable;

public class CDLSDataModel extends ResponseData implements Serializable {

    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("success")
    @Expose
    private boolean success;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public ResponseData parseJsonResponseData(JSONObject response) {
        if (response != null) {
            return new Gson().fromJson(response.toString(), CDLSDataModel.class);

        }
        return null;
    }
}
