package com.philips.platform.mec.screens.detail


import android.content.Context
import com.bazaarvoice.bvandroidsdk.*
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.microService.ECSServices
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.only
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals


@PrepareForTest(EcsProductDetailViewModel::class, ECSProductDetailRepository::class, ECSProductDetailCallback::class, LoadCallDisplay::class, BVConversationsClient::class,
        MECReviewConversationsDisplayCallback::class, MECDetailBulkRatingConversationsDisplayCallback::class, ECSServices::class,ECSProduct::class,MECAddToProductCallback::class)
@RunWith(PowerMockRunner::class)
class ECSProductDetailRepositoryTest {

    @Mock
    lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    @Mock
    lateinit var eCSProductDetailRepository: ECSProductDetailRepository

    @Mock
    lateinit var ecsProductDetailCallBack: ECSProductDetailCallback

    @Mock
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    @Mock
    lateinit var microEcsServices: ECSServices


    @Mock
    lateinit var loadCallDisplayRatingsMock: LoadCallDisplay<BulkRatingsRequest, BulkRatingsResponse>

    @Mock
    lateinit var loadCallDisplayReviewMock: LoadCallDisplay<ReviewsRequest, ReviewResponse>


    @Mock
    lateinit var bVConversationsClient: BVConversationsClient

    @Mock
    lateinit var mECReviewConversationsDisplayCallback: MECReviewConversationsDisplayCallback

    @Mock
    lateinit var mECDetailBulkRatingConversationsDisplayCallback: MECDetailBulkRatingConversationsDisplayCallback

    lateinit var eCSProduct: ECSProduct

    @Mock
    lateinit var ecsProductMock :ECSProduct


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(ecsServices.microService).thenReturn(microEcsServices)
        eCSProductDetailRepository = ECSProductDetailRepository(ecsProductDetailViewModel, ecsServices)
        eCSProductDetailRepository.ecsProductDetailCallBack = ecsProductDetailCallBack

        eCSProduct = ECSProduct(null,"HX12345/00",null)


        MECDataHolder.INSTANCE.locale = "en"
        eCSProductDetailRepository.bvClient = bVConversationsClient
        eCSProductDetailRepository.reviewsCb = mECReviewConversationsDisplayCallback
        eCSProductDetailRepository.ratingCb = mECDetailBulkRatingConversationsDisplayCallback

        MECDataHolder.INSTANCE.bvClient = bVConversationsClient
    }


    @Test(expected = NullPointerException::class)
    fun getProductDetailShouldFetchProductDetail() {
        eCSProductDetailRepository.getProductDetail(ecsProductMock)
        assertEquals(MECRequestType.MEC_FETCH_PRODUCT_DETAILS ,ecsProductDetailCallBack.mECRequestType)
        Mockito.verify(microEcsServices).fetchProductDetails(ecsProductMock, ecsProductDetailCallBack)
    }


    @Test
    fun fetchProductReviewShouldLoadRequest() {
        Mockito.`when`(bVConversationsClient.prepareCall(ArgumentMatchers.any(ReviewsRequest::class.java))).thenReturn(loadCallDisplayReviewMock)
        eCSProductDetailRepository.fetchProductReview("CTN", 0, 20)
        Mockito.verify(loadCallDisplayReviewMock).loadAsync(mECReviewConversationsDisplayCallback)
    }

    @Test
    fun getRatingsShouldFetchBVRatings() {
        Mockito.`when`(bVConversationsClient.prepareCall(ArgumentMatchers.any(BulkRatingsRequest::class.java))).thenReturn(loadCallDisplayRatingsMock)
        eCSProductDetailRepository.getRatings("CTN")
        Mockito.verify(loadCallDisplayRatingsMock).loadAsync(mECDetailBulkRatingConversationsDisplayCallback)
    }

    @Mock
    lateinit var  mECAddToProductCallbackMock :MECAddToProductCallback

    @Test
    fun `addTo cart pil product should call occ ECSService to call add To shopping cart with occ ECS product`() {

        eCSProductDetailRepository.mECAddToProductCallback = mECAddToProductCallbackMock
        eCSProductDetailRepository.addTocart(ecsProduct = eCSProduct)
        assertEquals(MECRequestType.MEC_ADD_PRODUCT_TO_SHOPPING_CART ,mECAddToProductCallbackMock.mECRequestType)
        Mockito.verify(ecsServices).addProductToShoppingCart(ArgumentMatchers.any(com.philips.platform.ecs.model.products.ECSProduct::class.java), ArgumentMatchers.any(MECAddToProductCallback::class.java))
    }

    @Mock
    lateinit var createShoppingCartCallbackMock: ECSCallback<ECSShoppingCart, Exception>

    @Test
    fun `create cart method should call occ ecs Service create cart api`() {
        eCSProductDetailRepository.createCart(createShoppingCartCallbackMock)
        Mockito.verify(ecsServices).createShoppingCart(createShoppingCartCallbackMock)
    }
}