package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import com.philips.platform.ecs.model.orders.*
import com.philips.platform.ecs.model.voucher.AppliedValue
import com.philips.platform.ecs.model.voucher.ECSVoucher
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(PowerMockRunner::class)
class MECOrderDetailServiceTest {
     var mMECOrderDetailService : MECOrderDetailService = MECOrderDetailService()


    lateinit var  mCSOrderDetail : ECSOrderDetail
    var  mList =  mutableListOf<Entries>()
    var cartSummary : MutableList<MECCartSummary> = mutableListOf<MECCartSummary>()

    @Mock
    lateinit var mockContext: Context

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
        mCSOrderDetail = ECSOrderDetail()


    }

    @Test
    fun getProductSubcategory() {
        var entries : Entries = Entries()
        assertNull(mMECOrderDetailService.getProductSubcategory(mCSOrderDetail))


    }

    @Test
    fun addAppliedOrderPromotionsToCartSummaryList() {
        var mPromotion :Promotion= Promotion()
        mPromotion.code="Promo"
        var mAppliedOrderPromotions: AppliedOrderPromotions = AppliedOrderPromotions()
        mAppliedOrderPromotions.promotion=mPromotion
        var  promoList: ArrayList<AppliedOrderPromotions> = ArrayList<AppliedOrderPromotions>()
        promoList.add(mAppliedOrderPromotions)
        mCSOrderDetail.appliedOrderPromotions=promoList
        var mCost : Cost = Cost()
        mCost.formattedValue="123.00$"
        mCSOrderDetail.orderDiscounts=mCost
        mMECOrderDetailService.addAppliedOrderPromotionsToCartSummaryList(mCSOrderDetail,cartSummary)

        assertEquals("Promo",cartSummary.get(0).name)
        assertEquals("-123.00\$",cartSummary.get(0).price)
    }

    @Test
    fun addAppliedVoucherToCartSummaryList() {
        var mAppliedValue = AppliedValue()
        mAppliedValue.formattedValue="333.55$"
        var mECSVoucher = ECSVoucher()
        mECSVoucher.code="Voucher"
        mECSVoucher.appliedValue=mAppliedValue
        var voucherList :ArrayList<ECSVoucher> = ArrayList<ECSVoucher>()
        voucherList.add(mECSVoucher)
        mCSOrderDetail.appliedVouchers=voucherList
        mMECOrderDetailService.addAppliedVoucherToCartSummaryList(mCSOrderDetail,cartSummary)

        assertEquals("Voucher",cartSummary.get(0).name)
        assertEquals("-333.55\$",cartSummary.get(0).price)
    }

    @Test
    fun addDeliveryCostToCartSummaryList() {
        var mCost : Cost = Cost()
        mCost.formattedValue="444.00$"
        mCSOrderDetail.deliveryCost=mCost
        var mContext =mock(Context::class.java)
        Mockito.`when`(mockContext.getString(R.string.mec_shipping_cost)).thenReturn("Delivery Cost")
        mMECOrderDetailService.addDeliveryCostToCartSummaryList(mockContext,mCSOrderDetail,cartSummary)

        assertEquals("Delivery Cost",cartSummary.get(0).name)
        assertEquals("444.00\$",cartSummary.get(0).price)

    }

    @Test
    fun getEntriesFromConsignMent() {
    }

    @Test
    fun getOrderTrackUrl() {
    }
}