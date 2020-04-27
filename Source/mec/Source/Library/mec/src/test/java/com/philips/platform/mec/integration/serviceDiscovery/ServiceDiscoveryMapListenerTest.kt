package com.philips.platform.mec.integration.serviceDiscovery

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES::class)
@RunWith(PowerMockRunner::class)
class ServiceDiscoveryMapListenerTest {

    private lateinit var serviceDiscoveryMapListener: ServiceDiscoveryMapListener

    @Mock
    lateinit var mockUrlmap: MutableMap<String, ServiceDiscoveryService>

    @Mock
    lateinit var error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        serviceDiscoveryMapListener = ServiceDiscoveryMapListener()
    }

    @Test
    fun testOnSuccess() {
//        Mockito.`when`(mockUrlmap[MECHandler.IAP_PRIVACY_URL]).thenReturn(ServiceDiscoveryService())
//        Mockito.`when`(mockUrlmap[MECHandler.IAP_FAQ_URL])
//        Mockito.`when`(mockUrlmap[MECHandler.IAP_TERMS_URL])
        serviceDiscoveryMapListener.onSuccess(mockUrlmap)
    }

    @Test
    fun testOnError() {
        serviceDiscoveryMapListener.onError(error, "")
    }
}