package com.philips.platform.ecs.microService.manager

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSErrorType
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.request.*
import com.philips.platform.ecs.microService.util.ECSDataHolder
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(GetProductsRequest::class)
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
    lateinit var eCSCallbackGetSummaryForSingleProductMock: ECSCallback<List<ECSProduct>, ECSError>

    @Mock
    lateinit var mGetProductForRequestMock: GetProductForRequest

    @Mock
    lateinit var mGetSummariesForProductsRequestMock: GetSummariesForProductsRequest

    @Mock
    lateinit var mGetProductAssetRequestMock: GetProductAssetRequest

    @Mock
    lateinit var mGetProductDisclaimerRequestMock: GetProductDisclaimerRequest

    @Mock
    lateinit var appInfraMock : AppInfra

    @Mock
    lateinit var loggingInterfaceMock: LoggingInterface

    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface


    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mECSProductManager = ECSProductManager()
        mECSProductManager.requestHandler = requestHandlerMock

    }

    private fun setApiKey() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    private fun setApiKeyNull() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn(null)
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    //======================================================================================================================================================================



    @Test
    fun getProducts() {

        ECSDataHolder.locale = "en_US"
        ECSDataHolder.config = ECSConfig("en_US",isHybris = true)
        setApiKey()

        var ecsCallbackGetProducts = object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                assertNotNull(result)
                // assertEquals("id", result?.id)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        var commerceProducts = ArrayList<ECSProduct>()
        var mECSProduct = ECSProducts(commerceProducts)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductsRequestMock)).then { ecsCallbackGetProducts.onResponse(mECSProduct) }
        mGetProductsRequestMock = GetProductsRequest("category", 1, 2, productFilterMock, ecsCallbackGetProducts)
        mECSProductManager.getProducts("category", 1, 2, productFilterMock, ecsCallbackGetProducts)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductsRequestMock)).then { ecsCallback.onResponse(mECSProduct) }
    }


    @Test
    fun `getProducts With limit greater than 50  when Locale is present and hybris is available with api key`() {

        ECSDataHolder.locale = "en_US"
        ECSDataHolder.config = ECSConfig("en_US",isHybris = true)
        setApiKey()

        var ecsCallbackGetProducts = object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                assertNotNull(result)
                // assertEquals("id", result?.id)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        var mProductFilter : ProductFilter = ProductFilter()
        try {
            mECSProductManager.getProducts("category", 51, 2, mProductFilter, ecsCallbackGetProducts)
        }catch(e: ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PRODUCT_SEARCH_LIMIT.errorCode,e.errorCode)
        }

    }

    //======================================================================================================================================================================

    @Test
    fun getProductForHybrisON() {
        var eCSCallbackGetProductForHybrisON = object : ECSCallback<ECSProduct?, ECSError> {

            override fun onResponse(result: ECSProduct?) {
                assertNotNull(result)
                assertEquals("id", result?.ctn)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
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
    fun `get Product should throw exception when hybris is there but api key is not present`() {

        ECSDataHolder.locale = "en_US"
        ECSDataHolder.config = ECSConfig("en_US",isHybris = true)

        setApiKeyNull()


        var ecsCallbackGetProducts = object : ECSCallback<ECSProducts, ECSError> {
            override fun onResponse(result: ECSProducts) {
                assertNotNull(result)
                // assertEquals("id", result?.id)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        var mProductFilter : ProductFilter = ProductFilter()
        try {
            mECSProductManager.getProducts("category", 51, 2, mProductFilter, ecsCallbackGetProducts)
        }catch(e: ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_API_KEY.errorCode,e.errorCode)
        }
    }

    @Test
    fun getProductForHybrisOFF() {

        var eCSCallbackGetProductForHybrisOFF = object : ECSCallback<ECSProduct?, ECSError> {

            override fun onResponse(result: ECSProduct?) {
                assertNotNull(result)
                assertEquals("id", result?.ctn)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        var mECSProduct = ECSProduct(null, "id", "type")
        mECSProduct.ctn = "new id"
        ECSDataHolder.config.isHybris = false
        Mockito.`when`(mECSProductManager.getSummaryForSingleProduct(mECSProduct, eCSCallbackGetProductForHybrisOFF)).then { eCSCallbackGetProductForHybrisOFF.onResponse(mECSProduct) }
        mECSProductManager.getProductFor("CTN", eCSCallbackGetProductForHybrisOFF)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProductMock.onResponse(productListMock) }

    }
//======================================================================================================================================================================


    @Mock
    lateinit var productListMock: ArrayList<ECSProduct>

    @Test
    fun getSummaryForSingleProduct() {
        var eCSCallbackGetSummaryForSingleProduct = object : ECSCallback<ECSProduct?, ECSError> {

            override fun onResponse(result: ECSProduct?) {
                assertNotNull(result)
                assertEquals("new id", result?.ctn)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        var mECSProduct = ECSProduct(null, "id", "type")
        mECSProduct.ctn = "new id"
        ECSDataHolder.config.isHybris = false
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProduct.onResponse(mECSProduct) }

        mECSProductManager.getSummaryForSingleProduct(mECSProduct, eCSCallbackGetSummaryForSingleProduct)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProductMock.onResponse(productListMock) }

    }

    //======================================================================================================================================================================


    @Test
    fun fetchProductSummaries() {

        val eCSCallbackGetSummaryForSingleProduct = object : ECSCallback<ECSProducts, ECSError> {

            override fun onResponse(result: ECSProducts) {
                assertNotNull(result)
                assertEquals(2, result.commerceProducts.size)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        val ctnList: ArrayList<String> = ArrayList<String>()
        ctnList.add("ctn1")
        ctnList.add("ctn2")

        val ecsProductList: ArrayList<ECSProduct> = ArrayList<ECSProduct>()
        for (ctn in ctnList) {
            val ecsProduct = ECSProduct(null, ctn, null)
            ecsProductList.add(ecsProduct)
        }
        val ecsProducts = ECSProducts(ecsProductList)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProduct.onResponse(ecsProducts) }
        mECSProductManager.fetchProductSummaries(ctnList, eCSCallbackGetSummaryForSingleProduct)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetSummariesForProductsRequestMock)).then { eCSCallbackGetSummaryForSingleProductMock.onResponse(productListMock) }


    }
    //======================================================================================================================================================================


    @Test
    fun fetchProductDetails() {
        val mEcscallback = object : ECSCallback<ECSProduct, ECSError> {

            override fun onResponse(result: ECSProduct) {
                assertNotNull(result)
                assertEquals("new id", result.ctn)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        val mECSProduct = ECSProduct(null, "id", "type")
        mECSProduct.ctn = "new id"

        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductAssetRequestMock)).then { mEcscallback.onResponse(mECSProduct) }
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductDisclaimerRequestMock)).then { mEcscallback.onResponse(mECSProduct) }
        mECSProductManager.fetchProductDetails(mECSProduct, mEcscallback)
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductAssetRequestMock)).then { ecsCallbackMock.onResponse(mECSProduct) }
        Mockito.`when`(requestHandlerMock.handleRequest(mGetProductDisclaimerRequestMock)).then { ecsCallbackMock.onResponse(mECSProduct) }
    }

    //======================================================================================================================================================================

    //======test case for product availability starts ===========
    @Mock
    lateinit var ecsCallbackBooleanMock: ECSCallback<Boolean, ECSError>

    @Test
    fun `product availability with invalid product should through invalid product exception`() {
        try {
            mECSProductManager.registerForProductAvailability("pabitrabapi1@gmail.com", " HX3245/00 ", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PARAMETER_VALUE_productId.errorCode,e.errorCode)
        }
    }


    @Test
    fun `product availability with blank product should through blank product exception`() {
        try {
            mECSProductManager.registerForProductAvailability("pabitrabapi1@gmail.com", "", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PARAMETER_VALUE_productId.errorCode,e.errorCode)
        }
    }


    @Test
    fun `product availability with invalid  email one should through invalid email exception`() {

        try {
            mECSProductManager.registerForProductAvailability("pabitrabapi", "HX3245/00", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PARAMETER_VALUE_Email.errorCode,e.errorCode)
        }
    }

    @Test
    fun `product availability with invalid  email two should through invalid email exception`() {

        try {
            mECSProductManager.registerForProductAvailability("pabitrabapi.com", "HX3245/00", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PARAMETER_VALUE_Email.errorCode,e.errorCode)
        }
    }

    @Test
    fun `product availability with invalid  email three should through invalid email exception`() {

        try {

            mECSProductManager.registerForProductAvailability("pabitrabapi@gmail", "HX3245/00", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PARAMETER_VALUE_Email.errorCode,e.errorCode)
        }
    }

    @Test
    fun `product availability with blank  email one should through invalid email exception`() {

        try {
            mECSProductManager.registerForProductAvailability("", "HX3245/00", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSPIL_INVALID_PARAMETER_VALUE_Email.errorCode,e.errorCode)
        }
    }

    @Test
    fun `product availability with without locale should through locale not set exception`() {

        ECSDataHolder.locale = null
        try {
            mECSProductManager.registerForProductAvailability("pabitrakumar.sahoo@philips.com", "HX3245/00", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSLocaleNotFound.errorCode,e.errorCode)
        }
    }


    @Test
    fun `product availability with without hybris should through hybris not set exception`() {

        ECSDataHolder.config = ECSConfig("en_US",isHybris = false)
        try {
            mECSProductManager.registerForProductAvailability("pabitrakumar.sahoo@philips.com", "HX3245/00", ecsCallbackBooleanMock)
        }catch (e : ECSException){
            assertEquals(ECSErrorType.ECSSiteIdNotFound.errorCode,e.errorCode)
        }
    }

    @Test
    fun `product availabilty api should call request handler to execute request when local validation is passed`() {
        ECSDataHolder.config = ECSConfig("en_US",isHybris = true)
        ECSDataHolder.locale = "en_US"
        setApiKey()
        mECSProductManager.registerForProductAvailability("pabitrakumar.sahoo@philips.com", "HX3245/00", ecsCallbackBooleanMock)
        Mockito.verify(requestHandlerMock).handleRequest(any(ProductAvailabilityRequest::class.java))

    }
}