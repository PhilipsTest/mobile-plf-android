package com.philips.platform.ccb.request

import com.android.volley.Request.Method.POST
import com.philips.platform.ccb.rest.CCBRequest
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBAuthenticationRequestTest : TestCase() {
    private var ccbAuthenticationRequest: CCBAuthenticationRequest? = null

    private val idAuthReq = "https://directline.botframework.com/v3/directline/tokens/generate"

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbAuthenticationRequest = CCBAuthenticationRequest(idAuthReq)
    }

    @Test
    fun testGetUrl() {
        val url: String? = ccbAuthenticationRequest?.getUrl()
        assertEquals(url, idAuthReq)
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbAuthenticationRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 2)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbAuthenticationRequest?.getBody()
        assertNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbAuthenticationRequest?.getMethodType()!!
        assertEquals(methodType, POST)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}