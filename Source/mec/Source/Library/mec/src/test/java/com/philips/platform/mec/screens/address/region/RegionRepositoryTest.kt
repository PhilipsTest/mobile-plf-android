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

import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.util.ECSConfiguration
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSRegionListCallback::class)
@RunWith(PowerMockRunner::class)
class RegionRepositoryTest{

    @Mock
    private lateinit var callBackMock: ECSRegionListCallback

    @Mock
    private lateinit var ecsServicesMock: ECSServices

    private lateinit var regionRepository : RegionRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ECSConfiguration.INSTANCE.locale = "en_US"
        regionRepository = RegionRepository()
    }

    @Test
    fun `getRegion should fetch ecs region`() {
        regionRepository.getRegions(ecsServicesMock,callBackMock)
        Mockito.verify(ecsServicesMock).fetchRegions("US",callBackMock)
    }


}