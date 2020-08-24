/**
 * Description : Class gets the data from the PRX Component & created the object of this class required for the "View Product Details UI Screen".
 * <p/>
 *
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.productdetails.model;


import java.util.List;



public class ViewProductDetailsModel {

    private static final String TAG = ViewProductDetailsModel.class.getSimpleName();

    private String mProductName = null;
    private String mCtnName = null;
    private String mProductImage = null;
    private String mManualLink = null;
    private String mDfuLink = null;
    private String mProductInfoLink = null;
    private List<String> mVideoLinks = null;
    private String mDomain = null;

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        this.mProductName = productName;
    }

    public String getCtnName() {
        return mCtnName;
    }

    public void setCtnName(String ctnName) {
        this.mCtnName = ctnName;
    }

    public String getProductImage() {
        return mProductImage;
    }

    public void setProductImage(String productImage) {
        this.mProductImage = productImage;
    }

    public String getManualLink() {
        return mManualLink;
    }

    public void setManualLink(String manualLink) {
        this.mManualLink = manualLink;
    }

    public String getDfuLink() {
        return mDfuLink;
    }

    public void setDfuLink(String DfuLink) {
        this.mDfuLink = DfuLink;
    }

    public String getProductInfoLink() {
        return mProductInfoLink;
    }

    public void setProductInfoLink(String productInfoLink) {
        this.mProductInfoLink = productInfoLink;
    }

    public List<String> getVideoLinks() {
        return mVideoLinks;
    }

    public void setmVideoLinks(List<String> videoLinks) {
        this.mVideoLinks = videoLinks;
    }

    public String getDomain() {
        return mDomain;
    }

    public void setDomain(String mDomain) {
        this.mDomain = mDomain;
    }
}
