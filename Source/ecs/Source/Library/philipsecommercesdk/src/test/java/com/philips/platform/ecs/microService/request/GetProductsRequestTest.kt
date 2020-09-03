package com.philips.platform.ecs.microService.request

import com.android.volley.VolleyError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSErrorType
import com.philips.platform.ecs.microService.error.ErrorHandler
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
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
class GetProductsRequestTest {

    val defaultOffset = 0
    val limit = 20
    val category: String = "FOOD_PREPARATION_CA2"
    var mGetProductsRequest: GetProductsRequest? = null
    var mProductFilter: ProductFilter? = null
    private lateinit var errorHandler: ErrorHandler

    var eCSCallback = object : ECSCallback<ECSProducts, ECSError> {
        override fun onResponse(result: ECSProducts) {

        }

        override fun onFailure(ecsError: ECSError) {

        }
    }


    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mProductFilter = ProductFilter(null, hashSetOf())
        errorHandler = ErrorHandler()


    }

    @Test
    fun getServiceID() {
    }

    @Test
    fun getURL() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.priceAscending


        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        val filters = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=OUT_OF_STOCK"
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL.contains(limit.toString()))
        assert(modifiedURL.contains(defaultOffset.toString()))

        assert(modifiedURL.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL.contains(ECSSortType.priceAscending.toString()))
        assertEquals(modifiedURL, filters)
    }

    @Test
    fun `getURL with all stock  level filters`() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.InStock)
        stockLevelSet.add(ECSStockLevel.LowStock)
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.priceAscending

        val filters = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=IN_STOCK,LOW_STOCK,OUT_OF_STOCK"

        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL.contains(limit.toString()))
        assert(modifiedURL.contains(defaultOffset.toString()))
        assert(modifiedURL.contains(ECSStockLevel.InStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.LowStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL.contains(ECSSortType.priceAscending.toString()))
    }

    @Test
    fun `getURL with all stock  level filters wit topRated`() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.InStock)
        stockLevelSet.add(ECSStockLevel.LowStock)
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.topRated

        val filters = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=IN_STOCK,OUT_OF_STOCK,LOW_STOCK"

        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL.contains(limit.toString()))
        assert(modifiedURL.contains(defaultOffset.toString()))

        assert(modifiedURL.contains(ECSStockLevel.InStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.LowStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL.contains(ECSSortType.topRated.toString()))
    }

    @Test
    fun `getURL with all stock  level filters wit priceDescending`() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.InStock)
        stockLevelSet.add(ECSStockLevel.LowStock)
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.priceDescending

        val filters = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=IN_STOCK,OUT_OF_STOCK,LOW_STOCK"

        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL.contains(limit.toString()))
        assert(modifiedURL.contains(defaultOffset.toString()))

        assert(modifiedURL.contains(ECSStockLevel.InStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.LowStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL.contains(ECSSortType.priceDescending.toString()))
    }

    @Test
    fun `getURL with all stock  level filters wit discountPercentageAscending`() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.InStock)
        stockLevelSet.add(ECSStockLevel.LowStock)
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.discountPercentageAscending

        val filters = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=IN_STOCK,LOW_STOCK,OUT_OF_STOCK"

        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL.contains(limit.toString()))
        assert(modifiedURL.contains(defaultOffset.toString()))

        assert(modifiedURL.contains(ECSStockLevel.InStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.LowStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL.contains(ECSSortType.discountPercentageAscending.toString()))
    }

    @Test
    fun `getURL with all stock  level filters wit discountPercentageDescending`() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.InStock)
        stockLevelSet.add(ECSStockLevel.LowStock)
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.discountPercentageDescending

        val filters = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=IN_STOCK,LOW_STOCK,OUT_OF_STOCK"

        mGetProductsRequest = GetProductsRequest(category, limit, defaultOffset, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        val modifiedURL: String? = mGetProductsRequest?.getURL()
        assert(modifiedURL!!.contains(category))
        assert(modifiedURL.contains(limit.toString()))
        assert(modifiedURL.contains(defaultOffset.toString()))

        assert(modifiedURL.contains(ECSStockLevel.InStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.LowStock.toString()))
        assert(modifiedURL.contains(ECSStockLevel.OutOfStock.toString()))
        assert(modifiedURL.contains(ECSSortType.discountPercentageDescending.toString()))
    }

    @Test
    fun getURLwithNegativeLimitAndOffset() {// this method will internally test method addParams()
        var stockLevelSet: HashSet<ECSStockLevel> = HashSet()
        stockLevelSet.add(ECSStockLevel.OutOfStock)
        mProductFilter!!.stockLevelSet = stockLevelSet
        mProductFilter!!.sortType = ECSSortType.priceAscending


        var modifiedURL: String? = ""
        // if limit is given negative, it will be become 0. If defaultOffset given is negative it will become default 0
        mGetProductsRequest = GetProductsRequest(category, -limit, -5, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        modifiedURL = mGetProductsRequest?.getURL()
        //https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=20&offset=0&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=OUT_OF_STOCK&modifiedSince=2019-10-31T20:34:55Z
        assert(modifiedURL!!.contains(limit.toString()))
        assert(modifiedURL!!.contains(defaultOffset.toString()))


        // if limit given is more than 50 then it will change to threshold 50
        mGetProductsRequest = GetProductsRequest(category, 1000, 5, mProductFilter, eCSCallback)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        modifiedURL = mGetProductsRequest?.getURL()
        //https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=en&country=US&limit=50&offset=5&category=FOOD_PREPARATION_CA2&sort=price&stockLevel=OUT_OF_STOCK&modifiedSince=2019-10-31T20:34:55Z
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
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
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
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
//        val modifiedURL: String? = mGetProductsRequest?.getURL()
        val errorString = ClassLoader.getSystemResource("pil/fetchProductsPILwithEmptyResponse.json").readText()
        val jsonObject = JSONObject(errorString)
        mGetProductsRequest!!.onResponse(jsonObject)
    }


    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSProducts, ECSError>

    lateinit var volleyError: VolleyError

    @Test
    fun onFailure() {

        volleyError = VolleyError("some exception")
        mGetProductsRequest = GetProductsRequest("Category Does Not Exist", limit, defaultOffset, mProductFilter, ecsCallbackMock)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        mGetProductsRequest!!.onErrorResponse(volleyError)
        Mockito.verify(ecsCallbackMock).onFailure(any(ECSError::class.java))

        var eCSCallbackOnFailure = object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                fail()
            }

            override fun onFailure(ecsError: ECSError) {
                assertNotNull(ecsError)
                assertEquals(ECSErrorType.ECSsomethingWentWrong.errorCode, ecsError.errorCode)
            }
        }
        mGetProductsRequest = GetProductsRequest("Category Does Not Exist", limit, defaultOffset, mProductFilter, eCSCallbackOnFailure)
        mGetProductsRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"
        mGetProductsRequest!!.onErrorResponse(volleyError)


    }

    @Test
    fun `on failure of wrong content type`() {

        val errorString = ClassLoader.getSystemResource("pil/fetchProductsPILwithFailureResponseIncorrectContentType.json").readText()
        var ba: ByteArray = ClassLoader.getSystemResource("pil/fetchProductsPILwithFailureResponseIncorrectContentType.json").readBytes()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError, ecsDefaultError)
        Assert.assertEquals(ECSErrorType.ECSPIL_NOT_ACCEPTABLE.errorCode, ecsDefaultError.errorCode)

    }
}