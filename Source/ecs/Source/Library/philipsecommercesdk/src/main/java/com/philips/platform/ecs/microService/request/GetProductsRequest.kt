package com.philips.platform.ecs.microService.request

import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_PRODUCTS
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.VolleyHandler
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.util.addQueryParam
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject


class GetProductsRequest(private val  productCategory:String?, private val limit:Int, private val offset:Int, private val productFilter: ProductFilter, private val  ecsCallback :ECSCallback<ECSProducts, ECSError> ) : ECSJsonRequest() {

    val limitKey = "limit"
    val offsetKey = "offset"
    val categoryKey= "category"
    val sortKey ="sort"
    val stockLevelKey ="stockLevel"
    val modifiedSinceKey = "modifiedSince"
    val limitThreshold =50 // limit threshhold as PRX supports only 5o product detaiil at a time
    
    var url ="https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%&limit=%limit%&offset=%offset%"

    override fun getServiceID(): String {
        return  SERVICEID_ECS_PRODUCTS
    }

    override fun getURL(): String {
        return addParamsToURL(url.replaceParam(getReplaceURLMap()))
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        var actualLimit :Int= if (limit<limitThreshold) limit else limitThreshold
        replaceURLMap.put(limitKey, offset.toString())
        replaceURLMap.put(offsetKey, actualLimit.toString())
        return  replaceURLMap

    }

    private fun addParamsToURL(url: String): String{
        var  urlWithParams= url
        productCategory?.let { urlWithParams = urlWithParams.addQueryParam(categoryKey, productCategory.trim()) }
        productFilter?.let{
            productFilter.sortType?.let{urlWithParams=urlWithParams.addQueryParam(sortKey, productFilter.sortType.toString())}
            productFilter.stockLevel?.let{urlWithParams=urlWithParams.addQueryParam(stockLevelKey, productFilter.stockLevel.toString())}
            productFilter.modifiedSince?.let{urlWithParams=urlWithParams.addQueryParam(modifiedSinceKey, productFilter.modifiedSince.toString())}
        }
        return urlWithParams
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     */
    override fun onErrorResponse(error: VolleyError?) {
        ecsCallback.onFailure(VolleyHandler().getECSError(error))
    }

    /** Called when a response is received.  */
    override fun onResponse(response: JSONObject) {
        val productList = response.getData(ECSProducts::class.java)
        productList?.let { ecsCallback.onResponse(it) }
    }


}

//https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=DE_Pub&language=de&country=DE&query=::category:FOOD_PREPARATION_CA2'
//public void fetchProducts(String productCategory, int limit, int offset, ProductFilters productFilters, @NonNull ECSCallback<ECSProducts, Exception> eCSCallback)