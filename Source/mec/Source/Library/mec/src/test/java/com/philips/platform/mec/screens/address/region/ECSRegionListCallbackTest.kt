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
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.model.region.ECSRegion
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(RegionViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSRegionListCallbackTest{

    private lateinit var eCSRegionListCallback : ECSRegionListCallback

    @Mock
    private lateinit var regionViewModelMock: RegionViewModel

    @Mock
    private lateinit var   regionsListMock : MutableLiveData<List<ECSRegion>>

    @Mock
    private lateinit var mecErrorMock :MutableLiveData<MecError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(regionViewModelMock.regionsList).thenReturn(regionsListMock)
        Mockito.`when`(regionViewModelMock.mecError).thenReturn(mecErrorMock)
        eCSRegionListCallback = ECSRegionListCallback(regionViewModelMock)
    }

    @Test
    fun `request type should be fetch region`() {
        assertEquals(eCSRegionListCallback.mECRequestType, MECRequestType.MEC_FETCH_REGIONS)
    }

    @Test
    fun `should update view model on success response`() {
        val result: List<ECSRegion> = mutableListOf()
        eCSRegionListCallback.onResponse(result)
        assertNotNull(regionsListMock)
        //TODO
       // assertNotNull(regionsListMock.value)
    }

    @Mock
    lateinit var errorMock: Exception
    @Mock
    lateinit var ecsErrorMock: ECSError

    @Test
    fun `should call auth if call auth failure comes`() {

        Mockito.`when`(ecsErrorMock.errorcode).thenReturn(ECSErrorEnum.ECSInvalidTokenError.errorCode)
        eCSRegionListCallback.onFailure(errorMock,ecsErrorMock)
        Mockito.verify(regionViewModelMock).retryAPI(MECRequestType.MEC_FETCH_REGIONS)
    }

    @Test
    fun `should update error view model when api fails`() {
        eCSRegionListCallback.onFailure(errorMock,null)
        assertNotNull(mecErrorMock)
        //TODO
        //assertNotNull(mecErrorMock.value)
    }
}