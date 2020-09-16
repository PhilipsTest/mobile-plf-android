/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * The type Ecs delivery mode which contains all delivery details like cost, name and description.
 * The values are set in setDeliveryMode and it is returned with updated data in fetchDeliveryModes
 */
public class ECSDeliveryMode implements Parcelable, Serializable {
    private static final long serialVersionUID = -6520630116967292115L;
    private String code;
    private DeliveryCost deliveryCost;
    private String description;
    private String name;
    private boolean pickupPoint;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected;

    //Added to write test cases
    public ECSDeliveryMode(){

    }

    protected ECSDeliveryMode(Parcel in) {
        code = in.readString();
        description = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(description);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ECSDeliveryMode> CREATOR = new Creator<ECSDeliveryMode>() {
        @Override
        public ECSDeliveryMode createFromParcel(Parcel in) {
            return new ECSDeliveryMode(in);
        }

        @Override
        public ECSDeliveryMode[] newArray(int size) {
            return new ECSDeliveryMode[size];
        }
    };

    public void setCode(String code) {
        this.code = code;
    }

    public void setDeliveryCost(DeliveryCost deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public DeliveryCost getDeliveryCost() {
        return deliveryCost;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public boolean isPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(boolean pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
}
