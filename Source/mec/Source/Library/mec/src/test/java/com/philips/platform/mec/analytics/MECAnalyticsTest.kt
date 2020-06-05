package com.philips.platform.mec.analytics

import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.ecs.model.address.ECSDeliveryMode
import com.philips.platform.ecs.model.cart.BasePriceEntity
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.orders.AppliedOrderPromotions
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.ecs.model.orders.Promotion
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.model.products.PriceEntity
import com.philips.platform.ecs.model.voucher.AppliedValue
import com.philips.platform.ecs.model.voucher.ECSVoucher
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class MECAnalyticsTest {


    @Mock
    var mAppTaggingInterfaceMock: AppTaggingInterface = mock(AppTaggingInterface::class.java);


    val mECAnalytics get() = MECAnalytics.Companion


    @Before
    fun setUp() {

        mECAnalytics.setCurrencyString("en_US")
        mECAnalytics.mAppTaggingInterface = mAppTaggingInterfaceMock
        mECAnalytics.countryCode = "US"
        mECAnalytics.currencyCode = "USD"
    }

    @Test
    fun initMECAnalytics() {
    }

    @Test
    fun trackPage() {
        mECAnalytics.trackPage("some page")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackPageWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackPageWithInfo(any(String::class.java), anyMap())
    }


    @Test
    fun trackMultipleActions() {
        val map = HashMap<String, String>()
        map.put("key1", "value1")
        mECAnalytics.trackMultipleActions("state", map)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun trackInAppNotofication() {
        val map = HashMap<String, String>()
        map.put("key1", "value1")
        mECAnalytics.trackInAppNotofication("descriotion", "response")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())

    }

    @Test
    fun trackTechnicalError() {
        mECAnalytics.trackTechnicalError("Technical Error")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun trackUserError() {
        mECAnalytics.trackUserError("User Error")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun trackInformationError() {

        mECAnalytics.trackInformationError("Information Error")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun tagProductList() {

        var productlist: MutableList<ECSProduct> = mutableListOf()
        var mECSProduct1 = ECSProduct()
        mECSProduct1.code = "HX1234/01"

        var mECSProduct2 = ECSProduct()
        mECSProduct2.code = "HX1290/03"
        productlist.add(mECSProduct1)
        productlist.add(mECSProduct2)

        mECAnalytics.tagProductList(productlist)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun tagProductListWithListORGrid() {
        var productlist: MutableList<ECSProduct> = mutableListOf()
        var mECSProduct1 = ECSProduct()
        mECSProduct1.code = "HX1234/01"

        var mECSProduct2 = ECSProduct()
        mECSProduct2.code = "HX1290/03"
        productlist.add(mECSProduct1)
        productlist.add(mECSProduct2)

        mECAnalytics.tagProductList(productlist, "Grid")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun tagActionsWithOrderProductsInfo() {
        val map = HashMap<String, String>()
        map.put("key1", "value1")

        var eCSentry = ECSEntries()
        var mECSProduct = ECSProduct()
        mECSProduct.code = "ConsignmentCode123ABC"
        var priceEntity = PriceEntity()
        priceEntity.value = 12.9
        mECSProduct.price = priceEntity

        eCSentry.product = mECSProduct
        eCSentry.quantity = 2

        var basePriceEntity = BasePriceEntity()
        basePriceEntity.value = 10.7
        eCSentry.basePrice = basePriceEntity
        var entries = ArrayList<ECSEntries>()
        entries.add(eCSentry)

        mECAnalytics.tagActionsWithOrderProductsInfo(map, entries)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }


    @Test
    fun tagPurchaseOrder() {

        var mECSOrderDetail: ECSOrderDetail = ECSOrderDetail()
        var mECSDeliveryMode = ECSDeliveryMode()
        mECSDeliveryMode.name = "UPS"
        mECSOrderDetail.code = "123456"
        mECSOrderDetail.deliveryMode = mECSDeliveryMode

        var mPromotion: Promotion = Promotion()
        mPromotion.code = "Promo"
        var mAppliedOrderPromotions: AppliedOrderPromotions = AppliedOrderPromotions()
        mAppliedOrderPromotions.promotion = mPromotion
        var promoList: ArrayList<AppliedOrderPromotions> = ArrayList<AppliedOrderPromotions>()
        promoList.add(mAppliedOrderPromotions)
        mECSOrderDetail.appliedOrderPromotions = promoList


        var mAppliedValue = AppliedValue()
        mAppliedValue.formattedValue = "333.55$"
        var mECSVoucher = ECSVoucher()
        mECSVoucher.code = "Voucher"
        mECSVoucher.voucherCode = "KLOP"
        mECSVoucher.appliedValue = mAppliedValue
        var voucherList: ArrayList<ECSVoucher> = ArrayList<ECSVoucher>()
        voucherList.add(mECSVoucher)
        mECSOrderDetail.appliedVouchers = voucherList

        var eCSentry = ECSEntries()
        var mECSProduct = ECSProduct()
        mECSProduct.code = "ConsignmentCode123ABC"
        var priceEntity = PriceEntity()
        priceEntity.value = 12.9
        mECSProduct.price = priceEntity

        eCSentry.product = mECSProduct
        eCSentry.quantity = 2

        var basePriceEntity = BasePriceEntity()
        basePriceEntity.value = 10.7
        eCSentry.basePrice = basePriceEntity

        var entries = ArrayList<ECSEntries>()
        entries.add(eCSentry)
        mECSOrderDetail.entries = entries

        mECAnalytics.tagPurchaseOrder(mECSOrderDetail, "new")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())

    }


    @Test
    fun setCurrencyString() {
        mECAnalytics.setCurrencyString("en_US")
        assertEquals("USD", mECAnalytics.currencyCode)

    }


}