package com.philips.cdp.prxclient.request

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.Disclaimer.DisclaimerModel
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject

/**
 * The type Product disclaimer request.
 */
class ProductDisclaimerRequest : PrxRequest {
    private var mRequestTag: String? = null

    /**
     * Instantiates a new Product disclaimer request.
     * @since 1805
     * @param ctn         product ctn
     * @param requestTag  requestTag
     */
    constructor(ctn: String?, requestTag: String?) : super(ctn, PRXDisclaimerDataServiceID) {
        mRequestTag = requestTag
    }

    /**
     * Instantiates a new Product disclaimer request.
     * @since 1805
     * @param ctn         product ctn
     * @param sector      sector
     * @param catalog     catalog
     * @param requestTag  request tag
     */
    constructor(ctn: String?, sector: PrxConstants.Sector?,
                catalog: PrxConstants.Catalog?, requestTag: String?) : super(ctn, PRXDisclaimerDataServiceID, sector, catalog) {
        mRequestTag = requestTag
    }

    override fun getResponseData(jsonObject: JSONObject?): ResponseData? {
        return DisclaimerModel().parseJsonResponseData(jsonObject)
    }

    companion object {
        private const val PRXDisclaimerDataServiceID = "prxclient.disclaimers"
    }
}