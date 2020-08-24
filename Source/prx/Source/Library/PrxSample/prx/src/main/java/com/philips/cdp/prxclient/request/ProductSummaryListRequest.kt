package com.philips.cdp.prxclient.request

import android.text.TextUtils
import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.summary.PRXSummaryListResponse
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlMapListener
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import org.json.JSONObject
import java.util.*

/**
 * The type Product summary request.
 */
class ProductSummaryListRequest(override var ctns: List<String?>?, sector: PrxConstants.Sector?,
                                catalog: PrxConstants.Catalog?, requestTag: String?) : PrxRequest(ctns, PRXSummaryDataServiceID, sector, catalog) {
    override fun getResponseData(jsonObject: JSONObject?): ResponseData? {
        return PRXSummaryListResponse().parseJsonResponseData(jsonObject)
    }

    /**
     * Returns the base prx url from service discovery.
     * @param appInfra AppInfra instance.
     * @param listener callback url received
     * @since 1.0.0
     */
    override fun getRequestUrlFromAppInfra(appInfra: AppInfraInterface?, listener: OnUrlReceived) {
        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["ctns"] = getString(ctns)
        replaceUrl["sector"] = sector.toString()
        replaceUrl["catalog"] = catalog.toString()
        val serviceIDList = ArrayList<String>()
        serviceIDList.add(PRXSummaryDataServiceID)

        appInfra?.let {
            it.serviceDiscovery.getServicesWithCountryPreference(serviceIDList, object : OnGetServiceUrlMapListener {
                override fun onSuccess(urlMap: Map<String, ServiceDiscoveryService>) {
                    it.logging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx SUCCESS Url " + urlMap[PRXSummaryDataServiceID]!!.configUrls)
                    listener.onSuccess(urlMap[PRXSummaryDataServiceID]!!.configUrls)
                }

                override fun onError(error: ERRORVALUES, message: String) {
                    it.logging.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_REQUEST_MANAGER, "prx ERRORVALUES $message")
                    listener.onError(error, message)
                }
            }, replaceUrl)
        }
    }

    private fun getString(ctns: List<String?>?): String {
        return TextUtils.join(",", ctns!!)
    }

    companion object {
        private const val PRXSummaryDataServiceID = "prxclient.summarylist"
    }


}