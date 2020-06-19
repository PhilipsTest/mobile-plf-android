/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.summary;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.philips.platform.ecs.prx.response.ResponseData;
import com.philips.platform.ecs.prx.response.ResponseData;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by philips on 2/28/19.
 */

/**
 * The type ECS product summary contains summary data fetched from prx
 */

public class ECSProductSummary extends ResponseData implements Serializable {

    private static final long serialVersionUID = 2565404340609465538L;
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private ArrayList<Data> data;

    /**
     * No args constructor for use in serialization
     *
     */
    public ECSProductSummary() {
    }

    /**
     *
     * @param data
     * @param success
     */
    public ECSProductSummary(boolean success, ArrayList<Data> data) {
        this.success = success;
        this.data = data;
    }

    /**
     *
     * @return
     * The success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The data
     */
    public ArrayList<Data> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    @Override
    public ResponseData parseJsonResponseData(JSONObject response) {
        if (response != null) {
            return new Gson().fromJson(response.toString(), ECSProductSummary.class);
        }
        return null;
    }
}