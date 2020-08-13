package com.philips.platform.mec.screens.catalog

import android.content.Context
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.screens.detail.ECSProductDetailRepository
import com.philips.platform.mec.screens.detail.EcsProductDetailViewModel
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
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

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
        val ecsProduct = ECSProduct(null, "ctn", null)
        ecsProductDetailViewModel.getProductDetail(ecsProduct)
        Mockito.verify(eCSCatalogRepository).getProductDetail(ecsProduct)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun shouldGetBazaarVoiceReview() {
        ecsProductDetailViewModel.getBazaarVoiceReview("CTN", 0, 20)
        Mockito.verify(eCSCatalogRepository).fetchProductReview("CTN", 0, 20)
    }


}