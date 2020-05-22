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

package com.philips.platform.ecs.microService.manager

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.rest.RestInterface
import com.philips.platform.appinfra.rest.request.RequestQueue
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.request.ECSAbstractRequest
import com.philips.platform.ecs.microService.request.NetworkController
import com.philips.platform.ecs.microService.request.RequestType
import com.philips.platform.ecs.microService.request.any
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner
import java.util.ArrayList

@RunWith(PowerMockRunner::class)
class RequestHandlerTest {

    private lateinit var requestHandler: RequestHandler

    @Mock
    private lateinit var ecsAbstractRequestMock: ECSAbstractRequest

    @Mock
    lateinit var appInfraMock : AppInfra

    @Mock
    lateinit var serviceDiscoveryInterfaceMock: ServiceDiscoveryInterface

    @Mock
    lateinit var restClientMock : RestInterface
    @Mock
    lateinit var requestQueueMock : RequestQueue

    @Mock
    lateinit var networkControllerMock: NetworkController

    @Mock
    lateinit var ecsErrorCallbackMock: ECSCallback<*, ECSError>

    @Before
    fun setUp() {
        requestHandler = RequestHandler()

        val map = HashMap<String, String>()
        map["siteId"] = "IAP_MOB_DKA_SITE"
        Mockito.`when`(ecsAbstractRequestMock.ecsErrorCallback).thenReturn(ecsErrorCallbackMock)
        Mockito.`when`(ecsAbstractRequestMock.getServiceID()).thenReturn("MEC_BASE_URL")
        Mockito.`when`(ecsAbstractRequestMock.getRequestType()).thenReturn(RequestType.JSON)
        Mockito.`when`(ecsAbstractRequestMock.getReplaceURLMap()).thenReturn(map)
        Mockito.`when`(appInfraMock.serviceDiscovery).thenReturn(serviceDiscoveryInterfaceMock)


        Mockito.`when`(restClientMock.requestQueue).thenReturn(requestQueueMock)
        Mockito.`when`(appInfraMock.restClient).thenReturn(restClientMock)
        ECSDataHolder.appInfra = appInfraMock
        ECSDataHolder.appInfra = appInfraMock

        requestHandler.networkController = networkControllerMock


    }

    @Test
    fun `should call service discovery to fetch URL on handle request`() {
        val serviceIDList = mutableListOf<String>()
        serviceIDList.add("MEC_BASE_URL")
        requestHandler.handleRequest(ecsAbstractRequestMock)
        Mockito.verify(serviceDiscoveryInterfaceMock).getServicesWithCountryPreference(any( ArrayList::class.java) as ArrayList<String>?, any(ServiceDiscoveryInterface.OnGetServiceUrlMapListener::class.java), any(Map::class.java) as MutableMap<String, String>?)
    }

    @Test
    fun `get Service Listener should return service listener`() {
        val serviceListener = requestHandler.getServiceListener(ecsAbstractRequestMock)
        assertNotNull(serviceListener)
    }

    @Test
    fun `test service discovery fetch url success`() {
        val serviceListener = requestHandler.getServiceListener(ecsAbstractRequestMock)

        val map =  HashMap<String, ServiceDiscoveryService>()
        val serviceDiscoveryService = ServiceDiscoveryService()
        serviceDiscoveryService.init( "en_US","http://acc.iap.baseURL")
        map.put("MEC_BASE_URL",serviceDiscoveryService)
        serviceListener.onSuccess(map)
        Mockito.verify(networkControllerMock).executeRequest(ecsAbstractRequestMock)
    }

    @Test
    fun `service discovery with null url should not execute request`() {

        val serviceListener = requestHandler.getServiceListener(ecsAbstractRequestMock)

        val map =  HashMap<String, ServiceDiscoveryService>()
        val serviceDiscoveryService = ServiceDiscoveryService()
        serviceDiscoveryService.init( "en_US",null)
        map.put("MEC_BASE_URL",serviceDiscoveryService)
        serviceListener.onSuccess(map)
        Mockito.verify(networkControllerMock,Mockito.never()).executeRequest(ecsAbstractRequestMock)
        Mockito.verify(ecsErrorCallbackMock).onFailure(any(ECSError::class.java))
    }

    @Test
    fun `service discovery with failure should give failure callback`() {

        val serviceListener = requestHandler.getServiceListener(ecsAbstractRequestMock)

        var error = ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_NETWORK
        serviceListener.onError(error,"network error")
        Mockito.verify(ecsErrorCallbackMock).onFailure(any(ECSError::class.java))
    }


}