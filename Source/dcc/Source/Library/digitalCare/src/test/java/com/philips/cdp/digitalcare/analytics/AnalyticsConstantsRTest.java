package com.philips.cdp.digitalcare.analytics;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by philips on 7/15/17.
 */

public class AnalyticsConstantsRTest extends TestCase {


    @Test
    public void testConstants() {
        new AnalyticsConstants();

        assertEquals("Value is Same", "digitalcare:home", AnalyticsConstants.PAGE_HOME);
        assertEquals("Value is Same", "digitalcare:contactus", AnalyticsConstants.PAGE_CONTACT_US);
        assertEquals("Value is Same", "digitalcare:ratethisapp", AnalyticsConstants.PAGE_RATE_THIS_APP);
        assertEquals("Value is Same", "digitalcare:findphilips", AnalyticsConstants.PAGE_FIND_PHILIPS_NEAR_YOU);
        assertEquals("Value is Same", "digitalcare:faq", AnalyticsConstants.PAGE_FAQ);
        assertEquals("Value is Same", "digitalcare:faq:questionandanswer", AnalyticsConstants.PAGE_FAQ_QUESTION_ANSWER);

        assertEquals("Value is Same", "digitalcare:contactus:twitter", AnalyticsConstants.PAGE_CONTACTUS_TWITTER);
        assertEquals("Value is Same", "digitalcare:contactus:facebook", AnalyticsConstants.PAGE_CONTACTUS_FACEBOOK);
        assertEquals("Value is Same", "digitalcare:contactus:livechat:chatnow", AnalyticsConstants.PAGE_CONTACTUS_CHATNOW);
        assertEquals("Value is Same", "digitalcare:contactus:livechat", AnalyticsConstants.PAGE_CONTACTUS_LIVECHAT);
        assertEquals("Value is Same", "digitalcare:contactus:email", AnalyticsConstants.PAGE_CONTACTUS_EMAIL);
        assertEquals("Value is Same", "digitalcare:ratethisapp:writeReview", AnalyticsConstants.PAGE_REVIEW_WRITING);

        assertEquals("Value is Same", "digitalcare:productdetails", AnalyticsConstants.PAGE_VIEW_PRODUCT_DETAILS);
        assertEquals("Value is Same", "digitalcare:productdetails:website", AnalyticsConstants.PAGE_VIEW_PRODUCT_WEBSITE);
        assertEquals("Value is Same", "digitalcare:productdetails:manual", AnalyticsConstants.PAGE_VIEW_PRODUCT_MANUAL);
        assertEquals("Value is Same", "digitalcare:findphilips:servicelocator", AnalyticsConstants.PAGE_SERVICE_LOCATOR);

//        assertEquals("Value is Same", "setError", AnalyticsConstants.ACTION_SET_ERROR);
        assertEquals("Value is Same", "exit link", AnalyticsConstants.ACTION_EXIT_LINK);

        assertEquals("Value is Same", "serviceRequest", AnalyticsConstants.ACTION_SERVICE_REQUEST);
        assertEquals("Value is Same", "sendData", AnalyticsConstants.ACTION_SEND_DATA);

//        assertEquals("Value is Same", "technicalError", AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR);
        assertEquals("Value is Same", "socialType", AnalyticsConstants.ACTION_KEY_SOCIAL_TYPE);
        assertEquals("Value is Same", "serviceChannel", AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL);
        assertEquals("Value is Same", "url", AnalyticsConstants.ACTION_KEY_URL);
        assertEquals("Value is Same", "exitLinkName", AnalyticsConstants.ACTION_KEY_EXIT_LINK);

        assertEquals("Value is Same", "searchTerm", AnalyticsConstants.ACTION_KEY_LOCATE_PHILIPS_SEARCH_TERM);
        assertEquals("Value is Same", "numberOfSearchResults", AnalyticsConstants.ACTION_KEY_LOCATE_PHILIPS_SEARCH_RESULTS);
        assertEquals("Value is Same", "locationView", AnalyticsConstants.ACTION_KEY_LOCATE_PHILIPS_LOCATION_VIEW);

        assertEquals("Value is Same", "videoStart", AnalyticsConstants.ACTION_KEY_VIEW_PRODUCT_VIDEO_START);
        assertEquals("Value is Same", "videoName", AnalyticsConstants.ACTION_KEY_VIEW_PRODUCT_VIDEO_NAME);

        assertEquals("Value is Same", "Facebook", AnalyticsConstants.ACTION_VALUE_FACEBOOK);
        assertEquals("Value is Same", "getLocationDirections", AnalyticsConstants.ACTION_VALUE_LOCATE_PHILIPS_SEND_GET_DIRECTIONS);
        assertEquals("Value is Same", "callLocation", AnalyticsConstants.ACTION_VALUE_LOCATE_PHILIPS_CALL_LOCATION);

        assertEquals("Value is Same", "Error connecting to network", AnalyticsConstants.ACTION_VALUE_TECHNICAL_ERROR_NETWORK_CONNECITON);
        assertEquals("Value is Same", "Error response from CDLS server", AnalyticsConstants.ACTION_VALUE_TECHNICAL_ERROR_RESPONSE_CDLS);

        assertEquals("Value is Same", "chat", AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_CHAT);
        assertEquals("Value is Same", "email", AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_EMAIL);
        assertEquals("Value is Same", "call", AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_CALL);
        assertEquals("Value is Same", "Twitter", AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_TWITTER);
        assertEquals("Value is Same", "FAQ", AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_FAQ);
        assertEquals("Value is Same", "Facebook", AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_Facebook);

        assertEquals("Value is Same", "dcc", AnalyticsConstants.COMPONENT_NAME_CC);






    }
}
