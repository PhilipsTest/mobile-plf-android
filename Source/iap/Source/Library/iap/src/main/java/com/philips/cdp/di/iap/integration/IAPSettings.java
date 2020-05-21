/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.integration;

import android.content.Context;

import com.philips.cdp.di.iap.model.AddressFieldDecider;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.List;

/**
 * IAPSettings class is used to initialize basic settings for InAppPurchase. Right now InAppPurchase doesn’t have any settings to be initialized. So only default initialization of IAPSettings is required to be passed while creating IAPInterface object.
 * @since 1.0.0
 */
public class IAPSettings extends UappSettings {
    private static final long serialVersionUID = 2010576805451898226L;
    private boolean mUseLocalData;
    private String mProposition;
    private String mHostPort;
    private List<AddressFieldDecider> addressFieldDeciderList;
    private IAPMockInterface iapMockInterface;

    /**
     * Used to create IAPSettings instance
     * @param applicationContext context of proposition application
     * @since 1.0.0
     */
    public IAPSettings(Context applicationContext) {
        super(applicationContext);
    }

    /**
     * It specifies whether It is Hybris flow or not
     * @return mUseLocalData  get return true if hybris support is not available else get false
     * @since 1.0.0
     */
    public boolean isUseLocalData() {
        return mUseLocalData;
    }

    /**
     * Enable or disable Hybris flow.
     * @param isLocalData  pass true if hybris support required else pass false
     * @since 1.0.0
     */
    void setUseLocalData(boolean isLocalData) {
        mUseLocalData = isLocalData;
    }

    /**
     * Sets proposition ID
     * @param proposition  pass proposition id for backend
     * @since 1.0.0
     */
    public void setProposition(String proposition) {
        mProposition = proposition;
    }

    /**
     * Returns proposition ID
     * @return propositionID  ID provided by Hybris backend
     * @since 1.0.0
     */
    public String getProposition() {
        return mProposition;
    }

    /**
     * Get HostPort ID
     * @return hostPort  get hostPort URL string
     * @since 1.0.0
     */
    public String getHostPort() {
        return mHostPort;
    }

    /**
     * Sets hostPort id
     * @param hostPort  pass hostPort URL string
     * @since 1.0.0
     */
    public void setHostPort(String hostPort) {
        mHostPort = hostPort;
    }

    /**
     * Returns addressFieldDeciderList
     * @return addressFieldDeciderList provided by Service discovery backend
     * @since 2019.1.0
     */
    public List<AddressFieldDecider> getAddressFieldDeciderList() {
        return addressFieldDeciderList;
    }

    /**
     * Sets addressFieldDeciderList
     * @param addressFieldDeciderList  pass addressFieldDeciderList for deciding address fields
     * @since 2019.1.0
     */
    public void setAddressFieldDeciderList(List<AddressFieldDecider> addressFieldDeciderList) {
        this.addressFieldDeciderList = addressFieldDeciderList;
    }

    public IAPMockInterface getIapMockInterface() {
        return iapMockInterface;
    }

    public void setIapMockInterface(IAPMockInterface iapMockInterface) {
        this.iapMockInterface = iapMockInterface;
    }
}
