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

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.model.address.ECSUserProfile
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@PrepareForTest(ProfileViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSUserProfileCallBackTest {


    private lateinit var ecsUserProfileCallBack: ECSUserProfileCallBack
    @Mock
    lateinit var profileViewModel: ProfileViewModel

    @Mock
    private lateinit var userProfile : ECSUserProfile

    @Mock
    lateinit var  userProfileMock : MutableLiveData<ECSUserProfile>

    @Mock
    lateinit var authFailureCallbackMock: (Exception?, ECSError?) -> Unit


    @Mock
    lateinit var  mecErrorMock : MutableLiveData<MecError>

    @Mock
    lateinit var errorMock: Exception

    @Mock
    lateinit var ecsErrorMock: ECSError

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        profileViewModel.userProfile = userProfileMock
        profileViewModel.mecError = mecErrorMock
        ecsUserProfileCallBack = ECSUserProfileCallBack(profileViewModel)

    }

    @Test
    fun getMECRequestType() {
        assertEquals(MECRequestType.MEC_FETCH_USER_PROFILE,ecsUserProfileCallBack.mECRequestType)
    }

    @Test
    fun setMECRequestType() {
        assertNotNull(ecsUserProfileCallBack.mECRequestType)
    }


    @Test
    fun onResponse() {
        //TODO check if the value is set properl or not
        ecsUserProfileCallBack.onResponse(userProfile)
        assertNotNull(profileViewModel.userProfile)
    }

    @Test
    fun onFailure() {
        //TODO check if the value is set properl or not
        ecsUserProfileCallBack.onFailure(errorMock,ecsErrorMock)
        assertNotNull(profileViewModel.userProfile)
    }

    @Test
    fun testRetryAPIShouldCalled() {
        //TODO verif retr call
        ecsErrorMock.errorcode = 5009
        ecsUserProfileCallBack.onFailure(errorMock,ecsErrorMock)
        profileViewModel.authFailCallback = authFailureCallbackMock
        //Mockito.verify(profileViewModel).retryAPI(MECRequestType.MEC_FETCH_USER_PROFILE)
        //Mockito.verify(userDataInterfaceMock).refreshSession(any(RefreshSessionListener::class.java))

    }

    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }
}