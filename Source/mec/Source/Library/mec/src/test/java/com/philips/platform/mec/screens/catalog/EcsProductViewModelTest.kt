package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSCatalogRepository::class, ECSProductsCallback::class, com.philips.platform.ecs.microService.ECSServices::class)
@RunWith(PowerMockRunner::class)
class EcsProductViewModelTest {


    lateinit var ecsProductViewModel: EcsProductViewModel

    @Mock
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    @Mock
    lateinit var eCSCatalogRepository: ECSCatalogRepository

    @Mock
    lateinit var ecsProductsCallbackMock: ECSProductsCallback

    @Mock
    lateinit var microServiceMock: com.philips.platform.ecs.microService.ECSServices

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(ecsServices.microService).thenReturn(microServiceMock)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
        ecsProductViewModel = EcsProductViewModel()
        ecsProductViewModel.ecsProductsCallback = ecsProductsCallbackMock
        ecsProductViewModel.ecsCatalogRepository = eCSCatalogRepository
    }

    @Test(expected = NullPointerException::class)
    fun `fetch products should call repository pil getProduct api`() {
        val stockList: MutableList<ECSStockLevel> = mutableListOf()
        val productFilter: ProductFilter? = ProductFilter(null, stockList)
        ecsProductViewModel.fetchProducts(0, 20, productFilter)
        Mockito.verify(eCSCatalogRepository).getProducts(0, 20, productFilter, ecsProductsCallbackMock, microServiceMock)
    }

    @Test(expected = NullPointerException::class)
    fun `fetch product summaries should call repository product summaries`() {
        val ctnList: MutableList<String> = mutableListOf()
        ctnList.add("HX2054/00")
        ecsProductViewModel.fetchProductSummaries(ctnList)
        Mockito.verify(eCSCatalogRepository).fetchProductSummaries(ctnList, ecsProductsCallbackMock, microServiceMock)
    }

    @Test
    fun `fetch product review should call repository product review`() {
        MECDataHolder.INSTANCE.locale = "en_US"
        val products: MutableList<ECSProduct> = mutableListOf()
        products.add(ECSProduct(ctn = "HX2054/00"))
        ecsProductViewModel.fetchProductReview(products)
        Mockito.verify(eCSCatalogRepository).fetchProductReview(products, ecsProductViewModel)
    }

}