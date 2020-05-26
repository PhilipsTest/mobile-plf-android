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

package com.philips.platform.mec.integration

import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappSettings
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MECInterfaceTest {

    lateinit var mecInterface: MECInterface

    @Mock
    lateinit var uappDependenciesMock: UappDependencies
    @Mock
    lateinit var uappSettingsMock: UappSettings

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mecInterface = MECInterface()
    }

    @Test
    fun getMEC_NOTATION() {
    }

    @Test
    fun init() {
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
    }

    @Test
    fun launch() {
    }

    @Test
    fun getMECDataInterface() {
    }
}