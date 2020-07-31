package com.philips.platform.mec.screens.catalog

import com.bazaarvoice.bvandroidsdk.BVConversationsClient
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.LoadCallDisplay
import com.philips.platform.ecs.microService.ECSServices
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.any
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.lang.NullPointerException
import kotlin.test.assertEquals

@PrepareForTest(ECSProductsCallback::class,ECSServices::class,EcsProductViewModel::class,BVConversationsClient::class,LoadCallDisplay::class)
@RunWith(PowerMockRunner::class)
class ECSCatalogRepositoryTest{

    @Mock
    private lateinit var microsServiceMock: ECSServices

    @Mock
    private lateinit var ecsProductsCallBackMock: ECSProductsCallback

    private lateinit var ecsCatalogRepository: ECSCatalogRepository

    @Mock
    private lateinit var ecsProductViewModelMock: EcsProductViewModel

    @Mock
    private lateinit var bvConversationsClientMock : BVConversationsClient

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.rootCategory = "US_PUB"
        ecsCatalogRepository = ECSCatalogRepository()
    }

    @Test(expected = NullPointerException::class)
    fun `get Product should call microService fetch product api`() {

        ecsCatalogRepository.getProducts(0,20,ecsProductsCallBackMock,microsServiceMock)
        Mockito.verify(microsServiceMock).fetchProducts(productCategory ="US_PUB", offset = 0, limit = 20, ecsCallback = ecsProductsCallBackMock)
    }

    @Test(expected = NullPointerException::class)
    fun `fetch product summaries should call pil micro service fetch summaries`() {
        val ctnS: MutableList<String> = mutableListOf()
        ctnS.add("HX005/01")
        ecsCatalogRepository.fetchProductSummaries(ctnS,ecsProductsCallBackMock,microsServiceMock)
        Mockito.verify(microsServiceMock).fetchProductSummaries(ctnS, ecsProductsCallBackMock)
    }

    @Mock
    lateinit var loadDisplayMock : LoadCallDisplay<BulkRatingsRequest, BulkRatingsResponse>


    @Test
    fun `fetch product review should call bazzar voice`() {

        Mockito.`when`(bvConversationsClientMock.prepareCall(any(BulkRatingsRequest::class.java))).thenReturn(loadDisplayMock)
        MECDataHolder.INSTANCE.bvClient = bvConversationsClientMock
        MECDataHolder.INSTANCE.locale = "en_US"
        val products: MutableList<ECSProduct> = mutableListOf()
        products.add(ECSProduct(ctn = "HX005/01"))
        ecsCatalogRepository.fetchProductReview(products,ecsProductViewModelMock)
        Mockito.verify(loadDisplayMock).loadAsync(any(MECBulkRatingConversationsDisplayCallback::class.java))
    }

    @Test
    fun `test ctn list from product list`() {
        val expectedCTNList: MutableList<String> = mutableListOf()
        expectedCTNList.add("HX005_01")
        val products: MutableList<ECSProduct> = mutableListOf()
        products.add(ECSProduct(ctn = "HX005/01"))
        val actualCTNList = ecsCatalogRepository.getCtnList(products)
        assertEquals(expectedCTNList,actualCTNList)
    }
}