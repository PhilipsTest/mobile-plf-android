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
import com.philips.platform.mec.analytics.MECAnalyticsConstant.deliveryMethod
import com.philips.platform.mec.analytics.MECAnalyticsConstant.informationalError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.paymentType
import com.philips.platform.mec.analytics.MECAnalyticsConstant.productListLayout
import com.philips.platform.mec.analytics.MECAnalyticsConstant.promotion
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.analytics.MECAnalyticsConstant.technicalError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.transationID
import com.philips.platform.mec.analytics.MECAnalyticsConstant.userError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCode
import com.philips.platform.mec.analytics.MECAnalyticsConstant.voucherCodeStatus
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
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
        val techError= "Technical Error";
        val map = mECAnalytics.getTechnicalErrorMap(techError)
        assert(map.size>0)
        assert(map.containsKey(technicalError))
        map.get(technicalError)?.contains(techError)?.let { assert(it) }

        mECAnalytics.trackTechnicalError(techError)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun trackUserError() {
        val userErrorString= "User Error";
        val map = mECAnalytics.getUserErrorMap(userErrorString)
        assert(map.size>0)
        assert(map.containsKey(userError))
        map.get(userError)?.contains(userErrorString)?.let { assert(it) }

        mECAnalytics.trackUserError(userErrorString)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun trackInformationError() {
        val infoError= "Information Error";
        val map = mECAnalytics.getInformationErrorMap(infoError)
        assert(map.size>0)
        assert(map.containsKey(informationalError))
        map.get(informationalError)?.contains(infoError)?.let { assert(it) }

        mECAnalytics.trackInformationError(infoError)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun `tag Product List`() {
        MECDataHolder.INSTANCE.rootCategory = "Category"

        var productlist: MutableList<ECSProduct> = mutableListOf()
        var mECSProduct1 = ECSProduct()
        mECSProduct1.code = "HX1234/01"

        var mECSProduct2 = ECSProduct()
        mECSProduct2.code = "HX1290/03"
        productlist.add(mECSProduct1)
        productlist.add(mECSProduct2)

        val map = mECAnalytics.getProductListMap(productlist)
        assert(map.size==1)
        assert(map.containsKey(mecProducts))
        map.get(mecProducts)?.contains(mECSProduct1.code)?.let { assert (it) }
        map.get(mecProducts)?.contains(mECSProduct2.code)?.let { assert (it) }

        mECAnalytics.tagProductList(productlist)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun `tag Product Empty List`(){
        var productlist: MutableList<ECSProduct> = mutableListOf()
        val map = mECAnalytics.getProductListMap(productlist)
        assert(map.isEmpty())
        assert(!map.containsKey(mecProducts))
        mECAnalytics.tagProductList(productlist)
        Mockito.verify(mAppTaggingInterfaceMock, Mockito.never()).trackActionWithInfo(any(String::class.java), anyMap())

    }

    @Test
    fun `tag Product List With List OR Grid`() {
        var productlist: MutableList<com.philips.platform.ecs.microService.model.product.ECSProduct> = mutableListOf()
        val mECSProduct1 = com.philips.platform.ecs.microService.model.product.ECSProduct(ctn = "HX1234/01")

        var mECSProduct2 = com.philips.platform.ecs.microService.model.product.ECSProduct(ctn = "HX1234/01")
        productlist.add(mECSProduct1)
        productlist.add(mECSProduct2)

        val map= mECAnalytics.getProductListAndGridMap(productlist,"Grid")
        assert(map.size>1)
        assert(map.containsKey(mecProducts))
        assert(map.containsKey(productListLayout))
        map.get(mecProducts)?.contains(mECSProduct1.ctn)?.let { assert (it) }
        map.get(mecProducts)?.contains(mECSProduct2.ctn)?.let { assert (it) }

        mECAnalytics.tagProductList(productlist, "Grid")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun `tag Product Empty List With List OR Grid`() {
        var productlist: MutableList<com.philips.platform.ecs.microService.model.product.ECSProduct> = mutableListOf()
        val map= mECAnalytics.getProductListAndGridMap(productlist,"Grid")
        assert(map.isEmpty())
        assert(!map.containsKey(mecProducts))
        assert(!map.containsKey(productListLayout))

        mECAnalytics.tagProductList(productlist, "Grid")
        Mockito.verify(mAppTaggingInterfaceMock,  Mockito.never()).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun `tag Actions With Order Products Info`() {
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

        val mapObtained= mECAnalytics.getOrderProductInfoMap(map,entries)
        assert(mapObtained.size>1)
        assert(mapObtained.containsKey(mecProducts))
        map.get(mecProducts)?.contains(mECSProduct.code)?.let { assert (it) }

        mECAnalytics.tagActionsWithOrderProductsInfo(map, entries)
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())
    }

    @Test
    fun `tag Actions With Order empty Products Info`(){
        val map = HashMap<String, String>()
       // map.put("key1", "value1")


        var entries = ArrayList<ECSEntries>()


        val mapObtained= mECAnalytics.getOrderProductInfoMap(map,entries)
        assert(mapObtained.isEmpty())
        assert(!mapObtained.containsKey(mecProducts))


        mECAnalytics.tagActionsWithOrderProductsInfo(map, entries)
        Mockito.verify(mAppTaggingInterfaceMock, never()).trackActionWithInfo(any(String::class.java), anyMap())

    }


    @Test
    fun `tag Purchase Order`() {

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

        val map= mECAnalytics.getPurchaseOrderMap(mECSOrderDetail, "new")
        assert(map.size>=4)
        assert(map.containsKey(specialEvents))
        assert(map.containsKey(paymentType))
        assert(map.containsKey(transationID))
        assert(map.containsKey(deliveryMethod))
        assert(map.containsKey(promotion))
        assert(map.containsKey(voucherCode))
        assert(map.containsKey(voucherCodeStatus))

        mECAnalytics.tagPurchaseOrder(mECSOrderDetail, "new")
        Mockito.verify(mAppTaggingInterfaceMock, atLeastOnce()).trackActionWithInfo(any(String::class.java), anyMap())
        Mockito.verify(mAppTaggingInterfaceMock).trackActionWithInfo(any(String::class.java), anyMap())

    }

    @Test
    fun `tag Purchase Order with incomplete data`() {

        var mECSOrderDetail: ECSOrderDetail = ECSOrderDetail()


        val map= mECAnalytics.getPurchaseOrderMap(mECSOrderDetail, "new")
        assert(map.size==2)
        assert(map.containsKey(specialEvents))
        assert(map.containsKey(paymentType))
        assert(!map.containsKey(transationID))
        assert(!map.containsKey(deliveryMethod))
        assert(!map.containsKey(promotion))
        assert(!map.containsKey(voucherCode))
        assert(!map.containsKey(voucherCodeStatus))

        mECAnalytics.tagPurchaseOrder(mECSOrderDetail, "new")
        Mockito.verify(mAppTaggingInterfaceMock, never()).trackActionWithInfo(any(String::class.java), anyMap())

    }


    @Test
    fun setCurrencyString() {
        mECAnalytics.setCurrencyString("en_US")
        assertEquals("USD", mECAnalytics.currencyCode)

    }


}