package com.philips.platform.ccb.request

import com.android.volley.Request
import junit.framework.Assert
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBPostMessageRequestTest : TestCase() {
    private var ccbPostMessageRequest: CCBPostMessageRequest? = null


    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbPostMessageRequest = CCBPostMessageRequest("Hi")
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbPostMessageRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 2)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbPostMessageRequest?.getBody()
        assertNotNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbPostMessageRequest?.getMethodType()!!
        assertEquals(methodType, Request.Method.POST)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}