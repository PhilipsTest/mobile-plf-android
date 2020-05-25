package com.philips.cdp.prxclient.request;


import android.text.TextUtils;

import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.datamodels.summary.PRXSummaryListResponse;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The type Product summary request.
 */
public class ProductSummaryListRequest extends PrxRequest {

    private static final String PRXSummaryDataServiceID = "prxclient.summarylist";
    private String mRequestTag = null;
    private List<String> ctns;

    /**
     * Instantiates a new Product summary request.
     *
     * @param ctns       product ctns
     * @param sector     sector
     * @param catalog    catalog
     * @param requestTag request tag
     * @since 1.0.0
     */
    public ProductSummaryListRequest(List<String> ctns, PrxConstants.Sector sector,
                                     PrxConstants.Catalog catalog, String requestTag) {
        super(ctns, PRXSummaryDataServiceID, sector, catalog);
        this.ctns = ctns;
        this.mRequestTag = requestTag;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new PRXSummaryListResponse().parseJsonResponseData(jsonObject);
    }

    /**
     * Returns the base prx url from service discovery.
     * @param appInfra AppInfra instance.
     * @param listener callback url received
     * @since 1.0.0
     */
    public void getRequestUrlFromAppInfra(final AppInfraInterface appInfra, final OnUrlReceived listener) {
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("ctns", getString(ctns));
        replaceUrl.put("sector", getSector().toString());
        replaceUrl.put("catalog", getCatalog().toString());

        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(PRXSummaryDataServiceID);
        appInfra.getServiceDiscovery().getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                appInfra.getLogging().log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx SUCCESS Url "+urlMap.get(PRXSummaryDataServiceID).getConfigUrls());
                listener.onSuccess(urlMap.get(PRXSummaryDataServiceID).getConfigUrls());
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                appInfra.getLogging().log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx ERRORVALUES "+ message);
                listener.onError(error, message);
            }
        }, replaceUrl);
    }

    private String getString(List<String> ctns) {
        return TextUtils.join(",", ctns);
    }
}
