/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.platform.ecs.microService.request

import android.text.TextUtils
import android.util.Log
import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ServerError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.summary.ECSProductSummary
import com.philips.platform.ecs.microService.model.summary.Summary
import com.philips.platform.ecs.microService.prx.PRXError
import com.philips.platform.ecs.microService.prx.PrxConstants
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError
import com.philips.platform.ecs.microService.util.replaceParam
import org.json.JSONObject
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.mutableListOf
import kotlin.collections.set

class GetProductSummaryRequest(val ecsProducts:List<ECSProduct>, private val ecsCallback: ECSCallback<List<ECSProduct>, ECSError>) : ECSJsonRequest() {

    override fun getServiceID(): String {
        return ECSConstants.SERVICEID_PRX_SUMMARY_LIST
    }

    override fun onErrorResponse(error: VolleyError) {
        var prxError = error.getJsonError()?.getData(PRXError::class.java)
        Log.d("GetProductAsset",prxError.toString())
        val ecsError = ECSError(prxError?.ERROR?.errorMessage ?: "",prxError?.ERROR?.statusCode,null)

        ecsCallback.onFailure(ecsError)
    }

    override fun onResponse(response: JSONObject) {
        val ecsProductSummary = response.getData(ECSProductSummary::class.java)
        if(ecsProductSummary?.success == true) {
            updateProductsWithSummary(ecsProducts, ecsProductSummary)
            ecsCallback.onResponse(ecsProducts)
        }else{
            ecsCallback.onFailure(ECSError(ecsProductSummary?.failureReason?:"",null,null))
        }

    }

    override fun getReplaceURLMap(): MutableMap<String, String> {

        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["sector"] = PrxConstants.Sector.B2C.toString()
        replaceUrl["catalog"] = PrxConstants.Catalog.CONSUMER.toString()
        replaceUrl["ctns"] = getString(getCTNsFromProducts())
        return replaceUrl
    }

    fun getString(ctns: List<String>): String{
        return TextUtils.join(",", ctns)
    }

    private fun getCTNsFromProducts():List<String>{

        var arrayList = mutableListOf<String>()
        for (product in ecsProducts){
            product.id?.let { arrayList.add(it) }
        }
        return arrayList
    }


    private fun updateProductsWithSummary(products: List<ECSProduct>, ecsProductSummary: ECSProductSummary) {
        val summaryCtnMap = HashMap<String, Summary>()
        val productArrayList = ArrayList<ECSProduct>() // set back products for which summaries are available
        if (ecsProductSummary.data!=null) {
            for (summary in ecsProductSummary.data) { //there will be no chance it will be null
                summary.ctn?.let { summaryCtnMap.put(it,summary) }
            }
        }
        for (product in products) {
            val productSummaryData = summaryCtnMap[product.id]
            if (productSummaryData != null) {
                product.summary = productSummaryData
                productArrayList.add(product)
            }
        }
    }
}