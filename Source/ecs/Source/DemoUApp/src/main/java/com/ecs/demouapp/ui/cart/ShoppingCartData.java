/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;





import com.philips.platform.ecs.model.cart.AppliedOrderPromotionEntity;
import com.philips.platform.ecs.model.cart.AppliedVoucherEntity;
import com.philips.platform.ecs.model.cart.DeliveryAddressEntity;
import com.philips.platform.ecs.model.cart.DeliveryModeEntity;
import com.philips.platform.ecs.model.cart.ECSEntries;

import java.io.Serializable;
import java.util.List;

public class ShoppingCartData implements Serializable {


    private static final long serialVersionUID = -4398831597666931802L;
    private ECSEntries mEntry;
    private DeliveryModeEntity mDeliveryModeEntity;
    private DeliveryAddressEntity mDeliveryAddressEntity;
    private List<AppliedVoucherEntity> appliedVouchers;


    public List<AppliedOrderPromotionEntity> getAppliedOrderPromotionEntityList() {
        return appliedOrderPromotionEntityList;
    }

    public void setAppliedOrderPromotionEntityList(List<AppliedOrderPromotionEntity> appliedOrderPromotionEntityList) {
        this.appliedOrderPromotionEntityList = appliedOrderPromotionEntityList;
    }

    private List<AppliedOrderPromotionEntity> appliedOrderPromotionEntityList;

    private int mQuantity;
    private int mTotalItems;
    private int mEntryNumber;
    private int mStockLevel;
    private int mDeliveryItemsQuantity;

    private String mCtnNumber;
    private String mProductTitle;
    private String mImageURL;
    private String mFormattedTotalPrice;
    private String mFormattedTotalPriceWithTax;
    private String mFormattedPrice;
    private String mValuePrice;
    private String mVatValue;
    private String mVatActualValue;
    private String mMarketingTextHeader;
    private String mCategory;

    private boolean mVatInclusive;
    private double discountPrice;

    private String mAppliedVoucherCode;

    private String totalDiscounts;

    public ShoppingCartData() {
    }

    public boolean isVatInclusive() {
        return mVatInclusive;
    }

    public void setVatInclusive(boolean mVatInclusive) {
        this.mVatInclusive = mVatInclusive;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(final String mCategory) {
        this.mCategory = mCategory;
    }

    public String getValuePrice() {
        return mValuePrice;
    }

    public void setValuePrice(String mValuePrice) {
        this.mValuePrice = mValuePrice;
    }

    public int getDeliveryItemsQuantity() {
        return mDeliveryItemsQuantity;
    }

    public void setDeliveryItemsQuantity(final int mDeliveryItemsQuantity) {
        this.mDeliveryItemsQuantity = mDeliveryItemsQuantity;
    }

    public String getMarketingTextHeader() {
        return mMarketingTextHeader;
    }

    public void setMarketingTextHeader(final String pMarketingTextHeader) {
        this.mMarketingTextHeader = pMarketingTextHeader;
    }

    public DeliveryAddressEntity getDeliveryAddressEntity() {
        return mDeliveryAddressEntity;
    }

    public void setDeliveryAddressEntity(DeliveryAddressEntity mDeliveryAddressEntity) {
        this.mDeliveryAddressEntity = mDeliveryAddressEntity;
    }

    public ShoppingCartData(ECSEntries entry, DeliveryModeEntity deliveryCost) {
        mEntry = entry;
        mDeliveryModeEntity = deliveryCost;
    }

    public DeliveryModeEntity getDeliveryMode() {
        return mDeliveryModeEntity;
    }

    public int getEntryNumber() {
        return mEntry.getEntryNumber();
    }

    public void setEntryNumber(int entryNumber) {
        mEntryNumber = entryNumber;
    }

    public String getCtnNumber() {
        return mCtnNumber;
    }

    public void setCtnNumber(String ctnNumber) {
        mCtnNumber = ctnNumber;
    }

    public void setProductTitle(String productTitle) {
        mProductTitle = productTitle;
    }

    public String getProductTitle() {
        return mProductTitle;
    }

    public void setImageUrl(String url) {
        mImageURL = url;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public void setQuantity(int items) {
        mQuantity = items;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setTotalItems(int totalItems) {
        mTotalItems = totalItems;
    }

    public int getTotalItems() {
        return mTotalItems;
    }

    public void setStockLevel(final int stockLevel) {
        mStockLevel = stockLevel;
    }

    public int getStockLevel() {
        return mEntry.getProduct().getStock().getStockLevel();
    }

    public String getStockLevelStatus(){
        return mEntry.getProduct().getStock().getStockLevelStatus();
    }

    public String getFormattedTotalPriceWithTax() {
        return mFormattedTotalPriceWithTax;
    }

    public void setFormattedTotalPriceWithTax(String mFormattedTotalPriceWithTax) {
        this.mFormattedTotalPriceWithTax = mFormattedTotalPriceWithTax;
    }

    public String getFormattedPrice() {
        return mFormattedPrice;
    }

    public void setFormattedPrice(String mFormattedPrice) {
        this.mFormattedPrice = mFormattedPrice;
    }

    public String getFormattedTotalPrice() {
        return mFormattedTotalPrice;
    }

    public void setFormattedTotalPrice(String mFormattedTotalPrice) {
        this.mFormattedTotalPrice = mFormattedTotalPrice;
    }

    public String getVatValue() {
        return mVatValue;
    }

    public void setVatValue(String mVatValue) {
        this.mVatValue = mVatValue;
    }

    public String getVatActualValue() {
        return mVatActualValue;
    }

    public void setVatActualValue(String mVatActualValue) {
        this.mVatActualValue = mVatActualValue;
    }

    @Override
    public String toString() {
        return "ShoppingCartData == " +
                "CTN Number = " + mCtnNumber +
                "Product title =" + mProductTitle +
                "Image URL =" + mImageURL +
                "Quantity = " + mQuantity +
                "Total Item" + mTotalItems +
                "EntryNumber =" + mEntryNumber +
                "StockLevel" + mStockLevel;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public String getAppliedVoucherCode() {
        return mAppliedVoucherCode;
    }

    public void setAppliedVoucherCode(String mAppliedVoucherCode) {
        this.mAppliedVoucherCode = mAppliedVoucherCode;
    }

    public String getTotalDiscounts() {
        return totalDiscounts;
    }

    public void setTotalDiscounts(String totalDiscounts) {
        this.totalDiscounts = totalDiscounts;
    }

    public List<AppliedVoucherEntity> getAppliedVouchers() {
        return appliedVouchers;
    }

    public void setAppliedVouchers(List<AppliedVoucherEntity> appliedVouchers) {
        this.appliedVouchers = appliedVouchers;
    }
}