package com.philips.platform.mec.screens.catalog

import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox
import java.util.*

@PrepareForTest(ECSCatalogRepository::class, ECSProductListCallback::class, ECSProductsCallback::class)
@RunWith(PowerMockRunner::class)
class EcsProductViewModelTest {


    lateinit var ecsProductViewModel: EcsProductViewModel

    @Mock
    lateinit var ecsServices: ECSServices

    @Mock
    lateinit var eCSCatalogRepository: ECSCatalogRepository

    @Mock
    lateinit var ecsProductsCallback: ECSProductsCallback

    @Mock
    lateinit var eCSProductListCallback: ECSProductListCallback


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
        ecsProductViewModel = EcsProductViewModel()
        ecsProductViewModel.ecsCatalogRepository = eCSCatalogRepository
        ecsProductViewModel.ecsProductsCallback = ecsProductsCallback
    }


    @Test
    fun initShouldCallGetProducts() {
        ecsProductViewModel.init(0, 20)
        Mockito.verify(eCSCatalogRepository).getProducts(0, 20, ecsProductsCallback, ecsServices)
    }


//    @Test
//    fun initCategorizedShouldGetCategorizedProducts() {
//        val arrayList = ArrayList<String>()
//        arrayList.add("CTN")
//        ecsProductViewModel.initCategorized(0, 20, arrayList)
////        Mockito.verify(eCSCatalogRepository).getCategorizedProducts(0, 20, 1, arrayList, null, ecsProductViewModel)
//    }
}