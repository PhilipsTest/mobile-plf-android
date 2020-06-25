package com.philips.platform.ecs.microService.request

import android.util.Log
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.model.cart.ECSPILShoppingCart
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class CreateCartRequest(private val ctn: String, private val quantity: Int,private val ecsCallback: ECSCallback<ECSPILShoppingCart, ECSError>)  : ECSJsonAuthRequest(ecsCallback){

    val urlCreateCart = "https://acc.eu-west-1.api.philips.com/commerce-service/cart?siteId=%siteId%&language=%language%&country=%country%&productId=%ctn%&quantity=%quantity%"

    override fun getURL(): String {
        return urlCreateCart.replaceParam(getReplaceURLMap())
    }

    override fun getServiceID(): String {
       return  SERVICEID_ECS_CREATE_CART
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["ctn"] = ctn
        replaceURLMap["quantity"] = ""+quantity
        return replaceURLMap
    }

    override fun getHeader(): MutableMap<String, String>? {
        var headers:MutableMap<String, String>? = super.getHeader()
        headers?.put("Content-Type","application/json")
        return headers
    }
    


    /** Called when a response is received.  */
    override fun onResponse(response: JSONObject) {
        val shoppingCart = response.getData(ECSPILShoppingCart::class.java)
        Log.v("CREATE",shoppingCart.toString() )

        val eCSProductManager = ECSProductManager()
       /*var  productList: List<ECSProduct>  =  shoppingCart.data.attributes.items // PRX part

        productList?.let { getProductsSummary(eCSProductManager, it) }?:kotlin.run {
            val ecsError = ECSError(ECSErrorType.ECSPIL_NOT_FOUND_productId.getLocalizedErrorString(), ECSErrorType.ECSPIL_NOT_FOUND_productId.errorCode, ECSErrorType.ECSPIL_NOT_FOUND_productId)
            ecsCallback.onFailure(ecsError)
        }*/
    }

   /* private fun getProductsSummary(eCSProductManager: ECSProductManager, ecsProducts: ECSProducts) {
        eCSProductManager.fetchProductSummaries(ecsProducts, object : ECSCallback<List<ECSProduct>, ECSError> {
            override fun onResponse(result: List<ECSProduct>) {
                ecsProducts.commerceProducts = result
                ecsCallback.onResponse(ecsProducts)
            }

            override fun onFailure(ecsError: ECSError) {
                ecsCallback.onResponse(ecsProducts)
            }
        })
    }*/
}