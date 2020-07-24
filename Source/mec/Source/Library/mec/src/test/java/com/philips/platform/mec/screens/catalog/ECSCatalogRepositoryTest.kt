package com.philips.platform.mec.screens.catalog

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSPILProductsCallback::class)
@RunWith(PowerMockRunner::class)
class ECSCatalogRepositoryTest{

    private lateinit var ecsCatalogRepository: ECSCatalogRepository


    @Mock
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    @Mock
    lateinit var ecsCallback: ECSPILProductsCallback


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsCatalogRepository = ECSCatalogRepository()
    }

    @Test
    fun test1(){

    }

}