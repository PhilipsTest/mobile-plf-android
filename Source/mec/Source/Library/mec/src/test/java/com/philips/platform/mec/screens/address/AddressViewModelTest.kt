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

import com.philips.platform.ecs.ECSServices
import com.philips.platform.mec.any
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(AddressRepository::class)
@RunWith(PowerMockRunner::class)
class AddressViewModelTest {

    lateinit var addressViewModel : AddressViewModel

    @Mock
    lateinit var addressRepositoryMock : AddressRepository

    @Mock
    lateinit var ecsServicesMock : ECSServices

    @Before
    fun setUp() {
        addressRepositoryMock.ecsServices = ecsServicesMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        addressViewModel = AddressViewModel()
    }

    @Test
    fun `fetch Addresses should call repository fetch Address`() {
        addressViewModel.fetchAddresses()
        Mockito.verify(addressRepositoryMock).fetchSavedAddresses(any(ECSFetchAddressesCallback::class.java))
    }
}