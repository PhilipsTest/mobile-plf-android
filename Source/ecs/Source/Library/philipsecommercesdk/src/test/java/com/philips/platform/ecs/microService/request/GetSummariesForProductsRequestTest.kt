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

import android.text.TextUtils
import android.util.Log
import com.android.volley.NetworkError
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError
import com.philips.platform.ecs.microService.model.product.ECSProduct
import org.json.JSONObject
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(TextUtils::class,Log::class,ECSError::class)
@RunWith(PowerMockRunner::class)
class GetSummariesForProductsRequestTest {


    lateinit var getSummariesForProductsRequest : GetSummariesForProductsRequest


  //  val ecsProducts:List<ECSProduct>,
    @Mock
    lateinit var ecsCallbackMock: ECSCallback<List<ECSProduct>, ECSError>


    var ecsProduct1 = ECSProduct(null,"QP2520/70",null)
    var ecsProduct2 = ECSProduct(null,"HD9630/96",null)
    var ecsProduct3 = ECSProduct(null,"HX505/01",null)

    var productList = mutableListOf<ECSProduct>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        PowerMockito.mockStatic(TextUtils::class.java)
        PowerMockito.mockStatic(Log::class.java)
        productList.add(ecsProduct1)
        productList.add(ecsProduct2)
        productList.add(ecsProduct3)
        getSummariesForProductsRequest = GetSummariesForProductsRequest(productList,ecsCallbackMock)
    }

    @Test
    fun `service ID should be prxclient summary list`() {
        assertEquals("prxclient.summarylist",getSummariesForProductsRequest.getServiceID())
    }
    @Test
    fun `url mapper should have 3 value and ctns should present as expected`() {
        assertEquals(3,getSummariesForProductsRequest.getReplaceURLMap().size)
        assertEquals("QP2520/70,HD9630/96,HX505/01",getSummariesForProductsRequest.getReplaceURLMap()["ctns"])
    }

    @Test
    fun `request type should be jSON`() {
        assertEquals(RequestType.JSON,getSummariesForProductsRequest.getRequestType())
    }

    @Test
    fun `JSON Success Response Listener should not be null`() {
        assertNotNull(getSummariesForProductsRequest.getJSONSuccessResponseListener())
    }

    @Test
    fun `String Success Response Listener should  be null`() {
        assertNull(getSummariesForProductsRequest.getStringSuccessResponseListener())
    }

    @Test
    fun `get header should be null`() {
        assertNull(getSummariesForProductsRequest.getHeader())
    }

    @Test
    fun `handle no proper ctn success response`() {
        getSummariesForProductsRequest.onResponse(JSONObject(wrongCTNSuccessJSON))
        Mockito.verify(ecsCallbackMock).onFailure(any(ECSError::class.java))
    }

    @Test
    fun `handle success response`() {

        getSummariesForProductsRequest.onResponse(JSONObject(successJSON))

        assertNotNull(productList[0].summary)
        assertNotNull(productList[1].summary)
        assertNull(productList[2].summary)
        Mockito.verify(ecsCallbackMock).onResponse(productList)
    }


    @Test
    fun `handle partial success response`() {

        getSummariesForProductsRequest.onResponse(JSONObject(partialSuccessJSON))

        assertNotNull(productList[0].summary)
        assertNotNull(productList[1].summary)
        assertNull(productList[2].summary)
        Mockito.verify(ecsCallbackMock).onResponse(productList)
    }

    @Mock
    lateinit var networkErrorMock : NetworkError
    @Test
    fun `should do error callback when VolleyErrorComes`() {
        getSummariesForProductsRequest.onErrorResponse(networkErrorMock)
        Mockito.verify(ecsCallbackMock).onFailure(any(ECSError::class.java))
    }



    var wrongCTNSuccessJSON = "{\n" +
            "\"success\": false,\n" +
            "\"failureReason\": \"Provide Proper CTNs\"\n" +
            "}"

    //https://stg.philips.com/prx/product/B2C/en_US/CONSUMER/listproducts?ctnlist=QP2520/70,HD9630/96


    var partialSuccessJSON = "{\n" +
            "\"success\": true,\n" +
            "\"data\": [\n" +
            "{\n" +
            "\"locale\": \"en_US\",\n" +
            "\"ctn\": \"QP2520/70\",\n" +
            "\"dtn\": \"QP2520/70\",\n" +
            "\"leafletUrl\": \"https://www.download.p4c.philips.com/files/q/qp2520_70/qp2520_70_pss_aenus.pdf\",\n" +
            "\"productTitle\": \"Norelco OneBlade Face\",\n" +
            "\"alphanumeric\": \"QP2520/70\",\n" +
            "\"brandName\": \"Norelco\",\n" +
            "\"brand\": {\n" +
            "\"partnerBrandType\": \"MakersMark\",\n" +
            "\"partnerLogo\": \"https://images.philips.com/is/image/PhilipsConsumer/NOR-BRP-global-001\",\n" +
            "\"brandLogo\": \"https://images.philips.com/is/image/PhilipsConsumer/PHI-BRP-global-001\"\n" +
            "},\n" +
            "\"familyName\": \"OneBlade\",\n" +
            "\"productURL\": \"/c-p/QP2520_70/norelco-oneblade-face\",\n" +
            "\"productPagePath\": \"/content/B2C/en_US/product-catalog/QP2520_70\",\n" +
            "\"grpCode\": \"PERSONAL_CARE_GR\",\n" +
            "\"grpNme\": \"Personal care\",\n" +
            "\"catCode\": \"FACIAL_STYLERS_AND_GROOMING_KITS_CA\",\n" +
            "\"catNme\": \"FACE Stylers and grooming kits\",\n" +
            "\"subcatCode\": \"KITS_ONEBLADE_SU\",\n" +
            "\"subcategoryName\": \"OneBlade\",\n" +
            "\"subCatRank\": 20,\n" +
            "\"categoryPath\": \"/content/B2C/en_US/marketing-catalog/pe/face-stylers-and-grooming-kits/oneblade\",\n" +
            "\"descriptor\": \"Face\",\n" +
            "\"domain\": \"https://www.usa.philips.com\",\n" +
            "\"versions\": [\n" +
            "\"Trim, edge, shave\",\n" +
            "\"For any length of hair\",\n" +
            "\"3 x click-on stubble combs\",\n" +
            "\"Rechargeable, wet & dry use\"\n" +
            "],\n" +
            "\"productStatus\": \"SUPPORT\",\n" +
            "\"SEOProductName\": \"norelco-oneblade-face\",\n" +
            "\"imageURL\": \"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-IMS-en_US\",\n" +
            "\"sop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2020-01-11T00:00:00.000+01:00\",\n" +
            "\"isDeleted\": false,\n" +
            "\"priority\": 396793,\n" +
            "\"price\": {\n" +
            "\"productPrice\": \"34.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"34.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$34.95\",\n" +
            "\"formattedDisplayPrice\": \"\$34.95\"\n" +
            "},\n" +
            "\"reviewStatistics\": {\n" +
            "\"averageOverallRating\": 4.4716,\n" +
            "\"totalReviewCount\": 774\n" +
            "},\n" +
            "\"keyAwards\": [\n" +
            "\"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-KA1-en_US-001\",\n" +
            "\"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-KA2-en_US-001\"\n" +
            "],\n" +
            "\"wow\": \"Trim, edge and shave any length of hair\",\n" +
            "\"catalogs\": [\n" +
            "{\n" +
            "\"catalogId\": \"SHOPEMP\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"SHOPPUB\",\n" +
            "\"status\": \"DELETED\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CONSUMER\",\n" +
            "\"status\": \"SUPPORT\",\n" +
            "\"sop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2020-01-11T00:00:00.000+01:00\",\n" +
            "\"priority\": 396793,\n" +
            "\"rank\": 1,\n" +
            "\"isDeleted\": false,\n" +
            "\"price\": [\n" +
            "{\n" +
            "\"productPrice\": \"34.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"34.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$34.95\",\n" +
            "\"formattedDisplayPrice\": \"\$34.95\"\n" +
            "}\n" +
            "]\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CARE\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2040-01-11T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"isDeleted\": false\n" +
            "}\n" +
            "],\n" +
            "\"rank\": 1,\n" +
            "\"showOnlySupport\": false,\n" +
            "\"subWOW\": \"Designed to cut hair, not skin\",\n" +
            "\"marketingTextHeader\": \"The Philips Norelco OneBlade is a revolutionary new hybrid styler that can trim, shave and create clean lines and edges, on any length of hair. Forget about using multiple steps and tools. OneBlade does it all.\",\n" +
            "\"productType\": \"Key\",\n" +
            "\"careSop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"filterKeys\": [\n" +
            "\"FACIAL_STYLERS_AND_GROOMING_KITS_CA\",\n" +
            "\"FK_CLEA_PEC\",\n" +
            "\"FK_REF_PEC\",\n" +
            "\"KITS_ONEBLADE_SU\",\n" +
            "\"ONEBLADE_FACE_STYLE_SHAVE_CA\",\n" +
            "\"ONEBLADE_FACE_STYLE_SHAVE_SU\",\n" +
            "\"PERSONAL_CARE_GR\"\n" +
            "],\n" +
            "\"subcategory\": \"KITS_ONEBLADE_SU\",\n" +
            "\"gtin\": \"00075020051837\",\n" +
            "\"accessory\": false\n" +
            "},\n" +
            "{\n" +
            "\"locale\": \"en_US\",\n" +
            "\"ctn\": \"HD9630/96\",\n" +
            "\"dtn\": \"HD9630/96\",\n" +
            "\"leafletUrl\": \"https://www.download.p4c.philips.com/files/h/hd9630_96/hd9630_96_pss_aenus.pdf\",\n" +
            "\"productTitle\": \"Viva Collection Airfryer XXL with Twin TurboStar Technology\",\n" +
            "\"alphanumeric\": \"HD9630/96\",\n" +
            "\"brandName\": \"Philips\",\n" +
            "\"brand\": {\n" +
            "\"brandLogo\": \"https://images.philips.com/is/image/PhilipsConsumer/PHI-BRP-global-001\"\n" +
            "},\n" +
            "\"familyName\": \"Viva Collection\",\n" +
            "\"productURL\": \"/c-p/HD9630_96/viva-collection-airfryer-xxl-with-twin-turbostar-technology\",\n" +
            "\"productPagePath\": \"/content/B2C/en_US/product-catalog/HD9630_96\",\n" +
            "\"grpCode\": \"HOUSEHOLD_PRODUCTS_GR\",\n" +
            "\"grpNme\": \"Household products\",\n" +
            "\"catCode\": \"COOKING_CA\",\n" +
            "\"catNme\": \"Cooking\",\n" +
            "\"subcatCode\": \"AIRFRYER_SU\",\n" +
            "\"subcategoryName\": \"Airfryer\",\n" +
            "\"subCatRank\": 10,\n" +
            "\"categoryPath\": \"/content/B2C/en_US/marketing-catalog/ho/cooking/airfryer\",\n" +
            "\"descriptor\": \"Airfryer XXL with Twin TurboStar Technology\",\n" +
            "\"domain\": \"https://www.usa.philips.com\",\n" +
            "\"versions\": [\n" +
            "\"VIVA Collection\",\n" +
            "\"Twin TurboStar\",\n" +
            "\"Rapid Air Technology\",\n" +
            "\"Black\"\n" +
            "],\n" +
            "\"productStatus\": \"NORMAL\",\n" +
            "\"SEOProductName\": \"viva-collection-airfryer-xxl-with-twin-turbostar-technology\",\n" +
            "\"imageURL\": \"https://images.philips.com/is/image/PhilipsConsumer/HD9630_96-IMS-en_US\",\n" +
            "\"sop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2022-03-02T00:00:00.000+01:00\",\n" +
            "\"isDeleted\": false,\n" +
            "\"priority\": 211855,\n" +
            "\"price\": {\n" +
            "\"productPrice\": \"299.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"299.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$299.95\",\n" +
            "\"formattedDisplayPrice\": \"\$299.95\"\n" +
            "},\n" +
            "\"reviewStatistics\": {\n" +
            "\"averageOverallRating\": 4,\n" +
            "\"totalReviewCount\": 13\n" +
            "},\n" +
            "\"wow\": \"A healthier way to fry\",\n" +
            "\"catalogs\": [\n" +
            "{\n" +
            "\"catalogId\": \"SHOPEMP\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"SHOPPUB\",\n" +
            "\"status\": \"DELETED\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CONSUMER\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2022-03-02T00:00:00.000+01:00\",\n" +
            "\"priority\": 211855,\n" +
            "\"rank\": 50,\n" +
            "\"isDeleted\": false,\n" +
            "\"price\": [\n" +
            "{\n" +
            "\"productPrice\": \"299.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"299.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$299.95\",\n" +
            "\"formattedDisplayPrice\": \"\$299.95\"\n" +
            "}\n" +
            "]\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CARE\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2042-03-02T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"isDeleted\": false\n" +
            "}\n" +
            "],\n" +
            "\"rank\": 50,\n" +
            "\"showOnlySupport\": false,\n" +
            "\"subWOW\": \"Enjoy great tasting fried food with minimal fat\",\n" +
            "\"marketingTextHeader\": \"Uses hot air to fry your favorite foods. Fried food with up to 90% less fat. XXL 3lb/4qt capacity (6 portions). Instantly hot and ready to go in seconds. Includes a QuickClean basket\",\n" +
            "\"productType\": \"MCI\",\n" +
            "\"careSop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"filterKeys\": [\n" +
            "\"AIRFRYER_SU\",\n" +
            "\"AR_AIRFRYER\",\n" +
            "\"COOKING_CA\",\n" +
            "\"FK_AIRFRYER\",\n" +
            "\"FK_CLEA_HOU\",\n" +
            "\"FK_CLEA_HOUSEHOLD\",\n" +
            "\"FK_REF_HOU\",\n" +
            "\"FK_REF_HOUSEHOLD\",\n" +
            "\"HOUSEHOLD_PRODUCTS_GR\"\n" +
            "],\n" +
            "\"subcategory\": \"AIRFRYER_SU\",\n" +
            "\"gtin\": \"00075020072450\",\n" +
            "\"accessory\": false\n" +
            "}\n" +
            "],\n" +
            "\"invalidCtns\": [\n" +
            "\"HX505/01\"\n" +
            "]\n" +
            "}"

    var successJSON = "{\n" +
            "\"success\": true,\n" +
            "\"data\": [\n" +
            "{\n" +
            "\"locale\": \"en_US\",\n" +
            "\"ctn\": \"QP2520/70\",\n" +
            "\"dtn\": \"QP2520/70\",\n" +
            "\"leafletUrl\": \"https://www.download.p4c.philips.com/files/q/qp2520_70/qp2520_70_pss_aenus.pdf\",\n" +
            "\"productTitle\": \"Norelco OneBlade Face\",\n" +
            "\"alphanumeric\": \"QP2520/70\",\n" +
            "\"brandName\": \"Norelco\",\n" +
            "\"brand\": {\n" +
            "\"partnerBrandType\": \"MakersMark\",\n" +
            "\"partnerLogo\": \"https://images.philips.com/is/image/PhilipsConsumer/NOR-BRP-global-001\",\n" +
            "\"brandLogo\": \"https://images.philips.com/is/image/PhilipsConsumer/PHI-BRP-global-001\"\n" +
            "},\n" +
            "\"familyName\": \"OneBlade\",\n" +
            "\"productURL\": \"/c-p/QP2520_70/norelco-oneblade-face\",\n" +
            "\"productPagePath\": \"/content/B2C/en_US/product-catalog/QP2520_70\",\n" +
            "\"grpCode\": \"PERSONAL_CARE_GR\",\n" +
            "\"grpNme\": \"Personal care\",\n" +
            "\"catCode\": \"FACIAL_STYLERS_AND_GROOMING_KITS_CA\",\n" +
            "\"catNme\": \"FACE Stylers and grooming kits\",\n" +
            "\"subcatCode\": \"KITS_ONEBLADE_SU\",\n" +
            "\"subcategoryName\": \"OneBlade\",\n" +
            "\"subCatRank\": 20,\n" +
            "\"categoryPath\": \"/content/B2C/en_US/marketing-catalog/pe/face-stylers-and-grooming-kits/oneblade\",\n" +
            "\"descriptor\": \"Face\",\n" +
            "\"domain\": \"https://www.usa.philips.com\",\n" +
            "\"versions\": [\n" +
            "\"Trim, edge, shave\",\n" +
            "\"For any length of hair\",\n" +
            "\"3 x click-on stubble combs\",\n" +
            "\"Rechargeable, wet & dry use\"\n" +
            "],\n" +
            "\"productStatus\": \"SUPPORT\",\n" +
            "\"SEOProductName\": \"norelco-oneblade-face\",\n" +
            "\"imageURL\": \"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-IMS-en_US\",\n" +
            "\"sop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2020-01-11T00:00:00.000+01:00\",\n" +
            "\"isDeleted\": false,\n" +
            "\"priority\": 396793,\n" +
            "\"price\": {\n" +
            "\"productPrice\": \"34.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"34.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$34.95\",\n" +
            "\"formattedDisplayPrice\": \"\$34.95\"\n" +
            "},\n" +
            "\"reviewStatistics\": {\n" +
            "\"averageOverallRating\": 4.4716,\n" +
            "\"totalReviewCount\": 774\n" +
            "},\n" +
            "\"keyAwards\": [\n" +
            "\"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-KA1-en_US-001\",\n" +
            "\"https://images.philips.com/is/image/PhilipsConsumer/QP2520_70-KA2-en_US-001\"\n" +
            "],\n" +
            "\"wow\": \"Trim, edge and shave any length of hair\",\n" +
            "\"catalogs\": [\n" +
            "{\n" +
            "\"catalogId\": \"SHOPEMP\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"SHOPPUB\",\n" +
            "\"status\": \"DELETED\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CONSUMER\",\n" +
            "\"status\": \"SUPPORT\",\n" +
            "\"sop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2020-01-11T00:00:00.000+01:00\",\n" +
            "\"priority\": 396793,\n" +
            "\"rank\": 1,\n" +
            "\"isDeleted\": false,\n" +
            "\"price\": [\n" +
            "{\n" +
            "\"productPrice\": \"34.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"34.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$34.95\",\n" +
            "\"formattedDisplayPrice\": \"\$34.95\"\n" +
            "}\n" +
            "]\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CARE\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2040-01-11T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"isDeleted\": false\n" +
            "}\n" +
            "],\n" +
            "\"rank\": 1,\n" +
            "\"showOnlySupport\": false,\n" +
            "\"subWOW\": \"Designed to cut hair, not skin\",\n" +
            "\"marketingTextHeader\": \"The Philips Norelco OneBlade is a revolutionary new hybrid styler that can trim, shave and create clean lines and edges, on any length of hair. Forget about using multiple steps and tools. OneBlade does it all.\",\n" +
            "\"productType\": \"Key\",\n" +
            "\"careSop\": \"2016-02-11T00:00:00.000+01:00\",\n" +
            "\"filterKeys\": [\n" +
            "\"FACIAL_STYLERS_AND_GROOMING_KITS_CA\",\n" +
            "\"FK_CLEA_PEC\",\n" +
            "\"FK_REF_PEC\",\n" +
            "\"KITS_ONEBLADE_SU\",\n" +
            "\"ONEBLADE_FACE_STYLE_SHAVE_CA\",\n" +
            "\"ONEBLADE_FACE_STYLE_SHAVE_SU\",\n" +
            "\"PERSONAL_CARE_GR\"\n" +
            "],\n" +
            "\"subcategory\": \"KITS_ONEBLADE_SU\",\n" +
            "\"gtin\": \"00075020051837\",\n" +
            "\"accessory\": false\n" +
            "},\n" +
            "{\n" +
            "\"locale\": \"en_US\",\n" +
            "\"ctn\": \"HD9630/96\",\n" +
            "\"dtn\": \"HD9630/96\",\n" +
            "\"leafletUrl\": \"https://www.download.p4c.philips.com/files/h/hd9630_96/hd9630_96_pss_aenus.pdf\",\n" +
            "\"productTitle\": \"Viva Collection Airfryer XXL with Twin TurboStar Technology\",\n" +
            "\"alphanumeric\": \"HD9630/96\",\n" +
            "\"brandName\": \"Philips\",\n" +
            "\"brand\": {\n" +
            "\"brandLogo\": \"https://images.philips.com/is/image/PhilipsConsumer/PHI-BRP-global-001\"\n" +
            "},\n" +
            "\"familyName\": \"Viva Collection\",\n" +
            "\"productURL\": \"/c-p/HD9630_96/viva-collection-airfryer-xxl-with-twin-turbostar-technology\",\n" +
            "\"productPagePath\": \"/content/B2C/en_US/product-catalog/HD9630_96\",\n" +
            "\"grpCode\": \"HOUSEHOLD_PRODUCTS_GR\",\n" +
            "\"grpNme\": \"Household products\",\n" +
            "\"catCode\": \"COOKING_CA\",\n" +
            "\"catNme\": \"Cooking\",\n" +
            "\"subcatCode\": \"AIRFRYER_SU\",\n" +
            "\"subcategoryName\": \"Airfryer\",\n" +
            "\"subCatRank\": 10,\n" +
            "\"categoryPath\": \"/content/B2C/en_US/marketing-catalog/ho/cooking/airfryer\",\n" +
            "\"descriptor\": \"Airfryer XXL with Twin TurboStar Technology\",\n" +
            "\"domain\": \"https://www.usa.philips.com\",\n" +
            "\"versions\": [\n" +
            "\"VIVA Collection\",\n" +
            "\"Twin TurboStar\",\n" +
            "\"Rapid Air Technology\",\n" +
            "\"Black\"\n" +
            "],\n" +
            "\"productStatus\": \"NORMAL\",\n" +
            "\"SEOProductName\": \"viva-collection-airfryer-xxl-with-twin-turbostar-technology\",\n" +
            "\"imageURL\": \"https://images.philips.com/is/image/PhilipsConsumer/HD9630_96-IMS-en_US\",\n" +
            "\"sop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2022-03-02T00:00:00.000+01:00\",\n" +
            "\"isDeleted\": false,\n" +
            "\"priority\": 211855,\n" +
            "\"price\": {\n" +
            "\"productPrice\": \"299.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"299.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$299.95\",\n" +
            "\"formattedDisplayPrice\": \"\$299.95\"\n" +
            "},\n" +
            "\"reviewStatistics\": {\n" +
            "\"averageOverallRating\": 4,\n" +
            "\"totalReviewCount\": 13\n" +
            "},\n" +
            "\"wow\": \"A healthier way to fry\",\n" +
            "\"catalogs\": [\n" +
            "{\n" +
            "\"catalogId\": \"SHOPEMP\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"SHOPPUB\",\n" +
            "\"status\": \"DELETED\",\n" +
            "\"sop\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2000-01-01T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2100-01-01T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"visibility\": true,\n" +
            "\"clearance\": false,\n" +
            "\"isDeleted\": false\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CONSUMER\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2022-03-02T00:00:00.000+01:00\",\n" +
            "\"priority\": 211855,\n" +
            "\"rank\": 50,\n" +
            "\"isDeleted\": false,\n" +
            "\"price\": [\n" +
            "{\n" +
            "\"productPrice\": \"299.95\",\n" +
            "\"displayPriceType\": \"srp\",\n" +
            "\"displayPrice\": \"299.95\",\n" +
            "\"currencyCode\": \"USD\",\n" +
            "\"formattedPrice\": \"\$299.95\",\n" +
            "\"formattedDisplayPrice\": \"\$299.95\"\n" +
            "}\n" +
            "]\n" +
            "},\n" +
            "{\n" +
            "\"catalogId\": \"CARE\",\n" +
            "\"status\": \"NORMAL\",\n" +
            "\"sop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"somp\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"eop\": \"2042-03-02T00:00:00.000+01:00\",\n" +
            "\"priority\": 0,\n" +
            "\"rank\": 50,\n" +
            "\"isDeleted\": false\n" +
            "}\n" +
            "],\n" +
            "\"rank\": 50,\n" +
            "\"showOnlySupport\": false,\n" +
            "\"subWOW\": \"Enjoy great tasting fried food with minimal fat\",\n" +
            "\"marketingTextHeader\": \"Uses hot air to fry your favorite foods. Fried food with up to 90% less fat. XXL 3lb/4qt capacity (6 portions). Instantly hot and ready to go in seconds. Includes a QuickClean basket\",\n" +
            "\"productType\": \"MCI\",\n" +
            "\"careSop\": \"2018-02-02T00:00:00.000+01:00\",\n" +
            "\"filterKeys\": [\n" +
            "\"AIRFRYER_SU\",\n" +
            "\"AR_AIRFRYER\",\n" +
            "\"COOKING_CA\",\n" +
            "\"FK_AIRFRYER\",\n" +
            "\"FK_CLEA_HOU\",\n" +
            "\"FK_CLEA_HOUSEHOLD\",\n" +
            "\"FK_REF_HOU\",\n" +
            "\"FK_REF_HOUSEHOLD\",\n" +
            "\"HOUSEHOLD_PRODUCTS_GR\"\n" +
            "],\n" +
            "\"subcategory\": \"AIRFRYER_SU\",\n" +
            "\"gtin\": \"00075020072450\",\n" +
            "\"accessory\": false\n" +
            "}\n" +
            "],\n" +
            "\"invalidCtns\": [\n" +
            "\"\"\n" +
            "]\n" +
            "}"


}