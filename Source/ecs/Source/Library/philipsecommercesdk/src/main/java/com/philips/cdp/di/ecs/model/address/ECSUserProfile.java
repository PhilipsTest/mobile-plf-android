package com.philips.cdp.di.ecs.model.address;

import java.io.Serializable;

/**
 * The type Ecs user profile contains user details.
 *This object is returned when fetching user details
 *
 */
public class ECSUserProfile implements Serializable {
    private ECSAddress defaultAddress;

    private String type;
    private String name;
    private String uid;
    private String displayUid;
    private String firstName;
    private String lastName;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public ECSAddress getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(ECSAddress defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getDisplayUid() {
        return displayUid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
