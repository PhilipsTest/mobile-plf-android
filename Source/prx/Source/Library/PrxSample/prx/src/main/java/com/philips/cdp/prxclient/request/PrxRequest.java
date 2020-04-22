package com.philips.cdp.prxclient.request;

import com.philips.cdp.prxclient.PrxConstants;
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
 * This is the URL Builder base class to build all the PRX relevant URLs.
 * @since 1.0.0
 */
public abstract class PrxRequest {

    private PrxConstants.Sector mSector;
    private PrxConstants.Catalog mCatalog;
    private int maxRetries = 0;
    private int requestTimeOut = 15000;
    private String mCtn;
    private final String mServiceId;

    public List<String> getCtns() {
        return mCtns;
    }

    public void setCtns(List<String> mCtns) {
        this.mCtns = mCtns;
    }

    private List<String> mCtns;

    /**
     * PRX request constructor.
     * @param ctn CTN of the product
     * @param serviceId PRX ServiceId
     * @since 1.0.0
     */
    public PrxRequest(String ctn, String serviceId) {
        this.mCtn = ctn;
        this.mServiceId = serviceId;
    }

    /**
     * PRX request constructor.
     * @param ctn ctn of the product
     * @param serviceID PRX ServiceId
     * @param sector sector
     * @param catalog catalog
     * @since 1.0.0
     */
    public PrxRequest(String ctn, String serviceID, PrxConstants.Sector sector, PrxConstants.Catalog catalog) {
        this.mCtn = ctn;
        this.mServiceId = serviceID;
        this.mSector = sector;
        this.mCatalog = catalog;
    }

    /**
     * PRX request constructor.
     * @param serviceID PRX ServiceId
     * @param sector sector
     * @param catalog catalog
     * @since 1.0.0
     */
    public PrxRequest(String serviceID, PrxConstants.Sector sector, PrxConstants.Catalog catalog) {
        this.mServiceId = serviceID;
        this.mSector = sector;
        this.mCatalog = catalog;
    }


    /**
     * PRX request constructor.
     * @param ctns ctns of the products
     * @param serviceID PRX ServiceId
     * @param sector sector
     * @param catalog catalog
     * @since 1.0.0
     */
    public PrxRequest(List<String> ctns, String serviceID, PrxConstants.Sector sector, PrxConstants.Catalog catalog) {
        this.mCtns = ctns;
        this.mServiceId = serviceID;
        this.mSector = sector;
        this.mCatalog = catalog;
    }

    /**
     * Get the CTN.
     * @return returns the ctn
     * @since 1.0.0
     */
    public String getCtn() {
        return this.mCtn;
    }

    /**
     * Get the sector.
     * @return returns the sector
     * @since 1.0.0
     */
    public PrxConstants.Sector getSector() {
        return mSector;
    }

    /**
     * Set the sector.
     * @param mSector the type of sector
     * @since 1.0.0
     */
    public void setSector(final PrxConstants.Sector mSector) {
        this.mSector = mSector;
    }

    /**
     * Get the catalog.
     * @return returns the catalog
     */
    public PrxConstants.Catalog getCatalog() {
        return mCatalog;
    }

    /**
     * Set the catalog.
     * @param catalog catalog
     */
    public void setCatalog(PrxConstants.Catalog catalog) {
        this.mCatalog = catalog;
    }

    /**
     * Get the Response data.
     * @param jsonObject JSON Object
     * @return returns the response data
     */
    public abstract ResponseData getResponseData(JSONObject jsonObject);


    /**
     * Returns the base prx url from service discovery.
     * @param appInfra AppInfra instance.
     * @param listener callback url received
     * @since 1.0.0
     */
    public void getRequestUrlFromAppInfra(final AppInfraInterface appInfra, final OnUrlReceived listener) {


        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(mServiceId);
        appInfra.getServiceDiscovery().getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                appInfra.getLogging().log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx SUCCESS Url "+urlMap.get(mServiceId));
                listener.onSuccess(urlMap.get(mServiceId).getConfigUrls());
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                appInfra.getLogging().log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx ERRORVALUES "+ message);
                listener.onError(error, message);
            }
        },getReplaceURLMap());
    }

    /**
     *
     * @return map of values which is replaced on URL query
     */
    public Map<String, String> getReplaceURLMap(){
        Map<String, String> replaceUrl = new HashMap<>();
        replaceUrl.put("ctn", mCtn);
        replaceUrl.put("sector", getSector().toString());
        replaceUrl.put("catalog", getCatalog().toString());
        return replaceUrl;
    }


    /**
     * Interface which gives callback on Url Received.

     * @since 1.0.0
     */
    public interface OnUrlReceived extends ServiceDiscoveryInterface.OnErrorListener {
        void onSuccess(String url);
    }

    /**
     * returns request type.
     * @return request type for ex . GET/POST/PUT.
     * @since 1.0.0
     */
    public int getRequestType() {
        return RequestType.GET.getValue();
    }

    /**
     * Get the headers.
     * @return headers
     * @since 1.0.0
     */
    public Map<String, String> getHeaders() {
        return null;
    }

    /**
     *  Get the parameters.
     * @return params
     * @since 1.0.0
     */
    public Map<String, String> getParams() {
        return null;
    }

    public  String getBody(){
        return  null;
    }


    /**
     * Get Max num of retries.
     *
     * @return Max num of retries
     * @since 1.0.0
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Set the maximum number of retries.
     * @param maxRetries - Set maximum number of retries when request failed
     */
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Get request time out in milli seconds.
     *
     * @return timeout.
     * @since 1.0.0
     */
    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    /**
     * Set the request timeout.
     * @param requestTimeOut - Set request time out in milli seconds
     * @since 1.0.0
     */
    public void setRequestTimeOut(final int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }
}
