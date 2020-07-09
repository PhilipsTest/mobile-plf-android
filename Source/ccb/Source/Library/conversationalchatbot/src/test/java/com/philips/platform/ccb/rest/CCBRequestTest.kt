package com.philips.platform.ccb.rest

import com.android.volley.AuthFailureError
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

class CCBRequestTest : TestCase() {

    private var ccbRequest: CCBRequest? = null

    @Mock
    private val mockResponseListener: Response.Listener<String>? = null

    @Mock
    private val mockErrorListener: Response.ErrorListener? = null

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.initMocks(this)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}