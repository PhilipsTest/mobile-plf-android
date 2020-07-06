package com.philips.platform.ecs.microService.request

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSErrorType
import com.philips.platform.ecs.microService.error.ErrorHandler
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.util.ECSConfiguration
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
class CreateCartRequestTest {
    var mCreateCartRequest: CreateCartRequest? = null
    private lateinit var errorHandler: ErrorHandler

    @Mock
    lateinit var appInfraMock : AppInfra
    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    @Before
    fun setUp() {
        setApiKey()

        ECSDataHolder.locale = "en_US"
        errorHandler = ErrorHandler()
        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig
    }

    private fun setApiKey() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    @Test
    fun `test service id`(){

    }

    @Test
    fun `header should be as expected`() {

        ECSDataHolder.authToken = "authstring"
        ECSConfiguration.INSTANCE.setAuthToken("authstring")

        var ecsCallback = object : ECSCallback<ECSShoppingCart, ECSError> {
            override fun onResponse(eCSShoppingCart: ECSShoppingCart) {

            }

            override fun onFailure(ecsError: ECSError) {
                TODO("Not yet implemented")
            }
        }

        mCreateCartRequest = CreateCartRequest("HD9240/94",2,ecsCallback)
        val expectedMap = HashMap<String,String>()


        expectedMap["Accept"] = "application/json"
        expectedMap["Api-Key"] = "yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP"
        expectedMap["Api-Version"] = "1"
        expectedMap["Content-Type"]="application/json"
        expectedMap["Authorization"] = "bearer authstring"
        Assert.assertEquals(expectedMap, mCreateCartRequest!!.getHeader())
    }

    @Test
    fun getURL() {

        var ecsCallback = object : ECSCallback<ECSShoppingCart, ECSError> {
            override fun onResponse(eCSShoppingCart: ECSShoppingCart) {
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        mCreateCartRequest = CreateCartRequest("HD9240/94",2,ecsCallback)
        mCreateCartRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart?siteId=%siteId%&language=%language%&country=%country%&productId=%ctn%&quantity=%quantity%"

        val modifiedURL: String? = mCreateCartRequest?.getURL()
        assert(modifiedURL!!.contains("productId"))
        assert(modifiedURL!!.contains("quantity"))

        assertEquals(SERVICEID_ECS_CREATE_CART,mCreateCartRequest?.getServiceID())
    }


    @Test
    fun `on Response success Germany`() {
        var ecsCallback = object : ECSCallback<ECSShoppingCart, ECSError> {
            override fun onResponse(eCSShoppingCart: ECSShoppingCart) {
                assertNotNull(eCSShoppingCart)
                assertNotNull(eCSShoppingCart.data)
                assertNotNull(eCSShoppingCart.data?.id)
                assertNotNull(eCSShoppingCart.data?.attributes)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryAddress)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryAddress?.id)
              //  assertNotNull(eCSShoppingCart.data?.attributes?.appliedVouchers)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryMode)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryMode?.id)
                assertNotNull(eCSShoppingCart.data?.attributes?.applicableDeliveryModes)
                assertNotNull(eCSShoppingCart.data?.attributes?.items)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.appliedProductPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.appliedPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.potentialProductPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.potentialPromotions)

                assertNotNull(eCSShoppingCart.data?.attributes?.notifications)
                //assertNotNull(eCSShoppingCart.data?.attributes?.notifications?)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        mCreateCartRequest = CreateCartRequest("HD9240/94",2,ecsCallback)

        mCreateCartRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart?siteId=%siteId%&language=%language%&country=%country%&productId=%ctn%&quantity=%quantity%"

        val responseString = ClassLoader.getSystemResource("pil/cart/FetchCartGermany.json").readText()
        val jsonObject = JSONObject(responseString)
        mCreateCartRequest!!.onResponse(jsonObject)
    }


    @Test
    fun `on Response success US`() {
        var ecsCallback = object : ECSCallback<ECSShoppingCart, ECSError> {
            override fun onResponse(eCSShoppingCart: ECSShoppingCart) {
                assertNotNull(eCSShoppingCart)
                assertNotNull(eCSShoppingCart.data)
                assertNotNull(eCSShoppingCart.data?.id)
                assertNotNull(eCSShoppingCart.data?.attributes)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryAddress)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryAddress?.id)
                assertNotNull(eCSShoppingCart.data?.attributes?.appliedVouchers)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryMode)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryMode?.id)
                assertNotNull(eCSShoppingCart.data?.attributes?.applicableDeliveryModes)
                assertNotNull(eCSShoppingCart.data?.attributes?.items)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.appliedProductPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.appliedPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.potentialProductPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.potentialPromotions)

                assertNotNull(eCSShoppingCart.data?.attributes?.notifications)
                //assertNotNull(eCSShoppingCart.data?.attributes?.notifications?)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        mCreateCartRequest = CreateCartRequest("HD9240/94",2,ecsCallback)

        mCreateCartRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart?siteId=%siteId%&language=%language%&country=%country%&productId=%ctn%&quantity=%quantity%"

        val responseString = ClassLoader.getSystemResource("pil/cart/FetchCartUS.json").readText()
        val jsonObject = JSONObject(responseString)
        mCreateCartRequest!!.onResponse(jsonObject)
    }

    @Test
    fun `on Response success MasterJson`() {

        var ecsCallback = object : ECSCallback<ECSShoppingCart, ECSError> {
            override fun onResponse(eCSShoppingCart: ECSShoppingCart) {
                assertNotNull(eCSShoppingCart)
                assertNotNull(eCSShoppingCart.data)
                assertNotNull(eCSShoppingCart.data?.id)
                assertNotNull(eCSShoppingCart.data?.attributes)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryAddress)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryAddress?.id)
                assertNotNull(eCSShoppingCart.data?.attributes?.appliedVouchers)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryMode)
                assertNotNull(eCSShoppingCart.data?.attributes?.deliveryMode?.id)
                assertNotNull(eCSShoppingCart.data?.attributes?.applicableDeliveryModes)
                assertNotNull(eCSShoppingCart.data?.attributes?.items)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.appliedProductPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.appliedPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.potentialProductPromotions)
                assertNotNull(eCSShoppingCart.data?.attributes?.promotions?.potentialPromotions)

                assertNotNull(eCSShoppingCart.data?.attributes?.notifications)
                //assertNotNull(eCSShoppingCart.data?.attributes?.notifications?)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        mCreateCartRequest = CreateCartRequest("HD9240/94",2,ecsCallback)

        mCreateCartRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart?siteId=%siteId%&language=%language%&country=%country%&productId=%ctn%&quantity=%quantity%"

         val responseString = ClassLoader.getSystemResource("pil/cart/CreateCartUSMaster.json").readText()
        val jsonObject = JSONObject(responseString)
        mCreateCartRequest!!.onResponse(jsonObject)


    }

    @Test
    fun `invalid or missing CTN`() {

        val errorString = ClassLoader.getSystemResource("pil/cart/CreateCartMissingCTN.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var PilError = ECSError(ECSErrorType.MISSING_PARAMETER_productId.getLocalizedErrorString(), ECSErrorType.MISSING_PARAMETER_productId.errorCode, ECSErrorType.MISSING_PARAMETER_productId)
        errorHandler.setPILECSError(hybrisError,PilError)
        Assert.assertEquals(ECSErrorType.MISSING_PARAMETER_productId.errorCode, PilError.errorCode)
    }
}