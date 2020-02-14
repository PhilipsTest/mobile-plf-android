/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.mec.utils

import com.philips.cdp.di.mec.R

object MECConstant {

    val KEY_IS_HYBRIS: String? ="KEY_IS_HYBRIS"
    val KEY_ECS_ADDRESS: String? = "KEY_ECS_ADDRESS"
    val THRESHOLD = 5
    val FLOW_INPUT: String? ="FLOW_INPUT"
    val KEY_FLOW_CONFIGURATION: String? ="KEY_FLOW_CONFIGURATION"
    val DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight
    val IAP_KEY_ACTIVITY_THEME: String? = "IAP_KEY_ACTIVITY_THEME"
    val COMPONENT_NAME: String = "mec"
    const val SELECTED_RETAILER: String ="SELECTED_RETAILER"
    const val RETAILER_REQUEST_CODE: Int = 5000
    val KEY_BAZAAR_LOCALE: String? ="Locale"
    //Keys
    val MEC_KEY_PRODUCT ="mec_key_product"
    val MEC_PRODUCT     ="mec_product"
    val MEC_KEY_RETAILERS ="mec_key_product"
    val MEC_PRODUCT_CTN ="mec_product_ctn"
    val MEC_PRIVACY_URL = "MEC_PRIVACY_URL";
    val MEC_SHOPPING_CART = "MEC_SHOPPING_CART";
    val MEC_BUY_URL = "MEC_BUY_URL";
    val MEC_IS_PHILIPS_SHOP = "MEC_IS_PHILIPS_SHOP";
    val MEC_STORE_NAME = "MEC_STORE_NAME";
    val MEC_CLICK_LISTENER = "MEC_CLICK_LISTENER";
    internal val IN_STOCK = "inStock"
    internal val LOW_STOCK = "lowStock"

    //Theme
    val MEC_KEY_ACTIVITY_THEME = "mec_KEY_ACTIVITY_THEME"

    //Error code string constants
    val MEC_SUCCESS = 0
    val MEC_ERROR = -1
    val MEC_ERROR_NO_CONNECTION = 2
    val MEC_ERROR_CONNECTION_TIME_OUT = 3
    val MEC_ERROR_AUTHENTICATION_FAILURE = 4
    val MEC_ERROR_SERVER_ERROR = 5
    val MEC_ERROR_INSUFFICIENT_STOCK_ERROR = 6
    val MEC_ERROR_UNKNOWN = 7
    val MEC_ERROR_INVALID_CTN = 8


    val NEW_LINE_ESCAPE_CHARACTER = "\n"
    val MEC_LANDING_SCREEN = "MEC_LANDING_SCREEN"
    val MEC_PRODUCT_CTN_NUMBER_FROM_VERTICAL = "MEC_PRODUCT_CTN_NUMBER_FROM_VERTICAL"
    val CATEGORISED_PRODUCT_CTNS = "CATEGORISED_PRODUCT_CTNS"


    val SINGLE_BUTTON_DIALOG_TITLE = "SINGLE_BUTTON_DIALOG_TITLE"
    val SINGLE_BUTTON_DIALOG_DESCRIPTION = "SINGLE_BUTTON_DIALOG_DESCRIPTION"
    val SINGLE_BUTTON_DIALOG_TEXT = "SINGLE_BUTTON_DIALOG_TEXT"

    val MEC_IGNORE_RETAILER_LIST = "MEC_IGNORE_RETAILER_LIST"
    val PHILIPS_EXIT_LINK_PARAMETER = "15_global_%s_%s-app_%s-app"

    val HTTP_REDIRECT = 307

}