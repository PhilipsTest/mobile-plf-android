package com.philips.platform.mec.screens.orderSummary

import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.mec.screens.shoppingCart.MECCartSummary
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MECOrderSummaryServicesTest {
    private lateinit var mecOrderSummaryServices: MECOrderSummaryServices

    //TODO write fresh test cases from json
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mecOrderSummaryServices = MECOrderSummaryServices()
    }

    @Test
    fun test() {
        val ecsShoppingCart: ECSShoppingCart = ECSShoppingCart(null)
        val mECCartSummary = MECCartSummary("promotion","30.27")
        val mECCartSummary1 = MECCartSummary("promotion1","40.27")
        val cartSummaryList: MutableList<MECCartSummary> = mutableListOf()
        cartSummaryList.add(mECCartSummary)
        cartSummaryList.add(mECCartSummary1)
        mecOrderSummaryServices.addAppliedOrderPromotionsToCartSummaryList(ecsShoppingCart,cartSummaryList)
    }
}