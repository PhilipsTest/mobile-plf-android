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

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.ecs.ECSServices
import com.philips.platform.ecs.model.config.ECSConfig
import com.philips.platform.ecs.util.ECSConfiguration
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.uappframework.launcher.ActivityLauncher
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.launcher.UiLauncher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.ArrayList

@PowerMockIgnore("javax.net.ssl.*","okhttp3.*")
@PrepareForTest(MECLaunchInput::class,MECFlowConfigurator::class,MECSettings::class,UiLauncher::class)
@RunWith(PowerMockRunner::class)
class MECHandlerTest{


    val mecHandler = MECHandler()

    @Mock
    private lateinit var mecLaunchInputMock: MECLaunchInput

    @Mock
    private lateinit var mecFlowConfiguratorMock : MECFlowConfigurator


    @Mock
    private lateinit var mECSettingMock: MECSettings

    @Mock
    private lateinit var uiLaunchermock: UiLauncher

    @Mock
    private lateinit var launchInputMock: MECLaunchInput


    @Mock
    lateinit var appInfraMock: AppInfra

    @Mock
    lateinit var userDataInterfaceMock : UserDataInterface

    @Mock
    lateinit var loggingInterfaceMock : LoggingInterface

    @Mock
    lateinit var serviceDiscoveryInterfaceMock : ServiceDiscoveryInterface

    @Mock
    lateinit var appConfigurationInterfaceMock: AppConfigurationInterface

    @Mock
    lateinit var restInterfaceMock : RestInterface

    @Mock
    lateinit var ecsServiceMock: ECSServices

    @Mock
    lateinit var contextMock: Context

    @Mock
    lateinit var applicationContextMock : Application

    @Mock
    lateinit var packageManagerMock : PackageManager

    @Mock
    lateinit var packageInfoMock : PackageInfo

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mecLaunchInputMock.flowConfigurator = mecFlowConfiguratorMock
        setupAppInfra()
        setUpBazzarVoice()
        //mECSetting = MECSettings(null)

    }

    private fun setUpBazzarVoice() {
        packageInfoMock.versionName = "1.0"
        Mockito.`when`(packageManagerMock.getPackageInfo(anyString(),anyInt())).thenReturn(packageInfoMock)
        Mockito.`when`(applicationContextMock.packageManager).thenReturn(packageManagerMock)
        Mockito.`when`(applicationContextMock.packageName).thenReturn("com.test.mec")
        Mockito.`when`(applicationContextMock.applicationContext).thenReturn(applicationContextMock)
        Mockito.`when`(contextMock.applicationContext).thenReturn(applicationContextMock)
    }

    private fun setupAppInfra() {

        Mockito.`when`(loggingInterfaceMock.createInstanceForComponent(any(String::class.java),any(String::class.java))).thenReturn(loggingInterfaceMock)
        Mockito.`when`(appInfraMock.logging).thenReturn(loggingInterfaceMock)
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        Mockito.`when`(appInfraMock.serviceDiscovery).thenReturn(serviceDiscoveryInterfaceMock)

        MECDataHolder.INSTANCE.appinfra = appInfraMock
        MECDataHolder.INSTANCE.eCSServices = ecsServiceMock
    }

    @Test
    fun `getBundle should not return null`() {
        assertNotNull(mecHandler.getBundle(mecLaunchInputMock))
    }

    @Test
    fun `getBundle should not return when launch inputs values are pass`() {

        val ctnList = mutableListOf<String>()
        ctnList.add("CX204/04")
        mecFlowConfiguratorMock.productCTNs = ctnList as ArrayList<String>
        mecLaunchInputMock.flowConfigurator = mecFlowConfiguratorMock
        assertNotNull(mecHandler.getBundle(mecLaunchInputMock))
    }

    @Test
    fun `test default values set to mec data holder`() {
        mecHandler.launchMEC(mECSettingMock,uiLaunchermock, MECLaunchInput())
        assertNotNull(MECDataHolder.INSTANCE.mecBazaarVoiceInput)
        assertEquals(MECDataHolder.INSTANCE.maxCartCount,0)
        assertTrue(MECDataHolder.INSTANCE.hybrisEnabled)
        assertTrue(MECDataHolder.INSTANCE.retailerEnabled)
    }

    @Mock
    lateinit var activityLauncherMock : ActivityLauncher
    @Test
    fun `should start activity on config call back success`() {

        mECSettingMock = MECSettings(contextMock)
        mecLaunchInputMock.flowConfigurator = mecFlowConfiguratorMock
        val configCallback = mecHandler.getConfigCallback(activityLauncherMock, mECSettingMock, launchInputMock)

        val ecsConfig = ECSConfig()
        ecsConfig.isHybris = true
        ECSConfiguration.INSTANCE.locale = "en_US"
        ecsConfig.rootCategory = "category"
        configCallback.onResponse(ecsConfig)
        Mockito.verify(contextMock).startActivity(any(Intent::class.java))
    }

    @Mock
    lateinit var fragmentActivityMock : FragmentActivity
    @Mock
    lateinit var fragmentManagerMock : FragmentManager
    @Mock
    lateinit var fragmentTransactionMock : FragmentTransaction

    @Mock
    lateinit var fragmentLauncherMock: FragmentLauncher

    @Test
    fun `should start fragment on config call back success`() {
        Mockito.`when`(fragmentManagerMock.beginTransaction()).thenReturn(fragmentTransactionMock)
        Mockito.`when`(fragmentActivityMock.supportFragmentManager).thenReturn(fragmentManagerMock)
        Mockito.`when`(fragmentLauncherMock.fragmentActivity).thenReturn(fragmentActivityMock)

        mECSettingMock = MECSettings(contextMock)

        mecLaunchInputMock.flowConfigurator = mecFlowConfiguratorMock
        val configCallback = mecHandler.getConfigCallback(activityLauncherMock, mECSettingMock, launchInputMock)

        val ecsConfig = ECSConfig()
        ecsConfig.isHybris = true
        ECSConfiguration.INSTANCE.locale = "en_US"
        ecsConfig.rootCategory = "category"
        configCallback.onResponse(ecsConfig)
    }

    fun <T> any(type : Class<T>): T {
        Mockito.any(type)
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
}