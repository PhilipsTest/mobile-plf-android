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

package com.philips.cdp.prxclient.request;


import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.datamodels.cdls.CDLSDataModel;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The type Product CDLS request.
 */
public class CDLSRequest extends PrxRequest {

    private static final String PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID = "cc.cdls";
    private String mRequestTag = null;
    private String mProductCategory;

    /**
     * Instantiates a new Product summary request.
     * @since 2003.0
     * @param productCategory product Category
     */
    public CDLSRequest(String productCategory) {
        super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, PrxConstants.Sector.B2C, PrxConstants.Catalog.CARE);
        mProductCategory = productCategory;
    }

    /**
     * Instantiates a new Product summary request.
     * @since 2003.0
     * @param productCategory         product Category
     * @param sector      sector
     * @param catalog     catalog
     * @param requestTag  request tag
     */
    public CDLSRequest(String productCategory, PrxConstants.Sector sector, PrxConstants.Catalog catalog, String requestTag) {
        super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, sector, catalog);
        this.mRequestTag = requestTag;
        mProductCategory = productCategory;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new CDLSDataModel().parseJsonResponseData(jsonObject);
    }

    public void getRequestUrlFromAppInfra(final AppInfraInterface appInfra, final OnUrlReceived listener) {


        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID);
        appInfra.getServiceDiscovery().getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                appInfra.getLogging().log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx SUCCESS Url "+urlMap.get(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID));
                listener.onSuccess(urlMap.get(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID).getConfigUrls());
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                appInfra.getLogging().log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx ERRORVALUES "+ message);
                listener.onError(error, message);
            }
        },getReplaceURLMap());
    }



    public Map<String, String> getReplaceURLMap() {
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("productCategory", mProductCategory);
        replaceUrl.put("productSector",getSector().toString());
        replaceUrl.put("productCatalog",getCatalog().toString());
        return replaceUrl;
    }
}
