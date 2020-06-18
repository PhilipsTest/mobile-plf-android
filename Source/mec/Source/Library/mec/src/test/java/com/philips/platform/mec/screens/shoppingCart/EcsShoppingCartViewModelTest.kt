/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.mec.screens.shoppingCart

import android.content.Context
import android.view.View
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.cart.BasePriceEntity
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.ecs.model.products.PriceEntity
import com.philips.platform.ecs.model.products.StockEntity
import com.philips.platform.mec.R
import com.philips.platform.mec.any
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener
import com.philips.platform.uid.view.widget.Label
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals


@PrepareForTest(ECSShoppingCartRepository::class, ECSShoppingCartCallback::class, ECSVoucherCallback::class, ECSCallback::class)
@RunWith(PowerMockRunner::class)
class EcsShoppingCartViewModelTest {

    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel

    val mECutility get() = MECutility.Companion

    @Mock
    lateinit var appInfraMock: AppInfraInterface

    @Mock
    lateinit var ecsShoppingCartRepositoryMock: ECSShoppingCartRepository


    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var ecsShoppingCartCallbackMock: ECSShoppingCartCallback



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        MECDataHolder.INSTANCE.appinfra = appInfraMock
        MECDataHolder.INSTANCE.eCSServices = ecsServicesMock
        ecsShoppingCartRepositoryMock.ecsServices = ecsServicesMock
        ecsShoppingCartRepositoryMock.ecsShoppingCartCallback = ecsShoppingCartCallbackMock

        ecsShoppingCartViewModel = EcsShoppingCartViewModel()
        ecsShoppingCartViewModel.ecsServices = ecsServicesMock

        ecsShoppingCartViewModel.ecsShoppingCartRepository = ecsShoppingCartRepositoryMock
    }


    @Test
    fun testCreateShoppingCart() {

        ecsShoppingCartViewModel.createShoppingCart("")
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce())
                .createShoppingCart(ArgumentMatchers.any())

    }


    @Test
    fun testGetShoppingCart() {

      /*  ecsShoppingCartViewModel.getShoppingCart()
         //Mockito.verify(ecsServicesMock, Mockito.atLeastOnce()).fetchShoppingCart(ArgumentMatchers.any())
        Mockito.verify(ecsShoppingCartRepositoryMock, Mockito.atLeastOnce()).fetchShoppingCart()*/

    }


    @Test
    fun TestUpdateQuantity() {


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


        ecsShoppingCartViewModel.updateQuantity(eCSentry, 3)
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce())
                .updateShoppingCart(ArgumentMatchers.anyInt(), ArgumentMatchers.anyObject(), ArgumentMatchers.any())

    }

    @Test
    fun TestAddVoucher() {
        ecsShoppingCartViewModel.addVoucher("", MECRequestType.MEC_APPLY_VOUCHER)
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce())
                .applyVoucher(ArgumentMatchers.anyString(), ArgumentMatchers.any())
    }

    @Test
    fun TestRemoveVoucher() {
        ecsShoppingCartViewModel.removeVoucher("")
        Mockito.verify(ecsServicesMock, Mockito.atLeastOnce())
                .removeVoucher(ArgumentMatchers.anyString(), ArgumentMatchers.any())
    }


    @Test
    fun TestTagApplyOrDeleteVoucher() {
        ecsShoppingCartViewModel.deleteVoucherString=""
        ecsShoppingCartViewModel.tagApplyOrDeleteVoucher(MECRequestType.MEC_GET_APPLIED_VOUCHERS)
        ecsShoppingCartViewModel.addVoucherString=""
        ecsShoppingCartViewModel.tagApplyOrDeleteVoucher(MECRequestType.MEC_APPLY_VOUCHER)
        // Mockito.verify(ecsShoppingCartViewModelMock).tagApplyOrDeleteVoucher(MECRequestType.MEC_GET_APPLIED_VOUCHERS)

    }

    @Mock
    lateinit var mContext: Context

    @Mock
    lateinit var mLabel: Label

    @Mock
    lateinit var userDataInterfaceMock: UserDataInterface

    @Test
    fun comp1() {
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

        /*  mLabel = Label(mContext)
          Mockito.`when`( mLabel.context.getResources().getDimensionPixelSize(R.dimen.mec_product_detail_discount_price_label_size)).thenReturn(34)
          EcsShoppingCartViewModel.setPrice(mLabel,mECSProduct,basePriceEntity)*/
    }

    @Test
    fun TestSetDiscountPrice() {


        var mECSProduct = ECSProduct()
        mECSProduct.code = "ConsignmentCode123ABC"
        var priceEntity = PriceEntity()
        priceEntity.value = 12.9
        mECSProduct.price = priceEntity


        var basePriceEntity = BasePriceEntity()
        basePriceEntity.value = 10.7

        var entries = ArrayList<ECSEntries>()


        EcsShoppingCartViewModel.setDiscountPrice(mLabel, mECSProduct, basePriceEntity)
        assertEquals(View.VISIBLE, mLabel.visibility)


        basePriceEntity.value = 12.9 // make discounted price 0
        EcsShoppingCartViewModel.setDiscountPrice(mLabel, mECSProduct, basePriceEntity)
        assertEquals(View.VISIBLE, mLabel.visibility)

    }

    @Test
    fun TestSetStock() {

        val only: String = "Only"
        val available: String = "Available"
        mLabel.text=""

        Mockito.`when`(mLabel.context)
                .thenReturn(mContext)
        Mockito.`when`(mContext.getString(R.string.mec_only))
                .thenReturn(only)
        Mockito.`when`(mContext.getString(R.string.mec_stock_available))
                .thenReturn(available)


        var mECSProduct = ECSProduct()
        mECSProduct.code = "ConsignmentCode123ABC"
        var priceEntity = PriceEntity()
        priceEntity.value = 12.9
        mECSProduct.price = priceEntity

        var stockEntity: StockEntity = StockEntity()
        stockEntity.stockLevel = 10
        stockEntity.stockLevelStatus = "inStock"
        mECSProduct.stock = stockEntity



        EcsShoppingCartViewModel.setStock(mLabel, mECSProduct, 2)
        assert(mLabel.text.isNullOrEmpty())
        assertEquals(View.VISIBLE, mLabel.visibility)


        EcsShoppingCartViewModel.setStock(mLabel, mECSProduct, 12)
        assertEquals(View.VISIBLE, mLabel.visibility)


         stockEntity.stockLevel=4 // <5 low stocks
         EcsShoppingCartViewModel.setStock(mLabel,mECSProduct,2)
         assertEquals(View.VISIBLE,mLabel.visibility)
    }


    @Test
    fun `retry api should do auth call`() {
        ecsShoppingCartViewModel.retryAPI(MECRequestType.MEC_FETCH_SHOPPING_CART)
        Mockito.verify(userDataInterfaceMock).refreshSession(any(RefreshSessionListener::class.java))

    }

    @Test
    fun `retry api should refresh auth if refresh token exists`() {
        MECDataHolder.INSTANCE.refreshToken = "djhgwdhjcgdjwkjbd"
        ecsShoppingCartViewModel.retryAPI(MECRequestType.MEC_UPDATE_SHOPPING_CART)
        Mockito.verify(ecsServicesMock).hybrisRefreshOAuth(any(), any())
    }


    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }
}