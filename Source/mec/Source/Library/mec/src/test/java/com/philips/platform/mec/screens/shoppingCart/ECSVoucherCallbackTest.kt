package com.philips.platform.mec.screens.shoppingCart

import com.philips.platform.ecs.ECSServices
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@PrepareForTest(ECSShoppingCartRepository::class)
@RunWith(PowerMockRunner::class)
class ECSVoucherCallbackTest {

    @Mock
    lateinit var ecsServices: ECSServices

    @Mock
    lateinit var ecsShoppingCartViewModelMock:EcsShoppingCartViewModel

    @Mock
    lateinit var mECSVoucherCallback: ECSVoucherCallback

    @Mock
    lateinit var mECSShoppingCartRepositoryMock: ECSShoppingCartRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
       // ecsShoppingCartViewModelMock= EcsShoppingCartViewModel()
        ecsShoppingCartViewModelMock.deleteVoucherString=""

       // mECSVoucherCallback = ECSVoucherCallback(ecsShoppingCartViewModelMock)
       // mECSVoucherCallback.mECRequestType=MECRequestType.MEC_GET_APPLIED_VOUCHERS

       /* ecsShoppingCartViewModelMock.ecsShoppingCartRepository=mECSShoppingCartRepositoryMock
        val ECSShoppingCartRepositorySpy = PowerMockito.spy<Any>(ECSShoppingCartRepository(ecsShoppingCartViewModelMock,ecsServices))
        PowerMockito.doNothing().`when`<Any>(ECSShoppingCartRepositorySpy, "fetchShoppingCart")*/
    }

    @Test
    fun onResponse() {
        mECSVoucherCallback.onResponse(null)
        Mockito.verify(ecsShoppingCartViewModelMock).tagApplyOrDeleteVoucher(MECRequestType.MEC_GET_APPLIED_VOUCHERS)
        //Mockito.verify(ecsShoppingCartViewModelMock).getShoppingCart()
    }

    @Test
    fun onFailure() {
    }
}