/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ContextDataValue
import com.bazaarvoice.bvandroidsdk.Review
import com.bazaarvoice.bvandroidsdk.ReviewResponse
import com.google.gson.internal.LinkedTreeMap
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.asset.Asset
import com.philips.platform.ecs.model.asset.Assets
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.model.retailers.ECSRetailer
import com.philips.platform.ecs.model.retailers.ECSRetailerList
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.screens.detail.MECProductDetailsFragment.Companion.tagOutOfStockActions
import com.philips.platform.mec.screens.reviews.MECReview
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label
import com.philips.platform.uid.view.widget.ProgressBar
import java.util.*

class EcsProductDetailViewModel : com.philips.platform.mec.common.CommonViewModel() {

    var ecsProduct = MutableLiveData<com.philips.platform.ecs.model.products.ECSProduct>()

    lateinit var ecsProductAsParamter : com.philips.platform.ecs.model.products.ECSProduct
    lateinit var  addToProductCallBack : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.cart.ECSShoppingCart, Exception>

    val bulkRatingResponse= MutableLiveData<BulkRatingsResponse>()

    val review = MutableLiveData<ReviewResponse>()

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var ecsProductDetailRepository = ECSProductDetailRepository(this,ecsServices)

    var ecsProductCallback = ECSProductForCTNCallback(this)

    var ecsProductListCallback = ECSProductListForCTNsCallback(this)


    fun getRatings(ctn :String){
        ecsProductDetailRepository.getRatings(ctn)
    }

    fun getProductDetail(ecsProduct: com.philips.platform.ecs.model.products.ECSProduct){
        ecsProductDetailRepository.getProductDetail(ecsProduct)
    }

    fun getBazaarVoiceReview(ctn : String, pageNumber : Int, pageSize : Int){
        ecsProductDetailRepository.fetchProductReview(ctn, pageNumber, pageSize)
    }

    fun addProductToShoppingcart(ecsProduct: com.philips.platform.ecs.model.products.ECSProduct, addToProductCallback  : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.cart.ECSShoppingCart, Exception>){
        ecsProductAsParamter=ecsProduct
        addToProductCallBack=addToProductCallback
        ecsProductDetailRepository.addTocart(ecsProductAsParamter)
    }


    override fun authFailureCallback(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?){
        MECLog.v("Auth","refresh auth failed");
        addToProductCallBack.onFailure(error,ecsError)
    }

    fun retryAPI(mECRequestType : MECRequestType) {
        var retryAPI = { addProductToShoppingcart(ecsProductAsParamter,addToProductCallBack) }
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    fun createShoppingCart(request: String){
        val createShoppingCartCallback=  object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.cart.ECSShoppingCart, Exception> {
            override fun onResponse(result: com.philips.platform.ecs.model.cart.ECSShoppingCart?) {
                addProductToShoppingcart(ecsProductAsParamter,addToProductCallBack)
            }
            override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
                TODO(" create cart must NOT fail")
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

    fun removeBlacklistedRetailers(ecsRetailers: com.philips.platform.ecs.model.retailers.ECSRetailerList): com.philips.platform.ecs.model.retailers.ECSRetailerList {
        val list = MECDataHolder.INSTANCE.blackListedRetailers
        if(list == null){
            return ecsRetailers
        }

        for (name in list!!) {

            val iterator = ecsRetailers.retailers.iterator()

            while (iterator.hasNext()) {

                val retailerName = iterator.next().getName().replace("\\s+".toRegex(), "")
                if (name.equals(retailerName, true)) {

                    if (MECutility.indexOfSubString(true, retailerName, name) >= 0) {
                        iterator.remove()

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

    fun isPhilipsShop(retailer: com.philips.platform.ecs.model.retailers.ECSRetailer): Boolean {
        return retailer.isPhilipsStore.equals("Y", ignoreCase = true)
    }

    fun setStockInfoWithRetailer(stockLabel : Label, product: com.philips.platform.ecs.model.products.ECSProduct?, ecsRetailers: com.philips.platform.ecs.model.retailers.ECSRetailerList) {
            if(!MECDataHolder.INSTANCE.hybrisEnabled) {
                if (ecsRetailers.retailers.size>0) {
                    var availability=false
                    for (i in 0..ecsRetailers.retailers.size) {
                        if(ecsRetailers.retailers.get(i).availability.contains("YES")){
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

                } else if (ecsRetailers.retailers.size==0) {
                    stockLabel.text = stockLabel.context.getString(R.string.mec_out_of_stock)
                    stockLabel.setTextColor(stockLabel.context.getColor(R.color.uid_signal_red_level_30))
                    product?.let { tagOutOfStockActions(it) }
                }
            }
            else if(MECDataHolder.INSTANCE.hybrisEnabled){
                if(ecsRetailers.retailers.size==0) {
                    if (null != product && null != product.stock) {
                        if (MECutility.isStockAvailable(product.stock!!.stockLevelStatus, product.stock!!.stockLevel)) {
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

             else if (ecsRetailers.retailers.size>0) {
                    var availability=false
                    for (i in 0..ecsRetailers.retailers.size) {
                        if(ecsRetailers.retailers.get(i).availability.contains("YES")){
                            availability=true
                            break
                        } else if(ecsRetailers.retailers.get(i).availability.contains("NO")) {
                            availability=false
                            if (!availability) {
                                if (null != product && null != product.stock) {
                                    if (MECutility.isStockAvailable(product.stock!!.stockLevelStatus, product.stock!!.stockLevel)) {
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

    fun addNoAsset(product: com.philips.platform.ecs.model.products.ECSProduct) {
        var asset = com.philips.platform.ecs.model.asset.Asset()
        asset.asset = "NO Image Asset Found"
        asset.type = "APP"

        var assets = com.philips.platform.ecs.model.asset.Assets()
        assets.asset = Arrays.asList(asset)
        product.assets = assets

    }

}