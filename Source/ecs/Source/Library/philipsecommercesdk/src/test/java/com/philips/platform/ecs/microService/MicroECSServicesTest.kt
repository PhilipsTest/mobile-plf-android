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
import com.philips.platform.ecs.microService.manager.ECSCartManager
import com.philips.platform.ecs.microService.manager.ECSConfigManager
import com.philips.platform.ecs.microService.manager.ECSProductManager
import com.philips.platform.ecs.microService.manager.ECSRetailerManager
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.cart.ECSItem
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

    lateinit var eCSServices: ECSServices

    @Mock
    lateinit var ecsConfigManagerMock : ECSConfigManager
    @Mock
    lateinit var ecsProductManagerMock : ECSProductManager
    @Mock
    lateinit var ecsRetailerManagerMock: ECSRetailerManager

    @Mock
    lateinit var cartManagerMock: ECSCartManager

    @Mock
    lateinit var ecsCallBackBooleanMock: ECSCallback<Boolean, ECSError>

    @Mock
    lateinit var ecsCallbackConfigMock: ECSCallback<ECSConfig, ECSError>

    @Mock
    lateinit var eCSCallbackProductMock:ECSCallback<ECSProduct?, ECSError>

    @Mock
    lateinit var eCSCallbackNotNullProductMock:ECSCallback<ECSProduct, ECSError>


    @Mock
    lateinit var ecsCallbackRetailerListMock: ECSCallback<ECSRetailerList?, ECSError>

    @Mock
    lateinit var appInfraMock: AppInfra

    @Mock
    lateinit var ecsProductsCallback : ECSCallback<ECSProducts, ECSError>


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        eCSServices = ECSServices(appInfraMock)
        eCSServices.ecsConfigManager = ecsConfigManagerMock
        eCSServices.ecsProductManager = ecsProductManagerMock
        eCSServices.ecsRetailerManager = ecsRetailerManagerMock
        eCSServices.ecsCartManager = cartManagerMock
    }

    @Test
    fun `configureECSToGetConfiguration api should call corresponding manager method`() {
        eCSServices.configureECS(ecsCallbackConfigMock)
        Mockito.verify(ecsConfigManagerMock).getConfigObject(ecsCallbackConfigMock)

    }

    @Test
    fun `fetchProducts api should call corresponding manager method`() {

        var productFilter= ProductFilter()
        productFilter.sortType= ECSSortType.priceAscending
        productFilter.stockLevel=ECSStockLevel.InStock
        eCSServices.fetchProducts("FOOD_PREPARATION_CA2",5,0,productFilter,ecsProductsCallback)
    }

    @Test
    fun `fetchProduct api should call corresponding manager method`() {
        eCSServices.fetchProduct("123",eCSCallbackProductMock)
        Mockito.verify(ecsProductManagerMock).getProductFor("123",eCSCallbackProductMock)
    }

    @Test
    fun `fetchProductSummaries api should call corresponding manager method`() {
        eCSServices.fetchProductSummaries(listOf("123"),ecsProductsCallback)
        Mockito.verify(ecsProductManagerMock).fetchProductSummaries(listOf("123"),ecsProductsCallback)

    }

    @Test
    fun `fetchProductDetails api should call corresponding manager method`() {

        var ecsProduct = ECSProduct(null,"123",null)
        eCSServices.fetchProductDetails(ecsProduct,eCSCallbackNotNullProductMock)
        Mockito.verify(ecsProductManagerMock).fetchProductDetails(ecsProduct,eCSCallbackNotNullProductMock)
    }

    @Test
    fun `fetchRetailers for ctn api should call corresponding manager method`() {
        eCSServices.fetchRetailers("123",ecsCallbackRetailerListMock)
        Mockito.verify(ecsRetailerManagerMock).fetchRetailers("123",ecsCallbackRetailerListMock)
    }

    @Mock
    lateinit var  ecsCartCallBackMock:ECSCallback<ECSShoppingCart, ECSError>

    @Test
    fun `create shopping cart  api should call corresponding manager method`() {
        eCSServices.createShoppingCart("123  ",ecsCallback = ecsCartCallBackMock)
        Mockito.verify(cartManagerMock).createECSShoppingCart("123",ecsCallback = ecsCartCallBackMock)
    }

    @Test
    fun `fetch shopping cart  api should call corresponding manager method`() {
        eCSServices.fetchShoppingCart(ecsCallback = ecsCartCallBackMock)
        Mockito.verify(cartManagerMock).fetchShoppingCart(ecsCallback = ecsCartCallBackMock)
    }

    @Test
    fun `add product to shopping cart  api should call corresponding manager method`() {
        eCSServices.addProductToShoppingCart("123  ",ecsCallback = ecsCartCallBackMock)
        Mockito.verify(cartManagerMock).addProductToShoppingCart("123",ecsCallback = ecsCartCallBackMock)
    }

    @Test
    fun `update shopping cart  api should call corresponding manager method`() {
        val item:ECSItem = ECSItem(null,null, "entry_id1",null,"HD9648/90",null,null,null,null)
        eCSServices.updateShoppingCart(item,2,ecsCallback = ecsCartCallBackMock)
        Mockito.verify(cartManagerMock).updateShoppingCart(item.entryNumber,2,ecsCallback = ecsCartCallBackMock)
    }


    @Mock
    lateinit var  ecsBooleanCallBackMock:ECSCallback<Boolean, ECSError>

    @Test
    fun `register product availability should call product maanger register product availability`() {
        eCSServices.registerForProductAvailability("pabitrakumar.sahoo@philips.com"," HX2345/00 ",ecsBooleanCallBackMock)
        Mockito.verify(ecsProductManagerMock).registerForProductAvailability("pabitrakumar.sahoo@philips.com","HX2345/00",ecsBooleanCallBackMock)
    }
}