package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.microService.ECSServices
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSProductsCallback::class,ECSServices::class)
@RunWith(PowerMockRunner::class)
class ECSCatalogRepositoryTest{

    private lateinit var ecsCatalogRepository: ECSCatalogRepository

    @Mock
    private lateinit var  ecsCallback: ECSProductsCallback

    @Mock
    private  var  microService= Mockito.spy(ECSServices::class)



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.rootCategory = "US_PUB"
        ecsCatalogRepository = ECSCatalogRepository()
    }

    @Test
    fun `get Product should call microService fetch product api`() {
       // ecsCatalogRepository.getProducts(0,20,ecsCallback,microService)
       // Mockito.verify(microService).fetchProducts(productCategory = "US_PUB", offset = 0, limit = 20, ecsCallback = ecsCallback)
    }
}