/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.disclaimer;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.philips.platform.ecs.prx.response.ResponseData;
import com.philips.platform.ecs.prx.response.ResponseData;

import org.json.JSONObject;

import java.io.Serializable;

public class DisclaimerModel extends ResponseData implements Serializable {

    private static final long serialVersionUID = -5543216341859689589L;
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;


    /**
     * No args constructor for use in serialization
     */
    public DisclaimerModel() {
    }

    /**
     * @param data
     * @param success
     */
    public DisclaimerModel(boolean success, Data data) {
        this.success = success;
        this.data = data;
    }

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

    @Override
    public ResponseData parseJsonResponseData(JSONObject response) {
        if (response != null) {
            return new Gson().fromJson(response.toString(), DisclaimerModel.class);
        }
        return null;
    }
}
