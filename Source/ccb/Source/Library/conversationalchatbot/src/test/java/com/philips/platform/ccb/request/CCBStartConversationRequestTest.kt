package com.philips.platform.ccb.request

import com.android.volley.Request
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBStartConversationRequestTest : TestCase() {
    private var ccbStartConversationRequest: CCBStartConversationRequest? = null

    private val idAuthReq = "https://directline.botframework.com/v3/directline/conversations"

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbStartConversationRequest = CCBStartConversationRequest()
    }

    @Test
    fun testGetUrl() {
        val url: String? = ccbStartConversationRequest?.getUrl()
        assertEquals(url, idAuthReq)
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbStartConversationRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 2)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbStartConversationRequest?.getBody()
        assertNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbStartConversationRequest?.getMethodType()!!
        assertEquals(methodType, Request.Method.POST)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}