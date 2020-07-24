package com.philips.platform.ccb.request

import com.android.volley.Request
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBRefreshTokenRequestTest : TestCase() {
    private var ccbRefreshTokenRequest: CCBRefreshTokenRequest? = null

    private val idAuthReq = "https://directline.botframework.com/v3/directline/tokens/refresh"

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbRefreshTokenRequest = CCBRefreshTokenRequest()
    }

    @Test
    fun testGetUrl() {
        val url: String? = ccbRefreshTokenRequest?.getUrl()
        assertEquals(url, idAuthReq)
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbRefreshTokenRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 2)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbRefreshTokenRequest?.getBody()
        assertNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbRefreshTokenRequest?.getMethodType()!!
        assertEquals(methodType, Request.Method.POST)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}