package com.philips.platform.ecs.microService.request

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants.Companion.SERVICEID_ECS_CREATE_CART
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.util.ECSDataHolder
import junit.framework.Assert.*
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class CreateCartRequestTest {
    var mCreateCartRequest: CreateCartRequest? = null

    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
    }

    @Test
    fun `test service id`(){

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
        mCreateCartRequest?.url = "https://acc.eu-west-1.api.philips.com/commerce-service/product/search?siteId=%siteId%&language=%language%&country=%country%"

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
}