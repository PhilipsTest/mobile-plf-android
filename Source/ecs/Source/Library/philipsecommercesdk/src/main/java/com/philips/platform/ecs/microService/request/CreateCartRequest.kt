package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.model.cart.ECSPILShoppingCart
import com.philips.platform.ecs.microService.model.product.ECSProduct
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



    /** Called when a response is received.  */
    override fun onResponse(response: JSONObject) {
        val ecsShoppingCart = response.getData(ECSPILShoppingCart::class.java)
        
        ecsShoppingCart ?.let { getCartProductDetails(ecsShoppingCart)  } ?: kotlin.run {  ecsCallback.onFailure( ErrorHandler().getECSError(null))}

    }

    private fun getCartProductDetails(ecsShoppingCart: ECSPILShoppingCart) {
        for (item in ecsShoppingCart.data.attributes.items){
            ECSProductManager().getProductFor(item.id,object : ECSCallback<ECSProduct?, ECSError>{
                override fun onResponse(result: ECSProduct?) {
                    TODO("Not yet implemented")
                }

                override fun onFailure(ecsError: ECSError) {
                    TODO("Not yet implemented")
                }
            })
        }

        //once all products are fetched for ID , give back success callback
        ecsCallback.onResponse(ecsShoppingCart)
    }
}