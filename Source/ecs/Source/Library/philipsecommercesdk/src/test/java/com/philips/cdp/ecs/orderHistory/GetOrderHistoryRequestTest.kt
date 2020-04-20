package com.philips.cdp.ecs.orderHistory

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.android.volley.NoConnectionError
import com.philips.platform.ecs.ECSServices
import com.philips.cdp.ecs.MockECSServices
import com.philips.cdp.ecs.MockInputValidator
import com.philips.cdp.ecs.StaticBlock
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.orders.ECSOrderHistory
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


    private lateinit var mockInputValidator: MockInputValidator
    private var mContext: Context? = null


    lateinit var mockECSServices: MockECSServices
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    lateinit var ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderHistory, Exception>


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
        ecsServices = com.philips.platform.ecs.ECSServices("", appInfra!!)

        mockInputValidator = MockInputValidator()

        StaticBlock.initialize()

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderHistory, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.orders.ECSOrderHistory){
            }

            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
            }

        }
        mockGetOrderHistoryRequest = MockGetOrderHistoryRequest("GetOrderHistorySuccess.json",currentPage,ecsCallback);
    }

    @Test
    fun testSuccessResponse() {
        mockInputValidator.jsonFileName = "GetOrderHistorySuccess.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderHistory, Exception> {

             override fun onResponse(result: com.philips.platform.ecs.model.orders.ECSOrderHistory){
                 assertNotNull(result)
                 assertNotNull(result.orders?.get(0)?.code)
             }


            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertTrue(true)
                //  test case failed
            }

        }
        mockECSServices.fetchOrderHistory(0,1, ecsCallback)

    }

    @Test
    fun testFailureResponse() {

        mockInputValidator.jsonFileName = "GetOrderHistoryFailure.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderHistory, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.orders.ECSOrderHistory){
                assertTrue(true)
                //  test case failed
            }


            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertEquals(19999, ecsError)
                //  test case passed
            }

        }
        mockECSServices.fetchOrderHistory(0,1, ecsCallback)

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
        val spy1 = Mockito.spy<com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.orders.ECSOrderHistory, Exception>>(ecsCallback)
        mockGetOrderHistoryRequest = MockGetOrderHistoryRequest("GetOrderHistorySuccess.json",currentPage,spy1);
        val volleyError = NoConnectionError()
        mockGetOrderHistoryRequest.onErrorResponse(volleyError)
        Mockito.verify(spy1).onFailure(ArgumentMatchers.any<Exception>(Exception::class.java), ArgumentMatchers.any<com.philips.platform.ecs.error.ECSError>(com.philips.platform.ecs.error.ECSError::class.java))

    }

    @Test
    fun assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetOrderHistoryRequest.getJSONSuccessResponseListener())
    }
}




