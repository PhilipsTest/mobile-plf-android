/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs;


import android.support.annotation.NonNull;

import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode;
import com.philips.cdp.di.ecs.model.address.ECSUserProfile;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;


import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;
import com.philips.cdp.di.ecs.model.orders.ECSOrders;
import com.philips.cdp.di.ecs.model.payment.ECSPayment;
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider;
import com.philips.cdp.di.ecs.model.products.ECSProducts;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.region.ECSRegion;
import com.philips.cdp.di.ecs.model.config.ECSConfig;
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList;

import com.philips.cdp.di.ecs.model.voucher.ECSVoucher;

import java.util.List;

/**
 * The interface Iap services.
 */
 interface ECSServiceProvider {

    /**
     * Configure ecs.
     * @since 1.0
     * @param ecsCallback the ecs callback containing boolean response. If configuration is success returns true else false
     */
    void configureECS(ECSCallback<Boolean,Exception> ecsCallback);

    /**
     * Configure ecs to get configuration.
     *
     * @param ecsCallback the ecs callback containing ECSConfig object
     */
    void configureECSToGetConfiguration(ECSCallback<ECSConfig, Exception> ecsCallback);

    void hybrisOAthAuthentication(@NonNull ECSOAuthProvider ecsoAuthProvider, @NonNull ECSCallback<ECSOAuthData,Exception> ecsListener);

    /**
     * Hybris refresh o auth.
     *
     * @param oAuthInput  the ECSOAuthProvider object
     * @param ecsListener the ecs listener containing ECSOAuthData object
     */
    void hybrisRefreshOAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsListener);


    /**
     * Fetch Products with summary for hybris flow
     * @param currentPage the current page
     * @param pageSize    the page size
     * @param eCSCallback the ecs callback containing ECSProducts object (a list of ProductsEntity and other fields)
    */
    void fetchProducts(int currentPage, int pageSize, ECSCallback<ECSProducts,Exception> eCSCallback);

    /**
     * Fetch product specific to ctn
     *
     * @param ctn         the ctn
     * @param eCSCallback the ecs callback containing ECSProduct object
     */
    void fetchProduct(String ctn, ECSCallback<ECSProduct,Exception> eCSCallback );


    /**
     * Fetch product details containing assets and disclaimer details
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSProduct object
     */
    void fetchProductDetails(ECSProduct product, ECSCallback<ECSProduct,Exception> ecsCallback);

    /**
     * Fetch product summaries for retailer flow
     *
     * @param ctns        the list of ctns
     * @param ecsCallback the ecs callback containing list of ECSProduct object
     */
    void fetchProductSummaries(List<String> ctns, ECSCallback<List<ECSProduct>,Exception> ecsCallback);


    /**
     * Fetch existing shopping cart.
     *
     * @param ecsCallback the ecs callback containing ECSShoppingCart object
     */
    void fetchShoppingCart(ECSCallback<ECSShoppingCart,Exception> ecsCallback);

    /**
     * Create new shopping cart
     *
     * @param ecsCallback the ecs callback containing ECSShoppingCart object
     */
    void createShoppingCart(ECSCallback<ECSShoppingCart,Exception> ecsCallback);

    /**
     * Add product to existing shopping cart.
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSShoppingCart object
     */
    void addProductToShoppingCart(ECSProduct product, ECSCallback<ECSShoppingCart, Exception> ecsCallback);

    /**
     * Update shopping cart product quantity
     *
     * @param quantity      the quantity
     * @param entriesEntity the ECSEntries object
     * @param ecsCallback   the ecs callback containing ECSShoppingCart object
     */
    void updateShoppingCart(int quantity, ECSEntries entriesEntity, ECSCallback<ECSShoppingCart, Exception> ecsCallback) ;

    /**
     * Apply voucher.
     *
     * @param voucherCode the voucher code
     * @param ecsCallback the ecs callback containing list of ECSVoucher object
     */
//voucher
    void applyVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback);

    /**
     * Fetch applied vouchers.
     *
     * @param ecsCallback the ecs callback containing list of ECSVoucher object
     */
    void fetchAppliedVouchers(ECSCallback<List<ECSVoucher>, Exception> ecsCallback);

    /**
     * Remove voucher.
     *
     * @param voucherCode the voucher code
     * @param ecsCallback the ecs callback containing list of ECSVoucher object
     */
    void removeVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback);

    /**
     * Fetch delivery modes.
     *
     * @param ecsCallback the ecs callback containing list of ECSDeliveryMode object
     */
    void fetchDeliveryModes(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback);

    /**
     * Sets delivery mode.
     *
     * @param deliveryModes the ECSDeliveryMode object
     * @param ecsCallback   the ecs callback containing boolean response
     */
    void setDeliveryMode(ECSDeliveryMode deliveryModes, ECSCallback<Boolean, Exception> ecsCallback);

    /**
     * Fetch regions.
     *
     * @param ecsCallback the ecs callback containing list of ECSRegion object
     */
    void fetchRegions(ECSCallback<List<ECSRegion>, Exception> ecsCallback);

    /**
     * Fetch saved addresses.
     *
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    void fetchSavedAddresses(ECSCallback<List<ECSAddress>, Exception> ecsCallback);

    /**
     * Create and fetch address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    void createAndFetchAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback);

    /**
     * Create address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing ECSAddress object
     */
    void createAddress(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback);


    /**
     * Sets delivery address.
     *  @param isDefault
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing boolean response
     */
    void setDeliveryAddress(boolean isDefault, ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback);

    /**
     * Sets and fetch delivery address.
     *  @param isDefault
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    void setAndFetchDeliveryAddress(boolean isDefault, ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback);

    /**
     * Update address.
     *  @param address          the ECSAddress object
     * @param ecsCallback      the ecs callback containing boolean response
     */
    void updateAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback);

    /**
     * Update and fetch address.
     *  @param address          the ECSAddress object
     * @param ecsCallback      the ecs callback containing list of ECSAddress object
     */
    void updateAndFetchAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback);

    /**
     * Delete address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing boolean object
     */
    void deleteAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback);

    /**
     * Delete and fetch address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    void deleteAndFetchAddress(@NonNull ECSAddress address,@NonNull ECSCallback<List<ECSAddress>, Exception> ecsCallback);

    /**
     * Fetch retailers.
     *
     * @param ctn   the ctn
     * @param ecsCallback the ecs callback containing list of ECSRetailerList object
     */
    void fetchRetailers(String ctn, ECSCallback<ECSRetailerList,Exception> ecsCallback);

    /**
     * Fetch retailers.
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSRetailerList object
     */
    void fetchRetailers(ECSProduct product, ECSCallback<ECSRetailerList,Exception> ecsCallback);

    /**
     * Fetch payments details.
     *
     * @param ecsCallback the ecs callback containing list of ECSPayment object
     */
    void fetchPaymentsDetails(ECSCallback<List<ECSPayment>, Exception> ecsCallback);

    /**
     * Sets payment details.
     *
     * @param paymentDetailsId the payment details id
     * @param ecsCallback      the ecs callback containing boolean response
     */
    void setPaymentDetails(String paymentDetailsId, ECSCallback<Boolean, Exception> ecsCallback);

    /**
     * Submit order.
     *
     * @param cvv         the cvv
     * @param ecsCallback the ecs callback containing ECSOrderDetail object
     */
    void submitOrder(String cvv, ECSCallback<ECSOrderDetail, Exception> ecsCallback);

    /**
     * Make payment.
     *
     * @param orderDetail    the ECSOrderDetail object
     * @param billingAddress the ECSAddress object
     * @param ecsCallback    the ecs callback containing ECSPaymentProvider object
     */
    void makePayment(ECSOrderDetail orderDetail, ECSAddress billingAddress, ECSCallback<ECSPaymentProvider, Exception> ecsCallback);

    /**
     * Fetch order history.
     *
     * @param pageNumber  the page number
     * @param pageSize    the page size
     * @param ecsCallback the ecs callback containing ECSOrderHistory object
     */
    void fetchOrderHistory(int pageNumber, int pageSize, ECSCallback<ECSOrderHistory, Exception> ecsCallback);

    /**
     * Fetch order detail.
     *
     * @param orderId     the order id
     * @param ecsCallback the ecs callback containing ECSOrderDetail object
     */
    void fetchOrderDetail(String orderId, ECSCallback<ECSOrderDetail,Exception> ecsCallback);

    /**
     * Fetch order detail.
     *
     * @param orderDetail the ECSOrderDetail object
     * @param ecsCallback the ecs callback containing ECSOrderDetail object
     */
    void fetchOrderDetail(ECSOrderDetail orderDetail, ECSCallback<ECSOrderDetail,Exception> ecsCallback);

    /**
     * Fetch order detail.
     *
     * @param orders      the ECSOrders object
     * @param ecsCallback the ecs callback containing ECSOrders object
     */
    void fetchOrderDetail(ECSOrders orders, ECSCallback<ECSOrders, Exception> ecsCallback);

    /**
     * Fetch user profile.
     *
     * @param ecsCallback the ecs callback containing ECSUserProfile object
     */
    void fetchUserProfile(ECSCallback<ECSUserProfile,Exception> ecsCallback);


    /**
     * Sets proposition id.
     *
     * @param propositionID the proposition id
     */
    void setPropositionID(@NonNull String propositionID);

}
