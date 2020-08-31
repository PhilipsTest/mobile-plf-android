package com.philips.platform.ccb.integration

import android.content.Context
import android.content.SharedPreferences
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.securestorage.SecureStorageInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.ccb.manager.CCBSettingsManager
import com.philips.platform.uappframework.uappinput.UappDependencies
import com.philips.platform.uappframework.uappinput.UappSettings
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(CCBSettingsManager::class, CCBInterface::class)
@RunWith(PowerMockRunner::class)
class CCBInterfaceTest: TestCase(){

    @Mock
    private val mockAppInfraInterface: AppInfraInterface? = null

    @Mock
    private val mockUappDependencies: UappDependencies? = null

    @Mock
    private val mockUappSettings: UappSettings? = null

    @Mock
    var mockContext: Context? = null

    @Mock
    var mockLoggingInterface: LoggingInterface? = null

    @Mock
    var mockServiceDiscoveryInterface: ServiceDiscoveryInterface? = null

    @Mock
    var mockSecureStorageInterface: SecureStorageInterface? = null

    @Mock
    var mockCCBSettingManager: CCBSettingsManager? = null

    @Mock
    var mockSharedPreferences: SharedPreferences? = null

    @Mock
    private val loggingInterface: LoggingInterface? = null

    private var ccbInterface: CCBInterface? = null


    @Before
    @Throws(Exception::class)
    override fun setUp() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(CCBSettingsManager::class.java)
        PowerMockito.`when`<CCBSettingsManager>(CCBSettingsManager).thenReturn(mockCCBSettingManager)
        PowerMockito.`when`<LoggingInterface>(CCBSettingsManager.getLoggingInterface()).thenReturn(mockLoggingInterface)
        PowerMockito.`when`(mockUappDependencies!!.appInfra).thenReturn(mockAppInfraInterface)
        PowerMockito.`when`(mockAppInfraInterface!!.serviceDiscovery).thenReturn(mockServiceDiscoveryInterface)
        PowerMockito.`when`(mockUappSettings!!.context).thenReturn(mockContext)
        PowerMockito.`when`(mockContext!!.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(mockSharedPreferences)
        ccbInterface = CCBInterface()
    }
}