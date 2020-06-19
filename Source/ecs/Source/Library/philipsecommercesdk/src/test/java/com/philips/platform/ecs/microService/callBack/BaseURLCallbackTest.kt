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

package com.philips.platform.ecs.microService.callBack

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.ecs.microService.constant.ECSConstants
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.request.GetConfigurationRequest
import com.philips.platform.ecs.microService.request.any
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class BaseURLCallbackTest{

    @Mock
    lateinit var getConfigurationRequestMock: GetConfigurationRequest

    @Mock
    lateinit var eCSCallbackMock: ECSCallback<ECSConfig, ECSError>

    lateinit var baseURLCallback : BaseURLCallback

    @Before
    fun setUp() {
        Mockito.`when`(getConfigurationRequestMock.eCSCallback).thenReturn(eCSCallbackMock)
        baseURLCallback = BaseURLCallback(getConfigurationRequestMock)
    }

    @Test
    fun `test service discovery fetch by country returning null`() {
        baseURLCallback.onSuccess(null)
        Mockito.verify(getConfigurationRequestMock.eCSCallback).onResponse(any(ECSConfig::class.java))
    }

    @Test
    fun `should give hybris not available when base url is there but proposition ID is not present in config file`() {

        val callback = object : ECSCallback<ECSConfig, ECSError>{
            override fun onResponse(result: ECSConfig) {
                assertEquals(result.locale,"en_US")
                assertFalse(result.isHybris)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        baseURLCallback = BaseURLCallback(GetConfigurationRequest(callback))

        val urlMap: MutableMap<String, ServiceDiscoveryService> = mutableMapOf()
        val serviceDiscoveryService  = ServiceDiscoveryService()
        serviceDiscoveryService.init("en_US","http://acc.philipsCommerce/")
        urlMap[ECSConstants.SERVICEID_IAP_BASEURL] = serviceDiscoveryService
        baseURLCallback.onSuccess(urlMap)

    }

    @Mock
    lateinit var appInfraMock : AppInfra

    @Mock
    lateinit var loggingInterfaceMock: LoggingInterface

    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    @Test
    fun `should give hybris not available when base url is not there but proposition ID is  present in config file`() {

        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java),any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("IAP_MOB_DKA")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock

        val callback = object : ECSCallback<ECSConfig, ECSError>{
            override fun onResponse(result: ECSConfig) {
                assertEquals(result.locale,"en_US")
                assertFalse(result.isHybris)
            }

            override fun onFailure(ecsError: ECSError) {
                fail()
            }
        }

        baseURLCallback = BaseURLCallback(GetConfigurationRequest(callback))

        val urlMap: MutableMap<String, ServiceDiscoveryService> = mutableMapOf()
        val serviceDiscoveryService  = ServiceDiscoveryService()
        serviceDiscoveryService.init("en_US",null)
        urlMap[ECSConstants.SERVICEID_IAP_BASEURL] = serviceDiscoveryService
        baseURLCallback.onSuccess(urlMap)

    }

    @Test
    fun `should execute hybris config request when proposition ID and base url are available`() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java),any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("IAP_MOB_DKA")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock

        val urlMap: MutableMap<String, ServiceDiscoveryService> = mutableMapOf()
        val serviceDiscoveryService  = ServiceDiscoveryService()
        serviceDiscoveryService.init("en_US","http://acc.philipsCommerce/")
        urlMap[ECSConstants.SERVICEID_IAP_BASEURL] = serviceDiscoveryService
        baseURLCallback.onSuccess(urlMap)
        Mockito.verify(getConfigurationRequestMock).executeRequest()
    }

    @Test
    fun `ecs error should be thrown if service discovery call fails`() {
        baseURLCallback.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,"server not responding")
        Mockito.verify(eCSCallbackMock).onFailure(any(ECSError::class.java))
    }
}