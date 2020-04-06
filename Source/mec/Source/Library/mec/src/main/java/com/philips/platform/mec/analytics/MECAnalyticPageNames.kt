/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.analytics

object MECAnalyticPageNames {
    val productCataloguePage              = "IAP_01_01_product_list"
    val productDetailsPage                = "IAP_02_01_a_product_details"
    val retailerListPage                  = "IAP_02_08_view_retailers"

    // new page names
    val shoppingCartPage                    = "iap_03_02_a_shopping_cart"
    val createShippingAddressPage           = "iap_04_01_create_shipping_address"
    val editShippingAddressPage             = "iap_06_02_b_edit_shipping_address"
    val deliveryDetailPage                  = "iap_04_02_delivery"
    val addressSelectionPage                = "iap_shipping_address_selection" // Not mentioned in design spec
    val createBillingAddressPage            = "iap_06_01_b_create_billing_address"
    val editBillingAddressPage              = "iap_06_01_b_edit_billing_address"
    val orderSummaryPage                    = "iap_04_04_order_summary"
    val cvvPage                             = "iap_cvv"   // Not mentioned in design spec
    val paymentPage                         = "iap_worldpay_payment" // Not mentioned in design spec
    val oredrConfirmationPage               = "iap_07_01_order_confirmation"

}