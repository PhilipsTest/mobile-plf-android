package com.philips.platform.mec.screens.detail


import android.content.Context
import com.bazaarvoice.bvandroidsdk.*
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@PrepareForTest(EcsProductDetailViewModel::class, ECSProductDetailRepository::class, ECSProductDetailCallback::class, LoadCallDisplay::class, BVConversationsClient::class,
        MECReviewConversationsDisplayCallback::class, MECDetailBulkRatingConversationsDisplayCallback::class)
@RunWith(PowerMockRunner::class)
class ECSProductDetailRepositoryTest {

    lateinit var mContext: Context

    @Mock
    lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    @Mock
    lateinit var eCSProductDetailRepository: ECSProductDetailRepository

    @Mock
    lateinit var ecsProductDetailCallBack: ECSProductDetailCallback

    @Mock
    lateinit var ecsServices: ECSServices

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


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        eCSProductDetailRepository = ECSProductDetailRepository(ecsProductDetailViewModel, ecsServices)
        eCSProductDetailRepository.ecsProductDetailCallBack = ecsProductDetailCallBack

        eCSProduct = ECSProduct()
        eCSProduct.code = "HX12345/00"

        MECDataHolder.INSTANCE.locale = "en"
        eCSProductDetailRepository.bvClient = bVConversationsClient
        eCSProductDetailRepository.reviewsCb = mECReviewConversationsDisplayCallback
        eCSProductDetailRepository.ratingCb = mECDetailBulkRatingConversationsDisplayCallback

        MECDataHolder.INSTANCE.bvClient = bVConversationsClient
    }


    @Test
    fun getProductDetailShouldFetchProductDetail() {
        eCSProductDetailRepository.getProductDetail(eCSProduct)
        Mockito.verify(ecsServices).fetchProductDetails(eCSProduct, ecsProductDetailCallBack)
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
}