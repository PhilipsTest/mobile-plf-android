package com.philips.platform.mec.screens.catalog

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
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
    lateinit var mutableLiveData: MutableLiveData<MutableList<ECSProducts>>


    @Mock
    lateinit var mutableMECError: MutableLiveData<MecError>

    @Mock
    lateinit var exception: Exception

    /*@Mock
    lateinit var ecsError: ECSError*/

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Whitebox.setInternalState(ecsProductViewModel, "ecsPILProducts", mutableLiveData)
        Whitebox.setInternalState(ecsProductViewModel, "mecError", mutableMECError)
//        Mockito.`when`(ecsProductViewModel.ecsProductsList).thenReturn(mutableLiveData)
//        Mockito.`when`(ecsProductViewModel.mecError).thenReturn(mutableMECError)
        callback = ECSProductsCallback(ecsProductViewModel)
    }

    @Test
    fun onResponse() {

        //TODO
        var list: List<ECSProduct> = ArrayList<ECSProduct>()
        val ecsProducts = ECSProducts(list)
        val ecsProduct = ECSProduct(null,"ctn",null)
        ecsProducts.commerceProducts = listOf(ecsProduct)

        callback.onResponse(ecsProducts)

        // assertEquals(ecsProductViewModel.ecsProductsList.value,mutableListOf<ECSProducts>())
    }

    @Test
    fun onFailure() {
        //TODO
      // callback.onFailure( ecsError)
        // assertNotNull(ecsProductViewModel.mecError.value)
    }
}