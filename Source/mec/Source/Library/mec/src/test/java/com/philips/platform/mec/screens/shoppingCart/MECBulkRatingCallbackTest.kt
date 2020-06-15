package com.philips.platform.mec.screens.shoppingCart

import androidx.lifecycle.MutableLiveData
import com.bazaarvoice.bvandroidsdk.ConversationsException
import com.bazaarvoice.bvandroidsdk.Error
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.mec.common.MecError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MECBulkRatingCallbackTest {

    lateinit var mECBulkRatingCallback : MECBulkRatingCallback


    @Mock
    lateinit var ecsProductsMock: MutableList<ECSEntries>

    @Mock
    lateinit var ecsShoppingCartViewModelMock: EcsShoppingCartViewModel

    @Mock
    lateinit var mecErrorMock: MutableLiveData<MecError>

    @Before
    fun setUp() {
        ecsShoppingCartViewModelMock.mecError=mecErrorMock
        mECBulkRatingCallback=MECBulkRatingCallback(ecsProductsMock,ecsShoppingCartViewModelMock)

    }

    @Test
    fun onSuccess() {
    }

    @Test
    fun onFailure() {
        var error=Error()
        var lError= ArrayList<Error>()
        lError.add(error)

        var ex:ConversationsException= ConversationsException("detail message",lError )
        mECBulkRatingCallback.onFailure(ex)

    }

}