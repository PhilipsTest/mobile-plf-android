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

package com.philips.platform.ecs.microService.error

import android.util.Base64
import com.android.volley.*
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@PrepareForTest(Base64::class,ServerError::class,VolleyError::class,JSONObject::class)
@RunWith(PowerMockRunner::class)
class VolleyHandlerTest {

    private lateinit var  volleyHandler: VolleyHandler

    @Mock
    private lateinit var appInfraMock: AppInfra

    @Mock
    private lateinit var timeoutErrorMock : TimeoutError

    @Mock
    private lateinit var noConnectionErrorMock : NoConnectionError

    @Mock
    private lateinit var authFailureErrorMock : AuthFailureError

    @Mock
    private lateinit var networkErrorMock : NetworkError

    @Mock
    private lateinit var parseErrorMock : ParseError


    private var called : Boolean = false

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(Base64::class.java)

        PowerMockito.`when`(Base64.decode(any(ByteArray::class.java), anyInt())).thenAnswer(object : Answer<Any?> {
            @Throws(Throwable::class)
            override fun answer(invocationOnMock: InvocationOnMock?): Any? {
                called = true
                return null
            }
        })
        val bytesdecoded = Base64.decode("abcd".toByteArray(), 0)
        assertTrue(called)

        PowerMockito.`when`(Base64.encodeToString(any(ByteArray::class.java), anyInt())).thenAnswer(object : Answer<Any?> {
            @Throws(Throwable::class)
            override fun answer(invocationOnMock: InvocationOnMock?): Any? {
                called = true
                return null
            }
        })
        val bytesdecodedString = Base64.decode("abcd".toByteArray(), 0)
        assertTrue(called)


        volleyHandler = VolleyHandler()
        ECSDataHolder.appInfra = appInfraMock

    }

    @Test
    fun `test ECS error when Volley error is null`() {
        val ecsError = volleyHandler.getECSError(null)
        verifyECSError(ecsError,ECSErrorType.ECSsomethingWentWrong)
    }

    @Test
    fun `test ecs error when volley error is of Time Out`() {
        val ecsError = volleyHandler.getECSError(timeoutErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }

    @Test
    fun `test ecs error when volley error is of No connection`() {
        val ecsError = volleyHandler.getECSError(noConnectionErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }

    @Test
    fun `test ecs error when volley error is of ParseError`() {
        val ecsError = volleyHandler.getECSError(parseErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }

    @Test
    fun `test ecs error when volley error is of NetworkError`() {
        val ecsError = volleyHandler.getECSError(networkErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }


    @Mock
    private lateinit var serverErrorMock: ServerError

    @Mock
    private lateinit var networkResponseMock: NetworkResponse

    @Test
    fun `test volley server error with null networkResponse`() {

        Mockito.`when`(serverErrorMock.networkResponse).thenReturn(null)
        val ecsError = volleyHandler.getECSError(null)
        verifyECSError(ecsError,ECSErrorType.ECSsomethingWentWrong)
    }

    //TODO

    @Test
    fun `handle volley server error with json network response`() {

      /*  val byteArrayOfInts = byteArrayOfInts(203, 340, 125)

        Mockito.`when`(networkResponseMock.data).thenReturn(byteArrayOfInts)
        Mockito.`when`(serverErrorMock.networkResponse).thenReturn(networkResponseMock)*/

       // Mockito.`when`(serverErrorMock.getJsonError()).thenReturn(any(JSONObject::class.java))
        //val ecsError = volleyHandler.getECSError(serverErrorMock)
    }

    private fun verifyECSError(ecsError: ECSError, ecsErrorType: ECSErrorType) {
        assertNotNull(ecsError)
        assertNotNull(ecsError.errorMessage)
        assertEquals(ecsErrorType.errorCode, ecsError.errorCode)
        assertEquals(ecsErrorType, ecsError.errorType)
    }

    @Test
    fun `test When error string is not present `() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNInvalidAPIKeyError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        volleyHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(5999,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when country is missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoCountryError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        volleyHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_country.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when site ID is missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoSiteIDError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        volleyHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_siteId.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when language is missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoLanguageError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        volleyHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_language.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when multiple  parameters are missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoQueryParamsError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        volleyHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_country.errorCode,ecsDefaultError.errorCode)
    }

    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    var volleyServerErrorString = "{ \"errors\": [ { \"id\": \"916c6c9b-f7d9-43a9-ab46-0e9ebd78e880\", \"status\": \"400\", \"code\": \"MISSING_PARAMETER\", \"title\": \"The required parameter is missing\", \"source\":{\"parameter\": \"[siteId]\"} } ] }"
}