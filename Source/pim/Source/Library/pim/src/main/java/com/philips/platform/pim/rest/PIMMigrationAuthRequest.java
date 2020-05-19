package com.philips.platform.pim.rest;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

public class PIMMigrationAuthRequest implements PIMRequestInterface {

    private String endPoint;

    public PIMMigrationAuthRequest(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String getUrl() {
        return endPoint;
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public int getMethodType() {
        return PIMRequest.Method.GET;
    }
}
