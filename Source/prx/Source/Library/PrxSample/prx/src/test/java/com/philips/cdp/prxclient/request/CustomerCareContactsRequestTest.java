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

package com.philips.cdp.prxclient.request;

import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CustomerCareContactsRequestTest {

        private static final String TAG = CustomerCareContactsRequestTest.class.getSimpleName();

        private PrxRequest mCustomerCareContactsRequest = null;

        @Before
        public void setUp() throws Exception {
            mCustomerCareContactsRequest = new CustomerCareContactsRequest("AIRFRYER_SU");

        }

        @Test
        public void testPrxBuilderObjectWithOneParameter() {
            mCustomerCareContactsRequest = new CustomerCareContactsRequest("AIRFRYER_SU");
            assertNotNull(mCustomerCareContactsRequest);
        }

    @Test
    public void testPrxBuilderObjectWithAllParameter() {
        mCustomerCareContactsRequest = new CustomerCareContactsRequest("AIRFRYER_SU", PrxConstants.Sector.B2C, PrxConstants.Catalog.CARE,"REQUEST_TAG");
        assertNotNull(mCustomerCareContactsRequest);
    }

        private String getCustomerCareResponse() {
            String str = "{\n" +
                    "\"data\": {\n" +
                    "\"phone\": [\n" +
                    "{\n" +
                    "\"phoneNumber\": \"1-866-309-8817\",\n" +
                    "\"openingHoursWeekdays\": \"Monday – Saturday: 9:00 AM – 9:00 PM ET\",\n" +
                    "\"openingHoursSaturday\": \"Sunday: 9:00 am – 6:00 PM ET\",\n" +
                    "\"openingHoursSunday\": \"Excluding Major Holidays\"\n" +
                    "}\n" +
                    "],\n" +
                    "\"chat\": [\n" +
                    "{\n" +
                    "\"content\": \"<script id=\\\"salesforce_chat_script\\\">\\n\\n(function(){\\n    var extScript = document.createElement('script');\\n\\textScript.src = \\\"https://c.la1-c2-par.salesforceliveagent.com/content/g/js/43.0/deployment.js\\\";\\n    extScript.onload = function () {\\n        liveAgentDeployment = false; \\n\\t    liveagent.init(\\\"https://d.la1-c2-par.salesforceliveagent.com/chat\\\", \\\"5722X000000fxT4\\\", \\\"00Dw0000000Cp29\\\");\\n\\t    if (!window._laq) { \\n\\t        window._laq = []; \\n\\t    } \\n\\t    window._laq.push(function(){\\n\\t        liveagent.showWhenOnline(\\\"5732X000000fxTJ\\\", document.getElementById(\\\"liveagent_button_online_5732X000000fxTJ\\\"));\\n\\t        liveagent.showWhenOffline(\\\"5732X000000fxTJ\\\", document.getElementById(\\\"liveagent_button_offline_5732X000000fxTJ\\\"));\\n\\t    });\\n    };\\n\\n    var thisScript = document.getElementById('salesforce_chat_script');\\n\\tthisScript.parentNode.insertBefore(extScript, thisScript.nextSibling); \\n})();\\n</script>\\n\\n\\n<a id=\\\"liveagent_button_online_5732X000000fxTJ\\\" style=\\\"display: none; border: 0px none; cursor: pointer\\\" onclick=\\\"liveagent.startChat('5732X000000fxTJ')\\\" >Chat now</a><span id=\\\"liveagent_button_offline_5732X000000fxTJ\\\" style=\\\"display: none;\\\">Chat offline</span>\"\n" +
                    "}\n" +
                    "]\n" +
                    "},\n" +
                    "\"success\": true\n" +
                    "}";
            return str;
        }

        @Test
        public void testCustomerCareResponseResponseObject() {
            try {
                JSONObject mJsonObject = new JSONObject(getCustomerCareResponse());
                assertNotNull(mJsonObject);
                ResponseData mResponseData = mCustomerCareContactsRequest.getResponseData(mJsonObject);
                assertNotNull(mResponseData);
            } catch (JSONException e) {
                fail();

            } catch (Exception e) {
                fail();
            }
        }

    @Test
    public void testReplaceURL() {
        assertNotNull(mCustomerCareContactsRequest.getReplaceURLMap());
        assertEquals(3,mCustomerCareContactsRequest.getReplaceURLMap().size());
    }
}