package com.philips.cdp.prxclient.request

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlMapListener
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import org.json.JSONObject
import java.util.*

/**
 * This is the URL Builder base class to build all the PRX relevant URLs.
 * @since 1.0.0
 */
abstract class PrxRequest {
    /**
     * Get the sector.
     * @return returns the sector
     * @since 1.0.0
     */
    /**
     * Set the sector.
     * @param mSector the type of sector
     * @since 1.0.0
     */
    open var sector: PrxConstants.Sector? = null
    /**
     * Get the catalog.
     * @return returns the catalog
     */
    /**
     * Set the catalog.
     * @param catalog catalog
     */
    open var catalog: PrxConstants.Catalog? = null
    /**
     * Get Max num of retries.
     *
     * @return Max num of retries
     * @since 1.0.0
     */
    /**
     * Set the maximum number of retries.
     * @param maxRetries - Set maximum number of retries when request failed
     */
    var maxRetries = 0
    /**
     * Get request time out in milli seconds.
     *
     * @return timeout.
     * @since 1.0.0
     */
    /**
     * Set the request timeout.
     * @param requestTimeOut - Set request time out in milli seconds
     * @since 1.0.0
     */
    open var requestTimeOut = 15000

    /**
     * Get the CTN.
     * @return returns the ctn
     * @since 1.0.0
     */
    var ctn: String? = null
        private set
    private val mServiceId: String

    open var ctns: List<String?>? = null

    /**
     * PRX request constructor.
     * @param ctn CTN of the product
     * @param serviceId PRX ServiceId
     * @since 1.0.0
     */
    constructor(ctn: String?, serviceId: String) {
        this.ctn = ctn
        mServiceId = serviceId
    }

    /**
     * PRX request constructor.
     * @param ctn ctn of the product
     * @param serviceID PRX ServiceId
     * @param sector sector
     * @param catalog catalog
     * @since 1.0.0
     */
    constructor(ctn: String?, serviceID: String, sector: PrxConstants.Sector?, catalog: PrxConstants.Catalog?) {
        this.ctn = ctn
        mServiceId = serviceID
        this.sector = sector
        this.catalog = catalog
    }

    /**
     * PRX request constructor.
     * @param ctns ctns of the products
     * @param serviceID PRX ServiceId
     * @param sector sector
     * @param catalog catalog
     * @since 1.0.0
     */
    constructor(ctns: List<String?>?, serviceID: String, sector: PrxConstants.Sector?, catalog: PrxConstants.Catalog?) {
        this.ctns = ctns
        mServiceId = serviceID
        this.sector = sector
        this.catalog = catalog
    }

    /**
     * PRX request constructor.
     * @param serviceID PRX ServiceId
     * @param sector sector
     * @param catalog catalog
     * @since 2003
     */
    constructor(serviceID: String, sector: PrxConstants.Sector?, catalog: PrxConstants.Catalog?) {
        mServiceId = serviceID
        this.sector = sector
        this.catalog = catalog
    }

    /**
     * Get the Response data.
     * @param jsonObject JSON Object
     * @return returns the response data
     */
    abstract fun getResponseData(jsonObject: JSONObject?): ResponseData?

    /**
     * Returns the base prx url from service discovery.
     * @param appInfra AppInfra instance.
     * @param listener callback url received
     * @since 1.0.0
     */
    open fun getRequestUrlFromAppInfra(appInfra: AppInfraInterface?, listener: OnUrlReceived) {
        val replaceUrl: MutableMap<String, String?> = HashMap()
        replaceUrl["ctn"] = ctn
        replaceUrl["sector"] = sector.toString()
        replaceUrl["catalog"] = catalog.toString()
        // replaceUrl.put("locale", locale);
        val serviceIDList = ArrayList<String>()
        serviceIDList.add(mServiceId)
        appInfra!!.serviceDiscovery.getServicesWithCountryPreference(serviceIDList, object : OnGetServiceUrlMapListener {
            override fun onSuccess(urlMap: Map<String, ServiceDiscoveryService>) {
                appInfra.logging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx SUCCESS Url " + urlMap[mServiceId])
                listener.onSuccess(urlMap[mServiceId]!!.configUrls)
            }

            override fun onError(error: ERRORVALUES, message: String) {
                appInfra.logging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx ERRORVALUES $message")
                listener.onError(error, message)
            }
        }, replaceUrl)
    }

    /**
     * Interface which gives callback on Url Received.
     *
     * @since 1.0.0
     */
    interface OnUrlReceived : ServiceDiscoveryInterface.OnErrorListener {
        fun onSuccess(url: String?)
    }

    /**
     * returns request type.
     * @return request type for ex . GET/POST/PUT.
     * @since 1.0.0
     */
    open val requestType: Int
        get() = RequestType.GET.value

    /**
     * Get the headers.
     * @return headers
     * @since 1.0.0
     */
    open val headers: Map<String?, String?>?
        get() = null

    /**
     * Get the parameters.
     * @return params
     * @since 1.0.0
     */
    open val params: Map<String?, String?>?
        get() = null

    open val body: String?
        get() = null

}