package com.philips.platform.ecs.microService.manager

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.cart.Data
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.request.CreateCartRequest
import com.philips.platform.ecs.microService.request.any
import com.philips.platform.ecs.microService.util.ECSDataHolder
import junit.framework.Assert
import junit.framework.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class ECSCartManagerTest {

    lateinit var mECSCartManager: ECSCartManager

    @Mock
    lateinit var requestHandlerMock: RequestHandler

    @Mock
    lateinit var CreateCartRequestMock:  CreateCartRequest

    @Mock
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    @Mock
    lateinit var appInfraMock : AppInfra

    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        mECSCartManager = ECSCartManager()
       mECSCartManager.requestHandler=requestHandlerMock
    }

    private fun setApiKey() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn("yaTmSAVqDR4GNwijaJie3aEa3ivy7Czu22BxZwKP")
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    private fun setApiKeyNull() {
        Mockito.`when`(appConfigurationInterfaceMock.getPropertyForKey(any(String::class.java), any(String::class.java), any(AppConfigurationInterface.AppConfigurationError::class.java))).thenReturn(null)
        Mockito.`when`(appInfraMock.configInterface).thenReturn(appConfigurationInterfaceMock)
        ECSDataHolder.appInfra = appInfraMock
    }

    @Test
    fun fetchShoppingCart() {
    }

    @Test
    fun createECSShoppingCart() {
        ECSDataHolder.locale = "en_US"
        ECSDataHolder.config = ECSConfig(isHybris = true)
        setApiKey()

        var ecscallback = object: ECSCallback<ECSShoppingCart, ECSError> {
            override fun onResponse(result: ECSShoppingCart) {
                Assert.assertNotNull(result)
            }
            override fun onFailure(ecsError: ECSError) {
             fail()
            }
        };

        var data= Data(null,"id","type")
        var eCSShoppingCart = ECSShoppingCart(data)
        Mockito.`when`(requestHandlerMock.handleRequest(CreateCartRequestMock)).then { ecscallback.onResponse(eCSShoppingCart) }
        CreateCartRequestMock = CreateCartRequest("ctn",1,ecscallback)
        mECSCartManager.createECSShoppingCart("ctn",1,ecscallback)
        Mockito.`when`(requestHandlerMock.handleRequest(CreateCartRequestMock)).then { ecscallback.onResponse(eCSShoppingCart) }
    }

    @Test
    fun `invalid ctn`(){

    }

    @Test
    fun `invalid quantity`(){

    }


}