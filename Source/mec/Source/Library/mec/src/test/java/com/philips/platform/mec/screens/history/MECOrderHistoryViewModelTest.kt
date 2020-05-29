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
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.integration.ECSOAuthProvider
import com.philips.platform.ecs.model.oauth.ECSOAuthData
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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.test.assertNotNull

@PrepareForTest(MECOrderHistoryService::class,AppInfraInterface::class,ECSOrderHistoryCallback::class,MECOrderHistoryRepository::class)
@RunWith(PowerMockRunner::class)
class MECOrderHistoryViewModelTest {


    lateinit var mecOrderHistoryViewModel: MECOrderHistoryViewModel

    @Mock
    lateinit var ecsServiceMock: ECSServices



    @Mock
    lateinit var secureStorageMock: SecureStorageInterface

    @Mock
    lateinit var orderHistoryServiceMock: MECOrderHistoryService

    @Mock
    lateinit var appinfraMock: AppInfraInterface

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Mock
    lateinit var mECOrderHistoryRepositoryMock: MECOrderHistoryRepository

    @Mock
    lateinit var ecsOrderHistoryCallback: ECSOrderHistoryCallback

    @Mock
    lateinit var ecsOrdersMock: ECSOrders

    @Mock
    lateinit var errorMock: Exception

    @Mock
    lateinit var ecsErrorMock: ECSError

    @Mock
    lateinit var errorLiveDataMock : MutableLiveData<MecError>



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.eCSServices = ecsServiceMock

        mecOrderHistoryViewModel = MECOrderHistoryViewModel()
        mecOrderHistoryViewModel.mECOrderHistoryService = orderHistoryServiceMock
        mECOrderHistoryRepositoryMock.ecsService = ecsServiceMock
        mecOrderHistoryViewModel.mecOrderHistoryRepository = mECOrderHistoryRepositoryMock
        mecOrderHistoryViewModel.ecsOrderHistoryCallback = ecsOrderHistoryCallback
        mecOrderHistoryViewModel.mecError = errorLiveDataMock

    }

    @Test
    fun returnedFunctionUnitShouldNotBeNullForFOrderHistortyCall() {
        val selectAPIcall = mecOrderHistoryViewModel.selectAPIcall(MECRequestType.MEC_FETCH_ORDER_HISTORY)
        assertNotNull(selectAPIcall)
    }

    @Test
    fun shouldDoAuthForOrderHistory() {

        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN)

        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        Mockito.`when`(appinfraMock.secureStorage).thenReturn(secureStorageMock)
        MECDataHolder.INSTANCE.appinfra = appinfraMock

        mecOrderHistoryViewModel.fetchOrderHistory(0,20)
        Mockito.verify(ecsServiceMock).hybrisOAthAuthentication(any(ECSOAuthProvider::class.java), any())
    }

    @Test
    fun shouldFetchOrderHistory() {

        setAuthNotRequired()

        mecOrderHistoryViewModel.fetchOrderHistory(0,20)
        Mockito.verify(ecsServiceMock).fetchOrderHistory(0,20,ecsOrderHistoryCallback)
    }

    private fun setAuthNotRequired() {
        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn(UserLoggedInState.USER_LOGGED_IN)
        var hashMap = HashMap<String, Any>()
        hashMap.put(UserDetailConstants.EMAIL, "NONE")
        Mockito.`when`(userDataInterfaceMock.getUserDetails(any())).thenReturn(hashMap)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        Mockito.`when`(appinfraMock.secureStorage).thenReturn(secureStorageMock)
        MECDataHolder.INSTANCE.appinfra = appinfraMock
        ECSConfiguration.INSTANCE.setAuthToken("123")
    }

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSOrders, Exception>

    @Test
    fun shouldFetchOrderDetail() {

        mecOrderHistoryViewModel.fetchOrderDetail(ecsOrdersMock,ecsCallbackMock)
        Mockito.verify(ecsServiceMock).fetchOrderDetail(ecsOrdersMock,ecsCallbackMock)
    }

    @Mock
    lateinit var ecsOAuthDataMock: ECSOAuthData

    @Test
    fun shouldFetchHistoryOnAuthSuccessResponse() {
        setAuthNotRequired()
        mecOrderHistoryViewModel.onResponse(ecsOAuthDataMock)
        Mockito.verify(ecsServiceMock).fetchOrderHistory(0,20,ecsOrderHistoryCallback)
    }



    @Test
    fun onFailure() {
        //TODO check if live error data is updated
        mecOrderHistoryViewModel.onFailure(errorMock,ecsErrorMock)
        assertNotNull(mecOrderHistoryViewModel.mecError)
    }
}