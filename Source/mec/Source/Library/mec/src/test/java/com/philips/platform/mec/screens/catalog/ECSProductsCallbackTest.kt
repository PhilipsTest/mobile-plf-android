package com.philips.platform.mec.screens.catalog

import androidx.lifecycle.MutableLiveData
import com.philips.platform.mec.common.MecError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

@PrepareForTest(EcsProductViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSProductsCallbackTest {


    lateinit var callback: ECSProductsCallback

    @Mock
    lateinit var ecsProductViewModel: EcsProductViewModel

    @Mock
    lateinit var mutableLiveData: MutableLiveData<MutableList<com.philips.platform.ecs.model.products.ECSProducts>>


    @Mock
    lateinit var mutableMECError: MutableLiveData<MecError>

    @Mock
    lateinit var exception: Exception

    @Mock
    lateinit var ecsError: com.philips.platform.ecs.error.ECSError


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Whitebox.setInternalState(ecsProductViewModel, "ecsProductsList", mutableLiveData)
        Whitebox.setInternalState(ecsProductViewModel, "mecError", mutableMECError)
//        Mockito.`when`(ecsProductViewModel.ecsProductsList).thenReturn(mutableLiveData)
//        Mockito.`when`(ecsProductViewModel.mecError).thenReturn(mutableMECError)
        callback = ECSProductsCallback(ecsProductViewModel)
    }

    @Test
    fun onResponse() {

        //TODO
        val ecsProducts = com.philips.platform.ecs.model.products.ECSProducts()
        val ecsProduct = com.philips.platform.ecs.model.products.ECSProduct()
        ecsProducts.products = listOf(ecsProduct)

        callback.onResponse(ecsProducts)

        // assertEquals(ecsProductViewModel.ecsProductsList.value,mutableListOf<ECSProducts>())
    }

    @Test
    fun onFailure() {
        //TODO
        callback.onFailure(exception, ecsError)
        // assertNotNull(ecsProductViewModel.mecError.value)
    }
}