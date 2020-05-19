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

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.rest.request.JsonObjectRequest
import com.philips.platform.appinfra.rest.request.RequestQueue
import com.philips.platform.appinfra.rest.request.StringRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder
import junit.framework.Assert.assertNotNull
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@PrepareForTest(Log::class, VolleyLog::class,TextUtils::class, Uri::class,ECSDataHolder::class)
@RunWith(PowerMockRunner::class)
class NetworkControllerTest {

    private lateinit var networkController: NetworkController

    @Mock
    private lateinit var ecsRequestMock: ECSRequestInterface
    
    @Mock
    private lateinit var jsonObjectRequestMock : JsonObjectRequest

    @Mock
    private lateinit var stringRequestMock : StringRequest


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(ecsRequestMock.getMethod()).thenReturn(Request.Method.GET)
        Mockito.`when`(ecsRequestMock.getURL()).thenReturn("http://google.com")

        `mock static classes`()
        networkController = NetworkController()
    }




    private fun `mock static classes`() {
        PowerMockito.mockStatic(Log::class.java)
        PowerMockito.mockStatic(TextUtils::class.java)
        PowerMockito.mockStatic(Uri::class.java)
        PowerMockito.mockStatic(ECSDataHolder::class.java)
    }

    @Mock
    lateinit var appInfraMock : AppInfra
    @Mock
    lateinit var restClientMock : RestInterface
    @Mock
    lateinit var requestQueueMock : RequestQueue

    @Test
    fun `executeRequest should execute jsonRequest`() {

        Mockito.`when`(restClientMock.requestQueue).thenReturn(requestQueueMock)
        Mockito.`when`(appInfraMock.restClient).thenReturn(restClientMock)
        ECSDataHolder.appInfra = appInfraMock

        Mockito.`when`(ecsRequestMock.getAppInfraJSONObject()).thenReturn(jsonObjectRequestMock)
        Mockito.`when`(ecsRequestMock.getRequestType()).thenReturn(RequestType.JSON)
        networkController.executeRequest(ecsRequestMock)

        //JsonObjectRequest
        Mockito.verify(requestQueueMock).add(any(JsonObjectRequest::class.java))
    }

    @Test
    fun `executeRequest should execute stringRequest`() {

        Mockito.`when`(restClientMock.requestQueue).thenReturn(requestQueueMock)
        Mockito.`when`(appInfraMock.restClient).thenReturn(restClientMock)
        ECSDataHolder.appInfra = appInfraMock

        Mockito.`when`(ecsRequestMock.getAppInfraStringRequest()).thenReturn(stringRequestMock)
        Mockito.`when`(ecsRequestMock.getRequestType()).thenReturn(RequestType.STRING)
        networkController.executeRequest(ecsRequestMock)

        Mockito.verify(requestQueueMock).add(any(StringRequest::class.java))
    }
}