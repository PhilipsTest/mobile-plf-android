package com.philips.cdp.di.iap.response.orders;

public class ProductData {

    private Entries mEntry;
    private String mOrderCode;
    private String mCtnNumber;
    private String mProductTitle;
    private String mImageURL;
    private int mQuantity;
    private String mFormatedPrice;
    private String mSubCategory;

    private String mMarketingTextHeader;

    public String getSubCategory() {
        return mSubCategory;
    }

    public void setSubCategory(String subCategory) {
        this.mSubCategory = subCategory;
    }

    public ProductData() {
    }

    public ProductData(Entries entry) {
        mEntry = entry;
    }

    public String getCtnNumber() {
        return mCtnNumber;
    }

    public void setCtnNumber(String mCtnNumber) {
        this.mCtnNumber = mCtnNumber;
    }

    public String getProductTitle() {
        return mProductTitle;
    }

    public void setProductTitle(String mProductTitle) {
        this.mProductTitle = mProductTitle;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public void setImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }

    public String getFormatedPrice() {
        return mFormatedPrice;
    }


    public void setFormatedPrice(String mFormatedPrice) {
        this.mFormatedPrice = mFormatedPrice;
    }

    public String getOrderCode() {
        return mOrderCode;
    }

    public void setOrderCode(String mOrderCode) {
        this.mOrderCode = mOrderCode;
    }


    public String getMarketingTextHeader() {
        return mMarketingTextHeader;
    }
    public void setMarketingTextHeader(String marketingTextHeader) {
        this.mMarketingTextHeader = marketingTextHeader;
    }
}
