/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */

package com.philips.cdp2.ews.tagging;

public class Tag {

    public static class KEY {
        public static final String IN_APP_NOTIFICATION = "inAppNotification";
        public static final String CONNECTED_PRODUCT_NAME = "connectedProductName";
        public static final String TECHNICAL_ERROR = "technicalError";
        public static final String MACHINE_ID = "machineId";
        public static final String PRODUCT_NAME = "connectedProductName";
        public static final String PRODUCT_MODEL = "productModel";

        public static final String SEND_DATA = "sendData";
        public static final String SPECIAL_EVENTS = "specialEvents";

    }

    public static class VALUE {
        public static final String CONN_ERROR_NOTIFICATION = "Connection unsuccessful:Cannot connect to device's WiFi signal";
    }

    public static class ACTION {

        public static final String GET_STARTED = "getStartedToConnectWiFi";
        public static final String CONFIRM_NETWORK = "connectToExistingNetwork";
        public static final String CHANGE_NETWORK = "changeNetworkToConnect";
        public static final String USER_NEEDS_HELP = "helpMeEnablingSetupMode";
        public static final String WIFI_BLINKING = "wifiBlinking";
        public static final String WIFI_NOT_BLINKING = "wifiNotBlinking";

        public static final String CONNECTION_UNSUCCESSFUL = "connectionUnsuccessful";
        public static final String CONNECTION_SUCCESS = "successConnection";
        public static final String CONNECTION_START = "startConnection";
        public static final String TIME_TO_CONNECT = "timeToConnect";
    }

    public static class ERROR {
        public static final String DEVICE_PORT_ERROR = "EWS:Network:AWSDK:devicePortError";
        public static final String WIFI_PORT_ERROR = "EWS:Network:AWSDK:wifiPortError";

    }
}
