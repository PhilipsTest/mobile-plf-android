package com.ecs.demouapp.ui.response.placeorder;


import com.ecs.demouapp.ui.response.addresses.Country;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class DeliveryAddressEntity {
    private Country country;
    private String id;
    private String line1;
    private String houseNumber;
    private String line2;
    private String postalCode;
    private String town;

    public Country getCountry() {
        return country;
    }

    public String getId() {
        return id;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getTown() {
        return town;
    }

    public String getHouseNumber() {
        return houseNumber;
    }
}
