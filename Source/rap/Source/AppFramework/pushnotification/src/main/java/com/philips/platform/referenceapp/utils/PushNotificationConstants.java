/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.referenceapp.utils;

/**
 * @author Abhishek Gadewar
 *
 * Class to manage push notification related Constants
 */

public class PushNotificationConstants {

        public static final String IS_TOKEN_REGISTERED = "sentTokenToServer";
        public static final String REGISTRATION_COMPLETE = "registrationComplete";
        // only renamed variable not value as it is already there in shared preferences
        public static final String FB_TOKEN="fb_token";
        public static final String APP_VARIANT = "RAP-ANDROID";
        public static final String PUSH_GCMA="Push.Gcma";
        public static final String PUSH_JPUSH="Push.JPush";
        public static final String PLATFORM_KEY ="platform";
        public static final String DSC = "dsc";
        public static final String NEURA_PUSH_TYPE = "pushType";
        public static final String NEURA_EVENT = "nuera_event";

}