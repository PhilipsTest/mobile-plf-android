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
package com.philips.cdp.prxclient.request

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.cdls.CDLSDataModel
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlMapListener
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import org.json.JSONObject
import java.util.*

/**
 * The type Product CDLS request.
 */
class CDLSRequest : PrxRequest {
    private var mRequestTag: String? = null
    private var mProductCategory: String

    /**
     * Instantiates a new CDLS request.
     * @since 2003.0
     * @param productCategory : category of the product
     */
    constructor(productCategory: String) : super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, PrxConstants.Sector.B2C, PrxConstants.Catalog.CARE) {
        mProductCategory = productCategory
    }

    /**
     * Instantiates a new Product CDLS request.
     * @since 2003.0
     * @param productCategory         product Category
     * @param sector      sector
     * @param catalog     catalog
     * @param requestTag  request tag
     */
    constructor(productCategory: String, sector: PrxConstants.Sector?, catalog: PrxConstants.Catalog?, requestTag: String?) : super(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID, sector, catalog) {
        mRequestTag = requestTag
        mProductCategory = productCategory
    }

    override fun getResponseData(jsonObject: JSONObject?): ResponseData? {
        return CDLSDataModel().parseJsonResponseData(jsonObject)
    }

    override fun getRequestUrlFromAppInfra(appInfra: AppInfraInterface?, listener: OnUrlReceived) {
        val serviceIDList = ArrayList<String>()
        serviceIDList.add(PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID)
        appInfra!!.serviceDiscovery.getServicesWithCountryPreference(serviceIDList, object : OnGetServiceUrlMapListener {
            override fun onSuccess(urlMap: Map<String, ServiceDiscoveryService>) {
                appInfra.logging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx SUCCESS Url " + urlMap[PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID])
                listener.onSuccess(urlMap[PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID]!!.configUrls)
            }

            override fun onError(error: ERRORVALUES, message: String) {
                appInfra.logging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx ERRORVALUES $message")
                listener.onError(error, message)
            }
        }, replaceURLMap)
    }

    val replaceURLMap: Map<String, String>
        get() {
            val replaceUrl: MutableMap<String, String> = HashMap()
            replaceUrl["productCategory"] = mProductCategory
            replaceUrl["productSector"] = sector.toString()
            replaceUrl["productCatalog"] = catalog.toString()
            return replaceUrl
        }

    companion object {
        private const val PRX_CONSUMER_CARE_DIGITAL_SERVICE_ID = "cc.cdls"
    }
}