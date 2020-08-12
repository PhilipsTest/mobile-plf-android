package com.philips.cdp.prxclient.request

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.specification.SpecificationModel
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject

/**
 * The type Product summary request.
 */
class ProductSpecificationRequest : PrxRequest {
    private var mRequestTag: String? = null

    /**
     * Instantiates a new Product summary request.
     * @since 1.0.0
     * @param ctn        product ctn
     * @param requestTag requestTag
     */
    constructor(ctn: String?, requestTag: String?) : super(ctn, PRXSummaryDataServiceID) {
        mRequestTag = requestTag
    }

    /**
     * Instantiates a new Product summary request.
     * @since 1.0.0
     * @param ctn         product ctn
     * @param sector      sector
     * @param catalog     catalog
     * @param requestTag  request tag
     */
    constructor(ctn: String?, sector: PrxConstants.Sector?,
                catalog: PrxConstants.Catalog?, requestTag: String?) : super(ctn, PRXSummaryDataServiceID, sector, catalog) {
        mRequestTag = requestTag
    }

    override fun getResponseData(jsonObject: JSONObject?): ResponseData? {
        return SpecificationModel().parseJsonResponseData(jsonObject)
    }

    companion object {
        private const val PRXSummaryDataServiceID = "prxclient.specification"
    }
}