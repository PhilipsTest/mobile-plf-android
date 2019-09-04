/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.ecs.store;

import com.philips.cdp.di.ecs.integration.OAuthInput;

/**
 * Contains generic URL required to query from Server.
 * It should be refactored again to minimize the apis, as these apis are server dependant and
 * it's not scalable to add all the apis in single config interface.
 */
public interface URLProvider {

    //OAuth
    String getOauthUrl(OAuthInput oAuthInput);


    void refreshLoginSession();

    //Product
    String getProductCatalogUrl(int currentPage, int pageSize);

    String getProduct(String ctnNumber);

    String getSearchProductUrl(String ctnNumber);

    String getUpdateProductUrl(String productID);

    //Carts
    String getCartsUrl();

    String getCurrentCartUrl();

    String getCreateCartUrl();

    String getDeleteCartUrl();

    String getAddToCartUrl();

    //Address
    String getRegionsUrl();

    String getUserUrl();

    String getAddressesUrl();

    String getEditAddressUrl(String addressID);

    String getSetDeliveryAddressUrl();

    //Delivery mode
    String getDeliveryModesUrl();

    String getSetDeliveryModeUrl();

    //Payment
    String getPaymentDetailsUrl();

    String getSetPaymentDetailsUrl();

    String getMakePaymentUrl(String id);

    String getPlaceOrderUrl();

    //Orders
    String getOrderHistoryUrl(String pageNumber);

    String getOrderDetailUrl(String orderID);

    String getPhoneContactUrl(String category);


    //Vouchers
    String getApplyVoucherUrl();

    String getDeleteVoucherUrl(String voucherId);

    String getAppliedVoucherUrl();

}
