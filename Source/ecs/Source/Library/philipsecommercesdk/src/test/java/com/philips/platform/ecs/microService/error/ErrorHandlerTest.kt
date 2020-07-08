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
class ErrorHandlerTest {

    private lateinit var  errorHandler: ErrorHandler

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


        errorHandler = ErrorHandler()
        ECSDataHolder.appInfra = appInfraMock

    }

    @Test
    fun `test ECS error when Volley error is null`() {
        val ecsError = errorHandler.getECSError(null)
        verifyECSError(ecsError,ECSErrorType.ECSsomethingWentWrong)
    }

    @Test
    fun `test ecs error when volley error is of Time Out`() {
        val ecsError = errorHandler.getECSError(timeoutErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }

    @Test
    fun `test ecs error when volley error is of No connection`() {
        val ecsError = errorHandler.getECSError(noConnectionErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }

    @Test
    fun `test ecs error when volley error is of ParseError`() {
        val ecsError = errorHandler.getECSError(parseErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }

    @Test
    fun `test ecs error when volley error is of NetworkError`() {
        val ecsError = errorHandler.getECSError(networkErrorMock)
        verifyECSError(ecsError,ECSErrorType.ECS_volley_error)
    }


    @Mock
    private lateinit var serverErrorMock: ServerError

    @Mock
    private lateinit var networkResponseMock: NetworkResponse

    @Test
    fun `test volley server error with null networkResponse`() {

        Mockito.`when`(serverErrorMock.networkResponse).thenReturn(null)
        val ecsError = errorHandler.getECSError(null)
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
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_INVALID_API_KEY.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test When error string is not valid `() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/PILErrorWithInvalidSource.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSsomethingWentWrong.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when country is missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoCountryError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_country.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when site ID is missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoSiteIDError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_siteId.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when language is missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoLanguageError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_language.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when multiple  parameters are missing`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/fetchProductPILCTNNoQueryParamsError.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_MISSING_PARAMETER_country.errorCode,ecsDefaultError.errorCode)
    }

    @Test
    fun `test ecs Error when only code to be considered`() {
        //volleyHandler.setPILECSError()
        val errorString =   ClassLoader.getSystemResource("pil/PILErrorWithOnlyCodeToConsider.json").readText()
        val jsonObject = JSONObject(errorString)
        val hybrisError = jsonObject.getData(HybrisError::class.java)
        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        errorHandler.setPILECSError(hybrisError,ecsDefaultError)
        assertEquals(ECSErrorType.ECSPIL_BAD_REQUEST.errorCode,ecsDefaultError.errorCode)
    }

    // get cart relates error test cases =======

    @Test
    fun `test ecs error  for get cart with invalid auth`() {
        TODO("Not yet implemented")
    }


    @Test
    fun `test ecs error  for get cart with missing  auth`() {
        TODO("Not yet implemented")
    }


    @Test
    fun `test ecs error  for get cart with invalid cart id`() {
        TODO("Not yet implemented")
    }

    // create cart error test cases ==============


    @Test
    fun `test ecs error  for create cart with invalid CTN`() {
        TODO("Not yet implemented")
    }


    @Test
    fun `test ecs error  for create cart with OUT Of Stock CTN`() {
        TODO("Not yet implemented")
    }

    @Test
    fun `test ecs error  for create cart with quantity more than stock`() {
        TODO("Not yet implemented")
    }


    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

}