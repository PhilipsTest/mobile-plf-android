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

import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.datamodels.summary.Summary
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.model.summary.ECSProductSummary
import com.philips.platform.ecs.microService.util.getData
import org.json.JSONObject
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.mutableListOf
import kotlin.collections.set

class GetSummariesForProductsRequest(val ecsProducts:List<ECSProduct>, private val ecsCallback:ECSCallback<ECSProducts, ECSError>) : ECSJsonRequest(ecsCallback) {

    override fun getServiceID(): String {
        return ECSConstants.SERVICEID_PRX_SUMMARY_LIST
    }

    override fun onResponse(response: JSONObject) {
        val ecsProductSummary = response.getData(com.philips.platform.ecs.microService.model.summary.ECSProductSummary::class.java)
        if(ecsProductSummary?.success == true) {
            updateProductsWithSummary(ecsProducts, ecsProductSummary)
            ecsCallback.onResponse(ECSProducts(ecsProducts))
        }else{
            var ecsError : ECSError= ECSError(ecsProductSummary?.failureReason?:"",null,null)
            ecsCallback.onFailure(ecsError)
        }

    }

    override fun getReplaceURLMap(): MutableMap<String, String> {

        val replaceUrl: MutableMap<String, String> = HashMap()
        replaceUrl["sector"] = PrxConstants.Sector.B2C.toString()
        replaceUrl["catalog"] = PrxConstants.Catalog.CONSUMER.toString()
        replaceUrl["ctns"] = getCTNsFromProducts().joinToString(",")
        return replaceUrl
    }

    override fun getHeader(): MutableMap<String, String>? {
        return null
    }

    private fun getCTNsFromProducts():List<String>{
        var arrayList = mutableListOf<String>()
        for (product in ecsProducts){
            product.ctn.let { arrayList.add(it) }
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
            val productSummaryData = summaryCtnMap[product.ctn]
            if (productSummaryData != null) {
                product.summary = productSummaryData
                productArrayList.add(product)
            }
        }
    }
}