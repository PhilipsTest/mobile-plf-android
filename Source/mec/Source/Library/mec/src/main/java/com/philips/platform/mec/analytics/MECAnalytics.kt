/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.analytics

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.NonNull
import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.appinfra.tagging.ErrorCategory
import com.philips.platform.appinfra.tagging.TaggingError
import com.philips.platform.ecs.model.cart.BasePriceEntity
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.analytics.MECAnalyticServer.other
import com.philips.platform.mec.analytics.MECAnalyticsConstant.appError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.country
import com.philips.platform.mec.analytics.MECAnalyticsConstant.currency
import com.philips.platform.mec.analytics.MECAnalyticsConstant.deliveryMethod
import com.philips.platform.mec.analytics.MECAnalyticsConstant.exceptionErrorCode
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.paymentType
import com.philips.platform.mec.analytics.MECAnalyticsConstant.productListLayout
import com.philips.platform.mec.analytics.MECAnalyticsConstant.promotion
import com.philips.platform.mec.analytics.MECAnalyticsConstant.purchase
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.analytics.MECAnalyticsConstant.transationID
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCode
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeRedeemed
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeStatus
import com.philips.platform.mec.integration.MECDependencies
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import java.util.*
import kotlin.collections.HashMap


class MECAnalytics {

    companion object {
        private val TAG: String = MECAnalytics::class.java.simpleName

        val defaultLocale: String = "en_US"
        var mAppTaggingInterface: AppTaggingInterface? = null
        var previousPageName = "uniquePageName";
        var countryCode = ""
        var currencyCode = ""

        @JvmStatic
        fun initMECAnalytics(dependencies: MECDependencies) {
            try {
                mAppTaggingInterface = dependencies.appInfra.tagging.createInstanceForComponent(MECAnalyticsConstant.COMPONENT_NAME, BuildConfig.VERSION_NAME)
                countryCode = dependencies.appInfra.serviceDiscovery.homeCountry
            } catch (e: Exception) {
                MECLog.d(TAG, "Exception :" + e.message);
            }
        }

        @JvmStatic
        fun trackPage(currentPage: String) {
            if (mAppTaggingInterface != null) {
                val map = HashMap<String, String>()
                if (currentPage != previousPageName) {
                    previousPageName = currentPage
                    MECLog.v(TAG, "trackPage$currentPage");
                    mAppTaggingInterface!!.trackPageWithInfo(currentPage, addCountryAndCurrency(map))
                }
            }
        }


        @JvmStatic
        fun trackMultipleActions(state: String, map: Map<String, String>) {
            if (mAppTaggingInterface != null) {
                MECLog.v(TAG, "trackMtlutipleAction ")
                mAppTaggingInterface!!.trackActionWithInfo(state, addCountryAndCurrency(map))
            }
        }


        /*
        * This API is used To tag In App notification with response
        *
        * */
        @JvmStatic
        fun trackInAppNotofication(errorDescription: String, errorResponse: String) {
            val actionMap = HashMap<String, String>()
            actionMap.put(MECAnalyticsConstant.inappnotification, errorDescription)
            actionMap.put(MECAnalyticsConstant.inappnotificationresponse, errorResponse)
            trackMultipleActions(sendData, actionMap)
        }


        /*
        * This API is used To tag and log Technical error at Error level
        * Tag format-    <Component_Code>:<Error_Category>:<server_name>:<ErrorMessage>:<Error_Code>
        * */
        @JvmStatic
        fun trackTechnicalError(value: Any) {
            val errorObject = value as String
            MECLog.e(MECAnalytics.TAG, javaClass.simpleName + " : " + errorObject)
//            val map = getTechnicalErrorMap(errorObject)
//            trackMultipleActions(sendData, map)
            mAppTaggingInterface?.trackErrorAction(ErrorCategory.TECHNICAL_ERROR, addCountryAndCurrency(mapOf()),
                    TaggingError(errorObject))
        }

//        internal fun getTechnicalErrorMap(errorObject: String): HashMap<String, String> {
//            val map = HashMap<String, String>()
//            map.put(technicalError, errorObject)
//            return map
//        }

        /*
       * This API is used To tag and log User error at Error level
       * Tag format-    <Component_Code>:<Error_Category>:<ErrorMessage>
       * */
        @JvmStatic
        fun trackUserError(value: Any) {
//            var errorString: String = MECConstant.COMPONENT_NAME + ":"
//            errorString += userError + ":"
//            val errorObject = value as String
//            errorString += errorObject
//            MECLog.e(MECAnalytics.TAG, javaClass.simpleName + " : " + errorString)
//            val map = getUserErrorMap(errorString)
//            trackMultipleActions(sendData, map)
            mAppTaggingInterface!!.trackErrorAction(ErrorCategory.USER_ERROR, addCountryAndCurrency(mapOf()),
                    TaggingError(value as String))
        }

//        internal fun getUserErrorMap(errorString: String): HashMap<String, String> {
//            val map = HashMap<String, String>()
//            map.put(userError, errorString)
//            return map
//        }

        /*
      * This API is used To tag and log Information error at Info level
      * Tag format-    <Component_Code>:<Error_Category>:<ErrorMessage>
      * */
        @JvmStatic
        fun trackInformationError(value: Any) {
            val errorObject = value as String

            MECLog.i(MECAnalytics.TAG, javaClass.simpleName + " : " + errorObject)
            mAppTaggingInterface?.trackErrorAction(ErrorCategory.INFORMATIONAL_ERROR, addCountryAndCurrency(mapOf()),
                    TaggingError(errorObject))
        }

//        internal fun getInformationErrorMap(errorObject: String): HashMap<String, String> {
//            val map = HashMap<String, String>()
//            map.put(informationalError, errorObject)
//            return map
//        }


        internal fun addCountryAndCurrency(map: Map<String, String>) =
                hashMapOf(country to countryCode, currency to currencyCode);


        /*
        * Each product list fetch including pagination/swipe will shall be tagged by this method
        * Here product list will be normally of page size (eg 20) or less that page size(Only for last page)
        *
        * */
        @JvmStatic
        fun tagProductList(productList: MutableList<ECSProduct>) {
            val map = getProductListMap(productList)
            if (map.size > 0) {
                trackMultipleActions(sendData, getProductListMap(productList))
            }
        }

        internal fun getProductListMap(productList: MutableList<ECSProduct>): Map<String, String> {
            val map = HashMap<String, String>()
            if (productList != null && productList.size > 0) {
                val mutableProductIterator = productList.iterator()
                var productListString: String = ""
                for (product in mutableProductIterator) {
                    productListString += "," + getProductInfo(product)
                }
                productListString = productListString.substring(1, productListString.length)
                MECLog.v(TAG, "prodList : $productListString")
                map.put(mecProducts, productListString)
            }
            return map
        }


        /*
        * To tag list or grid, during first launch list will be tagged and productList shall be empty
        * upon switch of grid and list top 10 products(or all available products with count less that 10) from product list will be tagged
        * format "[Category];[Product1];[Quantity];[Total Price]"
        * */
        @JvmStatic
        fun tagProductList(productList: MutableList<com.philips.platform.ecs.microService.model.product.ECSProduct>, listOrGrid: String) {
            val map = getProductListAndGridMap( productList, listOrGrid)
            if(map.size>0) {
                trackMultipleActions(sendData, map)
            }
        }

        internal fun getProductListAndGridMap( productList: MutableList<com.philips.platform.ecs.microService.model.product.ECSProduct>, listOrGrid: String): HashMap<String, String> {
            val map = HashMap<String, String>()
            if (productList.size > 0) {
                map.put(productListLayout, listOrGrid)
                val mutableProductIterator = productList.iterator()
                var productListString: String = ""
                var maxProductCount = 10
                for (product in mutableProductIterator) {
                    productListString += "," + getProductInfo(product)
                    if (--maxProductCount == 0) {
                        break
                    }
                }
                productListString = productListString.substring(1, productListString.length)
                MECLog.v("MEC_LOG", "prodList : $productListString")
                map.put(mecProducts, productListString);
            }
            return map
        }


        /*c
       * This method is to tag passed Action(s) with order products details in format "[Category];[Product1];[Quantity];[Total Price]"
       * */
        @JvmStatic
        fun tagActionsWithOrderProductsInfo(actionMap: Map<String, String>, entryList: List<ECSEntries>) {
            val productsMap = getOrderProductInfoMap(actionMap, entryList)
            if (productsMap.size > 0) { //
                trackMultipleActions(sendData, productsMap)
            }
        }

        internal fun getOrderProductInfoMap(actionMap: Map<String, String>, entryList: List<ECSEntries>): HashMap<String, String> {
            val productsMap = HashMap<String, String>()
            if (entryList != null && entryList.size > 0) { //Entries
                val mutableEntryIterator = entryList.iterator()
                var productListString: String = ""
                for (entry in mutableEntryIterator) {
                    productListString += "," + getProductInfoWithChangedQuantity(entry.product, entry.basePrice, entry.quantity)
                }
                productListString = productListString.substring(1, productListString.length - 1)
                MECLog.v("MEC_LOG", "Order prodList : " + productListString)
                productsMap.put(mecProducts, productListString);
            }
            productsMap.putAll(actionMap)
            return productsMap
        }


        /*
       * This method return singlet product details in format "[Category];[Product1]"
       * */
        @JvmStatic
        fun getProductInfo(product: ECSProduct): String {
            var productDetail: String = MECDataHolder.INSTANCE.rootCategory ?:""
            productDetail += ";" + product.code
           return productDetail
        }

        /*
  * This method return singlet product details in format "[Category];[Product1]"
  * */
        @JvmStatic
        fun getProductInfo(product: com.philips.platform.ecs.microService.model.product.ECSProduct): String {
            var productDetail: String = MECDataHolder.INSTANCE.rootCategory ?:""
            productDetail += ";" + product.ctn
            return productDetail
        }

        /*
        * This method return singlet product details in format "[Category];[Product1];[Quantity];[Total Price]"
        * product : Product
        * changedQuantity : qty added or removed
        * */
        @JvmStatic
        fun getProductInfoWithChangedQuantity(product: ECSProduct,basePriceEntity: BasePriceEntity, changedQuantity: Int): String {
            var productDetail: String = MECDataHolder.INSTANCE.rootCategory ?:""
            productDetail += ";" + product.code
            productDetail += ";" + changedQuantity //changed Quantity e.g. 2 product added OR 3 product deleted
            var totalPrice: Double =getProductPrice(product,basePriceEntity)*changedQuantity.toDouble() // unit Price * quantity
            totalPrice = Math.round(totalPrice * 100.0) / 100.0 // round off to 2 decimal
            productDetail += ";" + totalPrice
            return productDetail
        }

        /*
        * This method return product unit price (discounted if any)
        * */
        fun getProductPrice(product: ECSProduct, basePriceEntity: BasePriceEntity): Double {
            var productPrice: Double = (if (product.price.value != null) product.price.value else 0.0)
            if (null != basePriceEntity && null != basePriceEntity.value) {
                productPrice = basePriceEntity.value
            }
            return productPrice
        }

        /*
       * This method return singlet product details in format "[Category];[Product1];[Quantity];[Total Price]"
       * product : Product
       * changedQuantity : qty added or removed
       * */
        @JvmStatic
        fun getProductInfoWithChangedQuantity(product: ECSProduct, changedQuantity: Int): String {
            var productDetail: String = MECDataHolder.INSTANCE.rootCategory ?:""
            productDetail += ";" + product.code
            productDetail += ";" + changedQuantity //changed Quantity e.g. 1 product added
            productDetail += ";" + getProductPrice(product)
            return productDetail
        }

        /*
   * This method return singlet product details in format "[Category];[Product1];[Quantity];[Total Price]"
   * product : Product
   * changedQuantity : qty added or removed
   * */
        @JvmStatic
        fun getProductInfoWithChangedQuantity(product: com.philips.platform.ecs.microService.model.product.ECSProduct, changedQuantity: Int): String {
            var productDetail: String = MECDataHolder.INSTANCE.rootCategory ?:""
            productDetail += ";" + product.ctn
            productDetail += ";" + changedQuantity //changed Quantity e.g. 1 product added
            productDetail += ";" + getProductPrice(product)
            return productDetail
        }

        /*
     * This method return product unit price (discounted if any)
     * */
        fun getProductPrice(product: ECSProduct): String {
            var price: String = ""
            if (product.price != null && product.price.value != null) {
                price = "" + product.price.value
            }
            if (product.discountPrice != null && product.discountPrice.value != null) {
                price = "" + product.discountPrice.value
            }
            return price
        }

        /*
* This method return product unit price (discounted if any)
* */
        fun getProductPrice(product: com.philips.platform.ecs.microService.model.product.ECSProduct):String{

            val priceInDouble = product.attributes?.price?.value ?: product.attributes?.discountPrice?.value
            return ""+priceInDouble
        }


        /*
        * This method will tag a successful purchase order details
        * */
        @JvmStatic
        fun tagPurchaseOrder(mECSOrderDetail: ECSOrderDetail, paymentTypeOldOrNew: String) {
            var orderMap = getPurchaseOrderMap(mECSOrderDetail, paymentTypeOldOrNew)
            if (orderMap.size >= 4) {
                tagActionsWithOrderProductsInfo(orderMap, mECSOrderDetail.entries)
            }
        }

        internal fun getPurchaseOrderMap(mECSOrderDetail: ECSOrderDetail, paymentTypeOldOrNew: String): HashMap<String, String> {
            var orderMap = HashMap<String, String>()
            orderMap.put(specialEvents, purchase)
            orderMap.put(paymentType, paymentTypeOldOrNew)
            if (null != mECSOrderDetail.code) {
                orderMap.put(transationID, mECSOrderDetail.code)
            }
            if (null != mECSOrderDetail.deliveryMode) {
                orderMap.put(deliveryMethod, mECSOrderDetail.deliveryMode.name)
            }
            var orderPromotionList: String = ""
            if (null != mECSOrderDetail.appliedOrderPromotions && mECSOrderDetail.appliedOrderPromotions.size > 0) {
                for (appliedOrderPromotion in mECSOrderDetail.appliedOrderPromotions) {
                    orderPromotionList += "|" + appliedOrderPromotion.promotion.code // | separated promotion(s)
                }
                orderPromotionList = orderPromotionList.substring(1, orderPromotionList.length) // remove first "|" from string
            }
            if (orderPromotionList.isNotBlank()) {
                orderMap.put(promotion, orderPromotionList)
            }

            var voucherList: String = ""
            if (null != mECSOrderDetail.appliedVouchers && mECSOrderDetail.appliedVouchers.size > 0) {
                for (voucher in mECSOrderDetail.appliedVouchers) {
                    voucherList += "|" + voucher.voucherCode // "|" separated promotion(s)
                }
                voucherList = voucherList.substring(1, voucherList.length) // remove first "|" from string
            }
            if (voucherList.isNotBlank()) {
                orderMap.put(voucherCodeStatus, voucherCodeRedeemed)
                orderMap.put(voucherCode, voucherList)
            }
            return orderMap
        }

        /*
        * This method returns default english string resource
        * */
        @JvmStatic
        @NonNull
        fun getDefaultString(context: Context, id: Int): String {
            val configuration = Configuration(context.resources?.configuration)
            configuration.setLocale(Locale(defaultLocale))
            return context.createConfigurationContext(configuration)?.resources?.getString(id) ?: ""
        }

        fun setCurrencyString(localeString: String) {
            try {
                val localeArray = localeString.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val locale = Locale(localeArray[0], localeArray[1])
                val currency = Currency.getInstance(locale)
                currencyCode = currency.currencyCode
            } catch (e: Exception) {
                MECLog.d(TAG, "Exception : " + e.message)

//                trackTechnicalError(MECAnalyticsConstant.COMPONENT_NAME + ":" + appError + ":" + other + e.toString() + ":" + exceptionErrorCode)
                mAppTaggingInterface!!.trackErrorAction(ErrorCategory.TECHNICAL_ERROR, MECAnalytics.addCountryAndCurrency(mapOf()),
                        TaggingError(appError, other, exceptionErrorCode, e.toString()))
            }

        }


    }


}