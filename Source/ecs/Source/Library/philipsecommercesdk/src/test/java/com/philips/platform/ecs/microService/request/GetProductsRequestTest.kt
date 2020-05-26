package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSErrorType
import com.philips.platform.ecs.microService.error.VolleyHandler
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.model.filter.ECSSortType
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import junit.framework.Assert.*
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class GetProductsRequestTest {

    val defaultOffset = 0
    val limit = 20
    val limitThreshold = 50
    val category: String = "FOOD_PREPARATION_CA2"
    val modifiedSince = "2019-10-31T20:34:55Z"
    var mGetProductsRequest: GetProductsRequest? = null
    var mProductFilter: ProductFilter? = null
    private lateinit var  volleyHandler: VolleyHandler

    var eCSCallback = object : ECSCallback<ECSProducts, ECSError> {
        override fun onResponse(result: ECSProducts) {

        }
        override fun onFailure(ecsError: ECSError) {

        }
    }


    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mProductFilter = ProductFilter()
        volleyHandler = VolleyHandler()
    }

    @Test
    fun getServiceID() {
    }

    @Test
    fun getURL() {// this method will internally test method addParams()
        mProductFilter!!.stockLevel = ECSStockLevel.OutOfStock
        mProductFilter!!.sortType = ECSSortType.priceAscending
        mProductFilter!!.modifiedSince = modifiedSince
        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        //https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=OUT_OF_STOCK&modifiedSince=2019-10-31T20:34:55Z
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL!!.contains(limit.toString()))
        assert(modifiedURL!!.contains(defaultOffset.toString()))
        assert(modifiedURL!!.contains(modifiedSince))
        assert(modifiedURL!!.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL!!.contains(ECSSortType.priceAscending.toString()))
    }

    @Test
    fun getURLwithNegativeLimitAndOffset() {// this method will internally test method addParams()
        mProductFilter!!.stockLevel = ECSStockLevel.OutOfStock
        mProductFilter!!.sortType = ECSSortType.priceAscending
        mProductFilter!!.modifiedSince = modifiedSince

        var modifiedURL: String? = ""
        // if limit is given negative, it will be become 0. If defaultOffset given is negative it will become default 0
        mGetProductsRequest = GetProductsRequest(category, -limit, -5, mProductFilter, eCSCallback)
        modifiedURL = mGetProductsRequest?.getURL()
        //https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=OUT_OF_STOCK&modifiedSince=2019-10-31T20:34:55Z
        assert(modifiedURL!!.contains(limit.toString()))
        assert(modifiedURL!!.contains(defaultOffset.toString()))


        // if limit given is more than 50 then it will change to threshold 50
        mGetProductsRequest = GetProductsRequest(category, 1000, 5, mProductFilter, eCSCallback)
        modifiedURL = mGetProductsRequest?.getURL()
        //https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=50&offset=5&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=OUT_OF_STOCK&modifiedSince=2019-10-31T20:34:55Z
        assert(modifiedURL!!.contains(limitThreshold.toString()))
        assert(modifiedURL!!.contains("5"))

    }

    @Test
    fun onResponseSuccess() {
        var ecsCallback = object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                assertNotNull(result)
                assertEquals(100, result.commerceProducts.size) // 100 products
            }
            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, ecsCallback)
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        val errorString = ClassLoader.getSystemResource("pil/fetchProductsPILwithSiteLanguageCountry.json").readText()
        val jsonObject = JSONObject(errorString)
        mGetProductsRequest!!.onResponse(jsonObject)


    }

    @Test
    fun onResponseEmpty() {
        var ecsCallback = object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                assertNotNull(result)
                assertEquals(0, result.commerceProducts.size) // 0 products
            }
            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        mGetProductsRequest = GetProductsRequest("Category Does Not Exist", limit, defaultOffset, mProductFilter, ecsCallback)
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        val errorString = ClassLoader.getSystemResource("pil/fetchProductsPILwithEmptyResponse.json").readText()
        val jsonObject = JSONObject(errorString)
        mGetProductsRequest!!.onResponse(jsonObject)
    }

    @Test
    fun onFailure(){

        val errorString =   ClassLoader.getSystemResource("pil/fetchProductsPILwithTimeoutFailure.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var actualError = ECSError(ECSErrorType.ECSPIL_INTEGRATION_TIMEOUT.getLocalizedErrorString(), ECSErrorType.ECSPIL_INTEGRATION_TIMEOUT.errorCode, ECSErrorType.ECSPIL_INTEGRATION_TIMEOUT)
        volleyHandler.setPILECSError(hybrisError,actualError)
        Assert.assertEquals(ECSErrorType.ECSPIL_INTEGRATION_TIMEOUT.errorCode, actualError.errorcode)


    }
}