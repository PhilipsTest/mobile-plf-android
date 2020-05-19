/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.VisibleForTesting;

import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.voucher.ECSVoucher;

import java.io.Serializable;
import java.util.List;

/**
 * The type Ecs order detail contains details of each order including address, delivery mode, payment info, tax.
 * It is passed as input parameter while making payment specifying the order for which payment is to be made
 * This object is returned when order is submitted
 * It returns the order detail by passing the orderId for fetchOrderDetail
 */
public class ECSOrderDetail implements Parcelable, Serializable {


    private static final long serialVersionUID = 2079813913551660129L;
    private String type;
    private boolean calculated;
    private String code;

    private ECSAddress deliveryAddress;
    private Cost deliveryCost;
    private int deliveryItemsQuantity;

    private ECSDeliveryMode deliveryMode;
    private String guid;
    private boolean net;

    private Cost orderDiscounts;

    private PaymentInfo paymentInfo;
    private int pickupItemsQuantity;

    private Cost productDiscounts;
    private String site;
    private String store;

    private Cost subTotal;

    private Cost totalDiscounts;
    private int totalItems;

    private Cost totalPrice;
    private Cost totalPriceWithTax;
    private Cost totalTax;

    private User user;
    private String created;
    private String deliveryStatus;
    private boolean guestCustomer;
    private String status;
    private String statusDisplay;



    private List<ECSVoucher> appliedVouchers ;
    private List<AppliedOrderPromotions> appliedOrderPromotions;
    private List<DeliveryOrderGroups> deliveryOrderGroups;

    private List<ECSEntries> entries;

    private List<Consignment> consignments;


    public ECSOrderDetail() {
    }

    protected ECSOrderDetail(Parcel in) {
        type = in.readString();
        calculated = in.readByte() != 0;
        code = in.readString();
        deliveryItemsQuantity = in.readInt();
        guid = in.readString();
        net = in.readByte() != 0;
        pickupItemsQuantity = in.readInt();
        site = in.readString();
        store = in.readString();
        totalItems = in.readInt();
        created = in.readString();
        deliveryStatus = in.readString();
        guestCustomer = in.readByte() != 0;
        status = in.readString();
        statusDisplay = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeByte((byte) (calculated ? 1 : 0));
        dest.writeString(code);
        dest.writeInt(deliveryItemsQuantity);
        dest.writeString(guid);
        dest.writeByte((byte) (net ? 1 : 0));
        dest.writeInt(pickupItemsQuantity);
        dest.writeString(site);
        dest.writeString(store);
        dest.writeInt(totalItems);
        dest.writeString(created);
        dest.writeString(deliveryStatus);
        dest.writeByte((byte) (guestCustomer ? 1 : 0));
        dest.writeString(status);
        dest.writeString(statusDisplay);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ECSOrderDetail> CREATOR = new Creator<ECSOrderDetail>() {
        @Override
        public ECSOrderDetail createFromParcel(Parcel in) {
            return new ECSOrderDetail(in);
        }

        @Override
        public ECSOrderDetail[] newArray(int size) {
            return new ECSOrderDetail[size];
        }
    };

    public String getType() {
        return type;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public String getCode() {
        return code;
    }

    @VisibleForTesting
    public void setCode(String code){
        this.code = code;
    }

    public ECSAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Cost getDeliveryCost() {
        return deliveryCost;
    }
    public void setDeliveryCost(Cost deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public int getDeliveryItemsQuantity() {
        return deliveryItemsQuantity;
    }

    public ECSDeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public String getGuid() {
        return guid;
    }

    public boolean isNet() {
        return net;
    }

    public Cost getOrderDiscounts() {
        return orderDiscounts;
    }
    public void setOrderDiscounts(Cost orderDiscounts) {
        this.orderDiscounts = orderDiscounts;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public int getPickupItemsQuantity() {
        return pickupItemsQuantity;
    }

    public Cost getProductDiscounts() {
        return productDiscounts;
    }

    public String getSite() {
        return site;
    }

    public String getStore() {
        return store;
    }

    public Cost getSubTotal() {
        return subTotal;
    }

    public Cost getTotalDiscounts() {
        return totalDiscounts;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public Cost getTotalPrice() {
        return totalPrice;
    }

    public Cost getTotalPriceWithTax() {
        return totalPriceWithTax;
    }

    public Cost getTotalTax() {
        return totalTax;
    }

    public User getUser() {
        return user;
    }

    public String getCreated() {
        return created;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public boolean isGuestCustomer() {
        return guestCustomer;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public List<ECSVoucher> getAppliedVouchers() {
        return appliedVouchers;
    }
    public void setAppliedVouchers(List<ECSVoucher> appliedVouchers) {
        this.appliedVouchers = appliedVouchers;
    }

    public List<AppliedOrderPromotions> getAppliedOrderPromotions() {
        return appliedOrderPromotions;
    }
    public void setAppliedOrderPromotions(List<AppliedOrderPromotions> appliedOrderPromotions) {
        this.appliedOrderPromotions = appliedOrderPromotions;
    }

    public List<DeliveryOrderGroups> getDeliveryOrderGroups() {
        return deliveryOrderGroups;
    }

    public List<ECSEntries> getEntries() {
        return entries;
    }

    public List<Consignment> getConsignments() {
        return consignments;
    }
    public void setConsignments(List<Consignment> consignments) {
        this.consignments = consignments;
    }

}
