package com.philips.platform.ccb.request

import com.android.volley.Request
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CCBGetAllMessagesRequestTest : TestCase() {
    private var ccbGetAllMessagesRequest: CCBGetAllMessagesRequest? = null


    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        ccbGetAllMessagesRequest = CCBGetAllMessagesRequest()
    }

    @Test
    fun testGetHeader() {
        val header: Map<String, String>? = ccbGetAllMessagesRequest?.getHeader()
        val size = header?.size
        assertEquals(size, 2)
    }

    @Test
    fun testGetBody() {
        val body: String? = ccbGetAllMessagesRequest?.getBody()
        assertNull(body)
    }

    @Test
    fun testGetMethodType() {
        val methodType: Int = ccbGetAllMessagesRequest?.getMethodType()!!
        assertEquals(methodType, Request.Method.GET)
    }

    @After
    @Throws(Exception::class)
    override fun tearDown() {
    }
}