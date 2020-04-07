package com.philips.platform.mec.screens.orderSummary

import android.content.Context
import com.philips.cdp.di.ecs.model.cart.*
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeast
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

@RunWith(PowerMockRunner::class)
class MECOrderSummaryServicesTest {
    private lateinit var mecOrderSummaryServices: MECOrderSummaryServices

    @Mock
    lateinit var mockContext: Context

    @Mock
    lateinit var mockEcsShoppingCart: ECSShoppingCart

    @Mock
    lateinit var mockCartSummaryList: MutableList<MECCartSummary>

    @Mock
    lateinit var mockAppliedOrderPromotionEntity: AppliedOrderPromotionEntity

    @Mock
    lateinit var promotionEntity: PromotionEntity

    @Mock
    lateinit var promotionDiscount: PromotionDiscount

    @Mock
    lateinit var appliedVoucherEntity: AppliedVoucherEntity

    @Mock
    lateinit var deliveryCostEntity: DeliveryCostEntity

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Whitebox.setInternalState(promotionEntity, "name", "promotionEntity")
        Whitebox.setInternalState(mockAppliedOrderPromotionEntity, "promotion", promotionEntity)
        Whitebox.setInternalState(mockEcsShoppingCart, "appliedOrderPromotions", listOf(mockAppliedOrderPromotionEntity))
        `when`(mockEcsShoppingCart.appliedOrderPromotions).thenReturn(listOf(mockAppliedOrderPromotionEntity))
        `when`(mockAppliedOrderPromotionEntity.promotion).thenReturn(promotionEntity)
        `when`(promotionEntity.name).thenReturn("promotionEntity")
        `when`(promotionEntity.promotionDiscount).thenReturn(promotionDiscount)
        `when`(promotionEntity.promotionDiscount.formattedValue).thenReturn("formattedValue")

        Whitebox.setInternalState(appliedVoucherEntity, "name", "VoucherName")
        `when`(appliedVoucherEntity.name).thenReturn("VoucherName")
        Whitebox.setInternalState(mockEcsShoppingCart, "appliedVouchers", listOf(appliedVoucherEntity))
        `when`(mockEcsShoppingCart.appliedVouchers).thenReturn(listOf(appliedVoucherEntity))

        `when`(mockEcsShoppingCart.deliveryCost).thenReturn(deliveryCostEntity)

        mecOrderSummaryServices = MECOrderSummaryServices()
    }

    @Test
    fun testAppliedOrderPromotionsToCartSummaryList_With_PromotionName() {
        mecOrderSummaryServices.addAppliedOrderPromotionsToCartSummaryList(mockEcsShoppingCart, mockCartSummaryList)
        Mockito.verify(mockEcsShoppingCart, atLeast(1)).appliedOrderPromotions
    }

    @Test
    fun testAppliedOrderPromotionsToCartSummaryList_With_PromotionName_Null() {
        `when`(promotionEntity.name).thenReturn(null)
        mecOrderSummaryServices.addAppliedOrderPromotionsToCartSummaryList(mockEcsShoppingCart, mockCartSummaryList)
        Mockito.verify(mockEcsShoppingCart, atLeast(1)).appliedOrderPromotions
    }

    @Test
    fun testAppliedVoucherToCartSummaryList() {
        mecOrderSummaryServices.addAppliedVoucherToCartSummaryList(mockEcsShoppingCart, mockCartSummaryList)
        Mockito.verify(mockEcsShoppingCart, atLeast(1)).appliedVouchers
    }

    @Test
    fun testAppliedVoucherToCartSummaryList_With_VoucherNull() {
        `when`(appliedVoucherEntity.name).thenReturn(null)
        mecOrderSummaryServices.addAppliedVoucherToCartSummaryList(mockEcsShoppingCart, mockCartSummaryList)
        Mockito.verify(mockEcsShoppingCart, atLeast(1)).appliedVouchers
    }

    @Test
    fun testDeliveryCostToCartSummaryList() {
        `when`(deliveryCostEntity.formattedValue).thenReturn("deliveryCost")
        `when`(mockContext.getString(R.string.mec_shipping_cost)).thenReturn("Delivery Cost")
        mecOrderSummaryServices.addDeliveryCostToCartSummaryList(mockContext, mockEcsShoppingCart, mockCartSummaryList)
        Mockito.verify(mockEcsShoppingCart, atLeast(1)).deliveryCost
    }
}