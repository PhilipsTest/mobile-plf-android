package com.philips.platform.ecs.orderHistory

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.android.volley.NoConnectionError
import com.philips.platform.ecs.MockECSServices
import com.philips.platform.ecs.MockInputValidator
import com.philips.platform.ecs.StaticBlock
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.rest.RestInterface
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.HashMap

@RunWith(RobolectricTestRunner::class)
class GetOrderDetailRequestTest{


    private lateinit var mockInputValidator: MockInputValidator
    private var mContext: Context? = null


    lateinit var mockECSServices: MockECSServices
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    lateinit var ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception>


    private var appInfra: AppInfra? = null

    @Mock
    internal var mockRestInterface: RestInterface? = null

    lateinit var mockGetOrderDetailRequest: MockGetOrderDetailRequest
    val orderID : String = "123"

    @Before
    @Throws(Exception::class)
    fun setUp() {

        mContext = InstrumentationRegistry.getInstrumentation().getContext()
        appInfra = AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext)
        appInfra!!.serviceDiscovery.homeCountry = "DE"


        mockECSServices = MockECSServices("", appInfra!!)
        ecsServices = com.philips.platform.ecs.ECSServices("", appInfra!!)

        mockInputValidator = MockInputValidator()

        StaticBlock.initialize()

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.orders.ECSOrderDetail){

            }


            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){

            }

        }
        mockGetOrderDetailRequest = MockGetOrderDetailRequest("GetOrderDetailSuccess.json", orderID, ecsCallback)
    }

    @Test
    fun testSuccessResponse() {
        mockInputValidator.jsonFileName = "GetOrderDetailSuccess.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception> {

             override fun onResponse(result: com.philips.platform.ecs.model.orders.ECSOrderDetail){
                 assertNotNull(result)
                 assertNotNull(result.deliveryOrderGroups?.get(0)?.entries)
             }


            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertTrue(true)
                //  test case failed
            }

        }

        mockECSServices.fetchOrderDetail("123",ecsCallback)

    }

    @Test
    fun testFailureResponse() {

        mockInputValidator.jsonFileName = "GetOrderDetailFailure.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.orders.ECSOrderDetail){
                assertTrue(true)
                //  test case failed
            }

            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertEquals(19999, ecsError)
                //  test case passed
            }

        }
        mockECSServices.fetchOrderDetail("123",ecsCallback)

    }

    @Test
    fun isValidURL() {

        System.out.println("print url: "+mockGetOrderDetailRequest.getURL())
        //acc.us.pil.shop.philips.com/pilcommercewebservices/v2/US_Tuscany/users/current/orders/123?fields=FULL&lang=en_US
        val excepted = StaticBlock.getBaseURL() + "pilcommercewebservices" + "/v2/" + StaticBlock.getSiteID() + "/users/current/orders/"+orderID+"?fields=FULL&lang=" + StaticBlock.getLocale()
        assertEquals(excepted, mockGetOrderDetailRequest.getURL())
    }

    @Test
    fun isValidGetRequest() {
        assertEquals(0, mockGetOrderDetailRequest.getMethod().toLong())
    }

    @Test
    fun isValidHeader() {

        val expectedMap = HashMap<String, String>()
        expectedMap["Authorization"] = "Bearer " + "acceesstoken"

        val actual = mockGetOrderDetailRequest.getHeader()

        assertTrue(expectedMap == actual)
    }

    @Test
    fun verifyOnResponseError() {
        val spy1 = Mockito.spy<com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderDetail, Exception>>(ecsCallback)
        mockGetOrderDetailRequest = MockGetOrderDetailRequest("GetOrderDetailSuccess.json", orderID, spy1);
        val volleyError = NoConnectionError()
        mockGetOrderDetailRequest.onErrorResponse(volleyError)
        Mockito.verify(spy1).onFailure(any<Exception>(Exception::class.java), any(com.philips.platform.ecs.error.ECSError::class.java))

    }

    @Test
    fun assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetOrderDetailRequest.getJSONSuccessResponseListener())
    }
}




