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

package com.philips.platform.mec.screens.history

import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.model.orders.ECSOrders
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertNotNull

@PrepareForTest(MECOrderHistoryViewModel::class,ECSOrderDetailForOrdersCallback::class,ECSOrderHistoryCallback::class)
@RunWith(PowerMockRunner::class)
class MECOrderHistoryRepositoryTest {

    lateinit var mECOrderHistoryRepository : MECOrderHistoryRepository

    @Mock
    lateinit var ecServiceMock: ECSServices

    @Mock
    lateinit var ecsOrdersMock : ECSOrders

    @Mock
    lateinit var ecsOrderHistoryCallbackMock: ECSOrderHistoryCallback

    @Mock
    lateinit var ecsOrderDetailForOrdersCallbackMock: ECSOrderDetailForOrdersCallback

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mECOrderHistoryRepository = MECOrderHistoryRepository(ecServiceMock)
    }

    @Test
    fun fetchOrderSummary() {
        mECOrderHistoryRepository.fetchOrderHistory(0,20,ecsOrderHistoryCallbackMock)
        Mockito.verify(ecServiceMock).fetchOrderHistory(0,20,ecsOrderHistoryCallbackMock)
    }

    @Test
    fun fetchOrderDetail() {
        mECOrderHistoryRepository.fetchOrderDetail(ecsOrdersMock,ecsOrderDetailForOrdersCallbackMock)
        Mockito.verify(ecServiceMock).fetchOrderDetail(ecsOrdersMock,ecsOrderDetailForOrdersCallbackMock)
    }

    @Test
    fun getEcsService() {
        assertNotNull(mECOrderHistoryRepository.ecsService)
    }
}