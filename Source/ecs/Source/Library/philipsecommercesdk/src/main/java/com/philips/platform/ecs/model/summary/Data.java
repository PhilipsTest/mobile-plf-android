/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.summary;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The type Data contains data from prx
 */
public class Data implements Serializable {

    private static final long serialVersionUID = 2949684301119209075L;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("ctn")
    @Expose
    private String ctn;
    @SerializedName("dtn")
    @Expose
    private String dtn;
    @SerializedName("leafletUrl")
    @Expose
    private String leafletUrl;
    @SerializedName("productTitle")
    @Expose
    private String productTitle;
    @SerializedName("alphanumeric")
    @Expose
    private String alphanumeric;
    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("brand")
    @Expose
    private Brand brand;
    @SerializedName("familyName")
    @Expose
    private String familyName;
    @SerializedName("productURL")
    @Expose
    private String productURL;
    @SerializedName("productPagePath")
    @Expose
    private String productPagePath;
    @SerializedName("descriptor")
    @Expose
    private String descriptor;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("versions")
    @Expose
    private List<String> versions = new ArrayList<String>();
    @SerializedName("productStatus")
    @Expose
    private String productStatus;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;
    @SerializedName("sop")
    @Expose
    private String sop;
    @SerializedName("somp")
    @Expose
    private String somp;
    @SerializedName("eop")
    @Expose
    private String eop;
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("priority")
    @Expose
    private long priority;
    @SerializedName("price")
    @Expose
    private Price price;
    @SerializedName("reviewStatistics")
    @Expose
    private ReviewStatistics reviewStatistics;
    @SerializedName("keyAwards")
    @Expose
    private List<String> keyAwards = new ArrayList<String>();
    @SerializedName("wow")
    @Expose
    private String wow;
    @SerializedName("subWOW")
    @Expose
    private String subWOW;
    @SerializedName("marketingTextHeader")
    @Expose
    private String marketingTextHeader;
    @SerializedName("careSop")
    @Expose
    private String careSop;
    @SerializedName("filterKeys")
    @Expose
    private List<String> filterKeys = new ArrayList<String>();
    @SerializedName("subcategory")
    @Expose
    private String subcategory;

    /**
     * No args constructor for use in serialization
     *
     */
    public Data() {
    }

    /**
     *
     * @param descriptor
     * @param locale
     * @param subWOW
     * @param familyName
     * @param alphanumeric
     * @param brandName
     * @param dtn
     * @param wow
     * @param productStatus
     * @param priority
     * @param leafletUrl
     * @param domain
     * @param careSop
     * @param eop
     * @param ctn
     * @param productPagePath
     * @param reviewStatistics
     * @param marketingTextHeader
     * @param sop
     * @param isDeleted
     * @param filterKeys
     * @param versions
     * @param price
     * @param subcategory
     * @param somp
     * @param productTitle
     * @param brand
     * @param keyAwards
     * @param imageURL
     * @param productURL
     */
    public Data(String locale, String ctn, String dtn, String leafletUrl, String productTitle, String alphanumeric, String brandName, Brand brand, String familyName, String productURL, String productPagePath, String descriptor, String domain, List<String> versions, String productStatus, String imageURL, String sop, String somp, String eop, boolean isDeleted, long priority, Price price, ReviewStatistics reviewStatistics, List<String> keyAwards, String wow, String subWOW, String marketingTextHeader, String careSop, List<String> filterKeys, String subcategory) {
        this.locale = locale;
        this.ctn = ctn;
        this.dtn = dtn;
        this.leafletUrl = leafletUrl;
        this.productTitle = productTitle;
        this.alphanumeric = alphanumeric;
        this.brandName = brandName;
        this.brand = brand;
        this.familyName = familyName;
        this.productURL = productURL;
        this.productPagePath = productPagePath;
        this.descriptor = descriptor;
        this.domain = domain;
        this.versions = versions;
        this.productStatus = productStatus;
        this.imageURL = imageURL;
        this.sop = sop;
        this.somp = somp;
        this.eop = eop;
        this.isDeleted = isDeleted;
        this.priority = priority;
        this.price = price;
        this.reviewStatistics = reviewStatistics;
        this.keyAwards = keyAwards;
        this.wow = wow;
        this.subWOW = subWOW;
        this.marketingTextHeader = marketingTextHeader;
        this.careSop = careSop;
        this.filterKeys = filterKeys;
        this.subcategory = subcategory;
    }

    /**
     *
     * @return
     * The locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     *
     * @param locale
     * The locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     *
     * @return
     * The ctn
     */
    public String getCtn() {
        return ctn;
    }

    /**
     *
     * @param ctn
     * The ctn
     */
    public void setCtn(String ctn) {
        this.ctn = ctn;
    }

    /**
     *
     * @return
     * The dtn
     */
    public String getDtn() {
        return dtn;
    }

    /**
     *
     * @param dtn
     * The dtn
     */
    public void setDtn(String dtn) {
        this.dtn = dtn;
    }

    /**
     *
     * @return
     * The leafletUrl
     */
    public String getLeafletUrl() {
        return leafletUrl;
    }

    /**
     *
     * @param leafletUrl
     * The leafletUrl
     */
    public void setLeafletUrl(String leafletUrl) {
        this.leafletUrl = leafletUrl;
    }

    /**
     *
     * @return
     * The productTitle
     */
    public String getProductTitle() {
        return productTitle;
    }

    /**
     *
     * @param productTitle
     * The productTitle
     */
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    /**
     *
     * @return
     * The alphanumeric
     */
    public String getAlphanumeric() {
        return alphanumeric;
    }

    /**
     *
     * @param alphanumeric
     * The alphanumeric
     */
    public void setAlphanumeric(String alphanumeric) {
        this.alphanumeric = alphanumeric;
    }

    /**
     *
     * @return
     * The brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     *
     * @param brandName
     * The brandName
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     *
     * @return
     * The brand
     */
    public Brand getBrand() {
        return brand;
    }

    /**
     *
     * @param brand
     * The brand
     */
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    /**
     *
     * @return
     * The familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     *
     * @param familyName
     * The familyName
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     *
     * @return
     * The productURL
     */
    public String getProductURL() {
        return productURL;
    }

    /**
     *
     * @param productURL
     * The productURL
     */
    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    /**
     *
     * @return
     * The productPagePath
     */
    public String getProductPagePath() {
        return productPagePath;
    }

    /**
     *
     * @param productPagePath
     * The productPagePath
     */
    public void setProductPagePath(String productPagePath) {
        this.productPagePath = productPagePath;
    }

    /**
     *
     * @return
     * The descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     *
     * @param descriptor
     * The descriptor
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    /**
     *
     * @return
     * The domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     *
     * @param domain
     * The domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     *
     * @return
     * The versions
     */
    public List<String> getVersions() {
        return versions;
    }

    /**
     *
     * @param versions
     * The versions
     */
    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    /**
     *
     * @return
     * The productStatus
     */
    public String getProductStatus() {
        return productStatus;
    }

    /**
     *
     * @param productStatus
     * The productStatus
     */
    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    /**
     *
     * @return
     * The imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     *
     * @param imageURL
     * The imageURL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     *
     * @return
     * The sop
     */
    public String getSop() {
        return sop;
    }

    /**
     *
     * @param sop
     * The sop
     */
    public void setSop(String sop) {
        this.sop = sop;
    }

    /**
     *
     * @return
     * The somp
     */
    public String getSomp() {
        return somp;
    }

    /**
     *
     * @param somp
     * The somp
     */
    public void setSomp(String somp) {
        this.somp = somp;
    }

    /**
     *
     * @return
     * The eop
     */
    public String getEop() {
        return eop;
    }

    /**
     *
     * @param eop
     * The eop
     */
    public void setEop(String eop) {
        this.eop = eop;
    }

    /**
     *
     * @return
     * The isDeleted
     */
    public boolean isIsDeleted() {
        return isDeleted;
    }

    /**
     *
     * @param isDeleted
     * The isDeleted
     */
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     *
     * @return
     * The priority
     */
    public long getPriority() {
        return priority;
    }

    /**
     *
     * @param priority
     * The priority
     */
    public void setPriority(long priority) {
        this.priority = priority;
    }

    /**
     *
     * @return
     * The price
     */
    public Price getPrice() {
        return price;
    }

    /**
     *
     * @param price
     * The price
     */
    public void setPrice(Price price) {
        this.price = price;
    }

    /**
     *
     * @return
     * The reviewStatistics
     */
    public ReviewStatistics getReviewStatistics() {
        return reviewStatistics;
    }

    /**
     *
     * @param reviewStatistics
     * The reviewStatistics
     */
    public void setReviewStatistics(ReviewStatistics reviewStatistics) {
        this.reviewStatistics = reviewStatistics;
    }

    /**
     *
     * @return
     * The keyAwards
     */
    public List<String> getKeyAwards() {
        return keyAwards;
    }

    /**
     *
     * @param keyAwards
     * The keyAwards
     */
    public void setKeyAwards(List<String> keyAwards) {
        this.keyAwards = keyAwards;
    }

    /**
     *
     * @return
     * The wow
     */
    public String getWow() {
        return wow;
    }

    /**
     *
     * @param wow
     * The wow
     */
    public void setWow(String wow) {
        this.wow = wow;
    }

    /**
     *
     * @return
     * The subWOW
     */
    public String getSubWOW() {
        return subWOW;
    }

    /**
     *
     * @param subWOW
     * The subWOW
     */
    public void setSubWOW(String subWOW) {
        this.subWOW = subWOW;
    }

    /**
     *
     * @return
     * The marketingTextHeader
     */
    public String getMarketingTextHeader() {
        return marketingTextHeader;
    }

    /**
     *
     * @param marketingTextHeader
     * The marketingTextHeader
     */
    public void setMarketingTextHeader(String marketingTextHeader) {
        this.marketingTextHeader = marketingTextHeader;
    }

    /**
     *
     * @return
     * The careSop
     */
    public String getCareSop() {
        return careSop;
    }

    /**
     *
     * @param careSop
     * The careSop
     */
    public void setCareSop(String careSop) {
        this.careSop = careSop;
    }

    /**
     *
     * @return
     * The filterKeys
     */
    public List<String> getFilterKeys() {
        return filterKeys;
    }

    /**
     *
     * @param filterKeys
     * The filterKeys
     */
    public void setFilterKeys(List<String> filterKeys) {
        this.filterKeys = filterKeys;
    }

    /**
     *
     * @return
     * The subcategory
     */
    public String getSubcategory() {
        return subcategory;
    }

    /**
     *
     * @param subcategory
     * The subcategory
     */
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

}