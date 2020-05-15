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

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import org.json.JSONObject
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class GetProductForRequestTest {

    lateinit var getProductForRequest:GetProductForRequest

    var  ctn = "HX3631/06"

    @Mock
    lateinit var  ecsCallbackMock: ECSCallback<ECSProduct?, ECSError>

    @Mock
    lateinit var  ecsProductManagerMock : ECSProductManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ECSDataHolder.locale = "en_US"
        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig
        getProductForRequest = GetProductForRequest(ctn,ecsCallbackMock)
    }


    @Test
    fun `service id should be as expected`() {
        assertEquals("ecs.productDetails",getProductForRequest.getServiceID())
    }

    @Test
    fun `url should be as expected`() {
        var expected = "https://acc.eu-west-1.api.philips.com/commerce-service/product/HX3631_06?siteId=US_Tuscany&language=en&country=US"
        assertEquals(expected,getProductForRequest.getURL())
    }

    @Test
    fun `get URL map should be as expected`() {
        val expectedMap = HashMap<String,String>()
        expectedMap["siteId"] = "US_Tuscany"
        expectedMap["language"] = "en"
        expectedMap["country"] = "US"
        expectedMap["ctn"] = "HX3631_06"

        assertEquals(expectedMap,getProductForRequest.getReplaceURLMap())
    }

    @Test
    fun `header should be as expected`() {
        val expectedMap = HashMap<String,String>()
        expectedMap["Accept"] = "application/json"
        expectedMap["Api-Key"] = "yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP"
        expectedMap["Api-Version"] = "1"
        assertEquals(expectedMap,getProductForRequest.getHeader())
    }

    @Test
    fun `request type should be jSON`() {
        assertEquals(RequestType.JSON,getProductForRequest.getRequestType())
    }

    @Test
    fun `JSON Success Response Listener should not be null`() {
        assertNotNull(getProductForRequest.getJSONSuccessResponseListener())
    }

    @Test
    fun `String Success Response Listener should  be null`() {
        assertNull(getProductForRequest.getStringSuccessResponseListener())
    }

    @Test
    fun `should give null product call back on empty or null response`() {
        getProductForRequest.onResponse(JSONObject(emptyResponse))
        Mockito.verify(ecsCallbackMock).onResponse(null)
    }

    @Test
    fun `should fetch summary for product on success response`() {
        getProductForRequest.ecsProductManager = ecsProductManagerMock
        getProductForRequest.onResponse(JSONObject(successResponse))
        val ecsProduct = JSONObject(successResponse).getData(ECSProduct::class.java)
        Mockito.verify(ecsProductManagerMock).getSummaryForSingleProduct(ecsProduct!!,ecsCallbackMock)
    }

    var emptyResponse = "{}"

    var successResponse = "{\n" +
            "  \"id\": \"HX3631/06\",\n" +
            "  \"type\": \"commerceProduct\",\n" +
            "  \"attributes\": {\n" +
            "    \"title\": \"\",\n" +
            "    \"image\": \"https://images.philips.com/is/image/PhilipsConsumer/HX3631_06-IMS-en_US\",\n" +
            "    \"availability\": {\n" +
            "      \"status\": \"IN_STOCK\",\n" +
            "      \"quantity\": 249933\n" +
            "    },\n" +
            "    \"deliveryTime\": \"3-9 business days\",\n" +
            "    \"price\": {\n" +
            "      \"currency\": \"USD\",\n" +
            "      \"formattedValue\": \"14.99 \$\",\n" +
            "      \"value\": 14.99\n" +
            "    },\n" +
            "    \"discountPrice\": {\n" +
            "      \"currency\": \"USD\",\n" +
            "      \"formattedValue\": \"14.99 \$\",\n" +
            "      \"value\": 14.99\n" +
            "    },\n" +
            "    \"references\": [],\n" +
            "    \"taxRelief\": false\n" +
            "  }\n" +
            "}"


    //TODO centralize
    private fun <T> any(type : Class<T>): T {
        Mockito.any(type)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}