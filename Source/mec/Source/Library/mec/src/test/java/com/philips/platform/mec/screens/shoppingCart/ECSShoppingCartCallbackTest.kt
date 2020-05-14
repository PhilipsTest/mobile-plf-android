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
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@PrepareForTest(EcsShoppingCartViewModel::class,ECSShoppingCartRepository::class, HybrisAuth::class,ECSEntries::class)
@RunWith(PowerMockRunner::class)
class ECSShoppingCartCallbackTest {

    lateinit var ecsShoppingCartCallback: ECSShoppingCartCallback

    @Mock
    lateinit var ecsShoppingCartViewModelMock: EcsShoppingCartViewModel

    @Mock
    lateinit var ecsShoppingCartMock: ECSShoppingCart

    @Mock
    lateinit var updateQuantityEntriesMock : ECSEntries

    @Mock
    lateinit var ecsShoppingCartLiveDataMock : MutableLiveData<ECSShoppingCart>

    @Mock
    lateinit var errorMock: Exception

    @Mock
    lateinit var ecsErrorMock: ECSError

    @Mock
    lateinit var  mecErrorMock : MutableLiveData<MecError>

    @Mock
    lateinit var authFailureCallbackMock: (Exception?, ECSError?) -> Unit

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Mock
    lateinit var shoppingCartRepositoryMock: ECSShoppingCartRepository

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var refreshSessionListener : RefreshSessionListener

    @Mock
    lateinit var createCartCallback : ECSCallback<ECSShoppingCart, Exception>

    @Before
        fun setUp() {
            MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock

        ecsShoppingCartViewModelMock.ecsShoppingCart = ecsShoppingCartLiveDataMock
        ecsShoppingCartViewModelMock.mecError = mecErrorMock
        ecsShoppingCartViewModelMock.createShoppingCartCallback = createCartCallback

        shoppingCartRepositoryMock.ecsServices = ecsServicesMock
        shoppingCartRepositoryMock.ecsShoppingCartViewModel = ecsShoppingCartViewModelMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock

        ecsShoppingCartViewModelMock.ecsShoppingCartRepository = shoppingCartRepositoryMock

        ecsShoppingCartCallback = ECSShoppingCartCallback(ecsShoppingCartViewModelMock)
    }

    @Test
    fun getMECRequestType() {
       assertEquals(MECRequestType.MEC_FETCH_SHOPPING_CART,ecsShoppingCartCallback.mECRequestType)
    }

    @Test
    fun setMECRequestType() {
        ecsShoppingCartCallback.mECRequestType = MECRequestType.MEC_FETCH_SHOPPING_CART
        assertNotNull(ecsShoppingCartCallback.mECRequestType)
    }


/*    @Test
    fun onResponseOfUpdate() {
        //TODO
        Mockito.`when`(updateQuantityEntriesMock.quantity).thenReturn(-1)
        ecsShoppingCartViewModelMock.updateQuantityEntries= updateQuantityEntriesMock
        ecsShoppingCartCallback.mECRequestType =  MECRequestType.MEC_UPDATE_SHOPPING_CART
        ecsShoppingCartCallback.onResponse(ecsShoppingCartMock)
        Mockito.verify(ecsShoppingCartViewModelMock).tagProductAddedOrDeleted()
    }*/

    @Test
    fun onFailure() {
        //TODO check if the value is set properly or not
        ecsShoppingCartCallback.onFailure(errorMock,ecsErrorMock)
        assertNotNull(ecsShoppingCartViewModelMock.mecError)
    }

    @Test
    fun shouldCreateCartIfFetchShoppingCartFailsWithErrorCode() {
        Mockito.`when`(ecsErrorMock.errorcode) .thenReturn(5004)
        ecsShoppingCartCallback.onFailure(errorMock,ecsErrorMock)
        Mockito.verify(ecsServicesMock).createShoppingCart(createCartCallback)
    }

    //TODO

/*    @Test
    fun testRetryAuthAPIShouldCalled() {
        Mockito.`when`(ecsErrorMock.errorcode) .thenReturn(5009)
        ecsShoppingCartViewModelMock.authFailCallback = authFailureCallbackMock

        ecsShoppingCartCallback.onFailure(errorMock,ecsErrorMock)
       // Mockito.verify(ecsShoppingCartViewModelMock).retryAPI(MECRequestType.MEC_FETCH_SHOPPING_CART)

        Mockito.verify(ecsShoppingCartViewModelMock).authAndCallAPIagain(ArgumentMatchers.any(),authFailureCallbackMock)
    }*/

    @After
    fun validate() {
        validateMockitoUsage()
    }
}