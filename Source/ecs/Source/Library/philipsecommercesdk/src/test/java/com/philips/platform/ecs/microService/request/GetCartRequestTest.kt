/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.ecs.microService.request

import com.android.volley.Request
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.util.ECSConfiguration
import org.json.JSONObject
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(AppInfra::class,AppConfigurationInterface::class)
@RunWith(PowerMockRunner::class)
class GetCartRequestTest {

    lateinit var getCartRequest : GetCartRequest

    @Mock
    lateinit var appInfraMock : AppInfra
    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        setApiKey()

        ECSDataHolder.locale = "en_US"
        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig

        val callBack = object : ECSCallback<ECSShoppingCart, ECSError>{
            override fun onResponse(result: ECSShoppingCart) {

            }

            override fun onFailure(ecsError: ECSError) {

            }
        }
        getCartRequest = GetCartRequest(callBack)
        getCartRequest.url = "https://acc.eu-west-1.api.philips.com/commerce-service/cart/%cartId%?siteId=%siteId%&language=%language%&country=%country%"
    }

    @Test
    fun `service discovery url should be as expected`() {
        val expectedServiceDiscoveryURL = "https://acc.eu-west-1.api.philips.com/commerce-service/cart/%cartId%?siteId=%siteId%&language=%language%&country=%country%"
        assertEquals(expectedServiceDiscoveryURL,getCartRequest.url)
    }

    @Test
    fun getServiceID() {
        assertEquals("ecs.getCart",getCartRequest.getServiceID())
    }

    @Test
    fun `request method should be get`() {
        assertEquals(Request.Method.GET,getCartRequest.getRequestMethod())
    }

    @Test
    fun getReplaceURLMap() {
        val expectedMap = HashMap<String,String>()
        expectedMap["siteId"] = "US_Tuscany"
        expectedMap["language"] = "en"
        expectedMap["country"] = "US"
        expectedMap["cartId"]="current"
        assertEquals(expectedMap,getCartRequest.getReplaceURLMap())
    }

    @Test
    fun `header should be as expected`() {

        ECSDataHolder.authToken = "authstring"
        ECSConfiguration.INSTANCE.setAuthToken("authstring")

        val expectedMap = HashMap<String,String>()


        expectedMap["Accept"] = "application/json"
        expectedMap["Api-Key"] = "yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP"
        expectedMap["Api-Version"] = "1"
        expectedMap["Authorization"] = "bearer authstring"
        Assert.assertEquals(expectedMap, getCartRequest.getHeader())
    }

    @Test
    fun onResponseForAddToCartSuccessWithProductMoreQuantityThanStockJson() {

        val callBack = object : ECSCallback<ECSShoppingCart, ECSError>{
            override fun onResponse(result: ECSShoppingCart) {
              assertNotNull(result.data?.attributes?.notifications?.get(0)?.id)
              assertNotNull(result.data?.attributes?.deliveryAddress?.id)
                assertNotNull(result.data?.attributes?.items?.get(0)?.ctn)
            }

            override fun onFailure(ecsError: ECSError) {
                 fail()
            }
        }
        getCartRequest = GetCartRequest(callBack)

        val errorString =   ClassLoader.getSystemResource("pil/cart/Success/AddToCartSuccessWithProductMoreQuantityThanStock.json").readText()
        val jsonObject = JSONObject(errorString)
        getCartRequest.onResponse(jsonObject)
    }

    @Test
    fun onResponseForDeleteProductFromCartSuccessJson() {

        val callBack = object : ECSCallback<ECSShoppingCart, ECSError>{
            override fun onResponse(result: ECSShoppingCart) {
                assertEquals(0,result.data?.attributes?.items?.size)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        getCartRequest = GetCartRequest(callBack)

        val errorString =   ClassLoader.getSystemResource("pil/cart/Success/DeleteProductFromCartSuccess.json").readText()
        val jsonObject = JSONObject(errorString)
        getCartRequest.onResponse(jsonObject)
    }

    @Test
    fun onResponseForFetchCartGermanySuccessJson() {

        val callBack = object : ECSCallback<ECSShoppingCart, ECSError>{
            override fun onResponse(result: ECSShoppingCart) {
                assertNotNull(result.data?.attributes?.deliveryMode?.id)
                assertNotEquals(0,result.data?.attributes?.applicableDeliveryModes?.size)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        getCartRequest = GetCartRequest(callBack)

        val errorString =   ClassLoader.getSystemResource("pil/cart/Success/FetchCartGermany.json").readText()
        val jsonObject = JSONObject(errorString)
        getCartRequest.onResponse(jsonObject)
    }


    @Test
    fun onResponseFetchCartUSSuccessJson() {

        val callBack = object : ECSCallback<ECSShoppingCart, ECSError>{
            override fun onResponse(result: ECSShoppingCart) {
                assertNotEquals(0,result.data?.attributes?.promotions?.appliedProductPromotions?.size)
                assertNotEquals(0,result.data?.attributes?.promotions?.appliedPromotions?.size)

                assertEquals("ECS PIL Voucher Description",result.data?.attributes?.appliedVouchers?.get(0)?.name)
                assertEquals("ECS PIL Voucher Promotion",result.data?.attributes?.appliedVouchers?.get(0)?.description)
                assertEquals(10.0,result.data?.attributes?.appliedVouchers?.get(0)?.value?.value)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        getCartRequest = GetCartRequest(callBack)

        val errorString =   ClassLoader.getSystemResource("pil/cart/Success/FetchCartUS.json").readText()
        val jsonObject = JSONObject(errorString)
        getCartRequest.onResponse(jsonObject)
    }


    @Test
    fun `Fetch Cart US Success Json with no delivery address`() {

        val callBack = object : ECSCallback<ECSShoppingCart, ECSError>{
            override fun onResponse(result: ECSShoppingCart) {
                assertNotEquals(0,result.data?.attributes?.promotions?.appliedProductPromotions?.size)
                assertNotEquals(0,result.data?.attributes?.promotions?.appliedPromotions?.size)

                assertEquals("ECS PIL Voucher Description",result.data?.attributes?.appliedVouchers?.get(0)?.name)
                assertEquals("ECS PIL Voucher Promotion",result.data?.attributes?.appliedVouchers?.get(0)?.description)
                assertEquals(10.0,result.data?.attributes?.appliedVouchers?.get(0)?.value?.value)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }
        getCartRequest = GetCartRequest(callBack)

        val errorString =   ClassLoader.getSystemResource("pil/cart/Success/FetchCartNoDeliveryAddress.json").readText()
      // todo json need to be replaced with new one, this old json has format error

       /* val jsonObject = JSONObject(errorString)
        getCartRequest.onResponse(jsonObject)*/
    }

    private fun setApiKey() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }
}