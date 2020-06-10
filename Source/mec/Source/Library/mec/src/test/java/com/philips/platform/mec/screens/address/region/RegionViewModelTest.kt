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

package com.philips.platform.mec.screens.address.region

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.integration.ECSOAuthProvider
import com.philips.platform.ecs.model.region.ECSRegion
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.any
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSRegionListCallback::class,RegionRepository::class,UserDataInterface::class,ECSServices::class)
@RunWith(PowerMockRunner::class)
class RegionViewModelTest{

    private lateinit var regionViewModel : RegionViewModel

    @Mock
    lateinit var ecsRegionListCallbackMock : ECSRegionListCallback

    @Mock
    lateinit var  regionsListMock : MutableLiveData<List<ECSRegion>>

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var regionRepositoryMock : RegionRepository



    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        regionViewModel = RegionViewModel()
        regionViewModel.ecsRegionListCallback = ecsRegionListCallbackMock
        regionViewModel.regionsList = regionsListMock
        regionViewModel.ecsServices = ecsServicesMock

        regionViewModel.regionRepository = regionRepositoryMock
    }

    @Test
    fun `fetch region should call repository get Region`() {
        ECSConfiguration.INSTANCE.locale = "en_US"
        regionViewModel.fetchRegions()
        Mockito.verify(ecsServicesMock).fetchRegions("US",ecsRegionListCallbackMock)
    }

    @Test
    fun `retry api should do auth call`() {
        regionViewModel.retryAPI(MECRequestType.MEC_FETCH_REGIONS)
        Mockito.verify(userDataInterfaceMock).refreshSession(any(RefreshSessionListener::class.java))
    }

    @Test
    fun `retry api should refresh auth if refresh token exists`() {
        MECDataHolder.INSTANCE.refreshToken = "djhgwdhjcgdjwkjbd"
        regionViewModel.retryAPI(MECRequestType.MEC_FETCH_REGIONS)
        Mockito.verify(ecsServicesMock).hybrisRefreshOAuth(any(), any())
    }
}