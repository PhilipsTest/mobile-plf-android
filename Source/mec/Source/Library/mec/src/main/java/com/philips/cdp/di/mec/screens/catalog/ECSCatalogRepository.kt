package com.philips.cdp.di.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.*
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.cdp.di.ecs.model.products.ECSProducts
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.integration.MecHolder
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECDataHolder
import okhttp3.internal.Internal

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
        val ecsServices = MecHolder.INSTANCE.eCSServices
        ecsServices.fetchProducts(pageNumber, pageSize, object : ECSCallback<ECSProducts, Exception> {

            override fun onFailure(error: Exception?, ecsError: ECSError?) {
                val mecError = MecError(error, ecsError)
                ecsProductViewModel.mecError.value = mecError
            }

            override fun onResponse(ecsProducts: ECSProducts) {

                val mutableLiveData = ecsProductViewModel.ecsProductsList


                //add logic
                val ecsProductFoundList = mutableListOf<ECSProduct>()

                for (ctn in ctns) {

                    for (ecsProduct in ecsProducts.products) {

                        if (ecsProduct.code.equals(ctn, true)) {
                            ecsProductFoundList.add(ecsProduct)
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

            //Remove already found ctns from search list
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
                didProductsFondReachPageSize(ecsProducts)
    }

    private fun getAllProductCount(ecsProductsList: MutableList<ECSProducts>):Int{

        var count = 0

        for(ecsProducts in ecsProductsList){
            count += ecsProducts.products.size
        }
        return count
    }

    private fun didProductsFondReachPageSize(ecsProducts: MutableList<ECSProducts>) = getAllProductCount(ecsProducts) == ecsProducts.get(ecsProducts.size -1).pagination.pageSize

    private fun isProductNotFound(ecsProducts: MutableList<ECSProducts>) = getAllProductCount(ecsProducts) == 0

    private fun isAllProductsFound(ctnsTobeSearchedSize: Int ,ecsProducts: MutableList<ECSProducts>) = ctnsTobeSearchedSize == getAllProductCount(ecsProducts)

    private fun didReachThreshold(pageNumber : Int) =  0 == (pageNumber + 1) % MECConstant.THRESHOLD

    private fun didReachLastPage(pageNumber : Int,ecsProducts: MutableList<ECSProducts>) = pageNumber == ecsProducts.get(ecsProducts.size -1).pagination.totalPages - 1


    fun fetchProductReview(ecsProducts: List<ECSProduct> , ecsProductViewModel: EcsProductViewModel){

        val mecConversationsDisplayCallback = MECBulkRatingConversationsDisplayCallback(ecsProducts, ecsProductViewModel)
        var ctnList: MutableList<String> = mutableListOf()

        for(ecsProduct in ecsProducts){
            ctnList.add(ecsProduct.code.replace("/","_"))
        }
        val bvClient = MECDataHolder.INSTANCE.bvClient
        val request = BulkRatingsRequest.Builder(ctnList, BulkRatingOptions.StatsType.All).addFilter(BulkRatingOptions.Filter.ContentLocale, EqualityOperator.EQ, MECDataHolder.INSTANCE.locale).addCustomDisplayParameter(MECConstant.KEY_BAZAAR_LOCALE, MECDataHolder.INSTANCE.locale).build()
        bvClient!!.prepareCall(request).loadAsync(mecConversationsDisplayCallback)

    }

}

