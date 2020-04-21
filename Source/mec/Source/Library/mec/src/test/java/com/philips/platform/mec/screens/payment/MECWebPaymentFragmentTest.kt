package com.philips.platform.mec.screens.payment

import android.content.Context
import com.philips.platform.mec.analytics.MECAnalytics
import org.junit.Before
import org.junit.Test
import org.mockito.Mock


class MECWebPaymentFragmentTest {

    private val PAYMENT_SUCCESS_CALLBACK_URL = "http://www.philips.com/paymentSuccess"
    private val PAYMENT_PENDING_CALLBACK_URL = "http://www.philips.com/paymentPending"
    private val PAYMENT_FAILURE_CALLBACK_URL = "http://www.philips.com/paymentFailure"
    private val PAYMENT_CANCEL_CALLBACK_URL = "http://www.philips.com/paymentCancel"


    @Mock
    lateinit var mockContext: Context



@Mock
lateinit var mecAnalytics: MECAnalytics



    lateinit var mECWebPaymentFragment: MECWebPaymentFragment

    @Before
    fun setUp() {

       // MockitoAnnotations.initMocks(this)
        mECWebPaymentFragment = MECWebPaymentFragment()
       // Mockito.`when`(MECAnalytics.tagActionsWithOrderProductsInfo(Any:, Any))thenR

        //PowerMockito.mockStatic(MECAnalytics.javaClass)

       // (MECAnalytics.tagActionsWithOrderProductsInfo(org.mockito.internal.matchers.Any ,Any)).willReturn(...)

       // List<Entries>
       // doNothing().`when`(MECAnalytics.tagActionsWithOrderProductsInfo(anyMap(), anyList()))

      //doNothing().`when`(mECAnalytics).tagActionsWithOrderProductsInfo(anyObject(),anyObject())
    }

    @Test
    fun shouldOverrideUrlLoading() {
      // assertEquals(true,mECWebPaymentFragment.shouldOverrideUrlLoading(PAYMENT_SUCCESS_CALLBACK_URL))
    }
}