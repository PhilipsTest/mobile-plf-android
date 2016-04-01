package com.philips.cdp.prodreg.model;

import com.google.gson.Gson;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class RegisteredResponse extends ResponseData {

    private RegisteredResponseData[] results;

    private String result_count;

    private String stat;

    public RegisteredResponseData[] getResults() {
        return results;
    }

    public void setResults(RegisteredResponseData[] results) {
        this.results = results;
    }

    public String getResult_count() {
        return result_count;
    }

    public void setResult_count(String result_count) {
        this.result_count = result_count;
    }

    public String isSuccess() {
        return stat;
    }

    @Override
    public String toString() {
        return "ClassPojo [results = " + results + ", result_count = " + result_count + ", stat = " + stat + "]";
    }

    public ResponseData parseJsonResponseData(JSONObject response) {
        RegisteredResponse registeredData;
        registeredData = new Gson().fromJson(response.toString(), RegisteredResponse.class);
        return registeredData;
    }
}
