/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.BulkRatingOptions
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.EqualityOperator
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.cdp.di.ecs.model.products.ECSProducts
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder

class ECSCatalogRepository {


    fun getProducts(pageNumber: Int, pageSize: Int, ecsCallback: ECSProductsCallback, eCSServices: ECSServices) {
        eCSServices.fetchProducts(pageNumber, pageSize, ecsCallback)
    }

    fun getCategorizedProductsForRetailer(ctnS: MutableList<String>, ecsProductListCallback: ECSProductListCallback, eCSServices: ECSServices) {
        eCSServices.fetchProductSummaries(ctnS, ecsProductListCallback)
    }

    //TODO
    fun getCategorizedProducts(pageNumber: Int, pageSize: Int,numberOFCTnsTobeSerached: Int, ctns: List<String>, existingList : MutableList<ECSProducts>?, ecsProductViewModel: EcsProductViewModel) {


        var modifiedList = existingList
        val ecsServices = MECDataHolder.INSTANCE.eCSServices
        ecsServices.fetchProducts(pageNumber, pageSize, object : ECSCallback<ECSProducts, Exception> {

            override fun onFailure(error: Exception?, ecsError: ECSError?) {
                val mecError = MecError(error, ecsError,null)
                ecsProductViewModel.mecError.value = mecError
            }

            override fun onResponse(ecsProducts: ECSProducts) {

                val mutableLiveData = ecsProductViewModel.ecsProductsList


                //add logic
                val ecsProductFoundList = mutableListOf<ECSProduct>()

                for (ctn in ctns) {

                    for (ecsProduct in ecsProducts.products) {

                        if (ecsProduct.code.equals(ctn, true)) {

                           //TODO below code is added to avoide duplicate but duplicate call should be avoided  as well

                            var allProductExistingCTNs : MutableList<String> = mutableListOf()

                            if(modifiedList!=null){
                                 allProductExistingCTNs = getAllProductCTNs(modifiedList!!)
                            }
                            if(allProductExistingCTNs.contains(ecsProduct.code) ){
                                //dont add againn
                            }else{
                                ecsProductFoundList.add(ecsProduct)
                            }


                        }
                    }
                }




                if (modifiedList == null) {
                    modifiedList = mutableListOf()
                }

                ecsProducts.products = ecsProductFoundList
                modifiedList!!.add(ecsProducts)

                if(ecsProductFoundList.size!=0){
                    //Found one product
                    mutableLiveData.value = modifiedList
                }


                if (shouldBreakTheLoop(pageNumber, modifiedList!!, numberOFCTnsTobeSerached)) {

                    mutableLiveData.value = modifiedList

                } else {

                    var tempCTNS = getCTNsToBeSearched(ctns as MutableList<String>, ecsProductFoundList);
                    var newPageNumber: Int = pageNumber + 1
                    getCategorizedProducts(newPageNumber, pageSize,numberOFCTnsTobeSerached, tempCTNS,modifiedList, ecsProductViewModel)
                }

            }

            //Remove already found categorizedCtns from search list
            private fun getCTNsToBeSearched(ctns: MutableList<String>, ecsProductList: MutableList<ECSProduct>): MutableList<String> {
                for (ecsProduct in ecsProductList) {
                    ctns.remove(ecsProduct.code)
                }
                return ctns
            }

        })
    }

    /*
    *   These are the below conditions to break the loop
    *   1- if pageNumer equals to 4 -- Means 5 Pages are searched
    *   2- Searched for all the pages completed
    *   3- ALl CTNs are found
    *   4- Only show products of page size at a time .

    * */

    private fun shouldBreakTheLoop(pageNumber: Int, ecsProductsList: MutableList<ECSProducts>, numberOFCTnsTobeSerached: Int) :Boolean {
        return shouldDoFivePageCall(pageNumber, ecsProductsList, numberOFCTnsTobeSerached)

    }

    private fun shouldDoFivePageCall(pageNumber: Int, ecsProducts: MutableList<ECSProducts>, numberOFCTnsTobeSerached: Int): Boolean {
        return  (isProductNotFound(ecsProducts) && didReachThreshold(pageNumber))||
                didReachLastPage(pageNumber , ecsProducts) ||
                isAllProductsFound(numberOFCTnsTobeSerached,ecsProducts) ||
                didProductsFondReachPageSize(pageNumber,ecsProducts)
    }

    private fun getAllProductCount(ecsProductsList: MutableList<ECSProducts>):Int{

        var count = 0

        for(ecsProducts in ecsProductsList){
            count += ecsProducts.products.size
        }
        return count
    }


    private fun getAllProductCTNs(ecsProductsList: MutableList<ECSProducts>):ArrayList<String>{

        var ctnList: MutableList<String> = mutableListOf()

        for(ecsProducts in ecsProductsList){

            for(product in ecsProducts.products){

                ctnList.add(product.code)
            }
        }
        return ctnList as ArrayList<String>
    }

    private fun didProductsFondReachPageSize(pageNumber: Int,ecsProducts: MutableList<ECSProducts>) = (getAllProductCount(ecsProducts) / (pageNumber+1)) == ecsProducts.get(ecsProducts.size -1).pagination.pageSize

    private fun isProductNotFound(ecsProducts: MutableList<ECSProducts>) = getAllProductCount(ecsProducts) == 0

    private fun isAllProductsFound(ctnsTobeSearchedSize: Int ,ecsProducts: MutableList<ECSProducts>) = ctnsTobeSearchedSize == getAllProductCount(ecsProducts)

    private fun didReachThreshold(pageNumber : Int) =  0 == (pageNumber + 1) % MECConstant.THRESHOLD

    private fun didReachLastPage(pageNumber : Int,ecsProducts: MutableList<ECSProducts>) = pageNumber == ecsProducts.get(ecsProducts.size -1).pagination.totalPages-1


    fun fetchProductReview(ecsProducts: List<ECSProduct> , ecsProductViewModel: EcsProductViewModel){

        val mecConversationsDisplayCallback = MECBulkRatingConversationsDisplayCallback(ecsProducts, ecsProductViewModel)
        var ctnList: MutableList<String> = mutableListOf()

        for(ecsProduct in ecsProducts){
            ctnList.add(ecsProduct.code.replace("/","_"))
        }
        val bvClient = MECDataHolder.INSTANCE.bvClient
        val request = BulkRatingsRequest.Builder(ctnList, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE!!, MECDataHolder.INSTANCE.locale).build()
        bvClient!!.prepareCall(request).loadAsync(mecConversationsDisplayCallback)

    }

}

