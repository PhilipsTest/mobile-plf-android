package com.philips.platform.mec.screens.orderSummary

import android.content.Context
import com.philips.platform.ecs.microService.model.cart.*
import com.philips.platform.ecs.microService.model.common.Price
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals

@RunWith(PowerMockRunner::class)
class MECOrderSummaryServicesTest {
    private lateinit var mecOrderSummaryServices: MECOrderSummaryServices

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mecOrderSummaryServices = MECOrderSummaryServices()
    }

    @Test
    fun testAddAppliedOrderPromotionsToCartSummaryList() {

        val promotionList : MutableList<Promotion> = mutableListOf()
        val promotionDiscount1 = PromotionDiscount("USD","$ 10.00","BUY",10.0)
        val promotionDiscount2 = PromotionDiscount("USD","$ 20.00","BUY",20.0)
        val Promotion1 : Promotion = Promotion("1234","enabled","01/30/2020","summer promotion","new",promotionDiscount1)
        val Promotion2 : Promotion = Promotion("4321","enabled","01/30/2020","summer promotion","new",promotionDiscount2)
        promotionList.add(Promotion1)
        promotionList.add(Promotion2)
        val promotions = Promotions(null,null,null,promotionList.toList())
        val attributes : Attributes = Attributes(null,null,null,null,null,null,null,promotions,
                null,null,null,null,null,null)
        val data : Data = Data(attributes,"1234","OLD")
        val ecsShoppingCart: ECSShoppingCart = ECSShoppingCart(data)

        val cartSummaryList: MutableList<MECCartSummary> = mutableListOf()
        mecOrderSummaryServices.addAppliedOrderPromotionsToCartSummaryList(ecsShoppingCart,cartSummaryList)
        assertEquals(2,cartSummaryList.size)
        assertEquals("1234",cartSummaryList[0].name)
        assertEquals("4321",cartSummaryList[1].name)
        assertEquals("$ 10.00",cartSummaryList[0].price)
        assertEquals("$ 20.00",cartSummaryList[1].price)
    }

    @Test
    fun testAddAppliedVoucherToCartSummaryList() {
        val price1 = Price("$","20.30",20.30)
        val price2 = Price("$","30.30",30.30)

        val voucherDiscountPrice1 = Price("$","50.30",20.30)
        val voucherDiscountPrice2 = Price("$","60.30",30.30)
        val voucher1 = Voucher(true,"1234",price1,voucherDiscountPrice1,"grooming voucher","discount voucher")
        val voucher2 = Voucher(true,"4321",price2,voucherDiscountPrice2,"skin care voucher","discount voucher")

        val voucherList = mutableListOf<Voucher>()
        voucherList.add(voucher1)
        voucherList.add(voucher2)

        val attributes : Attributes = Attributes(null,null,null,null,null,null,null,null,
                null,null,null,voucherList,null,null)
        val data : Data = Data(attributes,"1234","OLD")
        val ecsShoppingCart: ECSShoppingCart = ECSShoppingCart(data)
        val cartSummaryList: MutableList<MECCartSummary> = mutableListOf()
        mecOrderSummaryServices.addAppliedVoucherToCartSummaryList(ecsShoppingCart,cartSummaryList)

        assertEquals(2,cartSummaryList.size)
        assertEquals("grooming voucher",cartSummaryList[0].name)
        assertEquals("skin care voucher",cartSummaryList[1].name)
        assertEquals("20.30",cartSummaryList[0].price)
        assertEquals("30.30",cartSummaryList[1].price)
    }

    @Mock
    lateinit var contextMock: Context

    @Test
    fun testAddDeliveryCostToCartSummaryList() {

        Mockito.`when`(contextMock.getString(R.string.mec_shipping_cost)).thenReturn("Shipping cost")

        val deliveryPrice = Price("$","20.30",20.30)

        val pricing = Pricing (deliveryPrice,null,null,null,null,null,null,null,null,null,null)
        val attributes : Attributes = Attributes(null,null,null,null,null,null,pricing,null,
                null,null,null,null,null,null)
        val data : Data = Data(attributes,"1234","OLD")
        val ecsShoppingCart: ECSShoppingCart = ECSShoppingCart(data)
        val cartSummaryList: MutableList<MECCartSummary> = mutableListOf()


        mecOrderSummaryServices.addDeliveryCostToCartSummaryList(contextMock,ecsShoppingCart,cartSummaryList)
        assertEquals("Shipping cost",cartSummaryList[0].name)
        assertEquals("20.30",cartSummaryList[0].price)
    }
}