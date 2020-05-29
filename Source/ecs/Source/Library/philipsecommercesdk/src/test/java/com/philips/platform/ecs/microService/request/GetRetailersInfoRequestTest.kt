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
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.ecs.microService.util.ECSDataHolder
import org.json.JSONObject
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class GetRetailersInfoRequestTest {

    lateinit var getRetailersInfoRequest : GetRetailersInfoRequest

    @Mock
    lateinit var ecsCallbackMock: ECSCallback<ECSRetailerList?, ECSError>

    var  ctn = "HX3631/06"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        ECSDataHolder.locale = "en_US"
        ECSDataHolder.lang = "en"
        var ecsConfig = ECSConfig("en_US",null,null,null,null,null,"Tuscany_Campaign","US_Tuscany",true)
        ECSDataHolder.config = ecsConfig
        getRetailersInfoRequest  = GetRetailersInfoRequest(ctn,ecsCallbackMock)
    }


    @Mock
    lateinit var networkErrorMock : NetworkError
    @Test
    fun `should do error callback when VolleyErrorComes`() {
        getRetailersInfoRequest.onErrorResponse(networkErrorMock)
        Mockito.verify(ecsCallbackMock).onFailure(any(ECSError::class.java))
    }


    @Test
    fun `get header should be null`() {
        assertNull(getRetailersInfoRequest.getHeader())
    }

    @Test
    fun `url should be as expected`() {

        val expectedURL = "https://www.philips.com/api/wtb/v1/B2C/en_US/online-retailers?product=HX3631/06"
        assertEquals(expectedURL,getRetailersInfoRequest.getURL())
    }

    @Test
    fun `handle Success Response`() {

        getRetailersInfoRequest.onResponse(JSONObject(successJSON))
        Mockito.verify(ecsCallbackMock).onResponse(any(ECSRetailerList::class.java))

    }

    val successJSON = "{\n" +
            "\"wrbresults\": {\n" +
            "\"storeLocatorUrl\": \"https://www.usa.philips.com/c-p/HX3631_06/-/store-locator\",\n" +
            "\"EloquaSiteURL\": \"http://secure.eloqua.com/e/f2.aspx?elqFormName=201210SSFRCaptureBuyButtonClick-1350460856473\",\n" +
            "\"ShowBuyButton\": \"Y\",\n" +
            "\"Texts\": {\n" +
            "\"Text\": [\n" +
            "{\n" +
            "\"Key\": \"InStockYes\",\n" +
            "\"Value\": \"Yes\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"Previous\",\n" +
            "\"Value\": \"Previous\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"Next\",\n" +
            "\"Value\": \"Next\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"InStock\",\n" +
            "\"Value\": \"In Stock\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"BuyOnline\",\n" +
            "\"Value\": \"Buy Online\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"OfStores\",\n" +
            "\"Value\": \"of {0} stores\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"InStockNo\",\n" +
            "\"Value\": \"No\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"BuyNowRecommendedStore\",\n" +
            "\"Value\": \"Buy now at this recommended online store.\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"InStockSeeSite\",\n" +
            "\"Value\": \"See site\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"FindALocalStore\",\n" +
            "\"Value\": \"Find a local store\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"CityOrPostalCode\",\n" +
            "\"Value\": \"City or postal code\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"VisitLocalStoreExperience\",\n" +
            "\"Value\": \"Visit a local store to experience this Philips product.\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"DealerPrice\",\n" +
            "\"Value\": \"Price\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"localStoreUrl\",\n" +
            "\"Value\": \"https://www.usa.philips.com/c/locators/wrb_retail_store_locator_results.jsp?locationType=retailStore&country=US&language=en&catalogType=CONSUMER&productId=HX3631_06_US_CONSUMER\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"FindNearbyStore\",\n" +
            "\"Value\": \"Find a store near you\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"OutOfStockInPhilipsShop\",\n" +
            "\"Value\": \"Unfortunately not in stock in the Philips shop\"\n" +
            "},\n" +
            "{\n" +
            "\"Key\": \"Disclaimer\",\n" +
            "\"Value\": \"\"\n" +
            "}\n" +
            "]\n" +
            "},\n" +
            "\"Ctn\": \"HX3631/06\",\n" +
            "\"OnlineStoresForProduct\": {\n" +
            "\"excludePhilipsShopInWTB\": \"N\",\n" +
            "\"showPrice\": \"N\",\n" +
            "\"ctn\": \"HX3631/06\",\n" +
            "\"Stores\": {\n" +
            "\"Store\": [\n" +
            "{\n" +
            "\"name\": \"Amazon - US\",\n" +
            "\"availability\": \"YES\",\n" +
            "\"isPhilipsStore\": \"N\",\n" +
            "\"philipsOnlinePrice\": \"USD14.99\",\n" +
            "\"storeType\": \"buy_at_others\",\n" +
            "\"logoHeight\": 31,\n" +
            "\"logoWidth\": 88,\n" +
            "\"xactparam\": \"subTag\",\n" +
            "\"buyURL\": \"https://philips.channelsight.com//ClickThru/Index/7ef57669-b703-4472-be81-7bfb093d185d?lang=en\",\n" +
            "\"logoURL\": \"https://channelsightstorage.blob.core.windows.net/logos/PhilipsAmazonUS.png\"\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"WalmartUS - US\",\n" +
            "\"availability\": \"YES\",\n" +
            "\"isPhilipsStore\": \"N\",\n" +
            "\"philipsOnlinePrice\": \"USD14.99\",\n" +
            "\"storeType\": \"buy_at_others\",\n" +
            "\"logoHeight\": 31,\n" +
            "\"logoWidth\": 88,\n" +
            "\"xactparam\": \"subTag\",\n" +
            "\"buyURL\": \"https://philips.channelsight.com//ClickThru/Index/bde7e5f9-d341-4351-b904-e8f946e45506?lang=en\",\n" +
            "\"logoURL\": \"https://channelsightstorage.blob.core.windows.net/logos/PhilipsWalmartUS.png\"\n" +
            "},\n" +
            "{\n" +
            "\"name\": \"Walgreens.com\",\n" +
            "\"availability\": \"YES\",\n" +
            "\"isPhilipsStore\": \"N\",\n" +
            "\"philipsOnlinePrice\": \"\$17.49\",\n" +
            "\"storeType\": \"buy_at_others\",\n" +
            "\"logoHeight\": 31,\n" +
            "\"logoWidth\": 88,\n" +
            "\"xactparam\": \"guid\",\n" +
            "\"buyURL\": \"https://itrack.where-to-buy.co/wheretobuy/v1/WidgetClicks/?click=FFWmTQCDnHsSrNqp0Hj7lJUcUFLASpDfbw000rGOjIf1vc3bT2p2%2BbOZny%2FzqjrwCxv4iTsUwS4xnrK2%2BqA9v1UJTaMpPAlsYwiIDpDTaGbBILUrR7vDzpvhssKJwfoqbVK83XFAWdnXw%2FUbm6I9cTqGz00%2F9FimLiw5vOLM75k4ozmBzH0JUTObOMElZxOtVs14Q7SiZUta%2Bi4t0PnpxKQ%2FZV9CWoBvlcyTUzpdRol%2F6wFxyfBU5jFh5KSjE1CHFmW5Erxl6AAaI8LDuPX%2Bip91c%2BKCnw4a42Bnhb5lDC2ojoVDyM6YjZwqr33Ww9gVpZx8JKyRS6POesROQtgx74EENN8MBPj4MTtwgHfOsCDPzxLpEvEPs8Bm6S9txJ5MUggQWoQYoSjzoE%2FqIDGArEQ80HfcixGxbwMlDG6kVMebyLxyT4lzTAgoLH3ADVpXItPlqBWYsJhFo6lCUTIpPU2B9zD9tWv7PuJ43L0ZLYmWivcpmnyAzXk7FJiG%2BdnRLTe1ZFiBiKW3bsUu%2FTD0BDi1b89yBJcZh0i4DqFlz7HQRvJw25BYVKF8r3pyRGvQ7nCy9LjW04PgVCgQIMFUU4CRiT%2FHoGzdniEpyCIDOxp8V13Z9EOof6d4EIxfK2V8P4bS%2FFYCCi3TbRAHmTSJWMSUDSti3gzv39l7mKnFKKxmqm2vUBY3Y7RGaWQTOUzXFXZk5DjmL3jD4J2rdcYF3Lij%2FyBpya2%2BFMc8dQ9aFowGJWJEnAfoNfSo7fnhxT6TKTZshPZsshUs4HKfrUbfZKfjXkvmgZG7JY%2Bv34ApexA%3D&rUrl=aHR0cDovL3JlZGlyZWN0LnZpZ2xpbmsuY29tP3U9aHR0cHMlM0ElMkYlMkZ3d3cud2FsZ3JlZW5zLmNvbSUyRnN0b3JlJTJGYyUyRnBoaWxpcHMtc29uaWNhcmUtcG93ZXItdXAtYmF0dGVyeS10b290aGJydXNoJTJDLWh4MzYzMSUyRjA2JTJGSUQlM0Rwcm9kNjI3MDU4NS1wcm9kdWN0JmtleT03NzA0NGQwNGFjYmI2NmJjMzkyODhlMjdmMTI5ZDc1MCZjdWlkPXt3dGJfY2xpY2tyZWZ9&ct=True\",\n" +
            "\"logoURL\": \"https://where-to-buy.co/content/images/logos/walgreensus176x62.png\"\n" +
            "}\n" +
            "]\n" +
            "}\n" +
            "},\n" +
            "\"EloquaSiteId\": 1065054172,\n" +
            "\"RetailStoreAvailableFlag\": true\n" +
            "}\n" +
            "}"
}