package com.philips.cdp.prxclient.request;

import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ProductSpecificationRequestTest {
    private static final String TAG = ProductSpecificationRequestTest.class.getSimpleName();

    private PrxRequest mProductSpecificationRequest = null;

    @Before
    public void setUp() throws Exception {
        mProductSpecificationRequest = new ProductSpecificationRequest("HX3631/06", null, null, null);

    }

    @Test
    public void testPrxBuilderObjectWithQueueParameter() {
        mProductSpecificationRequest = new ProductSpecificationRequest("HX3631/06", null, null, "TAGINFO");
        assertNotNull(mProductSpecificationRequest);
    }

    @Test
    public void testSpecificationResponseObject() {
        try {
            JSONObject mJsonObject = new JSONObject(getSpecificationResponse());
            assertNotNull(mJsonObject);
            ResponseData mResponseData = mProductSpecificationRequest.getResponseData(mJsonObject);
            assertNotNull(mResponseData);
        } catch (JSONException e) {
            fail();

        } catch (Exception e) {
            fail();
        }
    }

   /*
   * This is sample response for disclaimer request:
   * Below url is used to fetch sample response data and can be verified in browser
   * https://www.philips.com/prx/product/B2C/en_CA/CONSUMER/products/HX8331/01.disclaimers
   * */
    private String getSpecificationResponse() {
        String str = "{\n" +
                "\"success\": true,\n" +
                "\"data\": {\n" +
                "\"csChapter\": [\n" +
                "{\n" +
                "\"csChapterCode\": \"CH4006741\",\n" +
                "\"csChapterName\": \"Modes\",\n" +
                "\"csChapterRank\": \"1\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"IT4032871\",\n" +
                "\"csItemName\": \"Clean\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4076686\",\n" +
                "\"csValueName\": \"For exceptional everyday clean\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csChapterCode\": \"CH4002821\",\n" +
                "\"csChapterName\": \"Items included\",\n" +
                "\"csChapterRank\": \"2\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"IT4035144\",\n" +
                "\"csItemName\": \"Handles\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"1\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4081784\",\n" +
                "\"csValueName\": \"1 PowerUp\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015645\",\n" +
                "\"csItemName\": \"Brush heads\",\n" +
                "\"csItemRank\": \"2\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084108\",\n" +
                "\"csValueName\": \"1 PowerUp medium\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015642\",\n" +
                "\"csItemName\": \"Batteries\",\n" +
                "\"csItemRank\": \"3\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4041257\",\n" +
                "\"csValueName\": \"2 AA\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csChapterCode\": \"CHA_0000020\",\n" +
                "\"csChapterName\": \"Design and finishing\",\n" +
                "\"csChapterRank\": \"3\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"4010351\",\n" +
                "\"csItemName\": \"Color\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4063793\",\n" +
                "\"csValueName\": \"Scuba Blue\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csChapterCode\": \"CH4002822\",\n" +
                "\"csChapterName\": \"Cleaning performance\",\n" +
                "\"csChapterRank\": \"4\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015660\",\n" +
                "\"csItemName\": \"Speed\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084109\",\n" +
                "\"csValueName\": \"Up to 15,000 brush strokes/min\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015661\",\n" +
                "\"csItemName\": \"Performance\",\n" +
                "\"csItemRank\": \"2\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084110\",\n" +
                "\"csValueName\": \"Helps remove plaque\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015663\",\n" +
                "\"csItemName\": \"Health benefits\",\n" +
                "\"csItemRank\": \"3\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4031266\",\n" +
                "\"csValueName\": \"Helps improve gum health\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015664\",\n" +
                "\"csItemName\": \"Whitening benefits\",\n" +
                "\"csItemRank\": \"4\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084111\",\n" +
                "\"csValueName\": \"Helps whiten teeth\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015666\",\n" +
                "\"csItemName\": \"Timer\",\n" +
                "\"csItemRank\": \"5\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4031276\",\n" +
                "\"csValueName\": \"SmarTimer\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csChapterCode\": \"CH4001524\",\n" +
                "\"csChapterName\": \"Ease of use\",\n" +
                "\"csChapterRank\": \"5\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015667\",\n" +
                "\"csItemName\": \"Brush head system\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084106\",\n" +
                "\"csValueName\": \"PowerUp brush heads\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4028255\",\n" +
                "\"csItemName\": \"Battery Life\",\n" +
                "\"csItemRank\": \"2\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084112\",\n" +
                "\"csValueName\": \"120 2-minute brushings\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015670\",\n" +
                "\"csItemName\": \"Handle\",\n" +
                "\"csItemRank\": \"3\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084099\",\n" +
                "\"csValueName\": \"Ergonomic design\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "},\n" +
                "{\n" +
                "\"csValueCode\": \"VA4083968\",\n" +
                "\"csValueName\": \"Rubber grip for easy handling\",\n" +
                "\"csValueRank\": \"2\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csChapterCode\": \"3000956\",\n" +
                "\"csChapterName\": \"Technical specifications\",\n" +
                "\"csChapterRank\": \"6\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015617\",\n" +
                "\"csItemName\": \"Battery\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084104\",\n" +
                "\"csValueName\": \"2 AA Battery\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csItemCode\": \"IT4017634\",\n" +
                "\"csItemName\": \"Battery type\",\n" +
                "\"csItemRank\": \"2\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084105\",\n" +
                "\"csValueName\": \"Alkaline\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"csChapterCode\": \"CHA_0000049\",\n" +
                "\"csChapterName\": \"Service\",\n" +
                "\"csChapterRank\": \"7\",\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"csItemCode\": \"IT4015672\",\n" +
                "\"csItemName\": \"Warranty\",\n" +
                "\"csItemRank\": \"1\",\n" +
                "\"csItemIsFreeFormat\": \"0\",\n" +
                "\"csValue\": [\n" +
                "{\n" +
                "\"csValueCode\": \"VA4084103\",\n" +
                "\"csValueName\": \"1-year limited warranty\",\n" +
                "\"csValueRank\": \"1\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "],\n" +
                "\"filters\": {\n" +
                "\"purpose\": [\n" +
                "{\n" +
                "\"type\": \"Comparison\",\n" +
                "\"features\": {\n" +
                "\"feature\": [\n" +
                "{\n" +
                "\"code\": \"F400053852\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Familiar brushing motion - like a manual toothbrush\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400068954\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Helps remove plaque [PowerUp]\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400043847\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Helps reduce cavities\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053855\",\n" +
                "\"rank\": \"4\",\n" +
                "\"referenceName\": \"Helps improve gum health\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400068955\",\n" +
                "\"rank\": \"5\",\n" +
                "\"referenceName\": \"Helps whiten teeth [PowerUp]\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053854\",\n" +
                "\"rank\": \"6\",\n" +
                "\"referenceName\": \"Safe and Gentle\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053851\",\n" +
                "\"rank\": \"7\",\n" +
                "\"referenceName\": \"Smart timer\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053846\",\n" +
                "\"rank\": \"8\",\n" +
                "\"referenceName\": \"PowerUp Sonic Technology\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "\"csItems\": {\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"code\": \"4010351\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Design and finishing - Color\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015617\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Technical specifications - Battery\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015660\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Cleaning performance - Speed\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015667\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Ease of use - Brush head system\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015672\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Service - Warranty\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4032871\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Modes - Clean\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4035144\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Items included - Handles\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015645\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Items included - Brush heads\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015661\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Cleaning performance - Performance\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4017634\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Technical specifications - Battery type\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4028255\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Ease of use - Battery Life\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015642\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Items included - Batteries\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015663\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Cleaning performance - Health benefits\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015670\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Ease of use - Handle\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015664\",\n" +
                "\"rank\": \"4\",\n" +
                "\"referenceName\": \"Cleaning performance - Whitening benefits\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015666\",\n" +
                "\"rank\": \"5\",\n" +
                "\"referenceName\": \"Cleaning performance - Timer\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "\"type\": \"Detail\",\n" +
                "\"features\": {\n" +
                "\"feature\": [\n" +
                "{\n" +
                "\"code\": \"F400053852\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Familiar brushing motion - like a manual toothbrush\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400068954\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Helps remove plaque [PowerUp]\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400043847\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Helps reduce cavities\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053855\",\n" +
                "\"rank\": \"4\",\n" +
                "\"referenceName\": \"Helps improve gum health\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400068955\",\n" +
                "\"rank\": \"5\",\n" +
                "\"referenceName\": \"Helps whiten teeth [PowerUp]\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053854\",\n" +
                "\"rank\": \"6\",\n" +
                "\"referenceName\": \"Safe and Gentle\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053851\",\n" +
                "\"rank\": \"7\",\n" +
                "\"referenceName\": \"Smart timer\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"F400053846\",\n" +
                "\"rank\": \"8\",\n" +
                "\"referenceName\": \"PowerUp Sonic Technology\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "\"csItems\": {\n" +
                "\"csItem\": [\n" +
                "{\n" +
                "\"code\": \"4010351\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Design and finishing - Color\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015617\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Technical specifications - Battery\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015660\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Cleaning performance - Speed\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015667\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Ease of use - Brush head system\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015672\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Service - Warranty\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4032871\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Modes - Clean\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4035144\",\n" +
                "\"rank\": \"1\",\n" +
                "\"referenceName\": \"Items included - Handles\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015645\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Items included - Brush heads\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015661\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Cleaning performance - Performance\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4017634\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Technical specifications - Battery type\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4028255\",\n" +
                "\"rank\": \"2\",\n" +
                "\"referenceName\": \"Ease of use - Battery Life\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015642\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Items included - Batteries\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015663\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Cleaning performance - Health benefits\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015670\",\n" +
                "\"rank\": \"3\",\n" +
                "\"referenceName\": \"Ease of use - Handle\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015664\",\n" +
                "\"rank\": \"4\",\n" +
                "\"referenceName\": \"Cleaning performance - Whitening benefits\"\n" +
                "},\n" +
                "{\n" +
                "\"code\": \"IT4015666\",\n" +
                "\"rank\": \"5\",\n" +
                "\"referenceName\": \"Cleaning performance - Timer\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "}\n" +
                "}";
        return str;
    }

}