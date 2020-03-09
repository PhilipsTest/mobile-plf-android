
package com.philips.cdp.prodreg.model_request;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Attributes {

    @SerializedName("catalog")
    private String mCatalog;
    @SerializedName("locale")
    private String mLocale;
    @SerializedName("micrositeId")
    private String mMicrositeId;
    @SerializedName("productId")
    private String mProductId;
    @SerializedName("purchased")
    private String mPurchased;
    @SerializedName("sector")
    private String mSector;
    @SerializedName("serialNumber")
    private String mSerialNumber;
    @SerializedName("userProfile")
    private UserProfile mUserProfile;

    public String getCatalog() {
        return mCatalog;
    }

    public void setCatalog(String catalog) {
        mCatalog = catalog;
    }

    public String getLocale() {
        return mLocale;
    }

    public void setLocale(String locale) {
        mLocale = locale;
    }

    public String getMicrositeId() {
        return mMicrositeId;
    }

    public void setMicrositeId(String micrositeId) {
        mMicrositeId = micrositeId;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public String getPurchased() {
        return mPurchased;
    }

    public void setPurchased(String purchased) {
        mPurchased = purchased;
    }

    public String getSector() {
        return mSector;
    }

    public void setSector(String sector) {
        mSector = sector;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        mSerialNumber = serialNumber;
    }

    public UserProfile getUserProfile() {
        return mUserProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        mUserProfile = userProfile;
    }

}
