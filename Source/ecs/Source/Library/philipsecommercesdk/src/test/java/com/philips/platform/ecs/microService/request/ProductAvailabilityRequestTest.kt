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
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class ProductAvailabilityRequestTest{

    lateinit var productAvailabilityRequest : ProductAvailabilityRequest

    @Mock
    lateinit var ecsCallBackMock : ECSCallback<Boolean, ECSError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        productAvailabilityRequest = ProductAvailabilityRequest("pabitrabapi1@gmail.com","HX2010/00",ecsCallBackMock)
    }

    @Test
    fun `service id should be notify me`() {
        assertEquals(ECSConstants.SERVICEID_ECS_NOTIFY_ME,productAvailabilityRequest.getServiceID())
    }

    @Test
    fun `Request method should be post`() {
        assertEquals(Request.Method.POST,productAvailabilityRequest.getRequestMethod())
    }

    @Test
    fun `should trigger false call back if response is null`() {
        productAvailabilityRequest.onResponse(null)
        Mockito.verify(ecsCallBackMock).onResponse(false)
    }

    @Test
    fun `should trigger false call back when response gets a non parceble json`() {
        val successFalseString =   ClassLoader.getSystemResource("pil/notifyMe/success/notify_me_not_parceble.json").readText()
        val jsonObjectFalse = JSONObject(successFalseString)
        productAvailabilityRequest.onResponse(jsonObjectFalse)
        Mockito.verify(ecsCallBackMock).onResponse(false)

    }

    @Test
    fun `should trigger false call back when response gets a empty  json`() {
        val successFalseString =   ClassLoader.getSystemResource("pil/notifyMe/success/notify_me_blank_success.json").readText()
        val jsonObjectFalse = JSONObject(successFalseString)
        productAvailabilityRequest.onResponse(jsonObjectFalse)
        Mockito.verify(ecsCallBackMock).onResponse(false)

    }


    @Test
    fun `should trigger value of success when response gets a valid json`() {
        val successTrueString =   ClassLoader.getSystemResource("pil/notifyMe/success/notify_me_true_success.json").readText()
        val jsonObjectTrue = JSONObject(successTrueString)
        productAvailabilityRequest.onResponse(jsonObjectTrue)
        Mockito.verify(ecsCallBackMock).onResponse(true)


        val successFalseString =   ClassLoader.getSystemResource("pil/notifyMe/success/notify_me_false_success.json").readText()
        val jsonObjectFalse = JSONObject(successFalseString)
        productAvailabilityRequest.onResponse(jsonObjectFalse)
        Mockito.verify(ecsCallBackMock).onResponse(false)
    }

    @Test
    fun `request body should be as expected`() {
        val map = HashMap<String, String>()
        map["email"] = "pabitrabapi1@gmail.com"
        map["productId"] = "HX2010/00"
        assertEquals(map,productAvailabilityRequest.getBody())
    }
}