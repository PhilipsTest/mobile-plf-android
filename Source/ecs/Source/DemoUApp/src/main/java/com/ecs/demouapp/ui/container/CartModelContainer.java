/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.container;


import com.ecs.demouapp.ui.address.AddressFields;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.cdp.prxclient.datamodels.Disclaimer.DisclaimerModel;

import com.philips.cdp.prxclient.datamodels.summary.Data;
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
    private List<ECSRegion> mRegionList;

    private ArrayList<Data> mPRXSummaryObjects;
    private HashMap<String, ArrayList<String>> mPRXAssetObjects;
    private HashMap<String, DisclaimerModel> mPRXDisclaimerObjects;


    private List<ECSDeliveryMode> mDeliveryModes;

    private boolean switchToBillingAddress;

    private String language;
    private String country;

    private AppInfraInterface appInfraInstance;
    private boolean addessStateVisible = false;
    private String mAddressIdFromDelivery;
    private String voucherCode;


    private CartModelContainer() {
        mPRXSummaryObjects = new ArrayList<>();
        mPRXAssetObjects = new HashMap<>();
        mPRXDisclaimerObjects = new HashMap<>();
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

    public List<ECSDeliveryMode> getDeliveryModes() {
        return mDeliveryModes;
    }

    public void setDeliveryModes(List<ECSDeliveryMode> mDeliveryModes) {
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

    public void setRegionList(List<ECSRegion> regionList) {
        mRegionList = regionList;
    }

    public List<ECSRegion> getRegionList() {
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
        if(mPRXSummaryObjects == null || mPRXSummaryObjects.size()==0) return false;
        for (Data data : mPRXSummaryObjects) {
            if (data.getCtn().equalsIgnoreCase(ctn)) {
                return true;
            }
        }
        return false;
    }

    public Data getProductSummary(String ctn) {
        for (Data data : mPRXSummaryObjects) {
            if (data.getCtn().equalsIgnoreCase(ctn)) {
                return data;
            }
        }
        return null;
    }

    public void addProductDisclaimer(String ctn, DisclaimerModel model) {
        mPRXDisclaimerObjects.put(ctn, model);
    }

    public ArrayList<Data> getPRXSummaryList() {
        return mPRXSummaryObjects;
    }

    public void setPRXSummaryList(ArrayList<Data> data){
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
