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

package com.philips.platform.ecs.microService

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.manager.ECSConfigManager
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.manager.ECSRetailerManager
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.retailers.ECSRetailerList
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MicroECSServicesTest {

    lateinit var microECSServices: MicroECSServices

    @Mock
    lateinit var ecsConfigManagerMock : ECSConfigManager
    @Mock
    lateinit var ecsProductManagerMock : ECSProductManager
    @Mock
    lateinit var ecsRetailerManagerMock: ECSRetailerManager

    @Mock
    lateinit var ecsCallBackBooleanMock: ECSCallback<Boolean, ECSError>

    @Mock
    lateinit var ecsCallbackConfigMock: ECSCallback<ECSConfig, ECSError>

    @Mock
    lateinit var eCSCallbackProductMock:ECSCallback<ECSProduct?, ECSError>

    @Mock
    lateinit var eCSCallbackNotNullProductMock:ECSCallback<ECSProduct, ECSError>


    @Mock
    lateinit var ecsCallbackProductListMock: ECSCallback<List<ECSProduct>, ECSError>

    @Mock
    lateinit var ecsCallbackRetailerListMock: ECSCallback<ECSRetailerList?, ECSError>

    @Mock
    lateinit var appInfraMock: AppInfra

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        microECSServices = MicroECSServices(appInfraMock)
        microECSServices.ecsConfigManager = ecsConfigManagerMock
        microECSServices.ecsProductManager = ecsProductManagerMock
        microECSServices.ecsRetailerManager = ecsRetailerManagerMock
    }

    @Test
    fun `configureECS api should call corresponding manager method`() {
        microECSServices.configureECS(ecsCallBackBooleanMock)
        Mockito.verify(ecsConfigManagerMock).configureECS(ecsCallBackBooleanMock)
    }

    @Test
    fun `configureECSToGetConfiguration api should call corresponding manager method`() {
        microECSServices.configureECSToGetConfiguration(ecsCallbackConfigMock)
        Mockito.verify(ecsConfigManagerMock).configureECSToGetConfiguration(ecsCallbackConfigMock)

    }

    @Test
    fun `fetchProducts api should call corresponding manager method`() {
        microECSServices.fetchProducts(0,1) //TODO
    }

    @Test
    fun `fetchProduct api should call corresponding manager method`() {
        microECSServices.fetchProduct("123",eCSCallbackProductMock)
        Mockito.verify(ecsProductManagerMock).getProductFor("123",eCSCallbackProductMock)
    }

    @Test
    fun `fetchProductSummaries api should call corresponding manager method`() {
        microECSServices.fetchProductSummaries(listOf("123"),ecsCallbackProductListMock)
        Mockito.verify(ecsProductManagerMock).fetchProductSummaries(listOf("123"),ecsCallbackProductListMock)

    }

    @Test
    fun `fetchProductDetails api should call corresponding manager method`() {

        var ecsProduct = ECSProduct(null,"123",null)
        microECSServices.fetchProductDetails(ecsProduct,eCSCallbackNotNullProductMock)
        Mockito.verify(ecsProductManagerMock).fetchProductDetails(ecsProduct,eCSCallbackNotNullProductMock)
    }

    @Test
    fun `fetchRetailers for ctn api should call corresponding manager method`() {
        microECSServices.fetchRetailers("123",ecsCallbackRetailerListMock)
        Mockito.verify(ecsRetailerManagerMock).fetchRetailers("123",ecsCallbackRetailerListMock)
    }

    @Test
    fun `fetchRetailers for Product api should call corresponding manager method`() {
        var ecsProduct = ECSProduct(null,"123",null)
        microECSServices.fetchRetailers(ecsProduct,ecsCallbackRetailerListMock)
        Mockito.verify(ecsRetailerManagerMock).fetchRetailers(ecsProduct.id,ecsCallbackRetailerListMock)
    }
}