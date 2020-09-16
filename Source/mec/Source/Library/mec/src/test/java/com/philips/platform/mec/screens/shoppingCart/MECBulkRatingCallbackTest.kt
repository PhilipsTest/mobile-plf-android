package com.philips.platform.mec.screens.shoppingCart

import androidx.lifecycle.MutableLiveData
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.bazaarvoice.bvandroidsdk.Error
import com.bazaarvoice.bvandroidsdk.Statistics
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(com.philips.platform.ecs.microService.ECSServices::class)
@RunWith(PowerMockRunner::class)
class MECBulkRatingCallbackTest {

    lateinit var mECBulkRatingCallback : MECBulkRatingCallback

    @Mock
    lateinit var ecsServices: ECSServices

    @Mock
    lateinit var ecsMicroServicesMock : com.philips.platform.ecs.microService.ECSServices


    @Mock
    lateinit var ecsShoppingCartViewModelMock: EcsShoppingCartViewModel


    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel

    @Mock
    lateinit var mecErrorMock: MutableLiveData<MecError>

    @Mock
    lateinit var mutableLiveData: MutableLiveData<MutableList<MECCartProductReview>>

    @Before
    fun setUp() {
        val map = HashMap<String, String>()
        map.put("key1", "value1")

        val ecsItem1= ECSItem(null,null,"1234",null,null,null,null,null,null)
        val ecsItem2= ECSItem(null,null,"12345",null,null,null,null,null,null)
        var  ecsEntriesList =  mutableListOf<ECSItem>()
        ///////
        var statisticsList: ArrayList<Statistics> = ArrayList<Statistics>()
        var st1= Statistics()

        var st2= Statistics()
        statisticsList.add(st1)
        statisticsList.add(st2)
       // statistics.productStatistics.productId
        Mockito.`when`(ecsServices.microService).thenReturn(ecsMicroServicesMock)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
        ecsShoppingCartViewModelMock.mecError=mecErrorMock
        //ecsShoppingCartViewModelMock.ecsProductsReviewList= mutableLiveData
        ecsShoppingCartViewModel=EcsShoppingCartViewModel()
      //  ecsShoppingCartViewModel.ecsServices = MECDataHolder.INSTANCE.eCSServices
        mECBulkRatingCallback=MECBulkRatingCallback(ecsEntriesList,ecsShoppingCartViewModelMock)
       // mECBulkRatingCallback.res

    }

    @Mock
    lateinit var bulkRatingsResponsemock: BulkRatingsResponse

    @Test
    fun onSuccess() {

        //var mBulkRatingsResponse: BulkRatingsResponse = BulkRatingsResponse()


      //  mECBulkRatingCallback.onSuccess(bulkRatingsResponsemock)
    }

    @Test
    fun onFailure() {
        var error=Error()
        var lError= ArrayList<Error>()
        lError.add(error)

        var ex= ConversationsException("detail message",lError )
        val ecsError = com.philips.platform.ecs.error.ECSError(1000, ex.localizedMessage)
        val mecError = MecError(ex, ecsError,null)
        Mockito.`when`(ecsShoppingCartViewModelMock.mecError.value).thenReturn(mecError)
        mECBulkRatingCallback.onFailure(ex)

        assertEquals(mecError.ecsError?.errorcode,ecsShoppingCartViewModelMock.mecError.value?.ecsError?.errorcode)


    }

}