package com.philips.platform.ecs.microService.manager

import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.error.ECSException
import com.philips.platform.ecs.microService.model.cart.Data
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.config.ECSConfig
import com.philips.platform.ecs.microService.request.CreateCartRequest
import com.philips.platform.ecs.microService.request.GetCartRequest
import com.philips.platform.ecs.microService.request.GetRetailersInfoRequest
import com.philips.platform.ecs.microService.request.any
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.util.ECSConfiguration
import junit.framework.Assert
import junit.framework.Assert.assertEquals
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
    lateinit var appConfigurationInterfaceMock : AppConfigurationInterface

    @Mock
    lateinit var appInfraMock : AppInfra

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSShoppingCart, ECSError>

    val validCTN = "HX3631/06"
    val inValidCTN = " HX   3631/06 "

    @Before
    fun setUp() {
        ECSDataHolder.locale = "en_US"
        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig


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
    fun `fetch shopping cart should throw exception if locale is not present`() {

        ECSDataHolder.locale = null
        try {
            mECSCartManager.fetchShoppingCart(ecsCallbackMock)
        }catch (e : ECSException){
          assertEquals(5052, e.errorCode)
        }
    }

    @Test
    fun `fetch shopping cart should throw exception if hybris is not present`() {

        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",false)
        ECSDataHolder.config = ecsConfig

        try {
            mECSCartManager.fetchShoppingCart(ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5054, e.errorCode)
        }
    }

    @Test
    fun `fetch shopping cart should throw exception if api key is not configured in config asset file`() {
        setApiKeyNull()

        try {
            mECSCartManager.fetchShoppingCart(ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(6009, e.errorCode)
        }
    }

    @Test
    fun `fetch shopping cart should throw exception if auth is not present`() {

        setApiKey()


        try {
            mECSCartManager.fetchShoppingCart(ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5057, e.errorCode)
        }
    }


    @Test
    fun `request handler should handle get shopping cart request if validation error is null`() {
        setApiKey()
        ECSConfiguration.INSTANCE.setAuthToken("testAuthToken")
        ECSDataHolder.authToken = "testAuthToken"
        mECSCartManager.fetchShoppingCart(ecsCallbackMock)

        //TODO uncomment the below line when urls are uploaded to service discovery
       // Mockito.verify(requestHandlerMock).handleRequest(any(GetCartRequest::class.java))
    }


    @Test
    fun `create shopping cart should throw exception for invalid ctn`() {

        ECSDataHolder.locale = null
        try {
            mECSCartManager.createECSShoppingCart(inValidCTN,ecsCallback = ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5006, e.errorCode)
        }
    }


    @Test
    fun `create shopping cart should throw exception if locale is not present`() {

        ECSDataHolder.locale = null
        try {
            mECSCartManager.createECSShoppingCart(validCTN,ecsCallback = ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5052, e.errorCode)
        }
    }

    @Test
    fun `craete shopping cart should throw exception if hybris is not present`() {

        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",false)
        ECSDataHolder.config = ecsConfig

        try {
            mECSCartManager.createECSShoppingCart(validCTN,ecsCallback = ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5054, e.errorCode)
        }
    }

    @Test
    fun `create shopping cart should throw exception if api key is not configured in config asset file`() {
        setApiKeyNull()

        try {
            mECSCartManager.createECSShoppingCart(validCTN,ecsCallback = ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(6009, e.errorCode)
        }
    }

    @Test
    fun `create shopping cart should throw exception if auth is not present`() {

        setApiKey()
        try {
            mECSCartManager.createECSShoppingCart(validCTN,ecsCallback = ecsCallbackMock)
        }catch (e : ECSException){
            assertEquals(5057, e.errorCode)
        }
    }


    @Test
    fun `create handler should handle create shopping cart request if validation error is null`() {
        setApiKey()
        ECSConfiguration.INSTANCE.setAuthToken("testAuthToken")
        ECSDataHolder.authToken = "testAuthToken"
        mECSCartManager.createECSShoppingCart(validCTN,ecsCallback = ecsCallbackMock)

        //TODO uncomment the below line when urls are uploaded to service discovery
        // Mockito.verify(requestHandlerMock).handleRequest(any(CreateCartRequest::class.java))
    }




}