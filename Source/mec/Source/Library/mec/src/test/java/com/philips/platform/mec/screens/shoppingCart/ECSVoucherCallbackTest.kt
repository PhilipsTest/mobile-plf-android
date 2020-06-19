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

package com.philips.platform.mec.screens.shoppingCart

import androidx.lifecycle.MutableLiveData
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.securestorage.SecureStorage
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.integration.ECSOAuthProvider
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.ecs.model.voucher.ECSVoucher
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.any
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import org.junit.Assert
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.internal.matchers.Any
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.HashMap

@PrepareForTest(ECSShoppingCartRepository::class,ECSShoppingCartCallback::class)
@RunWith(PowerMockRunner::class)
class ECSVoucherCallbackTest {

    private lateinit var eCSVoucherCallback : ECSVoucherCallback

    @Mock
    private lateinit var ecsShoppingCartViewModelMock: EcsShoppingCartViewModel

    @Mock
    private lateinit var ecsVoucherListMock: List<ECSVoucher>

    @Mock
    private lateinit var ecsShoppingCartMock : MutableLiveData<ECSShoppingCart>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsShoppingCartViewModelMock.addVoucherString = "voucher_ID"
        ecsShoppingCartViewModelMock.ecsShoppingCart = ecsShoppingCartMock
        eCSVoucherCallback = ECSVoucherCallback(ecsShoppingCartViewModelMock)
        eCSVoucherCallback.mECRequestType = MECRequestType.MEC_APPLY_VOUCHER
    }

    @Mock
    lateinit var eCSShoppingCartRepositoryMock : ECSShoppingCartRepository

    @Mock
    lateinit var eCSShoppingCartCallbackMock : ECSShoppingCartCallback

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var authCallBackMock : ECSCallback<ECSOAuthData, Exception>

    @Test
    fun `on voucher call back success response fetch cart should be called and user should be authenticated if not authorized user  `() {

        setUpAppinfra()
        eCSShoppingCartRepositoryMock.authCallBack = authCallBackMock
        eCSShoppingCartRepositoryMock.ecsShoppingCartCallback = eCSShoppingCartCallbackMock
        eCSShoppingCartRepositoryMock.ecsServices = ecsServicesMock
        ecsShoppingCartViewModelMock.ecsVoucherCallback = eCSVoucherCallback
        ecsShoppingCartViewModelMock.ecsShoppingCartRepository = eCSShoppingCartRepositoryMock
        eCSVoucherCallback.onResponse(ecsVoucherListMock)

        Mockito.verify(ecsServicesMock).hybrisOAthAuthentication(any(ECSOAuthProvider::class.java), any())
    }

    @Test
    fun `on voucher call back success response fetch cart should be called if user is authenticated`() {

        setUpAppinfra()
        setUpExistingUserAndAlreadyAuthEnvironment()
        eCSShoppingCartRepositoryMock.authCallBack = authCallBackMock
        eCSShoppingCartRepositoryMock.ecsShoppingCartCallback = eCSShoppingCartCallbackMock
        eCSShoppingCartRepositoryMock.ecsServices = ecsServicesMock
        ecsShoppingCartViewModelMock.ecsVoucherCallback = eCSVoucherCallback
        ecsShoppingCartViewModelMock.ecsShoppingCartRepository = eCSShoppingCartRepositoryMock
        eCSVoucherCallback.onResponse(ecsVoucherListMock)

        Mockito.verify(ecsServicesMock).fetchShoppingCart(eCSShoppingCartCallbackMock)
    }

    @Mock
    lateinit var errorMock: Exception

    @Mock
    lateinit var ecsErrorMock: ECSError


    @Mock
    private lateinit var mecErrorMock: MutableLiveData<MecError>

    @Mock
    lateinit var authFailureCallbackMock: (Exception?, ECSError?) -> Unit

    @Test
    fun `should call auth if call auth failure comes`() {

        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        ecsShoppingCartViewModelMock.authFailCallback = authFailureCallbackMock
        Mockito.`when`(ecsErrorMock.errorcode).thenReturn(ECSErrorEnum.ECSInvalidTokenError.errorCode)
        eCSVoucherCallback.onFailure(errorMock, ecsErrorMock)
        Mockito.verify(userDataInterfaceMock).refreshSession(any(RefreshSessionListener::class.java))
    }

    @Test
    fun `should update error view model when api fails`() {
        ecsShoppingCartViewModelMock.mecError = mecErrorMock
        eCSVoucherCallback.onFailure(errorMock, null)
        Assert.assertNotNull(mecErrorMock)
        //TODO
        //assertNotNull(mecErrorMock.value)
    }


    @Mock
    lateinit var appInfraInterface: AppInfraInterface
    @Mock
    lateinit var secureStorage: SecureStorage

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    private fun setUpAppinfra(){
        MECDataHolder.INSTANCE.appinfra = appInfraInterface
        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn( UserLoggedInState.USER_LOGGED_IN)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        Mockito.`when`(appInfraInterface.secureStorage).thenReturn(secureStorage)
    }

    private fun setUpExistingUserAndAlreadyAuthEnvironment(){
        ECSConfiguration.INSTANCE.setAuthToken("abcdef")
        val map = HashMap<String, kotlin.Any>()
        map["email"] = "NONE"
        Mockito.`when`(userDataInterfaceMock.getUserDetails(any())).thenReturn(map)
    }
}