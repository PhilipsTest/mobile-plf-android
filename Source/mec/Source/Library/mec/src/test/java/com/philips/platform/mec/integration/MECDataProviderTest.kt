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

import android.content.Context
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.mec.integration.serviceDiscovery.MECManager
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.pif.DataInterface.MEC.MECException
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECHybrisAvailabilityListener
import com.philips.platform.pif.DataInterface.USR.UserDataInterface
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.robolectric.annotation.Config

@PrepareForTest(Context::class,MECManager::class)
@RunWith(PowerMockRunner::class)
class MECDataProviderTest{


    @Mock
    private lateinit var mECHybrisAvailabilityListenerMock: MECHybrisAvailabilityListener

    @Mock
    private lateinit var mecFetchListenerMock: MECFetchCartListener

    @Mock
    private lateinit var mecCartUpdateListenerMock: MECCartUpdateListener

    @Mock
    private lateinit var contextMock :Context

    var mecDataProvider = MECDataProvider

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

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        Mockito.`when`(loggingInterfaceMock.createInstanceForComponent(any(String::class.java),any(String::class.java))).thenReturn(loggingInterfaceMock)
        Mockito.`when`(appInfraMock.logging).thenReturn(loggingInterfaceMock)
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        Mockito.`when`(appInfraMock.serviceDiscovery).thenReturn(serviceDiscoveryInterfaceMock)

        MECDataHolder.INSTANCE.appinfra = appInfraMock
        MECDataHolder.INSTANCE.userDataInterface = userDataInterfaceMock
        mecDataProvider.context = contextMock
    }

    @Test
    fun `should add cart update listener`() {
        mecDataProvider.addCartUpdateListener(mecCartUpdateListenerMock)
        assertNotNull(MECDataHolder.INSTANCE.mecCartUpdateListener)
    }

    @Test
    fun `should remove update listener`() {
        mecDataProvider.removeCartUpdateListener(mecCartUpdateListenerMock)
        assertNull(MECDataHolder.INSTANCE.mecCartUpdateListener)
    }

    @Test(expected = MECException::class)
    fun `fetch cart count should through exception and init ecs sdk to create ECSservices instance`() {
        mecDataProvider.fetchCartCount(mecFetchListenerMock)
        assertNotNull(MECDataHolder.INSTANCE.eCSServices)
    }

    @Test(expected = MECException::class)
    fun `fetch cart count should through exception when internet is not available`() {
        mecDataProvider.fetchCartCount(mecFetchListenerMock)
    }

    @Test(expected = MECException::class)
    fun `fetch cart count should through exception when user is not logged in`() {

        makeInternetAvailable()
        mecDataProvider.fetchCartCount(mecFetchListenerMock)
    }

    @Test(expected = MECException::class)
    fun `fetch cart count should through exception when hybris is not set`() {

        makeInternetAvailable()
        makeUserLoggedIn()
        MECDataHolder.INSTANCE.hybrisEnabled = false
        mecDataProvider.fetchCartCount(mecFetchListenerMock)
    }

    @Mock
    lateinit var mecManagerMock:MECManager


    fun `should fetch cart count when internet is available,user is logged in and hybris is enabled`() {

        makeInternetAvailable()
        makeUserLoggedIn()
        MECDataHolder.INSTANCE.hybrisEnabled = true
        mecDataProvider.mecManager = mecManagerMock
        mecDataProvider.fetchCartCount(mecFetchListenerMock)
        Mockito.verify(mecManagerMock).getProductCartCountWorker(mecFetchListenerMock)
    }

    @Test(expected = MECException::class)
    fun `should in it ECS SDK and instantiate ECSServices when isHybris available is called`() {
        makeInternetAvailable()
        MECDataHolder.INSTANCE.hybrisEnabled = false
        mecDataProvider.isHybrisAvailable(mECHybrisAvailabilityListenerMock)
        assertNotNull(MECDataHolder.INSTANCE.eCSServices)
    }

    @Test(expected = MECException::class)
    fun `should in it ECS SDK and instantiate ECSServices should through exception when internet is not available`() {
        makeInternetUnAvailable()
        mecDataProvider.isHybrisAvailable(mECHybrisAvailabilityListenerMock)
    }

    @Test(expected = MECException::class)
    fun `should through exception if hybris is not enabled when isHybris available is called`() {
        makeInternetAvailable()
        MECDataHolder.INSTANCE.hybrisEnabled = false
        mecDataProvider.isHybrisAvailable(mECHybrisAvailabilityListenerMock)
    }

    @Test
    fun `fetch hybris availability when internet and hybris are enabled`() {
        makeInternetAvailable()
        MECDataHolder.INSTANCE.hybrisEnabled = true
        mecDataProvider.isHybrisAvailable(mECHybrisAvailabilityListenerMock)
        Mockito.verify(mecManagerMock).ishybrisavailableWorker(mECHybrisAvailabilityListenerMock)
    }

    private fun makeUserLoggedIn(){
        Mockito.`when`(userDataInterfaceMock.userLoggedInState).thenReturn(UserLoggedInState.USER_LOGGED_IN)
    }

    private fun makeInternetAvailable() {
        Mockito.`when`(restInterfaceMock.isInternetReachable).thenReturn(true)
        Mockito.`when`(appInfraMock.restClient).thenReturn(restInterfaceMock)
    }

    private fun makeInternetUnAvailable() {
        Mockito.`when`(restInterfaceMock.isInternetReachable).thenReturn(false)
        Mockito.`when`(appInfraMock.restClient).thenReturn(restInterfaceMock)
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