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
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class GetProductDisclaimerRequestTest {

    lateinit var getProductDisclaimerRequest : GetProductDisclaimerRequest

    val ecsProduct=  ECSProduct(null,"HX505/01",null)

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSProduct, ECSError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getProductDisclaimerRequest = GetProductDisclaimerRequest(ecsProduct,ecsCallbackMock)
    }

    @Test
    fun `service ID should be prxclient disclaimers`() {
        assertEquals("prxclient.disclaimers",getProductDisclaimerRequest.getServiceID())
    }

    @Test
    fun onErrorResponse() {
    }

    @Test
    fun `should handle success respone with data`() {
        getProductDisclaimerRequest.onResponse(JSONObject(disclaimerSuccessJsonString))
        assertNotNull(ecsProduct.disclaimers)
        assertEquals("DIS40008014", ecsProduct.disclaimers?.disclaimerList!![0].code)
        Mockito.verify(ecsCallbackMock).onResponse(ecsProduct)
    }

    @Test
    fun `should handle success respone with no data`() {
        getProductDisclaimerRequest.onResponse(JSONObject(disclaimerSuccessJsonStringWithNoData))
        assertNull(ecsProduct.disclaimers?.disclaimerList)
        Mockito.verify(ecsCallbackMock).onResponse(ecsProduct)
    }

    @Mock
    lateinit var networkErrorMock : NetworkError
    @Test
    fun `should do error callback when VolleyErrorComes`() {
        getProductDisclaimerRequest.onErrorResponse(networkErrorMock)
        Mockito.verify(ecsCallbackMock).onFailure(any(ECSError::class.java))
    }

    @Test
    fun `url mapper should have 3 value and ctn should present`() {
        assertEquals(3,getProductDisclaimerRequest.getReplaceURLMap().size)
        assertEquals("HX505_01",getProductDisclaimerRequest.getReplaceURLMap()["ctn"])
    }

    @Test
    fun `get header should be null`() {
        assertNull(getProductDisclaimerRequest.getHeader())
    }



    val errorResponseCTNNotFound = "{\n" +
            "\"ERROR\": {\n" +
            "\"statusCode\": 404,\n" +
            "\"errorCode\": \"1200\",\n" +
            "\"errorMessage\": \"CTN not found\",\n" +
            "\"more_info\": \"Please check the upload status of the CTN, the CTN may be invalid or the upload for the CTN has failed\"\n" +
            "}\n" +
            "}"

     val disclaimerSuccessJsonString : String = "{\n" +
             "\"success\": true,\n" +
             "\"data\": {\n" +
             "\"disclaimers\": {\n" +
             "\"disclaimer\": [\n" +
             "{\n" +
             "\"disclaimerText\": \"based on two periods of two-minute brushings per day, on standard mode *vs a manual toothbrush**than a manual toothbrush, and with a leading whitening toothpaste\",\n" +
             "\"code\": \"DIS40008014\",\n" +
             "\"rank\": \"1\",\n" +
             "\"referenceName\": \"based on two periods of two-minute brushings per day\",\n" +
             "\"disclaimElements\": [\n" +
             "{}\n" +
             "]\n" +
             "}\n" +
             "]\n" +
             "}\n" +
             "}\n" +
             "}"

    val disclaimerSuccessJsonStringWithNoData = "{\n" +
            "\"success\": true,\n" +
            "\"data\": {\n" +
            "\"disclaimers\": {}\n" +
            "}\n" +
            "}"
}