/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.utils;

public class IAPConstant {
    //Theme
    public static final String IAP_KEY_ACTIVITY_THEME = "IAP_KEY_ACTIVITY_THEME";

    //Error code string constants
    public static final int IAP_SUCCESS = 0;
    public static final int IAP_ERROR = -1;
    public static final int IAP_ERROR_NO_CONNECTION = 2;
    public static final int IAP_ERROR_CONNECTION_TIME_OUT = 3;
    public static final int IAP_ERROR_AUTHENTICATION_FAILURE = 4;
    public static final int IAP_ERROR_SERVER_ERROR = 5;
    public static final int IAP_ERROR_INSUFFICIENT_STOCK_ERROR = 6;
    public static final int IAP_ERROR_UNKNOWN = 7;
    public static final int IAP_ERROR_INVALID_CTN = 8;

    //Cart
    public static final String IAP_DELETE_PRODUCT = "IAP_DELETE_PRODUCT";
    public static final String IAP_UPDATE_PRODUCT_COUNT = "IAP_UPDATE_PRODUCT_COUNT";
    public static final String IAP_EDIT_DELIVERY_MODE = "IAP_EDIT_DELIVERY_MODE";
    public static final String IAP_DELETE_PRODUCT_CONFIRM = "IAP_DELETE_PRODUCT_CONFIRM";

    //Cart Vouchers
    public static final String IAP_APPLY_VOUCHER = "IAP_APPLY_VOUCHER";
    public static final String IAP_DELETE_VOUCHER = "IAP_DELETE_VOUCHER";
    public static final String IAP_VOUCHER_CODE = "IAP_VOUCHER_CODE";
    public static final String IAP_DELETE_VOUCHER_CONFIRM = "IAP_DELETE_VOUCHER_CONFIRM";
    public static final String IAP_VOUCHER_FROM_APP="IAP_VOUCHER_FROM_APP";

    //Product
    public static final String PRODUCT_TITLE = "PRODUCT_TITLE";
    public static final String PRODUCT_CTN = "PRODUCT_CTN";
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String PRODUCT_VALUE_PRICE = "PRODUCT_VALUE_PRICE";
    public static final String PRODUCT_OVERVIEW = "PRODUCT_OVERVIEW";

    //Fragment
    public static final String PRODUCT_DETAIL_FRAGMENT = "PRODUCT_DETAIL_FRAGMENT ";
    public static final String PRODUCT_DETAIL_FRAGMENT_FROM_ORDER = "PRODUCT_DETAIL_FRAGMENT_FROM_ORDER ";



    //Application constants
    public static final Boolean BUTTON_STATE_CHANGED = false;
    public static final String EMPTY_CART_FRAGMENT_REPLACED = "EMPTY_CART_FRAGMENT_REPLACED";
    public static final String SHOPPING_CART_PRESENTER = "SHOPPING_CART_PRESENTER";
    public static final String IS_SECOND_USER = "IS_SECOND_USER";
    public static final String FROM_PAYMENT_SELECTION = "FROM_PAYMENT_SELECTION";
    public static final String PRODUCT_DETAIL_FRAGMENT_IMAGE_URL = "PRODUCT_DETAIL_FRAGMENT_IMAGE_URL";
    public final static String NEW_LINE_ESCAPE_CHARACTER = "\n";
    public static final String INSUFFICIENT_STOCK_LEVEL_ERROR = "InsufficientStockLevelError";
    public static final String IAP_LANDING_SCREEN = "IAP_LANDING_SCREEN";
    public static final String IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL = "IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL";
    public static final String IS_PRODUCT_CATALOG = "IS_PRODUCT_CATALOG";
    public static final String IAP_LAUNCH_PRODUCT_DETAIL = "IAP_LAUNCH_PRODUCT_DETAIL";
    public static final String IAP_LAUNCH_PRODUCT_CATALOG = "IAP_LAUNCH_PRODUCT_CATALOG";
    public static final String IAP_PRODUCT_DISCOUNTED_PRICE = "IAP_PRODUCT_DISCOUNTED_PRICE";
    public static final String IAP_FRAGMENT_STATE_KEY = "IAP_FRAGMENT_STATE_KEY";
    public static final String IAP_LAUNCH_PRODUCT_CATALOG_ON_ERROR = "IAP_LAUNCH_PRODUCT_CATALOG_ON_ERROR";
    public static final String IAP_BUY_URL = "IAP_BUY_URL";
    public static final String IAP_LAUNCH_PRODUCT_CATALOG_FROM_EMPTY_CART = "IAP_LAUNCH_PRODUCT_CATALOG_FROM_EMPTY_CART";
    public static final String SWITCH_TO_NO_NETWORK_CONNECTION = "SWITCH_TO_NO_NETWORK_CONNECTION";
    public static final String SET_DELIVERY_MODE = "DELIVERY_MODE";
    public static final String IAP_COUNTRY_KEY = "IAP_COUNTRY_KEY";
    public static final String IAP_STORE_NAME = "IAP_STORE_NAME";
    public static final String IAP_RETAILER_INFO = "IAP_RETAILER_INFO";
    public static final String CATEGORISED_PRODUCT_CTNS = "CATEGORISED_PRODUCT_CTNS";
    public static final String IAP_LAUNCH_SHOPPING_CART = "IAP_LAUNCH_SHOPPING_CART";

    //Shipping Address screen
    public static final String UPDATE_SHIPPING_ADDRESS_KEY = "UPDATE_SHIPPING_ADDRESS_KEY";
    public static final String BILLING_ADDRESS_FIELDS = "BILLING_ADDRESS_FIELDS";

    //Order detail
    public static final String DELIVERY_ADDRESS = "DELIVERY_ADDRESS";
    public static final String ORDER_TRACK_URL = "ORDER_TRACK_URL";
    public static final String PURCHASE_HISTORY_DETAIL = "PURCHASE_HISTORY_DETAIL";
    public static final String PURCHASE_ID = "PURCHASE_ID";
    public static final String IAP_ORDER_ID = "IAP_ORDER_ID";
    public static final String ORDER_STATUS = "ORDER_STATUS";
    public static final String TRACKING_ID = "TRACKING_ID";
    public static final String DELIVERY_NAME = "DELIVERY_NAME";
    public static final String ORDER_COMPLETED = "completed";
    public static final String ORDER_PROCESSING = "processing";
    public static final String ORDER_DETAIL = "ORDER_DETAIL";

    //Address selection screen
    public static final String ADDRESS_LIST = "ADDRESS_LIST";
    public static final String ADD_NEW_ADDRESS = "ADD_NEW_ADDRESS";
    public static final String DELIVER_TO_THIS_ADDRESS = "DELIVER_TO_THIS_ADDRESS";
    public static final String ADDRESS_SELECTION_EVENT_EDIT = "event_edit";
    public static final String ADDRESS_SELECTION_EVENT_DELETE = "event_delete";

    //Payment selection screen
    public static final String PAYMENT_METHOD_LIST = "PAYMENT_METHOD_LIST";
    public static final String USE_PAYMENT = "USE_PAYMENT";
    public static final String ADD_NEW_PAYMENT = "ADD_NEW_PAYMENT";
    public static final String SELECTED_PAYMENT = "SELECTED_PAYMENT";
    public static final String ADD_BILLING_ADDRESS = "ADD_BILLING_ADDRESS";

    //Customer care
    public static final String CUSTOMER_CARE_NUMBER = "CUSTOMER_CARE_NUMBER";
    public static final String CUSTOMER_CARE_SATURDAY_TIMING = "CUSTOMER_CARE_SATURDAY_TIMING";
    public static final String CUSTOMER_CARE_WEEKDAYS_TIMING = "CUSTOMER_CARE_WEEKDAYS_TIMING";

    //CVV
    public static final String CVV_KEY_BUNDLE = "CVV_KEY_BUNDLE";

    //Single button dialog string constants
    public static final String SINGLE_BUTTON_DIALOG_TITLE = "SINGLE_BUTTON_DIALOG_TITLE";
    public static final String SINGLE_BUTTON_DIALOG_DESCRIPTION = "SINGLE_BUTTON_DIALOG_DESCRIPTION";
    public static final String SINGLE_BUTTON_DIALOG_TEXT = "SINGLE_BUTTON_DIALOG_TEXT";

    //Two button dialog string constants
    public static final String TWO_BUTTON_DIALOG_TITLE = "TWO_BUTTON_DIALOG_TITLE";
    public static final String TWO_BUTTON_DIALOG_DESCRIPTION = "TWO_BUTTON_DIALOG_DESCRIPTION";
    public static final String TWO_BUTTON_DIALOG_POSITIVE_TEXT = "TWO_BUTTON_DIALOG_POSITIVE_TEXT";
    public static final String TWO_BUTTON_DIALOG_NEGATIVE_TEXT = "TWO_BUTTON_DIALOG_NEGATIVE_TEXT";

    //Service discover key constants
    public static final String IAP_IS_STORE_AVAILABLE = "iap.get.isStoreAvailable";
    public static final String IAP_BASE_URL = "iap.baseUrl";
    public static final String STOCK_LEVEL_STATUS = "STOCK_LEVEL_STATUS";
    public static final String STOCK_LEVEL = "STOCK_LEVEL";
    public static final String PRODUCT_QUANTITY = "PRODUCT_QUANTITY";
    public static final String PRODUCT_STOCK = "PRODUCT_STOCK";
    public static final String SHOPPING_CART_CODE = "SHOPPING_CART_CODE";
    public static final String EMPTY_BILLING_ADDRESS = "EMPTY_BILLING_ADDRESS";
    public static final String ASK_TO_FILL_EMPTY_BILLING_ADDRESS = "ASK_TO_FILL_EMPTY_BILLING_ADDRESS";
    public static final String IAP_SHIPING_ADDRESS = "IAP_SHIPING_ADDRESS";
    public static final String UPDATE_BILLING_ADDRESS_KEY = "UPDATE_BILLING_ADDRESS_KEY";
    public static final String IAP_IGNORE_RETAILER_LIST = "IAP_IGNORE_RETAILER_LIST";

    //For handling 307 - Temporary redirect
    public static final int HTTP_REDIRECT = 307;

    public static final int UN_LIMIT_CART_COUNT = 0;


    public static final String IAP_IS_PHILIPS_SHOP = "IAP_IS_PHILIPS_SHOP";
}