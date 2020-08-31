package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_PRODUCTS
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSErrorType
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.util.addQueryParam
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject


class GetProductsRequest(private val productCategory: String?, private val limit: Int, private val offset: Int, private val productFilter: ProductFilter?, private val ecsCallback: ECSCallback<ECSProducts, ECSError>) : ECSJsonRequest(ecsCallback) {

    val limitKey = "limit"
    val offsetKey = "offset"
    val categoryKey = "category"
    val sortKey = "sort"
    val stockLevelKey = "stockLevel"
    val limitDefault = 20   // default limit
    val offsetDefault = 0    // default Offset


    override fun getServiceID(): String {
        return SERVICEID_ECS_PRODUCTS
    }

    override fun getURL(): String {
        return addParamsToURL(url.replaceParam(getReplaceURLMap()))
    }

    private fun addParamsToURL(url: String): String {
        var urlWithParams = url
        var actualLimit: Int = if (limit > 0) limit else limitDefault
        urlWithParams = urlWithParams.addQueryParam(limitKey, actualLimit.toString())

        var actualOffset: Int = if (offset > 0) offset else offsetDefault
        urlWithParams = urlWithParams.addQueryParam(offsetKey, actualOffset.toString())

        productCategory?.let { urlWithParams = urlWithParams.addQueryParam(categoryKey, productCategory.trim()) }

        productFilter?.let {
            productFilter.sortType?.let { urlWithParams = urlWithParams.addQueryParam(sortKey, it.toString()) }

            var commaSeperatedString = productFilter.stockLevelSet?.joinToString { it.toString() }
            commaSeperatedString = commaSeperatedString?.replace("\\s".toRegex(), "")
            if (commaSeperatedString != null)
                productFilter.stockLevelSet.let { urlWithParams = urlWithParams.addQueryParam(stockLevelKey, commaSeperatedString) }
        }

        return urlWithParams
    }

    /** Called when a response is received.  */
    override fun onResponse(response: JSONObject) {
        val productList = response.getData(ECSProducts::class.java)
        val eCSProductManager = ECSProductManager()
        productList?.let { getProductsSummary(eCSProductManager, it) } ?: kotlin.run {
            val ecsError = ECSError(ECSErrorType.ECSPIL_NOT_FOUND_productId.getLocalizedErrorString(), ECSErrorType.ECSPIL_NOT_FOUND_productId.errorCode, ECSErrorType.ECSPIL_NOT_FOUND_productId)
            ecsCallback.onFailure(ecsError)
        }

    }

    private fun getProductsSummary(eCSProductManager: ECSProductManager, ecsProducts: ECSProducts) {
        eCSProductManager.fetchProductSummaries(ecsProducts, object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                ecsCallback.onResponse(result)
            }

            override fun onFailure(ecsError: ECSError) {
                ecsCallback.onResponse(ecsProducts)
            }
        })
    }

}
