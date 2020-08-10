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
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class UpdateCartRequestTest{


    @Mock
    lateinit var ecsCallBackMock : ECSCallback<ECSShoppingCart, ECSError>

    lateinit var updateCartRequest: UpdateCartRequest

    @Before
    fun setUp() {
        updateCartRequest = UpdateCartRequest("0",2,ecsCallBackMock)
    }

    @Test
    fun `service ID should be as expected`() {
        assertEquals(ECSConstants.SERVICEID_ECS_UPDATE_CART,updateCartRequest.getServiceID())
    }

    @Test
    fun `request method should be as expected`() {
        assertEquals(Request.Method.PUT,updateCartRequest.getRequestMethod())
    }

    @Test
    fun `url  replace map should be as expected`() {
        ECSDataHolder.locale = "en_US"
        val ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig


        val expectedMap = HashMap<String,String>()
        expectedMap["siteId"] = "US_Tuscany"
        expectedMap["language"] = "en"
        expectedMap["country"] = "US"
        expectedMap["entryNumber"] = "0"
        expectedMap["quantity"] = "2"
        expectedMap["cartId"]="current"
        assertEquals(expectedMap, updateCartRequest.getReplaceURLMap())
    }
}