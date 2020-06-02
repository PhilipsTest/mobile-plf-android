package com.philips.platform.mec.analytics

import android.content.Context
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

class MECAnalyticsTest {



    @Mock
     var mAppTaggingInterfaceMock :AppTaggingInterface = mock(AppTaggingInterface::class.java);


    val mECAnalytics get() =MECAnalytics.Companion



    @Mock
    lateinit var contextMock: Context

    @Before
    fun setUp() {

        MECAnalytics.setCurrencyString("USD")
        mECAnalytics.mAppTaggingInterface =mAppTaggingInterfaceMock
        mECAnalytics.countryCode="US"
        mECAnalytics.currencyCode="USD"
    }

    @Test
    fun initMECAnalytics() {
    }

    @Test
    fun trackPage() {
        mECAnalytics.trackPage("some page")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackPageWithInfo(any(String::class.java),anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackPageWithInfo(any(String::class.java),anyMap())
    }

    @Test
    fun trackAction() {
        val map = HashMap<String, String>()
        map.put("key1","value1")
        val any: Any =   map
        mECAnalytics.trackAction("state","key",any)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java),anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java),anyMap())
    }

    @Test
    fun trackMultipleActions() {
        val map = HashMap<String, String>()
        map.put("key1","value1")
        mECAnalytics.trackMultipleActions("state",map)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java),anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java),anyMap())
    }

    @Test
    fun trackInAppNotofication() {
    }

    @Test
    fun trackTechnicalError() {
    }

    @Test
    fun trackUserError() {
    }

    @Test
    fun trackInformationError() {
    }

    @Test
    fun tagProductList() {
    }

    @Test
    fun tagProductList1() {
    }

    @Test
    fun tagActionsWithOrderProductsInfo() {
    }

    @Test
    fun getProductInfo() {
    }

    @Test
    fun getProductInfoWithChangedQuantity() {
    }

    @Test
    fun getProductInfoWithChangedQuantity1() {
    }

    @Test
    fun tagPurchaseOrder() {

        var mECSOrderDetail:ECSOrderDetail= ECSOrderDetail()
        var mECSDeliveryMode = ECSDeliveryMode()
        mECSDeliveryMode.name="UPS"
        mECSOrderDetail.code="123456"
        mECSOrderDetail.deliveryMode=mECSDeliveryMode

        var mPromotion :Promotion= Promotion()
        mPromotion.code="Promo"
        var mAppliedOrderPromotions: AppliedOrderPromotions = AppliedOrderPromotions()
        mAppliedOrderPromotions.promotion=mPromotion
        var  promoList: ArrayList<AppliedOrderPromotions> = ArrayList<AppliedOrderPromotions>()
        promoList.add(mAppliedOrderPromotions)
        mECSOrderDetail.appliedOrderPromotions=promoList


        var mAppliedValue = AppliedValue()
        mAppliedValue.formattedValue="333.55$"
        var mECSVoucher = ECSVoucher()
        mECSVoucher.code="Voucher"
        mECSVoucher.voucherCode="KLOP"
        mECSVoucher.appliedValue=mAppliedValue
        var voucherList :ArrayList<ECSVoucher> = ArrayList<ECSVoucher>()
        voucherList.add(mECSVoucher)
        mECSOrderDetail.appliedVouchers=voucherList

        var eCSentry  = ECSEntries()
        var mECSProduct= ECSProduct()
        mECSProduct.code="ConsignmentCode123ABC"
        var priceEntity= PriceEntity()
        priceEntity.value=12.9
        mECSProduct.price=priceEntity

        eCSentry.product=mECSProduct
        eCSentry.quantity=2

        var basePriceEntity= BasePriceEntity()
        basePriceEntity.value=10.7
        eCSentry.basePrice=basePriceEntity

        var entries = ArrayList<ECSEntries>()
        entries.add(eCSentry)
        mECSOrderDetail.entries=entries

        mECAnalytics.tagPurchaseOrder(mECSOrderDetail,"new")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java),anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java),anyMap())

    }

    @Test
    fun getDefaultString() {
    }
}