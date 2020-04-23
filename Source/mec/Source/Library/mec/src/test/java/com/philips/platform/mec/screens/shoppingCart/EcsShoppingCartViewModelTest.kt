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

import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSShoppingCartRepository::class,ECSShoppingCartCallback::class,ECSVoucherCallback::class,ECSCallback::class)
@RunWith(PowerMockRunner::class)
class EcsShoppingCartViewModelTest {

    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel

    @Mock
    lateinit var ecsShoppingCartRepositoryMock: ECSShoppingCartRepository

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var ecsShoppingCartCallbackMock: ECSShoppingCartCallback

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        ecsShoppingCartRepositoryMock.ecsServices = ecsServicesMock
        ecsShoppingCartRepositoryMock.ecsShoppingCartCallback = ecsShoppingCartCallbackMock

        ecsShoppingCartViewModel = EcsShoppingCartViewModel()
        ecsShoppingCartViewModel.ecsServices = ecsServicesMock
        ecsShoppingCartViewModel.ecsShoppingCartRepository = ecsShoppingCartRepositoryMock
    }

    //TODO test cases
    @Test
    fun testCreateShoppingCart() {

    }

    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }
}