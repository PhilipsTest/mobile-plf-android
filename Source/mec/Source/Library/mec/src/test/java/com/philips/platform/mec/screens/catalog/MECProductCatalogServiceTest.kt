package com.philips.platform.mec.screens.catalog

import com.philips.platform.ecs.microService.model.filter.ECSSortType
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MECProductCatalogServiceTest {

    var mECProductCatalogService : MECProductCatalogService = MECProductCatalogService()
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun isSameDataClass() {

        val stockLevelSet1: MutableSet<ECSStockLevel> = mutableSetOf()
        stockLevelSet1.add(ECSStockLevel.InStock)
        val productFilter1 = ProductFilter(ECSSortType.topRated, stockLevelSet1 as HashSet<ECSStockLevel>)

        val stockLevelSet2: MutableSet<ECSStockLevel> = mutableSetOf()
        stockLevelSet2.add(ECSStockLevel.InStock)
        val productFilter2 = ProductFilter(ECSSortType.topRated,stockLevelSet2 as HashSet<ECSStockLevel>)

        mECProductCatalogService.isSameDataClass(productFilter1,productFilter2)
    }
}