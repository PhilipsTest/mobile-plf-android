package com.philips.platform.catk.model;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.philips.cdp.registration.User;
import com.philips.platform.catk.CatkInterface;
import com.philips.platform.catk.network.NetworkAbstractModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Maqsood on 10/13/17.
 */

public class GetConsentsModelRequest extends NetworkAbstractModel {

    //This field has to remove later(URL should take from service discovery)
    private StringBuilder URL = new StringBuilder("https://hdc-css-mst.cloud.pcftest.com/consent/");
    private String mApplicationName;
    private String mPropositionName;

    @Inject
    User user;

    public GetConsentsModelRequest(String applicationName, String propositionName, DataLoadListener dataLoadListener) {
        super(dataLoadListener);
        mApplicationName = applicationName;
        mPropositionName = propositionName;
        CatkInterface.getCatkComponent().inject(this);
    }

    @Override
    public GetConsentsModel[] parseResponse(JsonArray response) {
        return new Gson().fromJson(response, GetConsentsModel[].class);
    }

    @Override
    public int getMethod() {
       return Request.Method.GET;
    }

    @Override
    public Map<String, String> requestHeader() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("api-version", "1");
        params.put("content-type", "application/json");
        params.put("authorization","bearer "+user.getHsdpAccessToken());
        params.put("performerid",user.getHsdpUUID());
        params.put("cache-control", "no-cache");
        return params;
    }

    @Override
    public String requestBody() {
        return null;
    }

    @Override
    public String getUrl() {
        URL.append(user.getHsdpUUID()+"?applicationName="+mApplicationName+"&propositionName="+mPropositionName);
        return URL.toString();
    }
}
