package com.philips.cdp.prxclient.request

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.assets.AssetModel
import com.philips.cdp.prxclient.response.ResponseData
import org.json.JSONObject

/**
 * The type Product asset request.
 */
class ProductAssetRequest : PrxRequest {
    private var mRequestTag: String? = null

    /**
     * Instantiates a new Product asset request.
     * @since  1.0.0
     * @param ctn        product ctn
     * @param requestTag requestTag
     */
    constructor(ctn: String?, requestTag: String?) : super(ctn, PRXAssetAssetServiceID) {
        mRequestTag = requestTag
    }

    /**
     * Instantiates a new Product asset request.
     * @since 1.0.0
     * @param ctn         product ctn
     * @param sector      sector
     * @param catalog     catalog
     * @param requestTag  request tag
     */
    constructor(ctn: String?, sector: PrxConstants.Sector?, catalog: PrxConstants.Catalog?, requestTag: String?) : super(ctn, PRXAssetAssetServiceID, sector, catalog) {
        mRequestTag = requestTag
    }

    override fun getResponseData(jsonObject: JSONObject?): ResponseData? {
        return AssetModel().parseJsonResponseData(jsonObject)
    }

    companion object {
        private const val PRXAssetAssetServiceID = "prxclient.assets"
    }
}