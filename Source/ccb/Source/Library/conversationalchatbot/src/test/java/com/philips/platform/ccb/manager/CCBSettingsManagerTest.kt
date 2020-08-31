/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.manager

import com.android.volley.BuildConfig.VERSION_NAME
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.uappframework.uappinput.UappDependencies
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class CCBSettingsManagerTest : TestCase(){

    var ccbSettingManager: CCBSettingsManager? = null

    private val COMPONENT_TAGS_ID = "ccb"

    @Mock
    var mockUappDependencies: UappDependencies? = null

    @Mock
    var mockAppInfraInterface: AppInfraInterface? = null

    @Mock
    var mockLoggingInterface: LoggingInterface? = null

    @Mock
    var mockAppTaggingInterface: AppTaggingInterface? = null


    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.initMocks(this)
        `when`(mockUappDependencies?.appInfra).thenReturn(mockAppInfraInterface)
        `when`(mockAppInfraInterface?.logging).thenReturn(mockLoggingInterface)
        `when`(mockLoggingInterface?.createInstanceForComponent(COMPONENT_TAGS_ID, VERSION_NAME)).thenReturn(mockLoggingInterface)
        `when`(mockAppInfraInterface?.tagging).thenReturn(mockAppTaggingInterface)
        `when`(mockAppInfraInterface?.restClient).thenReturn(PowerMockito.mock(RestInterface::class.java))
        `when`(mockAppTaggingInterface?.createInstanceForComponent(COMPONENT_TAGS_ID, VERSION_NAME)).thenReturn(mockAppTaggingInterface)
        ccbSettingManager = CCBSettingsManager
        ccbSettingManager?.init(mockUappDependencies!!)
    }

    @Test
    fun testGetAppInfraInterface() {
        val appInfraInterface: AppInfraInterface? = ccbSettingManager?.getAppInfraInterface()
        assertNotNull(appInfraInterface)
    }

    @Test
    fun testGetLoggingInterface() {
        val loggingInterface: LoggingInterface? = ccbSettingManager?.getLoggingInterface()
        assertNotNull(loggingInterface)
    }

    @Test
    fun testGetTaggingInterface() {
        val taggingInterface: AppTaggingInterface? = ccbSettingManager?.getTaggingInterface()
        assertNotNull(taggingInterface)
    }
}