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

import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.securestorage.SecureStorageInterface
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(PowerMockRunner::class)
class MECOrderHistoryServiceTest {

    lateinit var mECOrderHistoryService : MECOrderHistoryService

    @Mock
    lateinit var appinfraMock: AppInfraInterface

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Mock
    lateinit var secureStorageMock: SecureStorageInterface

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mECOrderHistoryService = MECOrderHistoryService()
    }

    @Test
    fun shouldReturnFormattedDate() {
        assertEquals("Thursday Mar 12, 2020",mECOrderHistoryService.getFormattedDate("2020-03-12T05:12:23+0000"))
    }

    @Test
    fun shouldReturnEmpty() {
        assertEquals("",mECOrderHistoryService.getFormattedDate(null))
    }

    @Test
    fun shouldReturnEmptyOnBadDate() {
        assertEquals("",mECOrderHistoryService.getFormattedDate(""))
    }

    @Test
    fun assertFalseShouldCallAuth() {
        setAuthNotRequired()
        assertFalse{ mECOrderHistoryService.shouldCallAuth() }
    }

    @Test
    fun assertTrueShouldCallAuth() {
        setAuthRequired()
        assertTrue{ mECOrderHistoryService.shouldCallAuth() }
    }

    private fun setAuthNotRequired() {
        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn(UserLoggedInState.USER_LOGGED_IN)
        var hashMap = HashMap<String, Any>()
        hashMap.put(UserDetailConstants.EMAIL, "NONE")
        Mockito.`when`(userDataInterfaceMock.getUserDetails(ArgumentMatchers.any())).thenReturn(hashMap)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        Mockito.`when`(appinfraMock.secureStorage).thenReturn(secureStorageMock)
        MECDataHolder.INSTANCE.appinfra = appinfraMock
        ECSConfiguration.INSTANCE.setAuthToken("123")
    }

    private fun setAuthRequired() {
        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn(UserLoggedInState.USER_LOGGED_IN)
        var hashMap = HashMap<String, Any>()
        hashMap.put(UserDetailConstants.EMAIL, "NONE")
        Mockito.`when`(userDataInterfaceMock.getUserDetails(ArgumentMatchers.any())).thenReturn(hashMap)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        Mockito.`when`(appinfraMock.secureStorage).thenReturn(secureStorageMock)
        MECDataHolder.INSTANCE.appinfra = appinfraMock
        ECSConfiguration.INSTANCE.setAuthToken(null)
    }


    @Test
    fun shouldTestGetOrderMapForSameDayOrder() {

        var dateOrdersMap =LinkedHashMap<String, MutableList<ECSOrders>>()

        var ecsOrderList = mutableListOf<ECSOrders>()

        var ecsOrders1 = ECSOrders()
        ecsOrders1.placed = "2020-03-12T05:12:23+0000"

        var ecsOrders2 = ECSOrders()
        ecsOrders2.placed = "2020-03-12T05:12:23+0000"

        var ecsOrders3 = ECSOrders()
        ecsOrders3.placed = "2020-03-13T05:12:23+0000"

        ecsOrderList.add(ecsOrders1)
        ecsOrderList.add(ecsOrders2)
        ecsOrderList.add(ecsOrders3)

        mECOrderHistoryService.getDateOrderMap(dateOrdersMap,ecsOrderList)

        assertEquals(2,dateOrdersMap.size)

    }
}