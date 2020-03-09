/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.philips.cdp.di.ecs.error.ECSErrorEnum;
import com.philips.cdp.di.ecs.error.ECSErrorWrapper;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode;
import com.philips.cdp.di.ecs.model.address.ECSUserProfile;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.config.ECSConfig;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;
import com.philips.cdp.di.ecs.model.orders.ECSOrders;
import com.philips.cdp.di.ecs.model.payment.ECSPayment;
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.products.ECSProducts;
import com.philips.cdp.di.ecs.model.region.ECSRegion;
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList;
import com.philips.cdp.di.ecs.model.voucher.ECSVoucher;
import com.philips.cdp.di.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.BuildConfig;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.philips.cdp.di.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;

public class ECSServices implements ECSServiceProvider {

   // private ECSManager mECSManager;

    public static final String SERVICE_ID = "iap.baseurl";
    public static final String ECS_NOTATION = "ecs";

    private ECSCallValidator ecsCallValidator;

    public ECSServices(@Nullable String propositionID, @NonNull AppInfra appInfra) {
        ECSConfiguration.INSTANCE.setAppInfra(appInfra);
        ECSConfiguration.INSTANCE.setPropositionID(propositionID);
        ECSConfiguration.INSTANCE.setEcsLogging(appInfra.getLogging().createInstanceForComponent(ECS_NOTATION, BuildConfig.VERSION_NAME));
        ecsCallValidator = new ECSCallValidator();
    }

    /**
     * @since 1905.0.0
     * Configure ecs.
     *
     * @param ecsCallback the ecs callback containing boolean response. If configuration is success returns true else false
     *
     */
    public void configureECS(ECSCallback<Boolean,Exception> ecsCallback){

        ArrayList<String> listOfServiceId = new ArrayList<>();
        listOfServiceId.add(SERVICE_ID);

        ServiceDiscoveryInterface.OnGetServiceUrlMapListener onGetServiceUrlMapListener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onError(ERRORVALUES errorvalues, String s) {

                ECSErrorWrapper ecsErrorWrapper = getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,null,s+"\n"+errorvalues.name());
                ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
            }

            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> map) {

                Collection<ServiceDiscoveryService> values = map.values();

                ArrayList<ServiceDiscoveryService> serviceDiscoveryServiceArrayList = new ArrayList<>(values);

                ServiceDiscoveryService serviceDiscoveryService = serviceDiscoveryServiceArrayList.get(0);

                String locale = serviceDiscoveryService.getLocale();
                ECSConfiguration.INSTANCE.setLocale(locale);
                String configUrls = serviceDiscoveryService.getConfigUrls();

                if(configUrls!=null){
                    ECSConfiguration.INSTANCE.setBaseURL(configUrls+"/");
                    ecsCallValidator.getHybrisConfig(ecsCallback);
                }else {
                    ecsCallback.onResponse(false);
                }
            }

    };

        ECSConfiguration.INSTANCE.getAppInfra().getServiceDiscovery().getServicesWithCountryPreference(listOfServiceId, onGetServiceUrlMapListener,null);
    }

    /**
     * @since 1905.0.0
     * Configure ecs to get configuration.
     *
     * @param ecsCallback the ecs callback containing ECSConfig object
     */
    @Override
    public void configureECSToGetConfiguration(@NonNull ECSCallback<ECSConfig, Exception> ecsCallback) {

        ArrayList<String> listOfServiceId = new ArrayList<>();
        listOfServiceId.add(SERVICE_ID);

        ServiceDiscoveryInterface.OnGetServiceUrlMapListener onGetServiceUrlMapListener = new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onError(ERRORVALUES errorvalues, String s) {

                ECSErrorWrapper ecsErrorWrapper = getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,null,s+"\n"+errorvalues.name());
                ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
            }

            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> map) {

                Collection<ServiceDiscoveryService> values = map.values();

                ArrayList<ServiceDiscoveryService> serviceDiscoveryServiceArrayList = new ArrayList<>(values);

                ServiceDiscoveryService serviceDiscoveryService = serviceDiscoveryServiceArrayList.get(0);

                String locale = serviceDiscoveryService.getLocale();
                ECSConfiguration.INSTANCE.setLocale(locale);
                String configUrls = serviceDiscoveryService.getConfigUrls();

                if(configUrls!=null){
                    ECSConfiguration.INSTANCE.setBaseURL(configUrls+"/");
                    ecsCallValidator.getECSConfig(ecsCallback);
                }else {
                    ECSConfig hybrisConfigResponse = new ECSConfig();
                    hybrisConfigResponse.setLocale(locale);
                    hybrisConfigResponse.setHybris(false);
                    ecsCallback.onResponse(hybrisConfigResponse);
                }
            }

        };

        ECSConfiguration.INSTANCE.getAppInfra().getServiceDiscovery().getServicesWithCountryPreference(listOfServiceId, onGetServiceUrlMapListener,null);
    }



    /**
     * @since 1905.0.0
     * Hybris oath authentication, Janrain basic token is used to obtain Hybris oath token and save it within ECSKService and return true if success.
     *
     * @param ecsoAuthProvider      the ECSOAuthProvider object (Janrain token details)
     * @param ecsListener           the iapsdk callback success block containing ECSOAuthData object
     */
    @Override
    public void hybrisOAthAuthentication(@NonNull ECSOAuthProvider ecsoAuthProvider, @NonNull ECSCallback<ECSOAuthData,Exception> ecsListener){
        ecsCallValidator.getOAuth(ecsoAuthProvider,ecsListener);
    }

    /**
     * @since 1905.0.0
     * Hybris refresh o auth.
     *
     * @param ecsoAuthProvider  the ECSOAuthProvider object
     * @param ecsListener the ecs listener containing ECSOAuthData object
     */
    @Override
    public void hybrisRefreshOAuth(@NonNull ECSOAuthProvider ecsoAuthProvider, @NonNull ECSCallback<ECSOAuthData, Exception> ecsListener) {
        ecsCallValidator.refreshAuth(ecsoAuthProvider,ecsListener);
    }

    /**
     * @since 1905.0.0
     * Fetch Products with summary for hybris flow
     * @param currentPage the current page
     * @param pageSize    the page size
     * @param eCSCallback the ecs callback containing ECSProducts object (a list of ProductsEntity and other fields)
     */
    @Override
    public void fetchProducts(int currentPage, int pageSize, @NonNull ECSCallback<ECSProducts, Exception> eCSCallback) {
        ecsCallValidator.getProductList(currentPage,pageSize,eCSCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch product specific to ctn
     *
     * @param ctn         the ctn
     * @param eCSCallback the ecs callback containing ECSProduct object
     */
    @Override
    public void fetchProduct(@NonNull String ctn, @NonNull ECSCallback<ECSProduct, Exception> eCSCallback) {
        ecsCallValidator.getProductFor(ctn,eCSCallback);

    }

    /**
     * @since 1905.0.0
     * Fetch product summaries for retailer flow
     *
     * @param ctns        the list of ctns
     * @param ecsCallback the ecs callback containing list of ECSProduct object
     */
    @Override
    public void fetchProductSummaries(@NonNull List<String> ctns, @NonNull ECSCallback<List<ECSProduct>, Exception> ecsCallback) {
        ecsCallValidator.getProductSummary(ctns,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch product details containing assets and disclaimer details
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSProduct object
     */
    @Override
    public void fetchProductDetails(@NonNull ECSProduct product, @NonNull ECSCallback<ECSProduct, Exception> ecsCallback) {
        ecsCallValidator.getProductDetail(product,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch existing shopping cart.
     *
     * @param ecsCallback the ecs callback containing ECSShoppingCart object
     */
    @Override
    public void fetchShoppingCart(@NonNull ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ecsCallValidator.getECSShoppingCart(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Add product to existing shopping cart.
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSShoppingCart object
     */
    @Override
    public void addProductToShoppingCart(@NonNull ECSProduct product, @NonNull ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ecsCallValidator.addProductToShoppingCart(product,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Create new shopping cart
     *
     * @param ecsCallback the ecs callback containing ECSShoppingCart object
     */
    @Override
    public void createShoppingCart(@NonNull ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ecsCallValidator.createECSShoppingCart(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Update shopping cart product quantity
     *
     * @param quantity      the quantity
     * @param entriesEntity the ECSEntries object
     * @param ecsCallback   the ecs callback containing ECSShoppingCart object
     */
    @Override
    public void updateShoppingCart(int quantity, @NonNull ECSEntries entriesEntity, @NonNull ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ecsCallValidator.updateQuantity(quantity, entriesEntity,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Apply voucher.
     *
     * @param voucherCode the voucher code
     * @param ecsCallback the ecs callback containing list of ECSVoucher object
     */
    @Override
    public void applyVoucher(@NonNull String voucherCode, @NonNull ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        ecsCallValidator.setVoucher(voucherCode,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch applied vouchers.
     *
     * @param ecsCallback the ecs callback containing list of ECSVoucher object
     */
    @Override
    public void fetchAppliedVouchers(ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        ecsCallValidator.getVoucher(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Remove voucher.
     *
     * @param voucherCode the voucher code
     * @param ecsCallback the ecs callback containing list of ECSVoucher object
     */
    @Override
    public void removeVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        ecsCallValidator.removeVoucher(voucherCode,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch delivery modes.
     *
     * @param ecsCallback the ecs callback containing list of ECSDeliveryMode object
     */
    @Override
    public void fetchDeliveryModes(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback) {
        ecsCallValidator.getDeliveryModes(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Sets delivery mode.
     *
     * @param deliveryModes the ECSDeliveryMode object
     * @param ecsCallback   the ecs callback containing boolean response
     */
    @Override
    public void setDeliveryMode(@NonNull ECSDeliveryMode deliveryModes, @NonNull ECSCallback<Boolean, Exception> ecsCallback) {
        ecsCallValidator.setDeliveryMode(deliveryModes.getCode(),ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch regions.
     *
     * @param ecsCallback the ecs callback containing list of ECSRegion object
     */
    @Override
    public void fetchRegions(@NonNull String countryISO, @NonNull ECSCallback<List<ECSRegion>, Exception> ecsCallback) {
        ecsCallValidator.getRegions(countryISO,ecsCallback);
    }

    // ==== Address starts
    /**
     * @since 1905.0.0
     * Fetch saved addresses.
     *
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    @Override
    public void fetchSavedAddresses(@NonNull ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        ecsCallValidator.getListSavedAddress(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Create address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing ECSAddress object
     */
    @Override
    public void createAddress(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback){
        ecsCallValidator.createNewAddress(address, ecsCallback,true);
    }

    /**
     * @since 1905.0.0
     * Create and fetch address.
     *
     * @param ecsAddress     the ECSAddress object
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    @Override
    public void createAndFetchAddress(@NonNull ECSAddress ecsAddress, @NonNull ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        ecsCallValidator.createNewAddress(ecsAddress, ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Update address.
     *@param address          the ECSAddress object
     * @param ecsCallback      the ecs callback containing boolean response
     */
    @Override
    public void updateAddress(@NonNull ECSAddress address, @NonNull ECSCallback<Boolean, Exception> ecsCallback) {
        ecsCallValidator.updateAddress(address,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Update and fetch address.
     *@param address           the ECSAddress object
     * @param ecsCallback      the ecs callback containing list of ECSAddress object
     */
    @Override
    public void updateAndFetchAddress(@NonNull ECSAddress address, @NonNull ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        ecsCallValidator.updateAndFetchAddress(address,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Sets delivery address.
     *
     * @param isDefault   if true ,It sets the address as default
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing boolean response  */
    @Override
    public void setDeliveryAddress(boolean isDefault, @NonNull ECSAddress address, @NonNull ECSCallback<Boolean, Exception> ecsCallback) {
        address.setDefaultAddress(isDefault);
        ecsCallValidator.setDeliveryAddress(address,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Sets and fetch delivery address.
     *
     * @param isDefault   if true ,It sets the address as default
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing list of ECSAddress object  */
    @Override
    public void setAndFetchDeliveryAddress(boolean isDefault, @NonNull ECSAddress address, @NonNull ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        address.setDefaultAddress(isDefault);
        ecsCallValidator.setAndFetchDeliveryAddress(address,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Delete address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing boolean object
     */
    @Override
    public void deleteAddress(@NonNull ECSAddress address, @NonNull ECSCallback<Boolean, Exception> ecsCallback) {
        ecsCallValidator.deleteAddress(address,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Delete and fetch address.
     *
     * @param address     the ECSAddress object
     * @param ecsCallback the ecs callback containing list of ECSAddress object
     */
    @Override
    public void deleteAndFetchAddress(@NonNull ECSAddress address,@NonNull ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        ecsCallValidator.deleteAndFetchAddress(address,ecsCallback);
    }

    //Address ends
    /**
     * @since 1905.0.0
     * Fetch payments details.
     *
     * @param ecsCallback the ecs callback containing list of ECSPayment object
     */
    @Override
    public void fetchPaymentsDetails(@NonNull ECSCallback<List<ECSPayment>, Exception> ecsCallback) {
        ecsCallValidator.getPayments(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Sets payment details.
     *
     * @param paymentDetailsId the payment details id
     * @param ecsCallback      the ecs callback containing boolean response
     */
    @Override
    public void setPaymentDetails(@NonNull String paymentDetailsId, @NonNull ECSCallback<Boolean, Exception> ecsCallback) {
        ecsCallValidator.setPaymentMethod(paymentDetailsId,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Make payment.
     *
     * @param orderDetail    the ECSOrderDetail object
     * @param billingAddress the ECSAddress object
     * @param ecsCallback    the ecs callback containing ECSPaymentProvider object
     */
    @Override
    public void makePayment(@NonNull ECSOrderDetail orderDetail, @NonNull ECSAddress billingAddress, ECSCallback<ECSPaymentProvider, Exception> ecsCallback) {
        ecsCallValidator.makePayment(orderDetail,billingAddress,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Submit order.
     *
     * @param cvv         the cvv
     * @param ecsCallback the ecs callback containing ECSOrderDetail object
     */
    @Override
    public void submitOrder(@Nullable String cvv, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        ecsCallValidator.submitOrder(cvv,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch retailers.
     *
     * @param ctn   the ctn
     * @param ecsCallback the ecs callback containing list of ECSRetailerList object
     */
    @Override
    public void fetchRetailers(@NonNull String ctn, @NonNull ECSCallback<ECSRetailerList, Exception> ecsCallback) {
        ecsCallValidator.getRetailers(ctn,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch retailers.
     *
     * @param product     the ECSProduct object
     * @param ecsCallback the ecs callback containing ECSRetailerList object
     */
    @Override
    public void fetchRetailers(@NonNull ECSProduct product, @NonNull ECSCallback<ECSRetailerList, Exception> ecsCallback) {
        ecsCallValidator.getRetailers(product.getCode(),ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch order history.
     *
     * @param pageNumber  the page number
     * @param pageSize    the page size
     * @param ecsCallback the ecs callback containing ECSOrderHistory object
     */
    @Override
    public void fetchOrderHistory(int pageNumber, int pageSize, @NonNull ECSCallback<ECSOrderHistory, Exception> ecsCallback) {
        ecsCallValidator.getOrderHistory(pageNumber,pageSize,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch order detail.
     *
     * @param orderId     the order id
     * @param ecsCallback the ecs callback containing ECSOrderDetail object
     */
    @Override
    public void fetchOrderDetail(@NonNull String orderId, @NonNull ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        ecsCallValidator.getOrderDetail(orderId,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch order detail.
     *
     * @param orderDetail the ECSOrderDetail object
     * @param ecsCallback the ecs callback containing ECSOrderDetail object
     */
    @Override
    public void fetchOrderDetail(@NonNull ECSOrderDetail orderDetail, @NonNull ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        ecsCallValidator.getOrderDetail(orderDetail.getCode(),ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch order detail.
     *
     * @param orders      the ECSOrders object
     * @param ecsCallback the ecs callback containing ECSOrders object
     */
    @Override
    public void fetchOrderDetail(@NonNull ECSOrders orders, @NonNull ECSCallback<ECSOrders, Exception> ecsCallback) {
        ecsCallValidator.getOrderDetail(orders,ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Fetch user profile.
     *
     * @param ecsCallback the ecs callback containing ECSUserProfile object
     */
    @Override
    public void fetchUserProfile(@NonNull ECSCallback<ECSUserProfile, Exception> ecsCallback) {
        ecsCallValidator.getUserProfile(ecsCallback);
    }

    /**
     * @since 1905.0.0
     * Sets proposition id.
     *
     * @param propositionID the proposition id
     */
    @Override
    public void setPropositionID(@NonNull String propositionID) {
        ECSConfiguration.INSTANCE.setPropositionID(propositionID);
    }

    /**
     * Set volley timeout and retry count.
     *
     * @param defaultRetryPolicy the default retry policy
     */
    public void setVolleyTimeoutAndRetryCount(DefaultRetryPolicy defaultRetryPolicy){
        ECSConfiguration.INSTANCE.setDefaultRetryPolicy(defaultRetryPolicy);
    }

}
