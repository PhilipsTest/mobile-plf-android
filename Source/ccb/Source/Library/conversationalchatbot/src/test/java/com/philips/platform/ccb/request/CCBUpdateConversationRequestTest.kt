package com.philips.platform.ccb.request

import com.android.volley.Request
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBUpdateConversationRequestTest : TestCase() {
    private var ccbUpdateConversationRequest: CCBUpdateConversationRequest? = null


    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbUpdateConversationRequest = CCBUpdateConversationRequest()
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbUpdateConversationRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 3)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbUpdateConversationRequest?.getBody()
        assertNotNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbUpdateConversationRequest?.getMethodType()!!
        assertEquals(methodType, Request.Method.POST)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}