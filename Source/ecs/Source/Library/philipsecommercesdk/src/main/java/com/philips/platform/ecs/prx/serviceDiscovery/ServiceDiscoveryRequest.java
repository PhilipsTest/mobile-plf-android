/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.prx.serviceDiscovery;

import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the URL Builder base class to build all the PRX relevant URLs.
 * @since 1.0.0
 */
public abstract class ServiceDiscoveryRequest {

    private String mCtn;
    private final String mServiceId;

    private List<String> mCtns;


    public ServiceDiscoveryRequest(String ctn, String serviceID) {
        this.mCtn = ctn;
        this.mServiceId = serviceID;
    }

    public ServiceDiscoveryRequest(List<String> mCtns, String serviceID) {
        this.mCtns = mCtns;
        this.mServiceId = serviceID;
    }


    public String getCtn() {
        return this.mCtn;
    }


    public PrxConstants.Sector getSector() {
        return PrxConstants.Sector.B2C;
    }

    public PrxConstants.Catalog getCatalog() {
        return PrxConstants.Catalog.CONSUMER;
    }


    public void getRequestUrlFromAppInfra(final OnUrlReceived listener) {
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("ctn", mCtn);
        replaceUrl.put("sector", getSector().toString());
        replaceUrl.put("catalog", getCatalog().toString());

        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(mServiceId);
        ECSConfiguration.INSTANCE.getAppInfra().getServiceDiscovery().getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                listener.onSuccess(urlMap.get(mServiceId).getConfigUrls());
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                listener.onError(error, message);
            }
        },replaceUrl);
    }


    public interface OnUrlReceived extends ServiceDiscoveryInterface.OnErrorListener {
        void onSuccess(String url);
    }

}
