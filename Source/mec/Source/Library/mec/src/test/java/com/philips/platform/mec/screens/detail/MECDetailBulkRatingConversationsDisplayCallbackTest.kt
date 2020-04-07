package com.philips.platform.mec.screens.detail

import androidx.lifecycle.MutableLiveData
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.philips.platform.mec.common.MecError
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
class MECDetailBulkRatingConversationsDisplayCallbackTest {


    lateinit var mECDetailBulkRatingConversationsDisplayCallback: MECDetailBulkRatingConversationsDisplayCallback

    @Mock
    lateinit var ecsProductDetailViewModelMock: EcsProductDetailViewModel

    @Mock
    lateinit var responseMock: BulkRatingsResponse

    @Mock
    lateinit var mutableLiveDataMock: MutableLiveData<BulkRatingsResponse>

    @Mock
    lateinit var exceptionMock: ConversationsException

    @Mock
    lateinit var mutableLiveDataMecErrorMock: MutableLiveData<MecError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        Whitebox.setInternalState(ecsProductDetailViewModelMock, "bulkRatingResponse", mutableLiveDataMock)
        Whitebox.setInternalState(ecsProductDetailViewModelMock, "mecError", mutableLiveDataMecErrorMock)

//        Mockito.`when`(ecsProductDetailViewModelMock.bulkRatingResponse).thenReturn(mutableLiveDataMock)
//        Mockito.`when`(ecsProductDetailViewModelMock.mecError).thenReturn(mutableLiveDataMecErrorMock)
        mECDetailBulkRatingConversationsDisplayCallback = MECDetailBulkRatingConversationsDisplayCallback(ecsProductDetailViewModelMock)
    }

    @Test
    fun onSuccess() {
        mECDetailBulkRatingConversationsDisplayCallback.onSuccess(responseMock)
        assertNotNull(ecsProductDetailViewModelMock.bulkRatingResponse)

        //TODO
        //assertNotNull(ecsProductDetailViewModelMock.bulkRatingResponse.value)
    }

    @Test
    fun onFailure() {
        mECDetailBulkRatingConversationsDisplayCallback.onFailure(exceptionMock)
        assertNotNull(ecsProductDetailViewModelMock.mecError)
        //assertNotNull(ecsProductDetailViewModelMock.mecError.value)
    }
}