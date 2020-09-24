/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.container;

import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.products.ProductCatalogData;
import com.philips.cdp.di.iap.response.State.RegionsList;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.prxclient.datamodels.Disclaimer.DisclaimerModel;
import com.philips.cdp.prxclient.datamodels.summary.Summary;
import com.philips.platform.appinfra.AppInfraInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartModelContainer {
    private static CartModelContainer container;

    private AddressFields mBillingAddress;
    private AddressFields mShippingAddressFields;

    private String regionIsoCode;
    private String mAddressId;
    private String mOrderNumber;
    private RegionsList mRegionList;

    private HashMap<String, ProductCatalogData> mProductList;
    private ArrayList<Summary> mPRXSummaryObjects;
    private HashMap<String, ArrayList<String>> mPRXAssetObjects;
    private HashMap<String, DisclaimerModel> mPRXDisclaimerObjects;


    private List<DeliveryModes> mDeliveryModes;

    private boolean switchToBillingAddress;

    private String language;
    private String country;

    private AppInfraInterface appInfraInstance;
    private boolean addessStateVisible = false;
    private String mAddressIdFromDelivery;
    private String voucherCode;

    public void clearProductList() {
        if (mProductList != null) mProductList.clear();
    }

    private CartModelContainer() {
        mPRXSummaryObjects = new ArrayList<>();
        mPRXAssetObjects = new HashMap<>();
        mPRXDisclaimerObjects = new HashMap<>();
        mProductList = new HashMap<>();
    }

    public static CartModelContainer getInstance() {
        synchronized (CartModelContainer.class) {
            if (container == null) {
                container = new CartModelContainer();
            }
        }
        return container;
    }

    public String getAddressFromDelivery() {
        return mAddressIdFromDelivery;
    }

    public String setAddressIdFromDelivery(String mAddressId) {
        this.mAddressIdFromDelivery = mAddressId;
        return mAddressIdFromDelivery;
    }

    public String getAddressId() {
        return mAddressId;
    }

    public String setAddressId(String mAddressId) {
        this.mAddressId = mAddressId;
        return mAddressId;
    }

    public String getRegionIsoCode() {
        return regionIsoCode;
    }

    public void setRegionIsoCode(String regionIsoCode) {
        this.regionIsoCode = regionIsoCode;
    }

    public List<DeliveryModes> getDeliveryModes() {
        return mDeliveryModes;
    }

    public void setDeliveryModes(List<DeliveryModes> mDeliveryModes) {
        this.mDeliveryModes = mDeliveryModes;
    }

    public AddressFields getShippingAddressFields() {
        return mShippingAddressFields;
    }

    public void setShippingAddressFields(final AddressFields mShippingAddressFields) {
        this.mShippingAddressFields = mShippingAddressFields;
    }

    public void setBillingAddress(final AddressFields mBillingAddress) {
        this.mBillingAddress = mBillingAddress;
    }

    public AddressFields getBillingAddress() {
        return mBillingAddress;
    }

    public void setOrderNumber(String orderNumber) {
        mOrderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return mOrderNumber;
    }

    public void setSwitchToBillingAddress(boolean switchToBillingAddress) {
        this.switchToBillingAddress = switchToBillingAddress;
    }

    public boolean isSwitchToBillingAddress() {
        return switchToBillingAddress;
    }

    public void setRegionList(RegionsList regionList) {
        mRegionList = regionList;
    }

    public RegionsList getRegionList() {
        return mRegionList;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    //PRX Summary
    public boolean isPRXSummaryPresent(String ctn) {
        if (mPRXSummaryObjects == null || mPRXSummaryObjects.size() == 0) return false;
        for (Summary data : mPRXSummaryObjects) {
            if (data.getCtn().equalsIgnoreCase(ctn)) {
                return true;
            }
        }
        return false;
    }

    public Summary getProductSummary(String ctn) {
        for (Summary data : mPRXSummaryObjects) {
            if (data.getCtn().equalsIgnoreCase(ctn)) {
                return data;
            }
        }
        return null;
    }

    public void addProductDisclaimer(String ctn, DisclaimerModel model) {
        mPRXDisclaimerObjects.put(ctn, model);
    }

    public ArrayList<Summary> getPRXSummaryList() {
        return mPRXSummaryObjects;
    }

    public void setPRXSummaryList(ArrayList<Summary> data) {
        mPRXSummaryObjects = data;
    }

    //PRX Assets
    public boolean isPRXAssetPresent(String ctn) {
        return mPRXAssetObjects.containsKey(ctn);
    }

    public void addProductAsset(String ctn, ArrayList<String> assets) {
        mPRXAssetObjects.put(ctn, assets);
    }

    public HashMap<String, ArrayList<String>> getPRXAssetList() {
        return mPRXAssetObjects;
    }

    //Product Data
    public boolean isProductCatalogDataPresent(String ctn) {
        return mProductList.containsKey(ctn);
    }

    public void addProduct(String ctn, ProductCatalogData data) {
        if (!mProductList.containsKey(ctn)) {
            mProductList.put(ctn, data);
        }
    }

    public ProductCatalogData getProduct(String ctn) {
        if (mProductList.containsKey(ctn)) {
            return mProductList.get(ctn);
        }
        return null;
    }

    public HashMap<String, ProductCatalogData> getProductList() {
        return mProductList;
    }

    public void clearCategorisedProductList() {
        mProductList.clear();
    }

    public AppInfraInterface getAppInfraInstance() {
        return appInfraInstance;
    }

    public void setAppInfraInstance(AppInfraInterface appInfraInstance) {
        this.appInfraInstance = appInfraInstance;
    }

    public void resetApplicationFields() {
        mBillingAddress = null;
        mShippingAddressFields = null;
        mOrderNumber = null;
    }

    public void setAddessStateVisible(boolean addessStateVisible) {
        this.addessStateVisible = addessStateVisible;
    }

    public boolean isAddessStateVisible() {
        return addessStateVisible;
    }


    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getVoucherCode() {
        return voucherCode;
    }


}
