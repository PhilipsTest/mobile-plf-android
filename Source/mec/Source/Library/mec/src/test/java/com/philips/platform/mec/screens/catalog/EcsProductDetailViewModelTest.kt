package com.philips.platform.mec.screens.catalog

import android.content.Context
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
import kotlin.test.assertEquals

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
        val ecsProduct = com.philips.platform.ecs.model.products.ECSProduct()
        ecsProductDetailViewModel.getProductDetail(ecsProduct)
        Mockito.verify(eCSCatalogRepository).getProductDetail(ecsProduct)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun shouldGetBazaarVoiceReview() {
        val ecsProduct = com.philips.platform.ecs.model.products.ECSProduct()
        ecsProductDetailViewModel.getBazaarVoiceReview("CTN", 0, 20)
        Mockito.verify(eCSCatalogRepository).fetchProductReview("CTN", 0, 20)
    }

    @Test
    fun `test getRetailerUrl with out query parameter`() {
        MECDataHolder.INSTANCE.propositionId = "IAP_MOB_DKA"
        val uuid = "5ce053a2-9134-4479-be3f-1b020cb15d44"
        val param = "subTag"
        val buyURL = "https://click.channelsight.com/c/a6847045-cace-46e2-aa2f-bc5bba581d32/30e74748-1d98-4956-be76-c2b67935aa22/72d76fd3-4a36-4c47-adb8-8201b8cba7c1/98d39721-d031-42b0-a719-af1c95945191/de-DE/"
        val formattedRetailerURL = ecsProductDetailViewModel.getFormattedRetailerURL(buyURL, param, uuid)

        val expectedURL = "https://click.channelsight.com/c/a6847045-cace-46e2-aa2f-bc5bba581d32/30e74748-1d98-4956-be76-c2b67935aa22/72d76fd3-4a36-4c47-adb8-8201b8cba7c1/98d39721-d031-42b0-a719-af1c95945191/de-DE/?wtbSource=mobile_IAP_MOB_DKA&subTag=5ce053a2-9134-4479-be3f-1b020cb15d44"

        assertEquals(expectedURL,formattedRetailerURL)
    }
    @Test
    fun `test getRetailerUrl with  query parameter`() {
        val buyURL = "https://gethatch.com/iceleads_rest/merch/1904/go?AffiliateID=LP8012&campid=LP8012&wid=Philips_WTB&region=DE&affiliate_id=52419&prod_id=945273969&core_id=95358501"
        MECDataHolder.INSTANCE.propositionId = "IAP_MOB_DKA"
        val uuid = "5ce053a2-9134-4479-be3f-1b020cb15d44"
        val param = "subTag"

        val formattedRetailerURL = ecsProductDetailViewModel.getFormattedRetailerURL(buyURL, param, uuid)

        val expectedURL = "https://gethatch.com/iceleads_rest/merch/1904/go?AffiliateID=LP8012&campid=LP8012&wid=Philips_WTB&region=DE&affiliate_id=52419&prod_id=945273969&core_id=95358501&wtbSource=mobile_IAP_MOB_DKA&subTag=5ce053a2-9134-4479-be3f-1b020cb15d44"

        assertEquals(expectedURL,formattedRetailerURL)
    }

    @Test
    fun `test getRetailerUrl with  query parameter when propositonID is empty`() {
        val buyURL = "https://gethatch.com/iceleads_rest/merch/1904/go?AffiliateID=LP8012&campid=LP8012&wid=Philips_WTB&region=DE&affiliate_id=52419&prod_id=945273969&core_id=95358501"
        MECDataHolder.INSTANCE.propositionId = ""
        val uuid = "5ce053a2-9134-4479-be3f-1b020cb15d44"
        val param = "subTag"

        val formattedRetailerURL = ecsProductDetailViewModel.getFormattedRetailerURL(buyURL, param, uuid)

        val expectedURL = "https://gethatch.com/iceleads_rest/merch/1904/go?AffiliateID=LP8012&campid=LP8012&wid=Philips_WTB&region=DE&affiliate_id=52419&prod_id=945273969&core_id=95358501&subTag=5ce053a2-9134-4479-be3f-1b020cb15d44"

        assertEquals(expectedURL,formattedRetailerURL)
    }

    @Test
    fun `test getRetailerUrl with out query parameter when propositonID is empty`() {
        val buyURL = "https://click.channelsight.com/c/4236a32b-256a-4ff9-8865-83763c2f17be/30e74748-1d98-4956-be76-c2b67935aa22/72d76fd3-4a36-4c47-adb8-8201b8cba7c1/98d39721-d031-42b0-a719-af1c95945191/"
        MECDataHolder.INSTANCE.propositionId = ""
        val uuid = "5ce053a2-9134-4479-be3f-1b020cb15d44"
        val param = "subTag"

        val formattedRetailerURL = ecsProductDetailViewModel.getFormattedRetailerURL(buyURL, param, uuid)

        val expectedURL = "https://click.channelsight.com/c/4236a32b-256a-4ff9-8865-83763c2f17be/30e74748-1d98-4956-be76-c2b67935aa22/72d76fd3-4a36-4c47-adb8-8201b8cba7c1/98d39721-d031-42b0-a719-af1c95945191/?subTag=5ce053a2-9134-4479-be3f-1b020cb15d44"

        assertEquals(expectedURL,formattedRetailerURL)
    }
}