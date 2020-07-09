package com.philips.platform.ccb.manager

import com.android.volley.BuildConfig.VERSION_NAME
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.tagging.AppTaggingInterface
import com.philips.platform.uappframework.uappinput.UappDependencies
import junit.framework.TestCase
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
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
        PowerMockito.`when`(mockUappDependencies!!.appInfra).thenReturn(mockAppInfraInterface)
        PowerMockito.`when`(mockAppInfraInterface!!.logging).thenReturn(mockLoggingInterface)
        PowerMockito.`when`(mockLoggingInterface!!.createInstanceForComponent(COMPONENT_TAGS_ID, VERSION_NAME)).thenReturn(mockLoggingInterface)
        PowerMockito.`when`(mockAppInfraInterface!!.tagging).thenReturn(mockAppTaggingInterface)
        PowerMockito.`when`(mockAppInfraInterface!!.restClient).thenReturn(PowerMockito.mock(RestInterface::class.java))
        PowerMockito.`when`(mockAppTaggingInterface!!.createInstanceForComponent(COMPONENT_TAGS_ID, VERSION_NAME)).thenReturn(mockAppTaggingInterface)
        ccbSettingManager = CCBSettingsManager
        ccbSettingManager!!.init(mockUappDependencies!!)
    }
}