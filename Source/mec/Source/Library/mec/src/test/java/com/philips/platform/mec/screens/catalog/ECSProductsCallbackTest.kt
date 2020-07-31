package com.philips.platform.mec.screens.catalog

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox
import kotlin.test.assertEquals

@PrepareForTest(EcsProductViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSProductsCallbackTest {


    lateinit var callback: ECSProductsCallback

    @Mock
    lateinit var ecsProductViewModel: EcsProductViewModel

    @Mock
    lateinit var exception: Exception


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        callback = ECSProductsCallback(ecsProductViewModel)
    }

    @Test
    fun `request type should be as expected`() {
        assertEquals(MECRequestType.MEC_FETCH_PRODUCTS,callback.mECRequestType)
    }

    @Test
    fun onResponse() {

        val list: List<ECSProduct> = ArrayList()
        val ecsProducts = ECSProducts(list)
        val ecsProduct = ECSProduct(null,"ctn",null)
        ecsProducts.commerceProducts = listOf(ecsProduct)
        callback.onResponse(ecsProducts)
    }

    @Test
    fun onFailure() {

    }
}