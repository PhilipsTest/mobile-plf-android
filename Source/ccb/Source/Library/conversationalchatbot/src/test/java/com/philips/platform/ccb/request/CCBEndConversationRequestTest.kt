package com.philips.platform.ccb.request

import com.android.volley.Request
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBEndConversationRequestTest : TestCase() {
    private var ccbEndConversationRequest: CCBEndConversationRequest? = null


    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbEndConversationRequest = CCBEndConversationRequest()
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbEndConversationRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 3)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbEndConversationRequest?.getBody()
        assertNotNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbEndConversationRequest?.getMethodType()!!
        assertEquals(methodType, Request.Method.POST)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}