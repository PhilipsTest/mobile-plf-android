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

import androidx.lifecycle.MutableLiveData
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.securestorage.SecureStorageInterface
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.model.orders.ECSOrders
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
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
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@PrepareForTest(MECOrderHistoryViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSOrderDetailForOrdersCallbackTest {



    lateinit var eCSOrderDetailForOrdersCallback : ECSOrderDetailForOrdersCallback

    @Mock
    lateinit var mecOrderHistoryViewModelMock: MECOrderHistoryViewModel

    @Mock
    lateinit var eCSOrdersMock: ECSOrders

    @Mock
    lateinit var liveDataMock : MutableLiveData<ECSOrders>

    @Mock
    lateinit var errorLiveDataMock : MutableLiveData<MecError>

    @Mock
    lateinit var authFailureCallbackMock: (Exception?, ECSError?) -> Unit

    @Mock
    lateinit var appinfraMock: AppInfraInterface

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Mock
    lateinit var secureStorageMock: SecureStorageInterface


    @Mock
    lateinit var errorMock: Exception

    @Mock
    lateinit var ecsErrorMock: ECSError

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        mecOrderHistoryViewModelMock.ecsOrders = liveDataMock
        mecOrderHistoryViewModelMock.mecError = errorLiveDataMock
        eCSOrderDetailForOrdersCallback = ECSOrderDetailForOrdersCallback(mecOrderHistoryViewModelMock)
    }

    @Test
    fun getMECRequestType() {
       assertNotNull(eCSOrderDetailForOrdersCallback.mECRequestType)
       assertEquals( MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS,eCSOrderDetailForOrdersCallback.mECRequestType)
    }

    @Test
    fun setMECRequestType() {
        eCSOrderDetailForOrdersCallback.mECRequestType = MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS
        assertNotNull(eCSOrderDetailForOrdersCallback.mECRequestType)
    }

    @Test
    fun onResponse() {
        //TODO check if live data is updated
        eCSOrderDetailForOrdersCallback.onResponse(eCSOrdersMock)
       // Mockito.verify(mecOrderHistoryViewModelMock).countDownThread()
        assertNotNull(mecOrderHistoryViewModelMock.ecsOrders)
    }

    @Test
    fun onFailure() {
        //TODO check if live error data is updated
        eCSOrderDetailForOrdersCallback.onFailure(errorMock,ecsErrorMock)
        assertNotNull(mecOrderHistoryViewModelMock.mecError)
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

    @Test
    fun shouldRetryApiCallOnAuthFail() {
        ecsErrorMock.errorcode = 5009
        mecOrderHistoryViewModelMock.authFailCallback = authFailureCallbackMock
        eCSOrderDetailForOrdersCallback.onFailure(errorMock,ecsErrorMock)
        Mockito.verify(mecOrderHistoryViewModelMock).retryAPI(MECRequestType.MEC_FETCH_ORDER_DETAILS_FOR_ORDERS)
    }
}