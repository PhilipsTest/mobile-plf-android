package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_PRODUCTS
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.util.addQueryParam
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject


class GetProductsRequest  (private val productCategory: String?, private val limit: Int, private val offset: Int, private val productFilter: ProductFilter?, private val ecsCallback: ECSCallback<ECSProducts, ECSError>) : ECSJsonRequest(ecsCallback) {

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
            productFilter.sortType?.let { urlWithParams = urlWithParams.addQueryParam(sortKey, productFilter.sortType.toString()) }
            productFilter.stockLevel?.let { urlWithParams = urlWithParams.addQueryParam(stockLevelKey, productFilter.stockLevel.toString()) }
           }
        return urlWithParams
    }

    /** Called when a response is received.  */
    override fun onResponse(response: JSONObject) {
        val productList = response.getData(ECSProducts::class.java)
        productList?.let { ecsCallback.onResponse(it) }
    }

}

//https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=DE_Pub&language=de&country=DE&query=::category:FOOD_PREPARATION_CA2'
//public void fetchProducts(String productCategory, int limit, int offset, ProductFilters productFilters, @NonNull ECSCallback<ECSProducts, Exception> eCSCallback)