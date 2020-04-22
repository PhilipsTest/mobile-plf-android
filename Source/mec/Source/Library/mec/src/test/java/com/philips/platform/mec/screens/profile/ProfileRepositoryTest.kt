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

package com.philips.platform.mec.screens.profile

import com.philips.platform.ecs.ECSServices
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertNotNull

@PrepareForTest(ECSUserProfileCallBack::class)
@RunWith(PowerMockRunner::class)
class ProfileRepositoryTest {

    lateinit var profileRepository: ProfileRepository

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var ecsUserProfileCallBackMock: ECSUserProfileCallBack

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        profileRepository = ProfileRepository(ecsServicesMock)
    }

    @Test
    fun fetchUserProfile() {
        profileRepository.fetchUserProfile(ecsUserProfileCallBackMock)
        Mockito.verify(ecsServicesMock).fetchUserProfile(ecsUserProfileCallBackMock)
    }

    @Test
    fun getEcsServices() {
        assertNotNull(profileRepository.ecsServices)
    }
}