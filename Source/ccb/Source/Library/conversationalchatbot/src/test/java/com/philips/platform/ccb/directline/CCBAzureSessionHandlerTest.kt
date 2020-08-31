package com.philips.platform.ccb.directline

import com.android.volley.VolleyError
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.rest.CCBRestClient
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(CCBRestClient::class,CCBError::class)
@RunWith(PowerMockRunner::class)
class CCBAzureSessionHandlerTest {

    lateinit var cCBAzureSessionHandler : CCBAzureSessionHandler

    @Mock
    lateinit var  cCBRestClientMock : CCBRestClient

    @Mock
    lateinit var completionHandlerMock: (Boolean, CCBError?) -> Unit

    @Mock
    lateinit var volleyErrorMock : VolleyError

    @Mock
    lateinit var ccbErrorMock: CCBError

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        cCBAzureSessionHandler = CCBAzureSessionHandler()
        cCBAzureSessionHandler.ccbRestClient = cCBRestClientMock
        cCBAzureSessionHandler.ccbError = ccbErrorMock
    }

    @Test
    fun `test error listener should invoke completion handler error callback`() {
        val errorListener = cCBAzureSessionHandler.getErrorListener(completionHandlerMock)
        errorListener.onErrorResponse(volleyErrorMock)
        Mockito.verify( completionHandlerMock).invoke(false,ccbErrorMock)
    }

    fun <T> any(type : Class<T>): T {
        Mockito.any(type)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

}
