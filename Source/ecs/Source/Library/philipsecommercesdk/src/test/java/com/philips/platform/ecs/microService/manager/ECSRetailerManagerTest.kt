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

package com.philips.platform.ecs.microService.manager

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.request.GetRetailersInfoRequest
import com.philips.platform.ecs.microService.request.any
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class ECSRetailerManagerTest {

    lateinit var ecsRetailerManager: ECSRetailerManager

    @Mock
    lateinit var requestHandlerMock: RequestHandler

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSRetailerList?, ECSError>

     val validCTN = "HX3631/06"
     val inValidCTN = " HX   3631/06 "
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsRetailerManager = ECSRetailerManager()
    }

    @Test
    fun `fetch retailer should throw exception when locale is not present`() {
        try {
            ecsRetailerManager.fetchRetailers(validCTN,ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5052,e.errorCode)
        }

    }

    @Test
    fun `fetch retailer should throw exception when ctn is not valid even if the locale is present`() {
        ECSDataHolder.locale = "en_US"
        try {
            ecsRetailerManager.fetchRetailers(inValidCTN,ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(6019,e.errorCode)
        }

    }

    @Test
    fun `fetch retailer should should handle getRetailerInfo Request for valid ctn and locale`() {
        ECSDataHolder.locale = "en_US"
        ecsRetailerManager.requestHandler = requestHandlerMock
        ecsRetailerManager.fetchRetailers(validCTN,ecsCallbackMock)
        Mockito.verify(requestHandlerMock).handleRequest(any(GetRetailersInfoRequest::class.java))

    }
}