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

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
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
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class AddToCartRequestTest{

    @Mock
    lateinit var appInfraMock : AppInfra
    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    lateinit var addToCartRequest: AddToCartRequest

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSShoppingCart, ECSError>

    @Before
    fun setUp() {

        addToCartRequest = AddToCartRequest("HD9240/94",2,ecsCallbackMock)
        setApiKey()

        ECSDataHolder.locale = "en_US"
        val ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig
    }

    private fun setApiKey() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    @Test
    fun `service ID shlould be as expected`() {
        assertEquals(ECSConstants.SERVICEID_ECS_ADD_TO_CART,addToCartRequest.getServiceID())
    }

    @Test
    fun `replace url map should be as expected`() {

        val expectedMap = HashMap<String,String>()
        expectedMap["siteId"] = "US_Tuscany"
        expectedMap["language"] = "en"
        expectedMap["country"] = "US"
        expectedMap["ctn"] = "HD9240/94"
        expectedMap["quantity"] = "2"
        expectedMap["cartId"]="current"
        assertEquals(expectedMap, addToCartRequest.getReplaceURLMap())
    }
}