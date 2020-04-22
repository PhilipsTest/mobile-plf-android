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
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSUserProfileCallBack::class,ProfileRepository::class)
@RunWith(PowerMockRunner::class)
class ProfileViewModelTest {

    lateinit var profileViewModel: ProfileViewModel



    @Mock
    lateinit var ecsUserProfileCallBackMock: ECSUserProfileCallBack

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var profileRepository: ProfileRepository


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        profileRepository.ecsServices = ecsServicesMock
        profileViewModel = ProfileViewModel()
        profileViewModel.profileRepository = profileRepository
        profileViewModel.ecsUserProfileCallBack = ecsUserProfileCallBackMock
        profileViewModel.ecsServices = ecsServicesMock
    }

    @Test
    fun fetchUserProfile() {
        profileViewModel.fetchUserProfile()
        Mockito.verify(profileRepository).fetchUserProfile(ecsUserProfileCallBackMock)
    }

}