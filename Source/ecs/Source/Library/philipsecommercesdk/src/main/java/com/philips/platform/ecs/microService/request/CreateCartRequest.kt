package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class CreateCartRequest(private val ctn: String, private val quantity: Int,private val ecsCallback: ECSCallback<ECSShoppingCart, ECSError>)  : ECSJsonAuthRequest(ecsCallback){

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



    /** Called when a response is received.  */
    override fun onResponse(response: JSONObject) {
        val ecsShoppingCart = response.getData(ECSShoppingCart::class.java)
        
        ecsShoppingCart ?.let { ecsCallback.onResponse(ecsShoppingCart)  } ?: kotlin.run {  ecsCallback.onFailure( ErrorHandler().getECSError(null))}

    }

    
}