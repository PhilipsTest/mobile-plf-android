package com.philips.platform.mec.integration.serviceDiscovery

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertNull

@PrepareForTest(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES::class,MECLog::class)
@RunWith(PowerMockRunner::class)
class ServiceDiscoveryMapListenerTest {

    private lateinit var serviceDiscoveryMapListener: ServiceDiscoveryMapListener


    lateinit var urlmap: MutableMap<String, ServiceDiscoveryService>

    @Mock
    lateinit var error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES

    @Mock
    lateinit var mecLogMock : MECLog

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(MECLog::class.java)
        serviceDiscoveryMapListener = ServiceDiscoveryMapListener()
        serviceDiscoveryMapListener.mecLog = mecLogMock
    }

    companion object {
        val IAP_PRIVACY_URL = "iap.privacyPolicy"
        val IAP_FAQ_URL = "iap.faq"
        val IAP_TERMS_URL = "iap.termOfUse"
    }

    @Test
    fun testOnSuccess() {
        val serviceDiscoveryServicePrivacy = ServiceDiscoveryService()
        serviceDiscoveryServicePrivacy.init("en_US" ,"http://philips.privacy")

        val serviceDiscoveryServiceFaq = ServiceDiscoveryService()
        serviceDiscoveryServiceFaq.init("en_US" ,"http://philips.faq")

        val serviceDiscoveryServiceTerms = ServiceDiscoveryService()
        serviceDiscoveryServiceTerms.init("en_US" ,"http://philips.terms")

        urlmap = mutableMapOf()
        urlmap[IAP_PRIVACY_URL] = serviceDiscoveryServicePrivacy
        urlmap[IAP_FAQ_URL] = serviceDiscoveryServiceFaq
        urlmap[IAP_TERMS_URL] = serviceDiscoveryServiceTerms

        serviceDiscoveryMapListener.onSuccess(urlmap)

        assertNotNull(MECDataHolder.INSTANCE.getPrivacyUrl())
        assertNotNull(MECDataHolder.INSTANCE.getFaqUrl())
        assertNotNull(MECDataHolder.INSTANCE.getTermsUrl())
    }

    @Test
    fun testOnError() {
        serviceDiscoveryMapListener.onError(error, "Service discovery Failed")
        Mockito.verify(mecLogMock).d("ServiceDiscoveryMapListener","Service discovery Failed")
    }
}