/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.catk.dto;


import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.philips.platform.catk.ConsentAccessToolKit;
import com.philips.platform.catk.network.NetworkAbstractModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GetConsentsModelRequest extends NetworkAbstractModel {

    private String url;
    private String mApplicationName;
    private String mPropositionName;

    public GetConsentsModelRequest(String url, String applicationName, String propositionName, DataLoadListener dataLoadListener) {
        super(dataLoadListener);
        this.url = url;
        mApplicationName = applicationName;
        mPropositionName = propositionName;
    }

    @Override
    public List<GetConsentDto> parseResponse(JsonArray response) {
        List<GetConsentDto> consents = new ArrayList<>();
        if (response != null && response.size() > 0) {
            GetConsentDto[] modelResults = new Gson().fromJson(response, GetConsentDto[].class);
            for (GetConsentDto modelResult : modelResults) {
                consents.add(modelResult);
            }
        }
        return consents;
    }

    @Override
    public int getMethod() {
        return Request.Method.GET;
    }

    @Override
    public String requestBody() {
        return null;
    }

    @Override
    public String getUrl() {
        try {
            return new StringBuilder(url).append(url.endsWith("/") ? "" : "/").append(ConsentAccessToolKit.getInstance().getCatkComponent().getUser().getHsdpUUID())
                    .append("?applicationName=")
                    .append(URLEncoder.encode(mApplicationName, "UTF-8")).append("&propositionName=").append(URLEncoder.encode(mPropositionName, "UTF-8")).toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("incorrect enconding definition");
        }
    }
}
