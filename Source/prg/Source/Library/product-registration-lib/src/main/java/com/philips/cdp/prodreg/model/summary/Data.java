/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.model.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.philips.cdp.prxclient.datamodels.summary.Price;
import com.philips.cdp.prxclient.datamodels.summary.ReviewStatistics;
import com.philips.cdp.prxclient.datamodels.summary.Brand;



public class Data extends  com.philips.cdp.prxclient.datamodels.summary.Data implements Serializable  {

    private static final long serialVersionUID = 3102143091240290063L;
    private String locale;
    private String ctn;
    private String dtn;
    private String leafletUrl;
    private String productTitle;
    private String alphanumeric;
    private String brandName;
    private Brand brand;
    private String familyName;
    private String productURL;
    private String productPagePath;
    private String descriptor;
    private String domain;
    private List<String> versions = new ArrayList<String>();
    private String productStatus;
    private String imageURL;
    private String sop;
    private String somp;
    private String eop;
    private boolean isDeleted;
    private int priority;
    private Price price;
    private ReviewStatistics reviewStatistics;
    private List<String> keyAwards = new ArrayList<String>();
    private String wow;
    private String subWOW;
    private String marketingTextHeader;
    private String careSop;
    private List<String> filterKeys = new ArrayList<String>();
    private String subcategory;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * @param locale The locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * @return The ctn
     */
    public String getCtn() {
        return ctn;
    }

    /**
     * @param ctn The ctn
     */
    public void setCtn(String ctn) {
        this.ctn = ctn;
    }

    /**
     * @return The dtn
     */
    public String getDtn() {
        return dtn;
    }

    /**
     * @param dtn The dtn
     */
    public void setDtn(String dtn) {
        this.dtn = dtn;
    }

    /**
     * @return The leafletUrl
     */
    public String getLeafletUrl() {
        return leafletUrl;
    }

    /**
     * @param leafletUrl The leafletUrl
     */
    public void setLeafletUrl(String leafletUrl) {
        this.leafletUrl = leafletUrl;
    }

    /**
     * @return The productTitle
     */
    public String getProductTitle() {
        return productTitle;
    }

    /**
     * @param productTitle The productTitle
     */
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    /**
     * @return The alphanumeric
     */
    public String getAlphanumeric() {
        return alphanumeric;
    }

    /**
     * @param alphanumeric The alphanumeric
     */
    public void setAlphanumeric(String alphanumeric) {
        this.alphanumeric = alphanumeric;
    }

    /**
     * @return The brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @param brandName The brandName
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return The brand
     */
    public Brand getBrand() {
        return brand;
    }

    /**
     * @param brand The brand
     */
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    /**
     * @return The familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName The familyName
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * @return The productURL
     */
    public String getProductURL() {
        return productURL;
    }

    /**
     * @param productURL The productURL
     */
    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    /**
     * @return The productPagePath
     */
    public String getProductPagePath() {
        return productPagePath;
    }

    /**
     * @param productPagePath The productPagePath
     */
    public void setProductPagePath(String productPagePath) {
        this.productPagePath = productPagePath;
    }

    /**
     * @return The descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor The descriptor
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @return The domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain The domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return The versions
     */
    public List<String> getVersions() {
        return versions;
    }

    /**
     * @param versions The versions
     */
    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    /**
     * @return The productStatus
     */
    public String getProductStatus() {
        return productStatus;
    }

    /**
     * @param productStatus The productStatus
     */
    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    /**
     * @return The imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param imageURL The imageURL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @return The sop
     */
    public String getSop() {
        return sop;
    }

    /**
     * @param sop The sop
     */
    public void setSop(String sop) {
        this.sop = sop;
    }

    /**
     * @return The somp
     */
    public String getSomp() {
        return somp;
    }

    /**
     * @param somp The somp
     */
    public void setSomp(String somp) {
        this.somp = somp;
    }

    /**
     * @return The eop
     */
    public String getEop() {
        return eop;
    }

    /**
     * @param eop The eop
     */
    public void setEop(String eop) {
        this.eop = eop;
    }

    /**
     * @return The isDeleted
     */
    public boolean isIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted The isDeleted
     */
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return The priority
     */
    public long getPriority() {
        return priority;
    }

    /**
     * @param priority The priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return The price
     */
    public Price getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(Price price) {
        this.price = price;
    }

    /**
     * @return The reviewStatistics
     */
    public ReviewStatistics getReviewStatistics() {
        return reviewStatistics;
    }

    /**
     * @param reviewStatistics The reviewStatistics
     */
    public void setReviewStatistics(ReviewStatistics reviewStatistics) {
        this.reviewStatistics = reviewStatistics;
    }

    /**
     * @return The keyAwards
     */
    public List<String> getKeyAwards() {
        return keyAwards;
    }

    /**
     * @param keyAwards The keyAwards
     */
    public void setKeyAwards(List<String> keyAwards) {
        this.keyAwards = keyAwards;
    }

    /**
     * @return The wow
     */
    public String getWow() {
        return wow;
    }

    /**
     * @param wow The wow
     */
    public void setWow(String wow) {
        this.wow = wow;
    }

    /**
     * @return The subWOW
     */
    public String getSubWOW() {
        return subWOW;
    }

    /**
     * @param subWOW The subWOW
     */
    public void setSubWOW(String subWOW) {
        this.subWOW = subWOW;
    }

    /**
     * @return The marketingTextHeader
     */
    public String getMarketingTextHeader() {
        return marketingTextHeader;
    }

    /**
     * @param marketingTextHeader The marketingTextHeader
     */
    public void setMarketingTextHeader(String marketingTextHeader) {
        this.marketingTextHeader = marketingTextHeader;
    }

    /**
     * @return The careSop
     */
    public String getCareSop() {
        return careSop;
    }

    /**
     * @param careSop The careSop
     */
    public void setCareSop(String careSop) {
        this.careSop = careSop;
    }

    /**
     * @return The filterKeys
     */
    public List<String> getFilterKeys() {
        return filterKeys;
    }

    /**
     * @param filterKeys The filterKeys
     */
    public void setFilterKeys(List<String> filterKeys) {
        this.filterKeys = filterKeys;
    }

    /**
     * @return The subcategory
     */
    public String getSubcategory() {
        return subcategory;
    }

    /**
     * @param subcategory The subcategory
     */
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
