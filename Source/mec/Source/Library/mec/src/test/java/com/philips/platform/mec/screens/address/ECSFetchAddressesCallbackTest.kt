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

package com.philips.platform.mec.screens.address

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.mec.any
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import kotlinx.coroutines.channels.consumesAll
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(AddressViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSFetchAddressesCallbackTest{

    @Mock
    lateinit var addressViewModelMock: AddressViewModel
    lateinit var  ecsFetchAddressesCallback  : ECSFetchAddressesCallback

    @Mock
    lateinit var ecsAddressesLiveDataMock : MutableLiveData<List<ECSAddress>>

    @Mock
    lateinit var ecsAddressesMock: List<ECSAddress>

    @Mock
    lateinit var mecErrorLiveDataMock : MutableLiveData<MecError>

    @Mock
    lateinit var authFailureCallbackMock: (Exception?, ECSError?) -> Unit

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        addressViewModelMock.authFailCallback = authFailureCallbackMock
        addressViewModelMock.ecsAddresses = ecsAddressesLiveDataMock
        addressViewModelMock.mecError = mecErrorLiveDataMock
        ecsFetchAddressesCallback = ECSFetchAddressesCallback(addressViewModelMock)
    }

    @Test
    fun `request type should be fetch saved address`() {
        assertEquals(MECRequestType.MEC_FETCH_SAVED_ADDRESSES, ecsFetchAddressesCallback.mECRequestType )
    }

    @Test
    fun `should assign value to live data on success response comes`() {
        ecsFetchAddressesCallback.onResponse(ecsAddressesMock)
        //TODO
       // assertNotNull(ecsAddressesLiveDataMock.value)
    }

    @Mock
    lateinit var errorMock: Exception
    @Mock
    lateinit var ecsErrorMock: ECSError

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Test
    fun `should call auth if call auth failure comes`() {
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        Mockito.`when`(ecsErrorMock.errorcode).thenReturn(ECSErrorEnum.ECSInvalidTokenError.errorCode)
        ecsFetchAddressesCallback.onFailure(errorMock,ecsErrorMock)
        Mockito.verify(userDataInterfaceMock).refreshSession(any(RefreshSessionListener::class.java))
    }

    @Test
    fun `should update error view model when api fails`() {
        ecsFetchAddressesCallback.onFailure(errorMock,null)
        assertNotNull(mecErrorLiveDataMock)
        //TODO
        //assertNotNull(mecErrorMock.value)
    }
}