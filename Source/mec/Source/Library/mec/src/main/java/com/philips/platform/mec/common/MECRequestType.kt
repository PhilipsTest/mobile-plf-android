/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.common

enum class MECRequestType(val category: String) {
    MEC_APP_ERROR("appError"),
    MEC_CONFIGURE_ECS(""),
    MEC_CONFIGURE_ECS_TO_GET_CONFIGURATION("configureECSWithConfiguration"),
    MEC_HYBRIS_AUTH("hybrisOAuthAuthenticationWith"),
    MEC_HYBRIS_REFRESH("hybrisRefreshOAuthWith"),
    MEC_GET_CATALOG(""),
    MEC_FETCH_PRODUCTS("fetchProducts"),
    MEC_FETCH_PRODUCT_SUMMARIES("fetchProductSummariesForCTNList"),
    MEC_FETCH_PRODUCT_DETAILS_FOR_CTN("fetchProductForCTN"),
    MEC_FETCH_PRODUCT_DETAILS("fetchProductDetailsForProduct"),
    MEC_FETCH_SHOPPING_CART("fetchShoppingCart"),
    MEC_ADD_PRODUCT_TO_SHOPPING_CART("addProductToShoppingCart"),
    MEC_CREATE_SHOPPING_CART("createShoppingCart"),
    MEC_UPDATE_SHOPPING_CART("updateShoppingCart"),
    MEC_APPLY_VOUCHER("applyVoucher"),
    MEC_APPLY_VOUCHER_SILENT("applyVoucher"),
    MEC_GET_APPLIED_VOUCHERS(""),
    MEC_REMOVE_VOUCHER("removeVoucher"),
    MEC_FETCH_DELIVERY_MODES("fetchDeliveryModes"),
    MEC_SET_DELIVERY_MODE("setDeliveryMode"),
    MEC_FETCH_REGIONS("fetchRegionsFor"),
    MEC_FETCH_SAVED_ADDRESSES("fetchSavedAddresses"),
    MEC_CREATE_ADDRESS("createAddress"),
    MEC_CREATE_AND_FETCH_ADDRESS("createAddress"),
    MEC_UPDATE_ADDRESS("updateAddressWith"),
    MEC_UPDATE_AND_FETCH_ADDRESS("updateAddressWith"),
    MEC_SET_DELIVERY_ADDRESS("setDeliveryAddress"),
    MEC_SET_AND_FETCH_DELIVERY_ADDRESS(""),
    MEC_DELETE_ADDRESS("deleteAddress"),
    MEC_DELETE_AND_FETCH_ADDRESS("deleteAddress"),
    MEC_FETCH_PAYMENT_DETAILS("fetchPaymentDetails"),
    MEC_SET_PAYMENT_DETAILS("setPaymentDetail"),
    MEC_MAKE_PAYMENT("makePaymentFor"),
    MEC_SUBMIT_ORDER("submitOrder"),
    MEC_FETCH_RETAILER_FOR_CTN("fetchRetailerDetailsForCTN"),
    MEC_FETCH_RETAILER_FOR_PRODUCT(""),
    MEC_FETCH_ORDER_HISTORY(""),
    MEC_FETCH_ORDER_DETAILS_FOR_ORDER_ID(""),
    MEC_FETCH_ORDER_DETAILS_FOR_ORDER_DETAIL(""),
    MEC_FETCH_ORDER_DETAILS_FOR_ORDERS(""),
    MEC_FETCH_USER_PROFILE("fetchUserProfile"),
    MEC_SET_PROPOSITION_ID(""),
    MEC_SET_VOLLEY_TIMEOUT_AND_RETRY_COUNT("setVolleyTimeout"),
    MEC_FETCH_REVIEW("fetchAllReviewsForCTN"),
    MEC_FETCH__BULK_RATING("fetchBulkRatingsForCTNList"),
    MEC_FETCH_SPECIFICATION("fetchProductSpecsFor"),
    MEC_FETCH_FEATURE("fetchProductFeaturesFor"),;

}