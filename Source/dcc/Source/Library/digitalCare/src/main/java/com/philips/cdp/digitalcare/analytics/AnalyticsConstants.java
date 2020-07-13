/**
 * AnalyticsConstants is holding constant members used for TAGGING, Adobe
 * Analytics.
 * <p>
 * = author: ritesh.jha= philips.com = since: Mar 26, 2015
 *
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.analytics;


public class AnalyticsConstants {

    /* PAGE CONSTANTS */
    public static final String PAGE_HOME = "digitalcare:home";
    public static final String PAGE_CONTACT_US = "digitalcare:contactus";
    public static final String PAGE_RATE_THIS_APP = "digitalcare:ratethisapp";
    public static final String PAGE_FIND_PHILIPS_NEAR_YOU = "digitalcare:findphilips";
    public static final String PAGE_FAQ = "digitalcare:faq";
    public static final String PAGE_FAQ_QUESTION_ANSWER = "digitalcare:faq:questionandanswer";

    public static final String PAGE_CONTACTUS_TWITTER = "digitalcare:contactus:twitter";
    public static final String PAGE_CONTACTUS_FACEBOOK = "digitalcare:contactus:facebook";
    public static final String PAGE_CONTACTUS_CHATNOW = "digitalcare:contactus:livechat:chatnow";
    public static final String PAGE_CONTACTUS_LIVECHAT = "digitalcare:contactus:livechat";
    public static final String PAGE_CONTACTUS_EMAIL = "digitalcare:contactus:email";
    public static final String PAGE_REVIEW_WRITING = "digitalcare:ratethisapp:writeReview";

    public static final String PAGE_VIEW_PRODUCT_DETAILS = "digitalcare:productdetails";
    public static final String PAGE_VIEW_PRODUCT_WEBSITE = "digitalcare:productdetails:website";
    public static final String PAGE_VIEW_PRODUCT_MANUAL = "digitalcare:productdetails:manual";
    public static final String PAGE_SERVICE_LOCATOR = "digitalcare:findphilips:servicelocator";

    /***************** Page Context Data End **************/

    /*****************
     * Action Names
     **************/
//    public static final String ACTION_SET_ERROR = "setError";
    public static final String ACTION_EXIT_LINK = "exit link";
    public static final String ACTION_SERVICE_REQUEST = "serviceRequest";
    /* ACTION KEY FOR LOCATE PHILIPS */
    public static final String ACTION_SEND_DATA = "sendData";

    /*****************
     * Action Keys
     **************/
//    public static final String ACTION_KEY_TECHNICAL_ERROR = "technicalError";
    
    public static final String ACTION_KEY_SOCIAL_TYPE = "socialType";
    public static final String ACTION_KEY_SERVICE_CHANNEL = "serviceChannel";
    public static final String ACTION_KEY_URL = "url";
    // confirmed.
    public static final String ACTION_KEY_EXIT_LINK = "exitLinkName";

    /* Locate Near To You */
    public static final String ACTION_KEY_LOCATE_PHILIPS_SEARCH_TERM = "searchTerm";
    public static final String ACTION_KEY_LOCATE_PHILIPS_SEARCH_RESULTS = "numberOfSearchResults";
    public static final String ACTION_KEY_LOCATE_PHILIPS_LOCATION_VIEW = "locationView";

    /*  Action keys View Product Details*/
    public static final String ACTION_KEY_VIEW_PRODUCT_VIDEO_START = "videoStart";
    public static final String ACTION_KEY_VIEW_PRODUCT_VIDEO_NAME = "videoName";
    public static final String ACTION_KEY_SPECIAL_EVENTS = "specialEvents";

    /*****************
     * Action Values
     **************/
    public static final String ACTION_VALUE_FACEBOOK = "Facebook";
    public static final String ACTION_VALUE_LOCATE_PHILIPS_SEND_GET_DIRECTIONS = "getLocationDirections";
    public static final String ACTION_VALUE_LOCATE_PHILIPS_CALL_LOCATION = "callLocation";
    public static final String ACTION_VALUE_WRITE_PRODUCT_REVIEW = "writeProductReview";
    public static final String ACTION_VALUE_RATE_THIS_APP = "rateThisApp";

    /* Error text */
    public static final String ACTION_VALUE_TECHNICAL_ERROR_NETWORK_CONNECITON = "Error connecting to network";
    public static final String ACTION_VALUE_TECHNICAL_ERROR_RESPONSE_CDLS = "Error response from CDLS server";

    /* Service Channel */
    public static final String ACTION_VALUE_SERVICE_CHANNEL_CHAT = "chat";
    public static final String ACTION_VALUE_SERVICE_CHANNEL_EMAIL = "email";
    public static final String ACTION_VALUE_SERVICE_CHANNEL_CALL = "call";
    public static final String ACTION_VALUE_SERVICE_CHANNEL_TWITTER = "Twitter";
    public static final String ACTION_VALUE_SERVICE_CHANNEL_FAQ = "FAQ";
    public static final String ACTION_VALUE_SERVICE_CHANNEL_Facebook = "Facebook";
	
	public static final String COMPONENT_NAME_CC = "dcc";
}
