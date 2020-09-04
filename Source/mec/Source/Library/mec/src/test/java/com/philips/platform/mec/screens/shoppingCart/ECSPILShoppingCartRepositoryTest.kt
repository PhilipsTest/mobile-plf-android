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

import com.bazaarvoice.bvandroidsdk.BVConversationsClient
import com.bazaarvoice.bvandroidsdk.BulkRatingsRequest
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.LoadCallDisplay
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.ecs.model.cart.ECSEntries
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.ecs.model.products.ECSProduct
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertNotNull

@PrepareForTest(EcsShoppingCartViewModel::class,ECSShoppingCartCallback::class,ECSVoucherCallback::class,MECBulkRatingCallback::class,BVConversationsClient::class,LoadCallDisplay::class,com.philips.platform.ecs.microService.error.ECSError::class,com.philips.platform.ecs.microService.ECSServices::class,ECSItem::class)
@RunWith(PowerMockRunner::class)
class ECSPILShoppingCartRepositoryTest {

    lateinit var ecsShoppingCartRepository: ECSShoppingCartRepository

    @Mock
    lateinit var ecsServicesMock: ECSServices

    @Mock
    lateinit var ecsMicroServicesMock : com.philips.platform.ecs.microService.ECSServices

    @Mock
    lateinit var ecsShoppingCartViewModelMock: EcsShoppingCartViewModel

    @Mock
    lateinit var authCallbackMock : ECSCallback<ECSOAuthData, Exception>

    @Mock
    lateinit var ecsItemMock: ECSItem
    @Mock
    lateinit var ecsShoppingCartCallbackMock : ECSShoppingCartCallback


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(ecsServicesMock.microService).thenReturn(ecsMicroServicesMock)
        ecsShoppingCartRepository = ECSShoppingCartRepository(ecsShoppingCartViewModelMock,ecsServicesMock)
        ecsShoppingCartRepository.authCallBack = authCallbackMock
        ecsShoppingCartRepository.ecsShoppingCartCallback = ecsShoppingCartCallbackMock
    }

    @Test
    fun getAuthCallBack() {
        assertNotNull(ecsShoppingCartRepository.authCallBack)
    }

    @Test
    fun setAuthCallBack() {
        ecsShoppingCartRepository.authCallBack = authCallbackMock
        assertNotNull(ecsShoppingCartRepository.authCallBack)
    }

    @Test
    fun fetchShoppingCart() {
        //TODO
        //ecsShoppingCartRepository.fetchShoppingCart()
    }

    @Test(expected = NullPointerException::class)
    fun updateShoppingCart() {
        ecsShoppingCartRepository.updateShoppingCart(ecsItemMock, 1)
        Mockito.verify(ecsServicesMock.microService).updateShoppingCart( ecsItemMock,1,ecsShoppingCartCallbackMock)
    }






    @Mock
    lateinit var bvClientMock: BVConversationsClient


    @Mock
    lateinit var LoadCallDisplayMock : LoadCallDisplay<BulkRatingsRequest, BulkRatingsResponse>

    @Test
    fun fetchProductReview() {

        MECDataHolder.INSTANCE.locale = "US"

        val ecsItem1= ECSItem(null,null,"1234",null,null,null,null,null,null)
        val ecsItem2= ECSItem(null,null,"12345",null,null,null,null,null,null)
        var  ecsEntriesList =  mutableListOf<ECSItem>()

        ecsEntriesList.add(ecsItem1)
        ecsEntriesList.add(ecsItem2)

        Mockito.`when`(bvClientMock.prepareCall(any(BulkRatingsRequest::class.java))).thenReturn(LoadCallDisplayMock)

        ecsShoppingCartRepository.fetchProductReview(ecsEntriesList,ecsShoppingCartViewModelMock,bvClientMock)

        Mockito.verify(bvClientMock).prepareCall(any(BulkRatingsRequest::class.java))
        Mockito.verify(LoadCallDisplayMock).loadAsync(any(MECBulkRatingCallback::class.java))
    }

    @Mock
    lateinit var ecsVoucherCallback: ECSVoucherCallback

    @Test
    fun applyVoucher() {
        ecsShoppingCartRepository.applyVoucher("1234",ecsVoucherCallback)
        Mockito.verify(ecsServicesMock).applyVoucher("1234",ecsVoucherCallback)
    }

    @Test
    fun removeVoucher() {
        ecsShoppingCartRepository.removeVoucher("1234",ecsVoucherCallback)
        Mockito.verify(ecsServicesMock).removeVoucher("1234",ecsVoucherCallback)
    }

    @Test(expected = NullPointerException::class)
    fun createCart() {
        ecsShoppingCartRepository.createCart("CTN")
        Mockito.verify(ecsServicesMock.microService).createShoppingCart(ctn = "CTN",ecsCallback = ecsShoppingCartCallbackMock)
    }


}