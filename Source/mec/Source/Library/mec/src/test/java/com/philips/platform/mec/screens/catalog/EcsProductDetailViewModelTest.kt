package com.philips.platform.mec.screens.catalog

import android.app.Application
import android.content.Context
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.screens.detail.ECSProductDetailRepository
import com.philips.platform.mec.screens.detail.EcsProductDetailViewModel
import com.philips.platform.mec.screens.reviews.BazaarVoiceHelper
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(EcsProductDetailViewModel::class, ECSProductDetailRepository::class)
@RunWith(PowerMockRunner::class)
class EcsProductDetailViewModelTest {


    private lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    @Mock
    lateinit var ecsServices: ECSServices

    @Mock
    lateinit var eCSCatalogRepository: ECSProductDetailRepository

    @Mock
    lateinit var context: Context


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
        MECDataHolder.INSTANCE.locale = "en_US"
//        val bazarvoiceSDK = BazaarVoiceHelper().getBazaarVoiceClient(context.applicationContext as Application)
//        MECDataHolder.INSTANCE.bvClient = bazarvoiceSDK
//        MECDataHolder.INSTANCE.bvClient = BVConversationsClient()

        ecsProductDetailViewModel = EcsProductDetailViewModel()
        ecsProductDetailViewModel.ecsProductDetailRepository = eCSCatalogRepository
    }


    @Test(expected = NullPointerException::class)
    fun shouldGetRatings() {
        ecsProductDetailViewModel.getRatings("CTN")
        Mockito.verify(eCSCatalogRepository).getRatings("CTN")
    }


    @Test(expected = NullPointerException::class)
    fun shouldGetProductDetail() {
        val ecsProduct = ECSProduct()
        ecsProductDetailViewModel.getProductDetail(ecsProduct)
        Mockito.verify(eCSCatalogRepository).getProductDetail(ecsProduct)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun shouldGetBazaarVoiceReview() {
        val ecsProduct = ECSProduct()
        ecsProductDetailViewModel.getBazaarVoiceReview("CTN", 0, 20)
        Mockito.verify(eCSCatalogRepository).fetchProductReview("CTN", 0, 20)
    }
}