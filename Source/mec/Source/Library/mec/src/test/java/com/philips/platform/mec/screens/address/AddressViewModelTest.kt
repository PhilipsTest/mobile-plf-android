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

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.ecs.model.address.ECSDeliveryMode
import com.philips.platform.mec.any
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(AddressRepository::class,ECSCreateAddressCallBack::class,ECSFetchAddressesCallback::class,DeleteAddressCallBack::class,
        SetDeliveryAddressCallBack::class,UpdateAddressCallBack::class,ECSSetDeliveryModesCallback::class)
@RunWith(PowerMockRunner::class)
class AddressViewModelTest {

    @Mock
    lateinit var ecsAddressMock: ECSAddress

    lateinit var addressViewModel : AddressViewModel

    @Mock
    lateinit var addressRepositoryMock : AddressRepository

    @Mock
    lateinit var ecsServicesMock : ECSServices

    @Mock
    lateinit var appInfraMock: AppInfra

    @Mock
    lateinit var eCSCreateAddressCallBackMock : ECSCreateAddressCallBack

    @Mock
    lateinit var eCSFetchAddressesCallbackMock : ECSFetchAddressesCallback

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.appinfra = appInfraMock
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        addressRepositoryMock.ecsServices = ecsServicesMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        addressViewModel = AddressViewModel()
    }

    @Test
    fun `fetch Addresses should call ecs fetch Address`() {
        addressViewModel.fetchAddresses()
        Mockito.verify(ecsServicesMock).fetchSavedAddresses(any(ECSFetchAddressesCallback::class.java))
    }

    @Test
    fun `create address should call ecs create address and store address in a member variable`() {
        addressViewModel.ecsCreateAddressCallBack = eCSCreateAddressCallBackMock
        addressViewModel.createAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).createAddress(ecsAddressMock,eCSCreateAddressCallBackMock)
        assertEquals(ecsAddressMock,addressViewModel.paramEcsAddress)
    }

    @Test
    fun `create and fetch address should call ecs create address and store address in a member variable`() {
        addressViewModel.ecsFetchAddressesCallback = eCSFetchAddressesCallbackMock
        addressViewModel.createAndFetchAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).createAndFetchAddress(ecsAddressMock,eCSFetchAddressesCallbackMock)
        assertEquals(ecsAddressMock,addressViewModel.paramEcsAddress)
    }

    @Test
    fun `create and fetch call back type should be as expected`() {
        addressViewModel.createAndFetchAddress(ecsAddressMock)
        assertEquals(MECRequestType.MEC_CREATE_AND_FETCH_ADDRESS,addressViewModel.ecsFetchAddressesCallback.mECRequestType)
    }

    @Mock
    lateinit var deleteAddressCallBackMock : DeleteAddressCallBack

    @Test
    fun `delete and fetch address should call ecs create address and store address in a member variable`() {
        addressViewModel.deleteAddressCallBack = deleteAddressCallBackMock
        addressViewModel.deleteAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).deleteAddress(ecsAddressMock,deleteAddressCallBackMock)
    }

    @Test
    fun `set and fetch delivery address should call ecs set and fetch delivery address and store address in a member variable`() {
        addressViewModel.ecsFetchAddressesCallback = eCSFetchAddressesCallbackMock
        addressViewModel.setAndFetchDeliveryAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).setAndFetchDeliveryAddress(true,ecsAddressMock,eCSFetchAddressesCallbackMock)
        assertEquals(ecsAddressMock,addressViewModel.paramEcsAddress)
    }

    @Test
    fun `set and fetch delivery call back type should be as expected`() {
        addressViewModel.setAndFetchDeliveryAddress(ecsAddressMock)
        assertEquals(MECRequestType.MEC_SET_AND_FETCH_DELIVERY_ADDRESS,addressViewModel.ecsFetchAddressesCallback.mECRequestType)
    }

    @Mock
    lateinit var setDeliveryAddressCallBackMock : SetDeliveryAddressCallBack

    @Test
    fun `set  delivery address should call ecs set and fetch delivery address and store address in a member variable`() {
        addressViewModel.setDeliveryAddressCallBack = setDeliveryAddressCallBackMock
        addressViewModel.setDeliveryAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).setDeliveryAddress(true,ecsAddressMock,setDeliveryAddressCallBackMock)
        assertEquals(ecsAddressMock,addressViewModel.paramEcsAddress)
    }

    @Test
    fun `update and fetch address should call ecs update address and store address in a member variable`() {
        addressViewModel.ecsFetchAddressesCallback = eCSFetchAddressesCallbackMock
        addressViewModel.updateAndFetchAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).updateAndFetchAddress(ecsAddressMock,eCSFetchAddressesCallbackMock)
        assertEquals(ecsAddressMock,addressViewModel.paramEcsAddress)
    }

    @Test
    fun `update and fetch address request type should be as expected`() {
        addressViewModel.updateAndFetchAddress(ecsAddressMock)
        assertEquals(MECRequestType.MEC_UPDATE_AND_FETCH_ADDRESS,addressViewModel.ecsFetchAddressesCallback.mECRequestType)
    }

    @Mock
    lateinit var updateAddressCallBackMock : UpdateAddressCallBack

    @Test
    fun `update address should call ecs update address and store address in a memeber variable`() {
        addressViewModel.updateAddressCallBack = updateAddressCallBackMock
        addressViewModel.updateAddress(ecsAddressMock)
        Mockito.verify(ecsServicesMock).updateAddress(ecsAddressMock,updateAddressCallBackMock)
        assertEquals(ecsAddressMock,addressViewModel.paramEcsAddress)
    }

    @Test
    fun `fetch delivery mode should fetch ecs deliveryMode`() {
        addressViewModel.fetchDeliveryModes()
        Mockito.verify(ecsServicesMock).fetchDeliveryModes(any(ECSFetchDeliveryModesCallback::class.java))
    }

    @Mock
    lateinit var ecsDeliveryModeMock: ECSDeliveryMode
    @Mock
    lateinit var eCSSetDeliveryModesCallbackMock : ECSSetDeliveryModesCallback

    @Test
    fun `set delivery mode should call ecs set delivery mode and store the delivery mode in a member variable`() {
        addressViewModel.ecsSetDeliveryModesCallback = eCSSetDeliveryModesCallbackMock
        addressViewModel.setDeliveryMode(ecsDeliveryModeMock)
        assertEquals(ecsDeliveryModeMock,addressViewModel.paramEcsDeliveryMode)
        Mockito.verify(ecsServicesMock).setDeliveryMode(ecsDeliveryModeMock,eCSSetDeliveryModesCallbackMock)
    }

    @Mock
    lateinit var userDataInterfaceMock:UserDataInterface

    @Test
    fun `retry api should do auth call`() {
        addressViewModel.retryAPI(MECRequestType.MEC_UPDATE_AND_FETCH_ADDRESS)
        Mockito.verify(userDataInterfaceMock).refreshSession(any(RefreshSessionListener::class.java))
    }
}