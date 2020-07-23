package com.philips.platform.mec.screens.catalog

import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSCatalogRepository::class,ECSPILProductsCallback::class)
@RunWith(PowerMockRunner::class)
class EcsProductViewModelTest {


    lateinit var ecsProductViewModel: EcsProductViewModel

    @Mock
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    @Mock
    lateinit var eCSCatalogRepository: ECSCatalogRepository

    @Mock
    lateinit var ecsProductsCallback: ECSPILProductsCallback



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
        ecsProductViewModel = EcsProductViewModel()
        ecsProductViewModel.ecsCatalogRepository = eCSCatalogRepository
    }





//    @Test
//    fun initCategorizedShouldGetCategorizedProducts() {
//        val arrayList = ArrayList<String>()
//        arrayList.add("CTN")
//        ecsProductViewModel.initCategorized(0, 20, arrayList)
////        Mockito.verify(eCSCatalogRepository).getCategorizedProducts(0, 20, 1, arrayList, null, ecsProductViewModel)
//    }
}