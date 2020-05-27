package com.philips.platform.ecs.microService.manager

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.request.GetProductForRequest
import com.philips.platform.ecs.microService.request.GetProductsRequest
import com.philips.platform.ecs.microService.request.GetSummariesForProductsRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
class ECSProductManagerTest {

    lateinit var mECSProductManager: ECSProductManager

    lateinit var ecsCallback: ECSCallback<ECSProducts, ECSError>



    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSProduct, ECSError>

    @Mock
    lateinit var mGetProductsRequestMock: GetProductsRequest

    @Mock
    lateinit var requestHandlerMock: RequestHandler

    @Mock
    lateinit var productFilterMock: ProductFilter

    @Mock
    lateinit var eCSCallbackGetSummaryForSingleProductMock : ECSCallback<List<ECSProduct>, ECSError>

////////////////////

    lateinit var eCSCallback: ECSCallback<ECSProduct?, ECSError>

    @Mock
    lateinit var mGetProductForRequestMock: GetProductForRequest

    @Mock
    lateinit var mGetSummariesForProductsRequestMock: GetSummariesForProductsRequest

    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mECSProductManager = ECSProductManager()
        mECSProductManager.requestHandler = requestHandlerMock

    }

    //======================================================================================================================================================================

    @Test
    fun getProducts() {

      var  ecsCallbackGetProducts = object : ECSCallback<ECSProducts, ECSError> {

            override fun onResponse(result: ECSProducts) {
                assert(true)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }


        }

        // `when`( mECSApiValidator.getECSException(APIType.Locale)).thenReturn(null)
        var commerceProducts = ArrayList<ECSProduct>()
        var mECSProduct = ECSProducts(commerceProducts)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductsRequestMock)).then { ecsCallbackGetProducts.onResponse(mECSProduct) }
        mGetProductsRequestMock = GetProductsRequest("category", 1, 2, productFilterMock, ecsCallbackGetProducts)
        mECSProductManager.getProducts("category", 1, 2, productFilterMock, ecsCallbackGetProducts)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductsRequestMock)).then { ecsCallback.onResponse(mECSProduct) }
    }

    //======================================================================================================================================================================

    @Test
    fun getProductForHybrisON() {

        var eCSCallbackGetProductForHybrisON = object : ECSCallback<ECSProduct?, ECSError> {

            override fun onResponse(result: ECSProduct?) {
                assertNotNull(result)
                assertEquals("id", result?.id)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }
        }

        var mECSProduct = ECSProduct(null, "id", "type")
        ECSDataHolder.config.isHybris = true
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductForRequestMock)).then { eCSCallbackGetProductForHybrisON.onResponse(mECSProduct) }
        mECSProductManager.getProductFor("CTN", eCSCallbackGetProductForHybrisON)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductForRequestMock)).then { ecsCallbackMock.onResponse(mECSProduct) }

    }

    //======================================================================================================================================================================



    @Test
    fun getProductForHybrisOFF() {

      var   eCSCallbackGetProductForHybrisOFF = object : ECSCallback<ECSProduct?, ECSError> {

            override fun onResponse(result: ECSProduct?) {
                assertNotNull(result)
                assertEquals("id", result?.id)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }
        }

        var mECSProduct = ECSProduct(null, "id", "type")
        mECSProduct.id = "new id"
        ECSDataHolder.config.isHybris = false

        Mockito.`when`(mECSProductManager.getSummaryForSingleProduct(mECSProduct, eCSCallbackGetProductForHybrisOFF)).then { eCSCallbackGetProductForHybrisOFF.onResponse(mECSProduct) }
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetProductForHybrisOFF.onResponse(mECSProduct) }
        mECSProductManager.getProductFor("CTN", eCSCallbackGetProductForHybrisOFF)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetProductForHybrisOFF.onResponse(mECSProduct) }

    }
//======================================================================================================================================================================

    @Mock
    lateinit var jsonObjectMock: JSONObject

    @Mock
    lateinit var ecsProductmock :ECSProduct

    @Mock
    lateinit var productListMock: ArrayList<ECSProduct>

    @Test
    fun getSummaryForSingleProduct() {
        var eCSCallbackGetSummaryForSingleProduct = object : ECSCallback<ECSProduct?, ECSError> {

            override fun onResponse(result: ECSProduct?) {
                assertNotNull(result)
                assertEquals("new id", result?.id)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }
        }

        var mECSProduct = ECSProduct(null, "id", "type")
        mECSProduct.id = "new id"
        ECSDataHolder.config.isHybris = false
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProduct.onResponse(mECSProduct) }

        mECSProductManager.getSummaryForSingleProduct(mECSProduct, eCSCallbackGetSummaryForSingleProduct)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProductMock.onResponse(productListMock) }



    }

    //======================================================================================================================================================================





    @Test
    fun fetchProductSummaries() {

        var eCSCallbackGetSummaryForSingleProduct = object : ECSCallback<List<ECSProduct>, ECSError> {

            override fun onResponse(result: List<ECSProduct>) {
                assertNotNull(result)
                assertEquals(2,result.size)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }
        }
        var ctnList: ArrayList<String> = ArrayList<String>()
        ctnList.add("ctn1")
        ctnList.add("ctn2")

        var ecsProductList: ArrayList<ECSProduct> = ArrayList<ECSProduct>()
        for (ctn in ctnList) {
            var ecsProduct = ECSProduct(null, ctn, null)
            ecsProductList.add(ecsProduct)
        }
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProduct.onResponse(ecsProductList) }
        mECSProductManager.fetchProductSummaries(ctnList, eCSCallbackGetSummaryForSingleProduct)

       Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProductMock.onResponse(productListMock) }


    }
   //======================================================================================================================================================================


    @Test
    fun fetchProductDetails() {
    }

    //======================================================================================================================================================================


    //======================================================================================================================================================================

}