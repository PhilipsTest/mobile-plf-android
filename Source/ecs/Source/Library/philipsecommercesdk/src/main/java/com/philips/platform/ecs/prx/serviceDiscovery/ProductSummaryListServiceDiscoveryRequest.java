/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.prx.serviceDiscovery;


import android.text.TextUtils;

import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The type Product summary request.
 */
public class ProductSummaryListServiceDiscoveryRequest extends ServiceDiscoveryRequest {

    private static final String PRXSummaryDataServiceID = "prxclient.summarylist";
    private List<String> ctns;

    public ProductSummaryListServiceDiscoveryRequest(List<String> ctns) {
        super(ctns, PRXSummaryDataServiceID);
        this.ctns = ctns;
    }

    public void getRequestUrlFromAppInfra(final OnUrlReceived listener) {
        Map<String, String> replaceUrl = getReplaceURLMap();

        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(PRXSummaryDataServiceID);
        ECSConfiguration.INSTANCE.getAppInfra().getServiceDiscovery().getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                listener.onSuccess(urlMap.get(PRXSummaryDataServiceID).getConfigUrls());
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                listener.onError(error, message);
            }
        }, replaceUrl);
    }

    @NotNull
    public Map<String, String> getReplaceURLMap() {
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("ctns", getString(ctns));
        replaceUrl.put("sector", getSector().toString());
        replaceUrl.put("catalog", getCatalog().toString());
        return replaceUrl;
    }


    private String getString(List<String> ctns) {
        return TextUtils.join(",", ctns);
    }
}
