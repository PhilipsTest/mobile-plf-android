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

package com.philips.platform.mec.screens.history

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals

@RunWith(PowerMockRunner::class)
class MECOrderHistoryServiceTest {

    lateinit var mECOrderHistoryService : MECOrderHistoryService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mECOrderHistoryService = MECOrderHistoryService()
    }

    @Test
    fun shouldReturnFormattedDate() {
        assertEquals("Thursday Mar 12, 2020",mECOrderHistoryService.getFormattedDate("2020-03-12T05:12:23+0000"))
    }

    @Test
    fun shouldReturnEmpty() {
        assertEquals("",mECOrderHistoryService.getFormattedDate(null))
    }

    @Test
    fun shouldReturnEmptyOnBadDate() {
        assertEquals("",mECOrderHistoryService.getFormattedDate(""))
    }

    @Test
    fun assertShouldCallAuth() {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}