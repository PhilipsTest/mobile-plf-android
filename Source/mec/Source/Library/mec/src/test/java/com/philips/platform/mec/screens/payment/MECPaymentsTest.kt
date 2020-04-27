package com.philips.platform.mec.screens.payment

import com.philips.platform.ecs.model.payment.ECSPayment
import com.philips.platform.mec.utils.MECConstant
import junit.framework.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MECPaymentsTest {

    var mecPaymentList : MutableList<MECPayment> = mutableListOf()
    var ecsPayment : ECSPayment = ECSPayment()
    var mecPayment = MECPayment(ecsPayment)

    lateinit var mecPayments: MECPayments

    @Before
    fun setUp() {
        mecPaymentList.add(mecPayment)
        mecPayments=MECPayments(mecPaymentList,true)
    }

    @Test
    fun isNewCardPresent() {

        ecsPayment.id = MECConstant.NEW_CARD_PAYMENT;
        assertEquals(true,mecPayments.isNewCardPresent() )

    }

    @Test
    fun getNewCard() {
        ecsPayment.id = MECConstant.NEW_CARD_PAYMENT;
        assertNotNull(mecPayments.getNewCard())
        ecsPayment.id = ""
        assertNull(mecPayments.getNewCard())
    }

    @Test
    fun getSelectedPayment() {
        var ecsSelectedPayment : ECSPayment = ECSPayment()
        ecsSelectedPayment.id="12345"
        ecsPayment.id="12345"
        var mecSelectedPayment = MECPayment(ecsSelectedPayment)
        assertEquals("12345",mecSelectedPayment.ecsPayment.id)
    }



    @Test
    fun isPaymentDownloaded() {
        assertEquals(true,mecPayments.isPaymentDownloaded )
    }

    @Test
    fun setPaymentDownloaded() {
    }
}