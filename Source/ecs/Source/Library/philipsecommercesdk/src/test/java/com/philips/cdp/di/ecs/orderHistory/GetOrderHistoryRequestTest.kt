package com.philips.cdp.di.ecs.orderHistory

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.android.volley.NoConnectionError
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.MockECSServices
import com.philips.cdp.di.ecs.StaticBlock
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.order.OrdersData
import com.philips.cdp.di.ecs.model.orders.OrderDetail
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.rest.RestInterface
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.HashMap

@RunWith(RobolectricTestRunner::class)
class GetOrderHistoryRequestTest{


    private var mContext: Context? = null


    lateinit var mockECSServices: MockECSServices
    lateinit var ecsServices: ECSServices

    lateinit var ecsCallback: ECSCallback<OrdersData,Exception>


    private var appInfra: AppInfra? = null

    @Mock
    internal var mockRestInterface: RestInterface? = null

    lateinit var mockGetOrderHistoryRequest: MockGetOrderHistoryRequest

    val currentPage :Int = 1;

    @Before
    @Throws(Exception::class)
    fun setUp() {

        mContext = InstrumentationRegistry.getInstrumentation().getContext()
        appInfra = AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext)
        appInfra!!.serviceDiscovery.homeCountry = "DE"


        mockECSServices = MockECSServices("", appInfra!!)
        ecsServices = ECSServices("", appInfra!!)

        StaticBlock.initialize()

        ecsCallback = object: ECSCallback<OrdersData,Exception>{

            override fun onResponse(result: OrdersData){
            }

            override fun onFailure(error: Exception, errorCode: Int){
            }

        }
        mockGetOrderHistoryRequest = MockGetOrderHistoryRequest("GetOrderHistorySuccess.json",currentPage,ecsCallback);
    }

    @Test
    fun testSuccessResponse() {
        mockECSServices.jsonFileName = "GetOrderHistorySuccess.json"

        ecsCallback = object: ECSCallback<OrdersData,Exception>{

             override fun onResponse(result: OrdersData){
                 assertNotNull(result)
                 assertNotNull(result.orders?.get(0)?.code)
             }


            override fun onFailure(error: Exception, errorCode: Int){
                assertTrue(true)
                //  test case failed
            }

        }
        mockECSServices.getOrderHistory(0,ecsCallback)

    }

    @Test
    fun testFailureResponse() {

        mockECSServices.jsonFileName = "GetOrderHistoryFailure.json"

        ecsCallback = object: ECSCallback<OrdersData,Exception>{

            override fun onResponse(result: OrdersData){
                assertTrue(true)
                //  test case failed
            }


            override fun onFailure(error: Exception, errorCode: Int){
                assertEquals(19999, errorCode.toLong())
                //  test case passed
            }

        }
        mockECSServices.getOrderHistory(0,ecsCallback)

    }


    @Test
    fun isValidURL() {

        System.out.println("print url: "+mockGetOrderHistoryRequest.getURL())
        val excepted = StaticBlock.getBaseURL() + "pilcommercewebservices" + "/v2/" + StaticBlock.getSiteID() + "/users/current/orders?fields=FULL&lang=" + StaticBlock.getLocale()+"&currentPage="+currentPage
        assertEquals(excepted, mockGetOrderHistoryRequest.getURL())
    }

    @Test
    fun isValidGetRequest() {
        assertEquals(0, mockGetOrderHistoryRequest.getMethod())
    }

    @Test
    fun isValidHeader() {

        val expectedMap = HashMap<String, String>()
        expectedMap["Authorization"] = "Bearer " + "acceesstoken"

        val actual = mockGetOrderHistoryRequest.getHeader()
        assertTrue(expectedMap == actual)
    }

    @Test
    fun verifyOnResponseError() {
        val spy1 = Mockito.spy<ECSCallback<OrdersData, Exception>>(ecsCallback)
        mockGetOrderHistoryRequest = MockGetOrderHistoryRequest("GetOrderHistorySuccess.json",currentPage,spy1);
        val volleyError = NoConnectionError()
        mockGetOrderHistoryRequest.onErrorResponse(volleyError)
        Mockito.verify(spy1).onFailure(ArgumentMatchers.any<Exception>(Exception::class.java), ArgumentMatchers.anyInt())

    }

    @Test
    fun assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetOrderHistoryRequest.getJSONSuccessResponseListener())
    }
}




