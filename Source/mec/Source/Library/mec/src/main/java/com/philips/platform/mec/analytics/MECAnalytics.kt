/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.analytics

import android.app.Activity
import android.util.Log
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.analytics.MECAnalyticsConstant.country
import com.philips.platform.mec.analytics.MECAnalyticsConstant.currency
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.productListLayout
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.integration.MECDependencies
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.appinfra.BuildConfig
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import java.util.*
import kotlin.collections.HashMap


class MECAnalytics {

    companion object {

        var mAppTaggingInterface: AppTaggingInterface? = null
        var previousPageName = "uniquePageName";
        var countryCode = ""
        var currencyCode = ""

        @JvmStatic
        fun initMECAnalytics(dependencies: MECDependencies) {
            try {
                com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface = dependencies.appInfra.tagging.createInstanceForComponent(com.philips.platform.mec.analytics.MECAnalyticsConstant.COMPONENT_NAME, BuildConfig.VERSION_NAME)
                com.philips.platform.mec.analytics.MECAnalytics.Companion.countryCode = dependencies.appInfra.serviceDiscovery.homeCountry
            } catch (e: Exception) {

            }
            // currencyCode = Currency.getInstance(countryCode).currencyCode
        }

        @JvmStatic
        fun trackPage(currentPage: String) {
            if (com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface != null && currentPage != null) {
                var map = HashMap<String, String>()
                if (currentPage != com.philips.platform.mec.analytics.MECAnalytics.Companion.previousPageName) {
                    com.philips.platform.mec.analytics.MECAnalytics.Companion.previousPageName = currentPage
                    Log.v("MEC_LOG", "trackPage" + currentPage);
                    com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface!!.trackPageWithInfo(currentPage, com.philips.platform.mec.analytics.MECAnalytics.Companion.addCountryAndCurrency(map))
                }
            }
        }


        @JvmStatic
        fun trackAction(state: String, key: String, value: Any) {
            val valueObject = value as String
            Log.v("MEC_LOG", "trackAction" + valueObject);
            if (com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface != null)
                com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface!!.trackActionWithInfo(state, key, valueObject)
        }

        @JvmStatic
        fun trackMultipleActions(state: String, map: Map<String, String>) {
            if (com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface != null)
                Log.v("MEC_LOG", "trackMtlutipleAction ")
            com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface!!.trackActionWithInfo(state, com.philips.platform.mec.analytics.MECAnalytics.Companion.addCountryAndCurrency(map))
        }


        @JvmStatic
        fun pauseCollectingLifecycleData() {
            if (com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface != null)
                com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface!!.pauseLifecycleInfo()
        }


        @JvmStatic
        fun collectLifecycleData(activity: Activity) {
            if (com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface != null)
                com.philips.platform.mec.analytics.MECAnalytics.Companion.mAppTaggingInterface!!.collectLifecycleInfo(activity)
        }

        private fun addCountryAndCurrency(map: Map<String, String>): Map<String, String> {
            //var newMap = map.toMap<String,>()
            var newMap = HashMap(map)
            newMap.put(country, com.philips.platform.mec.analytics.MECAnalytics.Companion.countryCode)
            newMap.put(currency, com.philips.platform.mec.analytics.MECAnalytics.Companion.currencyCode)
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
                    productListString += "," + com.philips.platform.mec.analytics.MECAnalytics.Companion.getProductInfo(product)
                }
                productListString = productListString.substring(1, productListString.length - 1)
                Log.v("MEC_LOG", "prodList : " + productListString)
                com.philips.platform.mec.analytics.MECAnalytics.Companion.trackAction(sendData, mecProducts, productListString)
            }
        }


        /*
        * To tag list or grid, during first launch list will be tagged and productList shall be empty
        * upon switch of grid and list top 10 products(or all available products with count less that 10) from product list will be tagged
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
                    productListString += "," + com.philips.platform.mec.analytics.MECAnalytics.Companion.getProductInfo(product)
                    if (--maxProductCount == 0) {
                        break
                    }
                }
                productListString = productListString.substring(1, productListString.length - 1)
                Log.v("MEC_LOG", "prodList : " + productListString)
                map.put(mecProducts, productListString);
            }
            com.philips.platform.mec.analytics.MECAnalytics.Companion.trackMultipleActions(sendData, map)
        }

        @JvmStatic
        fun getProductInfo(product: ECSProduct): String {
            var protuctDetail: String = MECDataHolder.INSTANCE.rootCategory
            protuctDetail += ";" + product.code
            protuctDetail += ";" + (if (product.stock != null && product.stock.stockLevel != null) product.stock.stockLevel else 0)
            protuctDetail += ";" + (if (product.discountPrice != null) product.discountPrice.value else 0)
            return protuctDetail
        }

        fun setCurrencyString(localeString: String) {
            try {
                val localeArray = localeString.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val locale = Locale(localeArray[0], localeArray[1])
                val currency = Currency.getInstance(locale)
                com.philips.platform.mec.analytics.MECAnalytics.Companion.currencyCode = currency.currencyCode
            } catch (e: Exception) {

            }

        }


    }


}