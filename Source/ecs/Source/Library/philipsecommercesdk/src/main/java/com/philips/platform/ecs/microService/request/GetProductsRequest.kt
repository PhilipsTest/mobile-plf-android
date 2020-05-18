package com.philips.platform.ecs.microService.request

import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_PRODUCTS
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.VolleyHandler
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject


class GetProductsRequest(private val currentPage:Int, private val pageSize:Int, private val  ecsCallback :ECSCallback<ECSProducts, ECSError> ) : ECSJsonRequest() {

    val limit = "limit"
    val offset = "offset"
    
    var url ="https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%&limit=%limit%&offset=%offset%"

    override fun getServiceID(): String {
        return  SERVICEID_ECS_PRODUCTS
    }

    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap.put(limit, pageSize.toString())
        replaceURLMap.put(offset, currentPage.toString())
        return  replaceURLMap

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