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

import com.android.volley.NetworkError
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
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

    @Mock
    lateinit var appInfraMock : AppInfra


    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        getProductForRequest = GetProductForRequest(ctn,ecsCallbackMock)

        ECSDataHolder.locale = "en_US"
        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig

    }

    private fun setApiKey() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    @Test
    fun `service id should be as expected`() {
        assertEquals("ecs.productDetails",getProductForRequest.getServiceID())
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
        setApiKey()
        val expectedMap = HashMap<String,String>()
        expectedMap["Accept"] = "application/json"
        expectedMap["Api-Key"] = "yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP"
        expectedMap["Api-Version"] = "1"
        assertEquals(expectedMap,getProductForRequest.getHeader())
    }

    @Mock
    lateinit var networkErrorMock :NetworkError
    @Test
    fun `should do error callback when VolleyErrorComes`() {
        getProductForRequest.onErrorResponse(networkErrorMock)
        Mockito.verify(ecsCallbackMock).onFailure(any(ECSError::class.java))
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

}