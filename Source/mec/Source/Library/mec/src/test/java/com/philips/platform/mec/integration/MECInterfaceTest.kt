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

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.pif.DataInterface.MEC.MECException
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import com.philips.platform.uappframework.launcher.UiLauncher
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.ArrayList


@PrepareForTest(MECDependencies::class,MECSettings::class,UserDataInterface::class,
        MECAnalytics::class,UiLauncher::class,MECLaunchInput::class,MECException::class,MECFlowConfigurator::class,MECHandler::class)
@RunWith(PowerMockRunner::class)
class MECInterfaceTest {

    lateinit var mecInterface: MECInterface

    lateinit var uappDependenciesMock: MECDependencies
    @Mock
    lateinit var uappSettingsMock: MECSettings

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
    lateinit var mecHandlerMock: MECHandler



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(MECAnalytics::class.java)
        Mockito.`when`(loggingInterfaceMock.createInstanceForComponent(any(String::class.java),any(String::class.java))).thenReturn(loggingInterfaceMock)
        Mockito.`when`(appInfraMock.logging).thenReturn(loggingInterfaceMock)
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        Mockito.`when`(appInfraMock.serviceDiscovery).thenReturn(serviceDiscoveryInterfaceMock)
        uappDependenciesMock = MECDependencies(appInfraMock,userDataInterfaceMock)
        mecInterface = MECInterface()
        mecInterface.mecHandler = mecHandlerMock
    }

    @Test
    fun getMEC_NOTATION() {
    }

    @Test
    fun `should init MEC set up`() {

        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        assertTrue(MECLog.isLoggingEnabled)
        assertNotNull( MECDataHolder.INSTANCE.userDataInterface)
        assertNotNull(MECLog.appInfraLoggingInterface)

    }

    @Mock
    lateinit var uiLauncherMock: UiLauncher

    @Mock
    lateinit var mecLaunchInputMock: MECLaunchInput



    @Test(expected = MECException::class)
    fun `should instantiate ECS SDK on launch`() {
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        mecInterface.launch(uiLauncherMock,mecLaunchInputMock)
        assertNotNull(MECDataHolder.INSTANCE.eCSServices)
    }



    @Test(expected = MECException::class)
    fun `should through exception if internet is not available`() {
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        mecInterface.launch(uiLauncherMock,mecLaunchInputMock)
    }

    @Mock
    lateinit var restInterfaceMock : RestInterface
    @Test
    fun `should launch  mec and configure if internet is available and log in is not required`() {
        makeInternetAvailable()
        makeLoginNotRequired()
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        mecInterface.launch(uiLauncherMock,mecLaunchInputMock)
       // Mockito.verify(mecHandlerMock).launchMEC(uappSettingsMock, uiLauncherMock,mecLaunchInputMock)
    }

    @Test(expected = MECException::class)
    fun `should throw exception if internet is available and log in is  required but user is not logged in`() {
        makeInternetAvailable()
        makeLoginRequired()
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        mecInterface.launch(uiLauncherMock,mecLaunchInputMock)
    }

    @Test(expected = MECException::class)
    fun `should throw exception if internet is available and log in is  required and  user is  logged in but hybris is not supported`() {
        makeInternetAvailable()
        makeLoginRequired()
        makeUserLoggedIn()
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        mecInterface.launch(uiLauncherMock,mecLaunchInputMock)
    }

    @Test
    fun `should launch  mec if internet is available ,user is  logged in and hybris is  supported`() {
        makeInternetAvailable()
        makeLoginRequired()
        makeUserLoggedIn()
        mecLaunchInputMock.supportsHybris = true
        mecInterface.init(uappDependenciesMock,uappSettingsMock)
        mecInterface.launch(uiLauncherMock,mecLaunchInputMock)
        //todo
        // Mockito.verify(mecHandlerMock).launchMEC(uappSettingsMock, uiLauncherMock,mecLaunchInputMock)
    }

    @Mock
    lateinit var flowConfiguratorMock: MECFlowConfigurator

    private fun makeLoginNotRequired() {
        mecLaunchInputMock.flowConfigurator = flowConfiguratorMock
        flowConfiguratorMock.landingView = MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW
    }

    private fun makeLoginRequired() {
        mecLaunchInputMock.flowConfigurator = flowConfiguratorMock
        flowConfiguratorMock.landingView = MECFlowConfigurator.MECLandingView.MEC_SHOPPING_CART_VIEW
    }

    private fun makeUserLoggedIn(){
        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn(UserLoggedInState.USER_LOGGED_IN)
    }

    private fun makeInternetAvailable() {
        Mockito.`when`(restInterfaceMock.isInternetReachable).thenReturn(true)
        Mockito.`when`(appInfraMock.restClient).thenReturn(restInterfaceMock)

    }

    @Test
    fun getMECDataInterface() {
        assertNotNull(mecInterface.getMECDataInterface())
    }

    @After
    fun validate() {
        validateMockitoUsage()
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