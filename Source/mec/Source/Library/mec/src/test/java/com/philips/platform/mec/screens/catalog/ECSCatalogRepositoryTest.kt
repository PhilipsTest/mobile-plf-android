package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.ECSServices
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSProductsCallback::class)
@RunWith(PowerMockRunner::class)
class ECSCatalogRepositoryTest{

    private lateinit var ecsCatalogRepository: ECSCatalogRepository


    @Mock
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    @Mock
    lateinit var ecsCallback: ECSProductsCallback


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsCatalogRepository = ECSCatalogRepository()
    }


    @Test
    fun shouldFetchProducts() {
        ecsCatalogRepository.getProducts(0,20,ecsCallback,ecsServices)
        Mockito.verify(ecsServices).fetchProducts(0, 20, ecsCallback)
    }


}