package com.philips.platform.ecs.retailer

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.android.volley.NoConnectionError
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.ecs.MockECSServices
import com.philips.platform.ecs.MockInputValidator
import com.philips.platform.ecs.StaticBlock
import com.philips.platform.ecs.TestUtil
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.HashMap

@RunWith(RobolectricTestRunner::class)
class GetRetailerInfoRequestTest{


    private lateinit var mockInputValidator: MockInputValidator
    private var mContext: Context? = null


    lateinit var mockECSServices: MockECSServices
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    lateinit var ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception>


    private var appInfra: AppInfra? = null



    @Mock
    internal var mockRestInterface: RestInterface? = null

    lateinit var mockGetRetailersInfoRequest: MockGetRetailersInfoRequest


    val currentPage :Int = 1

    private var ctn:String = "DIS363_03"

    val PREFIX_RETAILERS = "www.philips.com/api/wtb/v1"
    val RETAILERS_ALTER = "online-retailers?product=%s&lang=en"
    val PRX_SECTOR_CODE = "B2C"

    @Before
    @Throws(Exception::class)
    fun setUp() {

        mContext = InstrumentationRegistry.getInstrumentation().getContext()
        appInfra = AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext)
        appInfra!!.serviceDiscovery.homeCountry = "DE"


        mockECSServices = MockECSServices(appInfra!!)
        ecsServices = com.philips.platform.ecs.ECSServices(appInfra!!)


        StaticBlock.initialize()

        mockInputValidator = MockInputValidator()

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.retailers.ECSRetailerList){
            }

            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
            }

        }
        mockGetRetailersInfoRequest = MockGetRetailersInfoRequest("GetRetailerInfoSuccess.json", ecsCallback, ctn);

    }

    @Test
    fun testSuccessResponse() {
        mockInputValidator.jsonFileName = "GetRetailerInfoSuccess.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception> {

             override fun onResponse(result: com.philips.platform.ecs.model.retailers.ECSRetailerList){
                 assertNotNull(result)
                 assertNotNull(result.wrbresults?.onlineStoresForProduct)
             }


            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertTrue(true)
                //  test case failed
            }

        }

        mockECSServices.fetchRetailers("1234",ecsCallback)

    }

    @Test
    fun testFailureResponse() {

        mockInputValidator.jsonFileName = "GetRetailerInfoFailure.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.retailers.ECSRetailerList){
                assertTrue(true)
                //  test case failed
            }

            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertEquals(19999, ecsError)
                //  test case passed
            }

        }
        mockECSServices.fetchRetailers("1234",ecsCallback)

    }

    @Test
    fun isValidURL() {

        System.out.println("print url: "+mockGetRetailersInfoRequest.getURL())
        val excepted = createURL()
        assertEquals(excepted, mockGetRetailersInfoRequest.getURL())
    }

    @Test
    fun isValidGetRequest() {
        assertEquals(0, mockGetRetailersInfoRequest.getMethod())
    }

    @Test
    fun isValidHeader() {

        val expectedMap = HashMap<String, String>()
        expectedMap["Authorization"] = "Bearer " + "acceesstoken"

        val actual = mockGetRetailersInfoRequest.getHeader()
        assertTrue(expectedMap == actual)
    }

    @Test
    fun verifyOnResponseError() {
        val spy1 = Mockito.spy<com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception>>(ecsCallback)
        mockGetRetailersInfoRequest = MockGetRetailersInfoRequest("GetRetailerInfoSuccess.json", spy1, ctn);
        val volleyError = NoConnectionError()
        mockGetRetailersInfoRequest.onErrorResponse(volleyError)
        Mockito.verify(spy1).onFailure(ArgumentMatchers.any<Exception>(Exception::class.java), ArgumentMatchers.any<com.philips.platform.ecs.error.ECSError>(com.philips.platform.ecs.error.ECSError::class.java))

    }

    @Test
    fun verifyOnResponseSuccess(){
        val spy1 = Mockito.spy<com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.retailers.ECSRetailerList, Exception>>(ecsCallback)
        mockGetRetailersInfoRequest = MockGetRetailersInfoRequest("GetRetailerInfoSuccess.json", spy1, ctn);

        mockGetRetailersInfoRequest.onResponse(getJsonObject("GetRetailerInfoSuccess.json"));
        Mockito.verify(spy1).onResponse(any(com.philips.platform.ecs.model.retailers.ECSRetailerList::class.java))
    }

    @Test
    fun assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetRetailersInfoRequest.getJSONSuccessResponseListener())
    }

    fun createURL():String{
        val builder = StringBuilder("https://")
        builder.append(PREFIX_RETAILERS).append("/")
        builder.append(PRX_SECTOR_CODE).append("/")
        builder.append(com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.locale).append("/")
        builder.append(RETAILERS_ALTER)
        return String.format(builder.toString(), ctn)
    }


    internal fun getJsonObject(jsonfileName: String): JSONObject? {

        val result: JSONObject? = null
        val `in` = javaClass.classLoader!!.getResourceAsStream(jsonfileName)//"PRXProductAssets.json"
        val jsonString = TestUtil.loadJSONFromFile(`in`)
        try {
            return JSONObject(jsonString!!)
        } catch (e: JSONException) {
            return null
        }

    }
}




