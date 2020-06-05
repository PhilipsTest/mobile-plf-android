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

package com.philips.platform.ecs.microService.request

import com.android.volley.NetworkError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.config.ECSConfig
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class GetConfigurationRequestTest {

    lateinit var getConfigurationRequest : GetConfigurationRequest

    @Mock
    private lateinit var  eCSCallbackMock: ECSCallback<ECSConfig, ECSError>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        getConfigurationRequest = GetConfigurationRequest(eCSCallbackMock)
        getConfigurationRequest.locale = "en_US"
    }

    @Test
    fun getURL() {
    }

    @Test
    fun getServiceID() {
    }

    @Test
    fun getReplaceURLMap() {
    }

    @Test
    fun onErrorResponse() {
    }

    @Test
    fun onResponse() {
    }

    @Test
    fun `should send hybris enable locale and success Response`() {
        getConfigurationRequest.onResponse(JSONObject(hysbrisSuccessResponse))
        Mockito.verify(eCSCallbackMock).onResponse(any(ECSConfig::class.java))

    }

    @Mock
    lateinit var networkErrorMock : NetworkError
    @Test
    fun `should do error callback when VolleyErrorComes`() {
        getConfigurationRequest.onErrorResponse(networkErrorMock)
        Mockito.verify(eCSCallbackMock).onResponse(any(ECSConfig::class.java))
    }

    var hysbrisSuccessResponse = "{\n" +
            "   \"catalogId\": \"US_PubProductCatalog\",\n" +
            "   \"faqUrl\": \"www.USFaqURL.com\",\n" +
            "   \"helpDeskEmail\": \"www.USHelpDeskMail.com\",\n" +
            "   \"helpDeskPhone\": \"www.USHelpDeskPhone.com\",\n" +
            "   \"helpUrl\": \"www.USHelpURL.com\",\n" +
            "   \"net\": true,\n" +
            "   \"rootCategory\": \"Tuscany_Campaign\",\n" +
            "   \"siteId\": \"US_Tuscany\"\n" +
            "}"
}