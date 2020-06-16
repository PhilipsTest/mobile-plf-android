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

package com.philips.platform.mec.screens.shoppingCart

import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.cart.BasePriceEntity
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.model.products.PriceEntity
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@PrepareForTest(ECSShoppingCartRepository::class, ECSShoppingCartCallback::class, ECSVoucherCallback::class, ECSCallback::class)
@RunWith(PowerMockRunner::class)
class EcsShoppingCartViewModelTest {

    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel

    @Mock
    lateinit var appInfraMock: AppInfraInterface

    @Mock
    lateinit var ecsShoppingCartRepositoryMock: ECSShoppingCartRepository


    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var ecsShoppingCartCallbackMock: ECSShoppingCartCallback

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.appinfra=appInfraMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        ecsShoppingCartRepositoryMock.ecsServices = ecsServicesMock
        ecsShoppingCartRepositoryMock.ecsShoppingCartCallback = ecsShoppingCartCallbackMock

        ecsShoppingCartViewModel = EcsShoppingCartViewModel()
        ecsShoppingCartViewModel.ecsServices = ecsServicesMock

        ecsShoppingCartViewModel.ecsShoppingCartRepository = ecsShoppingCartRepositoryMock
    }


    @Test
    fun testCreateShoppingCart() {

        ecsShoppingCartViewModel.createShoppingCart("")
        //  Mockito.verify(ecsServicesMock).createShoppingCart(ArgumentMatchers.any(ECSCallback::class.java) as ECSCallback<ECSShoppingCart, Exception>)
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce())
                .createShoppingCart(ArgumentMatchers.any())
        // Mockito.verify(ecsShoppingCartRepositoryMock, Mockito.atLeastOnce()).createCart(ArgumentMatchers.any())
    }


    @Test
    fun testGetShoppingCart() {

//        Mockito.`when`(MECutility.isExistingUser()) .thenReturn(true)
        ecsShoppingCartViewModel.getShoppingCart()

       // Mockito.verify(ecsServicesMock, Mockito.atLeastOnce()).fetchShoppingCart(ArgumentMatchers.any())
       //  Mockito.verify(ecsShoppingCartRepositoryMock, Mockito.atLeastOnce()).fetchShoppingCart()

    }


    @Test
    fun TestUpdateQuantity() {
        val map = HashMap<String, String>()
        map.put("key1", "value1")

        var eCSentry = ECSEntries()
        var mECSProduct = ECSProduct()
        mECSProduct.code = "ConsignmentCode123ABC"
        var priceEntity = PriceEntity()
        priceEntity.value = 12.9
        mECSProduct.price = priceEntity

        eCSentry.product = mECSProduct
        eCSentry.quantity = 2

        var basePriceEntity = BasePriceEntity()
        basePriceEntity.value = 10.7
        eCSentry.basePrice = basePriceEntity


        ecsShoppingCartViewModel.updateQuantity(eCSentry, 3)
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce()).updateShoppingCart(ArgumentMatchers.anyInt(), ArgumentMatchers.anyObject(), ArgumentMatchers.any())

    }

    @Test
    fun TestAddVoucher(){
        ecsShoppingCartViewModel.addVoucher("",MECRequestType.MEC_APPLY_VOUCHER)
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce()).applyVoucher(ArgumentMatchers.anyString(),ArgumentMatchers.any())
    }

    @Test
    fun TestRemoveVoucher(){
        ecsShoppingCartViewModel.removeVoucher("")
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce()).removeVoucher(ArgumentMatchers.anyString(),ArgumentMatchers.any())
    }


    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }
}