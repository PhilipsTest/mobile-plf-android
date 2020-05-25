package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.filter.ECSSortType
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class GetProductsRequestTest {

    val category :String = "category"
     var mGetProductsRequest : GetProductsRequest?=null
     var mProductFilter : ProductFilter?=null

    var essCallback = object: ECSCallback<ECSProducts, ECSError> {

        override fun onResponse(result: ECSProducts) {

        }

        override fun onFailure(ecsError: ECSError) {

        }
    }


        var url1="https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"

    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mProductFilter= ProductFilter()
        mProductFilter!!.stockLevel= ECSStockLevel.OutOfStock
        mProductFilter!!.sortType=ECSSortType.priceAscending
        mProductFilter!!.modifiedSince="2019-10-31T20:34:55Z"
        mGetProductsRequest=GetProductsRequest(category,10,0,mProductFilter, essCallback)
    }


    @Test
    fun testAddParams(){
       val modifiedURL:String? =  mGetProductsRequest?.addParamsToURL(url1)
        assert(modifiedURL!!.contains(category))
        Assert.assertEquals

    }

    @Test
    fun getServiceID() {
    }

    @Test
    fun getURL() {
    }

    @Test
    fun onResponse() {
    }
}