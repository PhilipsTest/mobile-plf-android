package com.philips.cdp.ecs.userProfile

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.android.volley.NoConnectionError
import com.philips.cdp.ecs.*
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.address.ECSUserProfile
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.rest.RestInterface
import org.json.JSONException
import org.json.JSONObject
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
class ECSUserProfileProfileRequestTest{


    private lateinit var mockInputValidator: MockInputValidator

    private var mContext: Context? = null


    lateinit var mockECSServices: MockECSServices
    lateinit var ecsServices: com.philips.platform.ecs.ECSServices

    lateinit var ecsCallback: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception>


    private var appInfra: AppInfra? = null

    @Mock
    internal var mockRestInterface: RestInterface? = null


    lateinit var mockGetUserProfileRequest: MockGetUserProfileRequest




    @Before
    @Throws(Exception::class)
    fun setUp() {

        mContext = InstrumentationRegistry.getInstrumentation().getContext()
        appInfra = AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext)
        appInfra!!.serviceDiscovery.homeCountry = "DE"


        mockECSServices = MockECSServices("", appInfra!!)
        ecsServices = com.philips.platform.ecs.ECSServices("", appInfra!!)


        StaticBlock.initialize()

        mockInputValidator = MockInputValidator()

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.address.ECSUserProfile){
            }

            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
            }

        }
        mockGetUserProfileRequest = MockGetUserProfileRequest("GetUserProfileSuccess.json",ecsCallback);

    }

    @Test
    fun testSuccessResponse() {
        mockInputValidator.jsonFileName = "GetUserProfileSuccess.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception> {

             override fun onResponse(result: com.philips.platform.ecs.model.address.ECSUserProfile){
                 assertNotNull(result)
             }


            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertTrue(true)
                //  test case failed
            }

        }

        mockECSServices.fetchUserProfile(ecsCallback)

    }

    @Test
    fun testFailureResponse() {

        mockInputValidator.jsonFileName = "GetUserProfileFailure.json"

        ecsCallback = object: com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception> {

            override fun onResponse(result: com.philips.platform.ecs.model.address.ECSUserProfile){
                assertTrue(true)
                //  test case failed
            }

            override fun onFailure(error: Exception, ecsError: com.philips.platform.ecs.error.ECSError){
                assertEquals(19999, ecsError)
                //  test case passed
            }

        }
        mockECSServices.fetchUserProfile(ecsCallback)

    }


    @Test
    fun isValidURL() {
        val excepted = StaticBlock.getBaseURL() + "pilcommercewebservices" + "/v2/"  + StaticBlock.getSiteID() + "/users/current?fields=FULL&lang=" + StaticBlock.getLocale()
        assertEquals(excepted, mockGetUserProfileRequest.getURL())
    }

    @Test
    fun isValidGetRequest() {
        assertEquals(0, mockGetUserProfileRequest.getMethod())
    }

    @Test
    fun isValidHeader() {

        val expectedMap = HashMap<String, String>()
        expectedMap["Authorization"] = "Bearer " + "acceesstoken"

        val actual = mockGetUserProfileRequest.getHeader()
        assertTrue(expectedMap == actual)
    }

    @Test
    fun verifyOnResponseError() {
        val spy1 = Mockito.spy<com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception>>(ecsCallback)
        mockGetUserProfileRequest = MockGetUserProfileRequest("GetUserProfileSuccess.json",spy1)
        val volleyError = NoConnectionError()
        mockGetUserProfileRequest.onErrorResponse(volleyError)
        Mockito.verify(spy1).onFailure(ArgumentMatchers.any<Exception>(Exception::class.java), ArgumentMatchers.any<com.philips.platform.ecs.error.ECSError>(com.philips.platform.ecs.error.ECSError::class.java))

    }

    @Test
    fun verifyOnResponseSuccess(){
        val spy1 = Mockito.spy<com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.address.ECSUserProfile, Exception>>(ecsCallback)
        mockGetUserProfileRequest = MockGetUserProfileRequest("GetUserProfileSuccess.json",spy1)

        mockGetUserProfileRequest.onResponse(getJsonObject("GetRetailerInfoSuccess.json"));
        Mockito.verify(spy1).onResponse(ArgumentMatchers.any(com.philips.platform.ecs.model.address.ECSUserProfile::class.java))
    }

    @Test
    fun assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetUserProfileRequest.getJSONSuccessResponseListener())
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




