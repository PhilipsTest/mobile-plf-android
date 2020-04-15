package com.philips.platform.mec.integration.serviceDiscovery

import com.google.gson.Gson
import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.config.ECSConfig
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.securestorage.SecureStorage
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECHybrisAvailabilityListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(Gson::class)
@RunWith(PowerMockRunner::class)
class MECManagerTest {
    private lateinit var mecManager: MECManager

    @Mock
    lateinit var mecHybrisAvailabilityListener: MECHybrisAvailabilityListener

    @Mock
    lateinit var ecsServices: ECSServices

    @Mock
    lateinit var appInfraInterface: AppInfraInterface

    @Captor
    lateinit var captor: ArgumentCaptor<ECSCallback<Boolean, java.lang.Exception>>

    @Captor
    lateinit var captor1: ArgumentCaptor<ECSCallback<ECSConfig, java.lang.Exception>>

    @Mock
    lateinit var ecsCallback: ECSCallback<Boolean, Exception>

    @Mock
    lateinit var ecsCallback1: ECSCallback<ECSConfig, Exception>

    @Mock
    lateinit var exception: java.lang.Exception

    @Mock
    lateinit var error: ECSError

    @Mock
    lateinit var ecsConfig: ECSConfig

    @Mock
    lateinit var mecFetchCartListener: MECFetchCartListener

    @Mock
    lateinit var mecCartUpdateListener: MECCartUpdateListener

    @Mock
    lateinit var secureStorage: SecureStorage

    @Mock
    lateinit var gson: Gson

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(Gson::class.java)
        MECDataHolder.INSTANCE.eCSServices = ecsServices
        MECDataHolder.INSTANCE.appinfra = appInfraInterface
        Mockito.`when`(appInfraInterface.secureStorage).thenReturn(secureStorage)
        Mockito.`when`(appInfraInterface.secureStorage.doesStorageKeyExist(HybrisAuth.KEY_MEC_AUTH_DATA)).thenReturn(true)
        mecManager = MECManager()
    }


    @Test
    fun testIshybrisAvailableWorkerOnResponse() {
        mecManager.ishybrisavailableWorker(mecHybrisAvailabilityListener)
        Mockito.verify(ecsServices).configureECS(captor.capture())
        ecsCallback = captor.value
        ecsCallback.onResponse(true)
        Mockito.verify(mecHybrisAvailabilityListener, Mockito.atLeast(1)).isHybrisAvailable(true)
    }

    @Test
    fun testIshybrisAvailableWorkerOnFailure() {
        mecManager.ishybrisavailableWorker(mecHybrisAvailabilityListener)
        Mockito.verify(ecsServices).configureECS(captor.capture())
        ecsCallback = captor.value
        ecsCallback.onFailure(exception, error)
        Mockito.verify(mecHybrisAvailabilityListener, Mockito.atLeast(0)).isHybrisAvailable(true)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetProductCartCountWorkerOnResponse() {
        mecManager.getProductCartCountWorker(mecFetchCartListener)
        Mockito.verify(ecsServices).configureECSToGetConfiguration(captor1.capture())
        ecsCallback1 = captor1.value
        Mockito.`when`(ecsConfig.isHybris).thenReturn(true)
        Mockito.`when`(ecsConfig.rootCategory).thenReturn("category")
        ecsCallback1.onResponse(ecsConfig)

        Mockito.verify(mecFetchCartListener, Mockito.atLeast(1)).onGetCartCount(1)
    }

    @Test
    fun testGetProductCartCountWorkerOnFailure() {
        mecManager.getProductCartCountWorker(mecFetchCartListener)
        Mockito.verify(ecsServices).configureECSToGetConfiguration(captor1.capture())
        ecsCallback1 = captor1.value
        Mockito.`when`(ecsConfig.isHybris).thenReturn(true)
        Mockito.`when`(ecsConfig.rootCategory).thenReturn("category")
        ecsCallback1.onFailure(exception, error)

        Mockito.verify(mecFetchCartListener, Mockito.atLeast(0)).onGetCartCount(1)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetShoppingCartDataDoCartCall() {
        Mockito.`when`(MECutility.isExistingUser()).thenReturn(true)
        mecManager.getShoppingCartData(mecCartUpdateListener)
    }

    @Test
    fun testGetShoppingCartDataDoHybrisAuthCall() {
        Mockito.`when`(MECutility.isExistingUser()).thenReturn(false)
        mecManager.getShoppingCartData(mecCartUpdateListener)
    }
}