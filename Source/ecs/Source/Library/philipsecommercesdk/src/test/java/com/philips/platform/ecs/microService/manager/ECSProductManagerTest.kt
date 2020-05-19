package com.philips.platform.ecs.microService.manager

import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.product.ECSProducts
import com.philips.platform.ecs.microService.request.GetProductForRequest
import com.philips.platform.ecs.microService.request.GetProductsRequest
import com.philips.platform.ecs.microService.request.GetSummariesForProductsRequest
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
class ECSProductManagerTest {

    lateinit var mECSProductManager : ECSProductManager

    lateinit var ecsCallback : ECSCallback<ECSProducts, ECSError>

    @Mock
    lateinit var mGetProductsRequestMock : GetProductsRequest

////////////////////

    lateinit var eCSCallback: ECSCallback<ECSProduct?, ECSError>

    @Mock
    lateinit var mGetProductForRequestMock: GetProductForRequest

    @Mock
    lateinit var mGetSummariesForProductsRequestMock : GetSummariesForProductsRequest

    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mECSProductManager=ECSProductManager()

    }

    @Test
    fun getProducts() {

        ecsCallback=object : ECSCallback<ECSProducts, ECSError>{

            override fun onResponse(result: ECSProducts) {
              assert(true)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }


        }

       // `when`( mECSApiValidator.getECSException(APIType.Locale)).thenReturn(null)
        var commerceProducts= ArrayList<ECSProduct>()
        var mECSProduct = ECSProducts(commerceProducts)
        Mockito.`when`(mGetProductsRequestMock.executeRequest()).then { ecsCallback.onResponse(mECSProduct) }
        mGetProductsRequestMock= GetProductsRequest(1,2,ecsCallback)
        mECSProductManager.getProducts(1,2,ecsCallback)
    }

    @Test
    fun getProductForHybrisON() {

        eCSCallback = object : ECSCallback<ECSProduct?, ECSError>{

            override fun onResponse(result: ECSProduct?) {
                assert(true)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }
        }

        var mECSProduct = ECSProduct(null,"id","type")
        ECSDataHolder.config.isHybris=true
        Mockito.`when`(mGetProductForRequestMock.executeRequest()).then { eCSCallback.onResponse(mECSProduct) }
        mECSProductManager.getProductFor("CTN",eCSCallback)

    }

    @Test
    fun getProductForHybrisOFF() {
        eCSCallback = object : ECSCallback<ECSProduct?, ECSError>{

            override fun onResponse(result: ECSProduct?) {
                assert(true)
            }

            override fun onFailure(ecsError: ECSError) {
                assert(false)
            }
        }

        var mECSProduct = ECSProduct(null,"id","type")
        mECSProduct.id="new id"
        ECSDataHolder.config.isHybris=false
        Mockito.`when`(mGetSummariesForProductsRequestMock.executeRequest()).then { eCSCallback.onResponse(mECSProduct) }


    }

    @Test
    fun getSummaryForSingleProduct() {
    }

    @Test
    fun fetchProductSummaries() {
    }

    @Test
    fun fetchProductDetails() {
    }
}