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

@RunWith(PowerMockRunner::class)
class MECOrderHistoryServiceTest {

    lateinit var mECOrderHistoryService : MECOrderHistoryService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mECOrderHistoryService = MECOrderHistoryService()
    }

    @Test
    fun isScrollDown() {
    }

    @Test
    fun getFormattedDate() {
        mECOrderHistoryService.getFormattedDate("")
    }
}