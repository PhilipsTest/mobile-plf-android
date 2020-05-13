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
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
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
    fun onResponse() {
    }

    @Test
    fun `url mapper should have 3 value and ctn should present`() {
        assertEquals(3,getProductDisclaimerRequest.getReplaceURLMap().size)
        assertEquals("HX505/01",getProductDisclaimerRequest.getReplaceURLMap()["ctn"])
    }

    @Test
    fun getEcsProduct() {
    }
}