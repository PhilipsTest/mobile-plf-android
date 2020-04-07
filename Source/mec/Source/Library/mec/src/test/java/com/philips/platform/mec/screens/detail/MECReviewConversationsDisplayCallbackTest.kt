package com.philips.platform.mec.screens.detail

import androidx.lifecycle.MutableLiveData
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.bazaarvoice.bvandroidsdk.ReviewResponse
import com.philips.platform.mec.common.MecError
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

@PrepareForTest(EcsProductDetailViewModel::class)
@RunWith(PowerMockRunner::class)
class MECReviewConversationsDisplayCallbackTest {

    lateinit var mecReviewConversationsDisplayCallback: MECReviewConversationsDisplayCallback

    @Mock
    lateinit var ecsProductDetailViewModelMock: EcsProductDetailViewModel

    @Mock
    lateinit var exceptionMock: ConversationsException

    @Mock
    lateinit var mutableLiveDataMecErrorMock: MutableLiveData<MecError>

    @Mock
    lateinit var mutableLiveDataMock: MutableLiveData<ReviewResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Whitebox.setInternalState(ecsProductDetailViewModelMock, "review", mutableLiveDataMock)
        mecReviewConversationsDisplayCallback = MECReviewConversationsDisplayCallback(ecsProductDetailViewModelMock)
    }

    @Test
    fun onSuccess() {

        val reviewResponse = ReviewResponse()
        mecReviewConversationsDisplayCallback.onSuccess(reviewResponse)
        assertNotNull(ecsProductDetailViewModelMock.review)

        //TODO
        //assertNotNull(ecsProductDetailViewModelMock.bulkRatingResponse.value)
    }

    /*   @Test
       fun onFailure() {
           mecReviewConversationsDisplayCallback.onFailure(exceptionMock)
           assertNotNull(ecsProductDetailViewModelMock.mecError)
           //TODO
           //assertNotNull(ecsProductDetailViewModelMock.mecError.value)
       }*/
}