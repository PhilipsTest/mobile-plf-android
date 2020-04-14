/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.analytics

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail
import com.philips.cdp.di.ecs.model.orders.Entries
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.mec.analytics.MECAnalyticsConstant.country
import com.philips.platform.mec.analytics.MECAnalyticsConstant.currency
import com.philips.platform.mec.analytics.MECAnalyticsConstant.deliveryMethod
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.productListLayout
import com.philips.platform.mec.analytics.MECAnalyticsConstant.promotion
import com.philips.platform.mec.analytics.MECAnalyticsConstant.purchase
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.analytics.MECAnalyticsConstant.technicalError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.transationID
import com.philips.platform.mec.analytics.MECAnalyticsConstant.userError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeRedeemed
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeStatus
import com.philips.platform.mec.integration.MECDependencies
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import java.util.*
import kotlin.collections.HashMap


class MECAnalytics {

    companion object {

        val  defaultLocale: String = "en_GB"
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

            }
        }

        @JvmStatic
        fun trackPage(currentPage: String) {
            if (mAppTaggingInterface != null && currentPage != null) {
                var map = HashMap<String, String>()
                if (currentPage != previousPageName) {
                    previousPageName = currentPage
                    Log.v("MEC_LOG", "trackPage" + currentPage);
                    mAppTaggingInterface!!.trackPageWithInfo(currentPage, addCountryAndCurrency(map))
                }
            }
        }


        @JvmStatic
        fun trackAction(state: String, key: String, value: Any) {
            val valueObject = value as String
            Log.v("MEC_LOG", "trackAction" + valueObject);
            if (mAppTaggingInterface != null)
                mAppTaggingInterface!!.trackActionWithInfo(state, key, valueObject)
        }

        @JvmStatic
        fun trackMultipleActions(state: String, map: Map<String, String>) {
            if (mAppTaggingInterface != null)
                Log.v("MEC_LOG", "trackMtlutipleAction ")
            mAppTaggingInterface!!.trackActionWithInfo(state, addCountryAndCurrency(map))
        }




        /*
        * This API is used To tag and log Technical error at Error level
        * Tag format-    <Component_Code>:<Error_Category>:<server_name>:<ErrorMessage><Error_Code>
        * */
        @JvmStatic
        fun trackTechnicalError( value: Any){
            val errorObject = value as String
            MECLog.e(technicalError, javaClass.simpleName +" : "+ errorObject)
            var map = HashMap<String, String>()
            map.put(technicalError,errorObject)
            trackMultipleActions(sendData,map)
        }

        /*
       * This API is used To tag and log User error at Error level
       * Tag format-    <Component_Code>:<Error_Category>:<ErrorMessage>
       * */
        @JvmStatic
        fun trackUserError( value: Any){
            val errorObject = value as String
            MECLog.e(userError, javaClass.simpleName +" : "+ errorObject)
            var map = HashMap<String, String>()
            map.put(userError,errorObject)
            trackMultipleActions(sendData,map)
        }


        private fun addCountryAndCurrency(map: Map<String, String>): Map<String, String> {
            var newMap = HashMap(map)
            newMap.put(country, countryCode)
            newMap.put(currency, currencyCode)
            return newMap;

        }


        /*
        * Each product list fetch including pagination/swipe willshall be tagged by this method
        * Here product list will be normally of page size (eg 20) or less that page size(Only for last page)
        *
        * */
        @JvmStatic
        fun tagProductList(productList: MutableList<ECSProduct>) {
            if (productList != null && productList.size > 0) {
                val mutableProductIterator = productList.iterator()
                var productListString: String = ""
                for (product in mutableProductIterator) {
                    productListString += "," + getProductInfo(product)
                }
                productListString = productListString.substring(1, productListString.length - 1)
                Log.v("MEC_LOG", "prodList : " + productListString)
                var map = HashMap<String, String>()
                map.put(mecProducts, productListString)
                trackMultipleActions(sendData,map )
            }
        }


        /*
        * To tag list or grid, during first launch list will be tagged and productList shall be empty
        * upon switch of grid and list top 10 products(or all available products with count less that 10) from product list will be tagged
        * format "[Category];[Product1];[Quantity];[Total Price]"
        * */
        @JvmStatic
        fun tagProductList(productList: MutableList<ECSProduct>, listOrGrid: String) {
            var map = HashMap<String, String>()
            map.put(productListLayout, listOrGrid)
            if (productList != null && productList.size > 0) {
                val mutableProductIterator = productList.iterator()
                var productListString: String = ""
                var maxProductCount = 10
                for (product in mutableProductIterator) {
                    productListString += "," + getProductInfo(product)
                    if (--maxProductCount == 0) {
                        break
                    }
                }
                productListString = productListString.substring(1, productListString.length - 1)
                Log.v("MEC_LOG", "prodList : " + productListString)
                map.put(mecProducts, productListString);
            }
            trackMultipleActions(sendData, map)
        }


        /*c
       * This method is to tag passed Action(s) with order products details in format "[Category];[Product1];[Quantity];[Total Price]"
       * */
        @JvmStatic
        fun tagActionsWithOrderProductsInfo(actionMap: Map<String, String>, entryList: List<Entries>) {
            var productsMap = HashMap<String, String>()
            if (entryList != null && entryList.size > 0) { //Entries
                val mutableEntryIterator = entryList.iterator()
                var productListString: String = ""
                for (entry in mutableEntryIterator) {
                    productListString += "," + getProductInfo(entry.product)
                }
                productListString = productListString.substring(1, productListString.length - 1)
                Log.v("MEC_LOG", "Order prodList : " + productListString)
                productsMap.put(mecProducts, productListString);
            }
            productsMap.putAll(actionMap)
            trackMultipleActions(sendData, productsMap)
        }

        /*c
        * This method is to tag passed Action(s) with shopping cart products details in format "[Category];[Product1];[Quantity];[Total Price]"
        * */
        @JvmStatic
        fun tagActionsWithCartProductsInfo(actionMap: Map<String, String>, ecsShoppingCart: ECSShoppingCart?) {
            var productsMap = HashMap<String, String>()
            val entryList = ecsShoppingCart?.entries // ECSEntries
            if (entryList != null && entryList.size > 0) {
                val mutableEntryIterator = entryList.iterator()
                var productListString: String = ""
                for (entry in mutableEntryIterator) {
                    productListString += "," + getProductInfo(entry.product)
                }
                productListString = productListString.substring(1, productListString.length - 1)
                Log.v("MEC_LOG", "Cart prodList : " + productListString)
                productsMap.put(mecProducts, productListString);
            }
            productsMap.putAll(actionMap)
            trackMultipleActions(sendData, productsMap)
        }


        /*
       * This method return singlet product details in format "[Category];[Product1];[Quantity];[Total Price]"
       * */
        @JvmStatic
        fun getProductInfo(product: ECSProduct): String {
            var protuctDetail: String = MECDataHolder.INSTANCE.rootCategory
            protuctDetail += ";" + product.code
            protuctDetail += ";" + (if (product.stock != null && product.stock.stockLevel != null) product.stock.stockLevel else 0)
            protuctDetail += ";" + (if (product.discountPrice != null) product.discountPrice.value else 0)
            return protuctDetail
        }


        /*
        * This method will tag a successful purchase order details
        * */
        @JvmStatic
        fun tagPurchaseOrder(mECSOrderDetail: ECSOrderDetail) {
            var orderMap = HashMap<String, String>()
            orderMap.put(specialEvents, purchase)
            orderMap.put(transationID, mECSOrderDetail.code)
            orderMap.put(deliveryMethod, mECSOrderDetail.deliveryMode.name)
            var orderPromotionList: String = ""
            if(null!=mECSOrderDetail.appliedOrderPromotions && mECSOrderDetail.appliedOrderPromotions.size>0) {
                for (appliedOrderPromotion in mECSOrderDetail.appliedOrderPromotions) {
                    orderPromotionList += "|" + appliedOrderPromotion.promotion.code // | separated promotion(s)
                }
                orderPromotionList = orderPromotionList.substring(1, orderPromotionList.length) // remove first "|" from string
            }
            if(orderPromotionList.isNotBlank()) {
                orderMap.put(promotion, orderPromotionList)
            }

            var voucherList: String = ""
            if(null!=mECSOrderDetail.appliedVouchers && mECSOrderDetail.appliedVouchers.size>0) {
                for (voucher in mECSOrderDetail.appliedVouchers) {
                    voucherList += "|" + voucher.voucherCode // "|" separated promotion(s)
                }
                voucherList = voucherList.substring(1, voucherList.length) // remove first "|" from string
            }
            if(voucherList.isNotBlank()) {
                orderMap.put(voucherCodeStatus, voucherCodeRedeemed)
                orderMap.put(promotion, voucherList)
            }

            tagActionsWithOrderProductsInfo(orderMap, mECSOrderDetail.entries)
        }

        /*
        * This method returns default string resource
        * */
        @JvmStatic
        @NonNull
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun getDefaultString(context: Context, id: Int): String {
            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(Locale(defaultLocale))
            return context.createConfigurationContext(configuration).resources.getString(id)
        }

        fun setCurrencyString(localeString: String) {
            try {
                val localeArray = localeString.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val locale = Locale(localeArray[0], localeArray[1])
                val currency = Currency.getInstance(locale)
                currencyCode = currency.currencyCode
            } catch (e: Exception) {
                trackTechnicalError( e.localizedMessage)
            }

        }


    }


}