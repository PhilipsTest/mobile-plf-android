package com.philips.platform.mec.screens.detail

import androidx.lifecycle.MutableLiveData
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

@PrepareForTest(ECSProduct::class, EcsProductDetailViewModel::class, ECSProductDetailCallback::class, MECDataHolder::class, MECRequestType::class)
@RunWith(PowerMockRunner::class)
class ECSProductDetailCallbackTest {


    lateinit var ecsProductDetailCallback: ECSProductDetailCallback

    @Mock
    lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    @Mock
    lateinit var mutableLiveDataMock: MutableLiveData<ECSProduct>

    @Mock
    lateinit var mutableLiveDataMecErrorMock: MutableLiveData<MecError>

    @Mock
    val ecsProduct = ECSProduct()

    @Mock
    lateinit var mecRequestType: MECRequestType

    @Mock
    private val mockMutableLiveData: MutableLiveData<ECSProduct>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsProductDetailCallback = ECSProductDetailCallback(ecsProductDetailViewModel)
    }


    @Test
    fun onResponse() {
        Whitebox.setInternalState(ecsProductDetailViewModel, "ecsProduct", mockMutableLiveData)
        Whitebox.setInternalState(ecsProductDetailViewModel, "mecError", mutableLiveDataMecErrorMock)


        ecsProductDetailCallback.onResponse(ecsProduct)

        assertNotNull(ecsProductDetailViewModel.ecsProduct)
        //TODO check value
    }

//    @Test
//    fun onFailure() {
//
//        val exception = Exception()
//        val ecsError = ECSError(1000, "UNKNOWN")
//        ecsProductDetailCallback.onFailure(exception, ecsError)
//        assertNotNull(ecsProductDetailViewModel.mecError)
//        //TODO check value
//    }
}