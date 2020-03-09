/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs;

import androidx.annotation.VisibleForTesting;

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

import java.util.List;

class ECSCallValidator {

    ECSManager ecsManager;

    @VisibleForTesting
    ECSManager getECSManager() {
        return ecsManager;
    }

    ECSCallValidator() {
        ecsManager = new ECSManager();
    }

    public void getECSConfig(ECSCallback<ECSConfig, Exception> ecsCallback) {
        ECSErrorWrapper configAPIValidateError = new ApiInputValidator().getConfigAPIValidateError();
        if (configAPIValidateError == null) {
            new ECSManager().getHybrisConfigResponse(ecsCallback);
        } else {
            ecsCallback.onFailure(configAPIValidateError.getException(), configAPIValidateError.getEcsError());
        }
    }

    public void getProductSummary(List<String> ctns, ECSCallback<List<ECSProduct>, Exception> ecsCallback) {

        ECSErrorWrapper configAPIValidateError = new ApiInputValidator().getProductSummaryAPIValidateError(ctns);
        if (configAPIValidateError == null) {
            ecsManager.getSummary(ctns, ecsCallback);
        } else {
            ecsCallback.onFailure(configAPIValidateError.getException(), configAPIValidateError.getEcsError());
        }
    }

    public void getECSShoppingCart(ECSCallback<ECSShoppingCart, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getECSShoppingCartAPIValidateError();
        if (ecsErrorWrapper == null) {
            ecsManager.getECSShoppingCart(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void createECSShoppingCart(ECSCallback<ECSShoppingCart, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getCreateShoppingCartAPIValidateError();
        if (ecsErrorWrapper == null) {
            ecsManager.createECSShoppingCart(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getOAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getHybrisOathAuthenticationAPIValidateError(oAuthInput);
        if (ecsErrorWrapper == null) {
            ecsManager.getOAuth(oAuthInput, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getProductList(int currentPage, int pageSize, ECSCallback<ECSProducts, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getProductListAPIValidateError();
        if (ecsErrorWrapper == null) {
            ecsManager.getProductList(currentPage, pageSize, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getProductFor(String ctn, ECSCallback<ECSProduct, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getProductForAPIValidateError(ctn);
        if (ecsErrorWrapper == null) {
            ecsManager.getProductFor(ctn, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getProductDetail(ECSProduct product, ECSCallback<ECSProduct, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getPRXProductDetailAPIValidateError(product);
        if (ecsErrorWrapper == null) {
            ecsManager.getProductDetail(product, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void addProductToShoppingCart(ECSProduct product, ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getAddProductToShoppingCartError(product);
        if (ecsErrorWrapper == null) {
            ecsManager.addProductToShoppingCart(product, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void updateQuantity(int quantity, ECSEntries entriesEntity, ECSCallback<ECSShoppingCart, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getUpdateQuantityError(quantity);
        if (ecsErrorWrapper == null) {
            ecsManager.updateQuantity(quantity, entriesEntity, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void setVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getSetVoucherError(voucherCode);
        if (ecsErrorWrapper == null) {
            ecsManager.setVoucher(voucherCode, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    public void getVoucher(ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getVoucherError();
        if (ecsErrorWrapper == null) {
            ecsManager.getVoucher(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void removeVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getRemoveVoucherError(voucherCode);
        if (ecsErrorWrapper == null) {
            ecsManager.removeVoucher(voucherCode, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getDeliveryModes(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getDeliveryModesError();
        if (ecsErrorWrapper == null) {
            ecsManager.getDeliveryModes(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void setDeliveryMode(String deliveryModeID, ECSCallback<Boolean, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getSetDeliveryModeError(deliveryModeID);
        if (ecsErrorWrapper == null) {
            ecsManager.setDeliveryMode(deliveryModeID, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }

    }

    public void getRegions(String countryISO, ECSCallback<List<ECSRegion>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getRegionsError();
        if (ecsErrorWrapper == null) {
            ecsManager.getRegions(countryISO, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getListSavedAddress(ECSCallback<List<ECSAddress>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getListSavedAddressError();
        if (ecsErrorWrapper == null) {
            ecsManager.getListSavedAddress(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void createNewAddress(ECSAddress ecsAddress, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {

        ECSErrorWrapper hybrisOathAuthenticationAPIValidateError = new ApiInputValidator().getCreateNewAddressError(ecsAddress);

        if (hybrisOathAuthenticationAPIValidateError == null) {
            ecsManager.createNewAddress(ecsAddress, ecsCallback);
        } else {
            ecsCallback.onFailure(hybrisOathAuthenticationAPIValidateError.getException(), hybrisOathAuthenticationAPIValidateError.getEcsError());
        }
    }

    public void createNewAddress(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback, boolean b) {

        ECSErrorWrapper hybrisOathAuthenticationAPIValidateError = new ApiInputValidator().getCreateNewAddressError(address);

        if (hybrisOathAuthenticationAPIValidateError == null) {
            ecsManager.createNewAddress(address, ecsCallback, b);
        } else {
            ecsCallback.onFailure(hybrisOathAuthenticationAPIValidateError.getException(), hybrisOathAuthenticationAPIValidateError.getEcsError());
        }
    }

    public void setDeliveryAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getSetDeliveryAddressError(address);

        if (ecsErrorWrapper == null) {
            ecsManager.setDeliveryAddress(address, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void setAndFetchDeliveryAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getSetDeliveryAddressError(address);

        if (ecsErrorWrapper == null) {
            ecsManager.setAndFetchDeliveryAddress(address, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void updateAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getUpdateAddressError(address);
        if (ecsErrorWrapper == null) {
            ecsManager.updateAddress(address, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void updateAndFetchAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getUpdateAddressError(address);
        if (ecsErrorWrapper == null) {
            ecsManager.updateAndFetchAddress(address, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void deleteAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getDeleteAddressError(address);
        if (ecsErrorWrapper == null) {
            ecsManager.deleteAddress(address, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void deleteAndFetchAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getDeleteAddressError(address);
        if (ecsErrorWrapper == null) {
            ecsManager.deleteAndFetchAddress(address, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getPayments(ECSCallback<List<ECSPayment>, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getPaymentsError();
        if (ecsErrorWrapper == null) {
            ecsManager.getPayments(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void setPaymentMethod(String paymentDetailsId, ECSCallback<Boolean, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getSetPaymentMethodError(paymentDetailsId);
        if (ecsErrorWrapper == null) {
            ecsManager.setPaymentMethod(paymentDetailsId, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getRetailers(String productID, ECSCallback<ECSRetailerList, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getRetailersError(productID);
        if (ecsErrorWrapper == null) {
            ecsManager.getRetailers(productID, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void submitOrder(String cvv, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getSubmitOrderError(cvv);
        if (ecsErrorWrapper == null) {
            ecsManager.submitOrder(cvv, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void makePayment(ECSOrderDetail orderDetail, ECSAddress billingAddress, ECSCallback<ECSPaymentProvider, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getMakePaymentError(orderDetail, billingAddress);
        if (ecsErrorWrapper == null) {
            ecsManager.makePayment(orderDetail, billingAddress, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getOrderHistory(int pageNumber, int pageSize, ECSCallback<ECSOrderHistory, Exception> ecsCallback) {

        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getOrderHistoryError(pageNumber);
        if (ecsErrorWrapper == null) {
            ecsManager.getOrderHistory(pageNumber, pageSize, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getOrderDetail(String orderId, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getOrderDetailError(orderId);

        if (ecsErrorWrapper == null) {
            ecsManager.getOrderDetail(orderId, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getOrderDetail(ECSOrders orders, ECSCallback<ECSOrders, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getOrderDetailError(orders.getCode());

        if (ecsErrorWrapper == null) {
            ecsManager.getOrderDetail(orders, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getUserProfile(ECSCallback<ECSUserProfile, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getUserProfileError();

        if (ecsErrorWrapper == null) {
            ecsManager.getUserProfile(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void refreshAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getHybrisOathAuthenticationAPIValidateError(oAuthInput);

        if (ecsErrorWrapper == null) {
            ecsManager.refreshAuth(oAuthInput, ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }

    public void getHybrisConfig(ECSCallback<Boolean, Exception> ecsCallback) {
        ECSErrorWrapper ecsErrorWrapper = new ApiInputValidator().getConfigAPIValidateError();

        if (ecsErrorWrapper == null) {
            ecsManager.getHybrisConfig(ecsCallback);
        } else {
            ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
        }
    }


}
