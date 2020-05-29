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
import com.philips.platform.ecs.microService.model.filter.ECSSortType
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MicroECSServicesTest {

    lateinit var ECSServices: ECSServices

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

    @Mock
    lateinit var ecsCallback : ECSCallback<ECSProducts, ECSError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ECSServices = ECSServices(appInfraMock)
        ECSServices.ecsConfigManager = ecsConfigManagerMock
        ECSServices.ecsProductManager = ecsProductManagerMock
        ECSServices.ecsRetailerManager = ecsRetailerManagerMock
    }

    @Test
    fun `configureECSToGetConfiguration api should call corresponding manager method`() {
        ECSServices.configureECS(ecsCallbackConfigMock)
        Mockito.verify(ecsConfigManagerMock).getConfigObject(ecsCallbackConfigMock)

    }

    @Test
    fun `fetchProducts api should call corresponding manager method`() {

        var productFilter= ProductFilter()
        productFilter.sortType= ECSSortType.priceAscending
        productFilter.stockLevel=ECSStockLevel.InStock
        ECSServices.fetchProducts("FOOD_PREPARATION_CA2",5,0,productFilter,ecsCallback) //TODO
    }

    @Test
    fun `fetchProduct api should call corresponding manager method`() {
        ECSServices.fetchProduct("123",eCSCallbackProductMock)
        Mockito.verify(ecsProductManagerMock).getProductFor("123",eCSCallbackProductMock)
    }

    @Test
    fun `fetchProductSummaries api should call corresponding manager method`() {
        ECSServices.fetchProductSummaries(listOf("123"),ecsCallbackProductListMock)
        Mockito.verify(ecsProductManagerMock).fetchProductSummaries(listOf("123"),ecsCallbackProductListMock)

    }

    @Test
    fun `fetchProductDetails api should call corresponding manager method`() {

        var ecsProduct = ECSProduct(null,"123",null)
        ECSServices.fetchProductDetails(ecsProduct,eCSCallbackNotNullProductMock)
        Mockito.verify(ecsProductManagerMock).fetchProductDetails(ecsProduct,eCSCallbackNotNullProductMock)
    }

    @Test
    fun `fetchRetailers for ctn api should call corresponding manager method`() {
        ECSServices.fetchRetailers("123",ecsCallbackRetailerListMock)
        Mockito.verify(ecsRetailerManagerMock).fetchRetailers("123",ecsCallbackRetailerListMock)
    }

    @Test
    fun `fetchRetailers for Product api should call corresponding manager method`() {
        var ecsProduct = ECSProduct(null,"123",null)
        ECSServices.fetchRetailers(ecsProduct,ecsCallbackRetailerListMock)
        Mockito.verify(ecsRetailerManagerMock).fetchRetailers(ecsProduct.id,ecsCallbackRetailerListMock)
    }
}