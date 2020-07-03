package com.philips.platform.ecs.microService.request

import com.android.volley.Request
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject

class CreateCartRequest(private val ctn: String, private val quantity: Int,private val ecsCallback: ECSCallback<ECSShoppingCart, ECSError>)  : ECSJsonAuthRequest(ecsCallback){

    override fun getURL(): String {
        return url.replaceParam(getReplaceURLMap())
    }

    override fun getServiceID(): String {
       return  SERVICEID_ECS_CREATE_CART
    }

    override fun getRequestMethod(): Int {
        return Request.Method.POST
    }

    override fun getReplaceURLMap(): MutableMap<String, String> {
        val replaceURLMap = super.getReplaceURLMap()
        replaceURLMap["ctn"] = ctn
        replaceURLMap["quantity"] = ""+quantity
        return replaceURLMap
    }

    override fun onResponse(response: JSONObject) {
        val ecsShoppingCart = response.getData(ECSShoppingCart::class.java)
        ecsShoppingCart ?.let { ecsCallback.onResponse(ecsShoppingCart)  } ?: kotlin.run {  ecsCallback.onFailure( ErrorHandler().getECSError(null))}
    }

    override fun getHeader(): MutableMap<String, String>? {
        val header = super.getHeader()
        header?.put("Content-Type","application/json")
        return header
    }
    
}