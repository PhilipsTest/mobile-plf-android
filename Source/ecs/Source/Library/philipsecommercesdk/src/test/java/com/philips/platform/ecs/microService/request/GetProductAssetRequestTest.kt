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
import com.philips.platform.ecs.microService.model.product.ECSProduct
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
class GetProductAssetRequestTest {

    private lateinit var getProductAssetRequest :GetProductAssetRequest

    val ecsProduct=  ECSProduct(null,"QP2520/70",null)

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSProduct, ECSError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getProductAssetRequest = GetProductAssetRequest(ecsProduct,ecsCallbackMock)
    }

    @Test
    fun `service ID should be prxclient summary list`() {
        assertEquals("prxclient.assets",getProductAssetRequest.getServiceID())
    }
    @Test
    fun `url mapper should have 3 value and ctns should present as expected`() {
        assertEquals(3,getProductAssetRequest.getReplaceURLMap().size)
        assertEquals("QP2520_70",getProductAssetRequest.getReplaceURLMap()["ctn"])
    }

    @Test
    fun `request type should be jSON`() {
        assertEquals(RequestType.JSON,getProductAssetRequest.getRequestType())
    }

    @Test
    fun `JSON Success Response Listener should not be null`() {
        assertNotNull(getProductAssetRequest.getJSONSuccessResponseListener())
    }

    @Test
    fun `String Success Response Listener should  be null`() {
        assertNull(getProductAssetRequest.getStringSuccessResponseListener())
    }

    @Test
    fun `get header should be null`() {
        assertNull(getProductAssetRequest.getHeader())
    }

    @Test
    fun `handle success response`() {
        getProductAssetRequest.onResponse(JSONObject(successJsonResponse))

        assertNotNull(ecsProduct.assets)
        Mockito.verify(ecsCallbackMock).onResponse(ecsProduct)
    }

    var errorJSONCTNNotFoun = "{\n" +
            "\"ERROR\": {\n" +
            "\"statusCode\": 404,\n" +
            "\"errorCode\": \"1200\",\n" +
            "\"errorMessage\": \"CTN not found\",\n" +
            "\"more_info\": \"Please check the upload status of the CTN, the CTN may be invalid or the upload for the CTN has failed\"\n" +
            "}\n" +
            "}"

    var successJsonResponse = "{\n" +
            "\t\"success\": true,\n" +
            "\t\"data\": {\n" +
            "\t\t\"assets\": {\n" +
            "\t\t\t\"asset\": [{},\n" +
            "\t\t\t\t{},\n" +
            "\t\t\t\t{},\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"code\": \"QP2520_70\",\n" +
            "\t\t\t\t\t\"description\": \"Key Award 2 \",\n" +
            "\t\t\t\t\t\"extension\": \"tif\",\n" +
            "\t\t\t\t\t\"extent\": \"845130\",\n" +
            "\t\t\t\t\t\"lastModified\": \"2020-04-04\",\n" +
            "\t\t\t\t\t\"locale\": \"en_US\",\n" +
            "\t\t\t\t\t\"number\": \"001\",\n" +
            "\t\t\t\t\t\"type\": \"KA2\",\n" +
            "\t\t\t\t\t\"asset\": \"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-KA2-en_US-001\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}"

    //TODO centralize
    private fun <T> any(type: Class<T>): T {
        Mockito.any(type)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T
}