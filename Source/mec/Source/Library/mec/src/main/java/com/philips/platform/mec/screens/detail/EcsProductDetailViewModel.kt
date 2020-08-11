/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import androidx.lifecycle.MutableLiveData
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ContextDataValue
import com.bazaarvoice.bvandroidsdk.Review
import com.bazaarvoice.bvandroidsdk.ReviewResponse
import com.google.gson.internal.LinkedTreeMap
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.microService.model.asset.Asset
import com.philips.platform.ecs.microService.model.asset.Assets
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.retailer.ECSRetailer
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.model.cart.ECSShoppingCart

import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.screens.detail.MECProductDetailsFragment.Companion.tagOutOfStockActions
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label
import java.util.*

class EcsProductDetailViewModel : com.philips.platform.mec.common.CommonViewModel() {

    var ecsProduct = MutableLiveData<ECSProduct>()

    lateinit var ecsProductAsParamter : ECSProduct
    lateinit var  addToProductCallBack : ECSCallback<ECSShoppingCart, Exception>

    val bulkRatingResponse= MutableLiveData<BulkRatingsResponse>()

    val review = MutableLiveData<ReviewResponse>()

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var ecsProductDetailRepository = ECSProductDetailRepository(this,ecsServices)

    fun getRatings(ctn :String){
        ecsProductDetailRepository.getRatings(ctn)
    }

    fun getProductDetail(ecsProduct: ECSProduct){
        ecsProductDetailRepository.getProductDetail(ecsProduct)
    }

    fun getBazaarVoiceReview(ctn : String, pageNumber : Int, pageSize : Int){
        ecsProductDetailRepository.fetchProductReview(ctn, pageNumber, pageSize)
    }

    fun addProductToShoppingcart(ecsProduct: ECSProduct, addToProductCallback  : ECSCallback<ECSShoppingCart, Exception>){
        ecsProductAsParamter=ecsProduct
        addToProductCallBack=addToProductCallback
        ecsProductDetailRepository.addTocart(ecsProductAsParamter)
    }


    override fun authFailureCallback(error: Exception?, ecsError: ECSError?){
        MECLog.v("Auth","refresh auth failed");
        addToProductCallBack.onFailure(error,ecsError)
    }

    fun retryAPI(mECRequestType : MECRequestType) {
        var retryAPI = { addProductToShoppingcart(ecsProductAsParamter,addToProductCallBack) }
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    fun createShoppingCart() {
        val createShoppingCartCallback=  object: ECSCallback<ECSShoppingCart, Exception> {
            override fun onResponse(result: ECSShoppingCart?) {
                addProductToShoppingcart(ecsProductAsParamter,addToProductCallBack)
            }
            override fun onFailure(error: Exception?, ecsError: ECSError?) {
               // todo create cart must NOT fail
            }
        }
        ecsProductDetailRepository.createCart(createShoppingCartCallback)
    }

    fun getValueFor(type: String, review: Review): String {
        var  reviewValue :String? = null
        var mapAdditionalFields: LinkedTreeMap<String, String>? = null
        if (review.additionalFields != null && review.additionalFields.get(type)!=null && review.additionalFields.size > 0 ) {
            mapAdditionalFields = review.additionalFields.get(type) as LinkedTreeMap<String, String>
            reviewValue= if (mapAdditionalFields.get("Value") != null) mapAdditionalFields?.get("Value") else ""
        }
        if (reviewValue == null) {
            reviewValue=""
            if (review.tagDimensions != null && review.tagDimensions!!.size > 0) {
                val tagD = review.tagDimensions?.get(type.substring(0,type.length-1))
               var list : MutableList<String>? = tagD?.values
                reviewValue = list?.joinToString(
                        prefix = "",
                        separator = ", ",
                        postfix = ""
                       )

            }
        }
        return reviewValue.toString()
    }


     fun getValueForUseDuration( review: Review): String {
        var useDurationValue: String? = ""
        if (review.contextDataValues != null && review.contextDataValues!!.size>0 && (null!=review.contextDataValues!!.get("HowLongHaveYouBeenUsingThisProduct"))) {
            val mapUseDuration: ContextDataValue = review.contextDataValues!!.get("HowLongHaveYouBeenUsingThisProduct") as ContextDataValue
            useDurationValue=mapUseDuration.valueLabel
        }
        return useDurationValue.toString()
    }

    fun removeBlacklistedRetailers(ecsRetailers: ECSRetailerList): ECSRetailerList {
        val list = MECDataHolder.INSTANCE.blackListedRetailers

        if (list != null) {
            for (name in list) {

                val iterator = ecsRetailers.getRetailers()?.toMutableList()?.iterator()

                while (iterator?.hasNext() == true) {

                    val retailerName = iterator.next().name?.replace("\\s+".toRegex(), "")
                    if (name.equals(retailerName, true)) {

                        if (MECutility.indexOfSubString(true, retailerName, name) >= 0) {
                            iterator.remove()

                        }
                    }
                }

            }
        }

        return ecsRetailers
    }

    fun uuidWithSupplierLink(buyURL: String ,param :String): String {

        val propositionId = MECDataHolder.INSTANCE.propositionId

        val supplierLinkWithUUID = "$buyURL&wtbSource=mobile_$propositionId&$param="

        return supplierLinkWithUUID + UUID.randomUUID().toString()
    }

    fun isPhilipsShop(retailer: ECSRetailer): Boolean {
        return retailer.isPhilipsStore.equals("Y", ignoreCase = true)
    }

    fun setStockInfoWithRetailer(stockLabel : Label, product: ECSProduct?, ecsRetailers: ECSRetailerList) {
            if(!MECDataHolder.INSTANCE.hybrisEnabled) {
                if (ecsRetailers.getRetailers()?.size ?:0 >0) {
                    var availability=false
                    for (i in 0..(ecsRetailers.getRetailers()?.size ?:0)) {
                        if(ecsRetailers.getRetailers()?.get(i)?.availability?.contains("YES") == true){
                            availability=true
                            break
                        }
                    }
                    if (availability) {
                        stockLabel.text = stockLabel.context.getString(R.string.mec_in_stock)
                        stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_green_level_30))
                    } else {
                        stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                        stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                        product?.let { tagOutOfStockActions(it) }
                    }

                } else if (ecsRetailers.getRetailers()?.size ?:0 ==0) {
                    stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                    stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                    product?.let { tagOutOfStockActions(it) }
                }
            }
            else if(MECDataHolder.INSTANCE.hybrisEnabled){
                if(ecsRetailers.getRetailers()?.size==0) {
                    if (null != product && null != product.attributes?.availability) {
                        if (MECutility.isStockAvailable(product.attributes?.availability?.status, product.attributes?.availability?.quantity ?:0)) {
                            stockLabel.text = stockLabel.context.getString(R.string.mec_in_stock)
                            stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_green_level_30))
                            // stockLabel.setTextColor(R.attr.uidContentItemSignalNormalTextSuccessColor)
                        } else {
                            stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                            stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                            // stockLabel.setTextColor(R.attr.uidContentItemSignalNormalTextErrorColor)
                            tagOutOfStockActions(product)
                        }
                    }
                }

             else if (ecsRetailers.getRetailers()?.size ?:0 >0) {
                    var availability=false
                    for (i in 0..(ecsRetailers.getRetailers()?.size ?:0)) {
                        if(ecsRetailers.getRetailers()?.get(i)?.availability?.contains("YES") == true){
                            availability=true
                            break
                        } else if(ecsRetailers.getRetailers()?.get(i)?.availability?.contains("NO") == true) {
                            availability=false
                            if (!availability) {
                                if (null != product?.attributes?.availability) {
                                    if (MECutility.isStockAvailable(product.attributes?.availability?.status, product.attributes?.availability?.quantity ?:0)) {
                                        stockLabel.text = stockLabel.context.getString(R.string.mec_in_stock)
                                        stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_green_level_30))
                                    } else {
                                        stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                                        stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                                        product?.let { tagOutOfStockActions(it) }
                                    }
                                }
                            }
                        }
                    }
                    if (availability) {
                        stockLabel.text = stockLabel.context.getString(R.string.mec_in_stock)
                        stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_green_level_30))
                    } else {
                        stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                        stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                        product?.let { tagOutOfStockActions(it) }
                }
            }  else  {
                stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                    product?.let { tagOutOfStockActions(it) }
            }
        }
    }

    fun addNoAsset(product: ECSProduct) {
        var asset = Asset(null,null,null,null,null,null, null,"APP", "NO Image Asset Found")
        var assets = Assets()
        assets.asset = Arrays.asList(asset)
        product.assets = assets

    }

}